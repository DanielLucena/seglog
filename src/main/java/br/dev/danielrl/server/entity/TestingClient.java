package br.dev.danielrl.server.entity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;
import br.dev.danielrl.server.service.ConsolidationRequest;

public class TestingClient implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;
    private InetAddress local;
    private Gson gson = new Gson();

    public TestingClient(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        try {
            this.local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        System.out.println("TestingClient node is starting...");
        protocol.startServer(port);
        
        while (true) {
            String userCommand = System.console().readLine("Enter a command (write/read/consolidate/ping): ");
            switch (userCommand) {
                case "write":
                    JsonObject event = new JsonObject();
                    event.addProperty("eventId", "evt-" + System.currentTimeMillis());
                    event.addProperty("timestamp", System.currentTimeMillis());
                    event.addProperty("type", "LOBBY_ACTIVITY");
                    event.addProperty("action", "ENTER");
                    event.addProperty("playerId", "player-01");
                    event.addProperty("lobbyId", "lobby-azul");
                    String writeRequestBodyString = gson.toJson(event);
                    Message testMessage = new Message(local, 9000, "writeLog", writeRequestBodyString);
                    protocol.send(testMessage);
                    printResponse();
                    break;
                case "read":
                    long now = System.currentTimeMillis();
                    JsonObject query = new JsonObject();
                    query.addProperty("startTimestamp", now - 60_000L);
                    query.addProperty("endTimestamp", now - 10_000L);
                    query.addProperty("eventType", "LOBBY_ACTIVITY");
                    String readRequestBodyString = gson.toJson(query);
                    Message readMessage = new Message(local, 9000, "readLog", readRequestBodyString);
                    protocol.send(readMessage);
                    printResponse();
                    break;
                case "consolidate":
                    long nowWindow = System.currentTimeMillis() - 10_000L;
                    long windowStart = (nowWindow / 10_000L) * 10_000L;
                    ConsolidationRequest consolidateRequest = new ConsolidationRequest(windowStart, windowStart + 9_999L);
                    Message consolidateMessage = new Message(local, 9000, "consolidateLogs", gson.toJson(consolidateRequest));
                    protocol.send(consolidateMessage);
                    printResponse();
                    break;
                case "ping":
                    Message pingMessage = new Message(local, 9000, "gatewayPing");
                    protocol.send(pingMessage);
                    printResponse();
                    break;

                default:
                    break;
            }
        }
    }

    private void printResponse() {
        Message response = protocol.receive();
        if (response != null) {
            System.out.println("Response endpoint=" + response.getEndpoint());
            System.out.println("Response body=" + response.getBody());
        }
    }

}
