package br.dev.danielrl.server.entity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import br.dev.danielrl.domain.LogQuery;
import br.dev.danielrl.server.heartbeat.HeartBeatScheduler;
import br.dev.danielrl.server.heartbeat.ServiceInstance;
import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;
import br.dev.danielrl.server.service.GatewayEnvelope;
import br.dev.danielrl.server.service.SegmentedLogReadService;
import br.dev.danielrl.server.service.ServiceJsonResponse;

public class LogReaderNode implements DistributedNode {

    private final CommunicationProtocol protocol;
    private final int port;
    private InetAddress local;
    private HeartBeatScheduler heartBeatScheduler;
    private final Gson gson = new Gson();
    private final String readerId;
    private final SegmentedLogReadService readService;

    public LogReaderNode(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        this.readerId = "logreader-" + port;
        this.readService = new SegmentedLogReadService();
        try {
            this.local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        System.out.println("LogReaderNode is starting with the protocol: " + protocol.getClass().getSimpleName());
        protocol.startServer(port);
        heartBeatScheduler = new HeartBeatScheduler(this::sendHeartbeat, 5000L);
        heartBeatScheduler.start();

        while (true) {
            Message message = protocol.receive();
            if (message == null) {
                continue;
            }

            GatewayEnvelope envelope = tryParseEnvelope(message.getBody());
            String payload = envelope != null ? envelope.getPayload() : message.getBody();

            InetAddress replyAddress = message.getAddress();
            int replyPort = message.getPort();

            String responseBody;
            String responseEndpoint;
            switch (message.getEndpoint()) {
                case "readLog":
                    LogQuery query = gson.fromJson(payload, LogQuery.class);
                    responseBody = readService.read(query);
                    responseEndpoint = "readLogResponse";
                    break;
                default:
                    responseBody = ServiceJsonResponse.error("UNSUPPORTED_ENDPOINT",
                            "LogReaderNode does not support endpoint: " + message.getEndpoint());
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
        String json = gson.toJson(new ServiceInstance(readerId, "logreader", local.getHostAddress(), port));
        Message heartbeatMessage = new Message(local, 9000, "heartbeat", json);
        protocol.send(heartbeatMessage);
    }
}