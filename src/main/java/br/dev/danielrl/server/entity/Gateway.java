package br.dev.danielrl.server.entity;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import br.dev.danielrl.server.heartbeat.ServiceInstance;
import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;
import br.dev.danielrl.server.service.GatewayEnvelope;
import br.dev.danielrl.server.service.ServiceJsonResponse;

public class Gateway implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;
    // private AbstractFailureDetector failureDetector;
    // private CopyOnWriteArrayList<NodeHBInfo> nodeHBInfos = new CopyOnWriteArrayList<>();
    private final Map<String, ServiceInstance> registry = new ConcurrentHashMap<>();
    private final Map<String, ClientTarget> pendingRequests = new ConcurrentHashMap<>();
    private final AtomicInteger writerCursor = new AtomicInteger(0);
    private final AtomicInteger readerCursor = new AtomicInteger(0);
    private final Gson gson = new Gson();
    private final ScheduledExecutorService healthCheckExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final long HEARTBEAT_CHECK_INTERVAL_MS = 10_000L;
    private static final long HEARTBEAT_TIMEOUT_MS = 20_000L;
    private static final long REQUEST_TTL_MS = 10_000L;

    private static final class ClientTarget {
        private final InetAddress address;
        private final int port;
        private final long createdAt;

        private ClientTarget(InetAddress address, int port, long createdAt) {
            this.address = address;
            this.port = port;
            this.createdAt = createdAt;
        }
    }

    public Gateway(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        // this.failureDetector = new FaliureDetector();
    }

    @Override
    public void start() {
        System.out.println("Gateway node is starting...");
        protocol.startServer(port);
        startRegistryHealthCheck();
        System.out.println("Gateway is running and waiting for messages...");
        while (true) {
            evictExpiredPendingRequests();

            Message receivedMessage = protocol.receive();
            if (receivedMessage == null) {
                continue;
            }

            switch (receivedMessage.getEndpoint()) {
                case "heartbeat":
                    // System.out.println("Received heartbeat: " + receivedMessage.getBody());
                    handleHeartbeatRequest(receivedMessage);
                    break;
                case "gatewayPing":
                    handleGatewayPingRequest(receivedMessage);
                    break;
                case "writeLog":
                    System.out.println("Received write log request: " + receivedMessage.getBody());
                    handleWriteLogRequest(receivedMessage);
                    break;
                case "readLog":
                    System.out.println("Received read log request: " + receivedMessage.getBody());
                    handleReadLogRequest(receivedMessage);
                    break;
                case "consolidateLogs":
                    System.out.println("Received consolidate logs request: " + receivedMessage.getBody());
                    handleConsolidateLogsRequest(receivedMessage);
                    break;
                case "writeLogResponse":
                case "readLogResponse":
                case "consolidateLogsResponse":
                case "error":
                    handleServiceResponse(receivedMessage);
                    break;

                default:
                    break;
            }
        }

    }

    private void handleHeartbeatRequest(Message request) {
        try {
            ServiceInstance instance = gson.fromJson(request.getBody(), ServiceInstance.class);

            instance.setLastHeartbeat(System.currentTimeMillis());
            registry.put(instance.getId(), instance);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void handleWriteLogRequest(Message request) {
        forwardWithRoundRobin(request, "logwriter", writerCursor);
    }

    private void handleGatewayPingRequest(Message request) {
        String body = ServiceJsonResponse.success("GATEWAY_ALIVE", "Gateway is alive", null);
        protocol.send(new Message(request.getAddress(), request.getPort(), "gatewayPong", body));
    }

    private void handleReadLogRequest(Message request) {
        forwardWithRoundRobin(request, "logreader", readerCursor);
    }

    private void handleConsolidateLogsRequest(Message request) {
        forwardWithRoundRobin(request, "logwriter", writerCursor);
    }

    private void handleServiceResponse(Message response) {
        GatewayEnvelope envelope = tryParseEnvelope(response.getBody());
        if (envelope == null || envelope.getRequestId() == null || envelope.getRequestId().isBlank()) {
            return;
        }

        ClientTarget target = pendingRequests.remove(envelope.getRequestId());
        if (target == null) {
            return;
        }

        String body = envelope.getPayload() == null ? "" : envelope.getPayload();
        protocol.send(new Message(target.address, target.port, response.getEndpoint(), body));
    }

    private void forwardWithRoundRobin(Message request, String targetType, AtomicInteger cursor) {
        try {
            ServiceInstance target = pickInstance(targetType, cursor);
            if (target == null) {
                String body = ServiceJsonResponse.error("NO_TARGET_AVAILABLE",
                        "No active target found for type=" + targetType);
                protocol.send(new Message(request.getAddress(), request.getPort(), "error", body));
                return;
            }

                String requestId = UUID.randomUUID().toString();
                pendingRequests.put(requestId, new ClientTarget(request.getAddress(), request.getPort(), System.currentTimeMillis()));

            GatewayEnvelope envelope = new GatewayEnvelope(
                    request.getBody(),
                    requestId);

            Message forwarded = new Message(
                    InetAddress.getByName(target.getHost()),
                    target.getPort(),
                    request.getEndpoint(),
                    gson.toJson(envelope));

            protocol.send(forwarded);
        } catch (Exception e) {
            String body = ServiceJsonResponse.error("FORWARDING_ERROR", e.getMessage());
            protocol.send(new Message(request.getAddress(), request.getPort(), "error", body));
        }
    }

    private GatewayEnvelope tryParseEnvelope(String body) {
        try {
            GatewayEnvelope envelope = gson.fromJson(body, GatewayEnvelope.class);
            if (envelope != null && envelope.getRequestId() != null && !envelope.getRequestId().isBlank()) {
                return envelope;
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void evictExpiredPendingRequests() {
        long now = System.currentTimeMillis();
        pendingRequests.entrySet().removeIf(entry -> now - entry.getValue().createdAt > REQUEST_TTL_MS);
    }

    private void startRegistryHealthCheck() {
        healthCheckExecutor.scheduleWithFixedDelay(
                this::evictStaleServiceInstances,
                HEARTBEAT_CHECK_INTERVAL_MS,
                HEARTBEAT_CHECK_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
    }

    private void evictStaleServiceInstances() {
        long now = System.currentTimeMillis();
        registry.entrySet().removeIf(entry -> {
            ServiceInstance instance = entry.getValue();
            boolean stale = now - instance.getLastHeartbeat() > HEARTBEAT_TIMEOUT_MS;
            if (stale) {
                System.out.println("Removing stale instance from registry: id=" + instance.getId()
                        + ", type=" + instance.getType()
                        + ", host=" + instance.getHost()
                        + ", port=" + instance.getPort());
            }
            return stale;
        });
    }

    private ServiceInstance pickInstance(String type, AtomicInteger cursor) {
        List<ServiceInstance> instances = new ArrayList<>();
        for (ServiceInstance instance : registry.values()) {
            if (instance.getType() != null && instance.getType().equalsIgnoreCase(type)) {
                instances.add(instance);
            }
        }
        if (instances.isEmpty()) {
            return null;
        }
        instances.sort(Comparator.comparing(ServiceInstance::getId));
        int index = Math.floorMod(cursor.getAndIncrement(), instances.size());
        return instances.get(index);
    }
}
