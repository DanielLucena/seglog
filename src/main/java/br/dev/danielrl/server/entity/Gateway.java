package br.dev.danielrl.server.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;

import br.dev.danielrl.server.heartbeat.AbstractFailureDetector;
import br.dev.danielrl.server.heartbeat.FaliureDetector;
import br.dev.danielrl.server.heartbeat.NodeHBInfo;
import br.dev.danielrl.server.heartbeat.ServiceInstance;
import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;

public class Gateway implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;
    private AbstractFailureDetector failureDetector;
    private CopyOnWriteArrayList<NodeHBInfo> nodeHBInfos = new CopyOnWriteArrayList<>();
    private final Map<String, ServiceInstance> registry = new ConcurrentHashMap<>();

    public Gateway(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        this.failureDetector = new FaliureDetector();
    }

    @Override
    public void start() {
        System.out.println("Gateway node is starting...");
        protocol.startServer(port);
        System.out.println("Gateway is running and waiting for messages...");
        while (true) {
            // Wait for incoming messages
            // For example, you could receive messages and route them to other nodes based
            // on some logic
            // This is a placeholder for the actual gateway logic
            Message receivedMessage = protocol.receive(); // This would be your method to receive messages

            switch (receivedMessage.getEndpoint()) {
                case "heartbeat":
                    // System.out.println("Received heartbeat: " + receivedMessage.getBody());
                    handleHeartbeatRequest(receivedMessage);
                    break;
                case "writeLog":
                    System.out.println("Received write log request: " + receivedMessage.getBody());
                    handleWriteLogRequest(receivedMessage);
                    break;

                default:
                    break;
            }
            
            // You would implement the logic to receive messages and route them here
            // For example, you could use protocol.receiveMessage() to get incoming messages
            // and then route them using protocol.sendMessage() to the appropriate nodes
        }

    }

    private void handleHeartbeatRequest(Message request) {
        try {
            Gson mapper = new Gson();
            String json = request.getBody();

            ServiceInstance instance = mapper.fromJson(json, ServiceInstance.class);

            instance.setLastHeartbeat(System.currentTimeMillis());
            registry.put(instance.getId(), instance);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void handleWriteLogRequest(Message request) {
        // Implement logic to handle write log requests, such as routing them to a
        // LogWriterNode
        System.out.println("Handling write log request: " + request.getBody());
        protocol.send(request); // This is a placeholder for the actual logic to route the request to a LogWriterNode
    }
}
