package br.dev.danielrl.server.entity;

import br.dev.danielrl.server.protocol.CommunicationProtocol;

public class NodeFactory {

    public static DistributedNode createNode(String nodeType, CommunicationProtocol protocol) {
        switch (nodeType.toLowerCase()) {
            case "logwriter":
                return new LogWriterNode(protocol);
            default:
                throw new IllegalArgumentException("Unsupported node type: " + nodeType);
        }
    }
}
