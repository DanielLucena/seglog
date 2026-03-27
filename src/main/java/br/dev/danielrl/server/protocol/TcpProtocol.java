package br.dev.danielrl.server.protocol;

public class TcpProtocol implements CommunicationProtocol {

    @Override
    public void send(String message) {
        // Implement TCP send logic here
        System.out.println("TCP Protocol: Sending message - " + message);
    }

    @Override
    public String receive() {
        // Implement TCP receive logic here
        System.out.println("TCP Protocol: Receiving message...");
        return "Received TCP message";
    }

    @Override
    public void startServer(int port) {
        // Implement TCP server start logic here
        System.out.println("TCP Protocol: Starting server on port " + port);
        // Here you would add the actual server socket code to listen for incoming connections
    }

}
