package br.dev.danielrl.server.protocol;

public class TcpProtocol implements CommunicationProtocol {

    @Override
    public void send(Message message) {
        // TODO: implementar logica para enviar mensagens TCP
        System.out.println("TCP Protocol: Sending message - " + message);
    }

    @Override
    public Message receive() {
        // TODO: implementar logica para receber mensagens TCP
        System.out.println("TCP Protocol: Receiving message...");
        return null;
    }

    @Override
    public void startServer(int port) {
        // TODO: implementar logica para iniciar um servidor TCP escutando na porta passada
        System.out.println("TCP Protocol: Starting server on port " + port);
    }

}
