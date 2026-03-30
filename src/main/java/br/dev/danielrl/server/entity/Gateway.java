package br.dev.danielrl.server.entity;

import br.dev.danielrl.server.protocol.CommunicationProtocol;

public class Gateway implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;

    public Gateway(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    @Override
    public void start() {
        System.out.println("Gateway node is starting...");
        // Here you would add logic to start the gateway, such as listening for incoming connections
        // and routing messages to the appropriate nodes in the distributed system.
    }

}
