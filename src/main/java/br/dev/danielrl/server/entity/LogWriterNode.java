package br.dev.danielrl.server.entity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import br.dev.danielrl.server.heartbeat.HeartBeatScheduler;
import br.dev.danielrl.server.heartbeat.ServiceInstance;
import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.Message;


public class LogWriterNode implements DistributedNode {

    private CommunicationProtocol protocol;
    private int port;
    private InetAddress local;
    private HeartBeatScheduler heartBeatScheduler;

    public LogWriterNode(CommunicationProtocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        try {
            this.local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        System.out.println("LogWriterNode is starting with the protocol: " + protocol.getClass().getSimpleName());
        // Inicia o servidor
        protocol.startServer(port);
        heartBeatScheduler = new HeartBeatScheduler(this::sendHeartbeat, 5000l);
        heartBeatScheduler.start();
        while (true) {

            // espera por mensagens
            Message message = protocol.receive();
            System.out.println("LogWriterNode received: " + message.getBody());

            // TODO: Processa a mensagem (por exemplo, escrevendo em um arquivo de log)
            String revert = new StringBuilder(message.getBody()).reverse().toString();
            message.setBody(revert); // Reverse the message body as a simple processing example

            // Envia uma resposta de volta para o remetente
            protocol.send(message);
        }
    }

    private void sendHeartbeat() {
        System.out.println("Sending heartbeat from LogWriterNode...");

        Gson mapper = new Gson();
        String json = mapper.toJson(new ServiceInstance("id-padrao", "LogWriterNode", local.getHostAddress(), port));
        Message heartbeatMessage = new Message(local, 9000, "heartbeat", json);
        protocol.send(heartbeatMessage);
    }

}
