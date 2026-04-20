package br.dev.danielrl.server.entity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

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
        Gson gson = new Gson();
        protocol.startServer(port);
        while (true) {
            // wait for user to enter a command, for example, through the console
            // This is a placeholder for the actual logic to read user input and send
            // messages
            // For example, you could read a command from the console and then create a
            // message
            // to send to the gateway or other nodes in the distributed system
            String userCommand = System.console().readLine("Enter a command to send to the gateway: ");
            switch (userCommand) {
                case "write":
                    String writeRequestBodyString = "{\"action\": \"enter-lobby\", \n\"user\": \"fulano\", \n\"lobbyId\": \"lobby456\"}";
                    Message testMessage = new Message(local, 9000, "writeLog", writeRequestBodyString); // Create a message with the user command
                    protocol.send(testMessage);
                    break;
                case "read":
                    String readRequestBodyString =  "{\"startTimestamp\": \"1234567890\"} \n{\"endTimestamp\": \"1234567899\"}";
                    Message readMessage = new Message(local, 9000, "readLog", readRequestBodyString); // Create a message with the user command
                    protocol.send(readMessage);
                    break;

                default:
                    break;
            }
        }
    }

}
