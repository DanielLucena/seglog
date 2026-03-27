package br.dev.danielrl.server.entity;

import br.dev.danielrl.server.protocol.CommunicationProtocol;

public class LogWriterNode implements DistributedNode {

    private CommunicationProtocol protocol;

    public LogWriterNode(CommunicationProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void start() {
        // Example of sending a log message using the protocol
        System.out.println("LogWriterNode is starting with the protocol: " + protocol.getClass().getSimpleName());
        // protocol.send("LogWriterNode started and ready to receive logs.");
        protocol.startServer(9003); // Start the server on port 8080
    }

}
