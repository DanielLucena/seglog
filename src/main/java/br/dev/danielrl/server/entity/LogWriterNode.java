package br.dev.danielrl.server.entity;

import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;

public class LogWriterNode implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;

    public LogWriterNode(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    @Override
    public void start() {
        System.out.println("LogWriterNode is starting with the protocol: " + protocol.getClass().getSimpleName());
        // Inicia o servidor
        protocol.startServer(port); 
        while (true) {

            // espera por mensagens
            Message message = protocol.receive();
            System.out.println("LogWriterNode received: " + message.getMessageText());

            //TODO: Processa a mensagem (por exemplo, escrevendo em um arquivo de log)
            String revert = new StringBuilder(message.getMessageText()).reverse().toString();
            message.setMessageText(revert); // Reverse the message text as a simple processing example

            // Envia uma resposta de volta para o remetente
            protocol.send(message);
        }
    }

}
