package br.dev.danielrl.server.entity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import br.dev.danielrl.server.heartbeat.HeartBeatScheduler;
import br.dev.danielrl.server.heartbeat.ServiceInstance;
import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;
import br.dev.danielrl.server.service.ConsolidationRequest;
import br.dev.danielrl.server.service.GatewayEnvelope;
import br.dev.danielrl.server.service.SegmentedLogConsolidationService;
import br.dev.danielrl.server.service.SegmentedLogWriteService;
import br.dev.danielrl.server.service.ServiceJsonResponse;


public class LogWriterNode implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;
    private InetAddress local;
    private HeartBeatScheduler heartBeatScheduler;
    private final Gson gson = new Gson();
    private final String writerId;
    private final SegmentedLogWriteService writeService;
    private final SegmentedLogConsolidationService consolidationService;

    public LogWriterNode(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        this.writerId = "logwriter-" + port;
        this.writeService = new SegmentedLogWriteService(writerId);
        this.consolidationService = new SegmentedLogConsolidationService();
        try {
            this.local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        System.out.println("LogWriterNode is starting with the protocol: " + protocol.getClass().getSimpleName());
        // Inicia o servidor
        protocol.startServer(port);
        heartBeatScheduler = new HeartBeatScheduler(this::sendHeartbeat, 5000l);
        heartBeatScheduler.start();
        while (true) {

            Message message = protocol.receive();
            if (message == null) {
                continue;
            }
            System.out.println("LogWriterNode received endpoint=" + message.getEndpoint());

            GatewayEnvelope envelope = tryParseEnvelope(message.getBody());
            String payload = envelope != null ? envelope.getPayload() : message.getBody();

            InetAddress replyAddress = message.getAddress();
            int replyPort = message.getPort();

            String responseBody;
            String responseEndpoint;

            switch (message.getEndpoint()) {
                case "writeLog":    
                    // responseBody = writeService.registerEvent(payload);
                    // todo: RETIRAR para escrita de fato acontecer
                    responseBody = writeService.registerEventMock(payload);
                    responseEndpoint = "writeLogResponse";
                    break;
                case "consolidateLogs":
                    ConsolidationRequest request = gson.fromJson(payload, ConsolidationRequest.class);
                    responseBody = consolidationService.consolidate(request.getStartTimestamp(), request.getEndTimestamp());
                    responseEndpoint = "consolidateLogsResponse";
                    break;
                default:
                    responseBody = ServiceJsonResponse.error("UNSUPPORTED_ENDPOINT",
                            "LogWriterNode does not support endpoint: " + message.getEndpoint());
                    responseEndpoint = "error";
                    break;
            }

            String responseToSend = responseBody;
            if (envelope != null && envelope.getRequestId() != null && !envelope.getRequestId().isBlank()) {
                responseToSend = gson.toJson(new GatewayEnvelope(responseBody, envelope.getRequestId()));
            }

            protocol.send(new Message(replyAddress, replyPort, responseEndpoint, responseToSend));
        }
    }

    private GatewayEnvelope tryParseEnvelope(String body) {
        try {
            GatewayEnvelope envelope = gson.fromJson(body, GatewayEnvelope.class);
            if (envelope != null && envelope.getPayload() != null) {
                return envelope;
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void sendHeartbeat() {
        System.out.println("Sending heartbeat from LogWriterNode...");

        Gson mapper = new Gson();
        String json = mapper.toJson(new ServiceInstance(writerId, "logwriter", local.getHostAddress(), port));
        Message heartbeatMessage = new Message(local, 9000, "heartbeat", json);
        protocol.send(heartbeatMessage);
    }

}
