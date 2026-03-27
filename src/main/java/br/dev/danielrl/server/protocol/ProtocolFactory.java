package br.dev.danielrl.server.protocol;


public class ProtocolFactory {
    
    public static CommunicationProtocol createProtocol(String protocolType) {
        switch (protocolType.toLowerCase()) {
            case "udp":
                return new UdpProtocol();
            case "tcp":
                return new TcpProtocol();
            default:
                throw new IllegalArgumentException("Unsupported protocol type: " + protocolType);
        }
    }

}
