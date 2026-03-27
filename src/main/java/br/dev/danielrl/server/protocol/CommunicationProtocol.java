package br.dev.danielrl.server.protocol;

public interface CommunicationProtocol {
    void send(String message);
    String receive();
    void startServer(int port);
}
