package br.dev.danielrl.server.entity;

import br.dev.danielrl.server.protocol.CommunicationProtocol;

public class NodeFactory {

    public static DistributedNode createNode(String nodeType, CommunicationProtocol protocol, int port) {
        switch (nodeType.toLowerCase()) {
            case "logwriter":
                return new LogWriterNode(protocol, port);
            case "gateway":
                return new Gateway(protocol, port);    
            default:
                throw new IllegalArgumentException("Unsupported node type: " + nodeType);
        }
    }
}
