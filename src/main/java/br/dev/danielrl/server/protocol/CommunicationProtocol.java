package br.dev.danielrl.server.protocol;

public interface CommunicationProtocol {
    void send(Message message);
    Message receive();
    void startServer(int port);
}
