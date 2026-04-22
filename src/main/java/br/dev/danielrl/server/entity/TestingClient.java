package br.dev.danielrl.server.entity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;

public class TestingClient implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;
    private InetAddress local;

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
            String userCommand = System.console().readLine("Enter a command to send to the gateway: ");
            switch (userCommand) {
                case "write":
                    String writeRequestBodyString = "{\"action\": \"enter-lobby\", \n\"user\": \"fulano\", \n\"lobbyId\": \"lobby456\"}";
                    Message testMessage = new Message(local, 9000, "writeLog", writeRequestBodyString);
                    protocol.send(testMessage);
                    break;
                case "read":
                    String readRequestBodyString =  "{\"startTimestamp\": \"1234567890\"} \n{\"endTimestamp\": \"1234567899\"}";
                    Message readMessage = new Message(local, 9000, "readLog", readRequestBodyString);
                    protocol.send(readMessage);
                    break;

                default:
                    break;
            }
        }
    }

}
