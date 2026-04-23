package br.dev.danielrl.server.protocol;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpProtocol implements CommunicationProtocol {

    private ExecutorService poolvthreads = Executors.newVirtualThreadPerTaskExecutor();

    private DatagramSocket serversocket;

    @Override
    public void send(Message message) {
        String response = message.getRawText();
        byte[] sendData = response.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, message.getAddress(),
                message.getPort());
        try {
            this.serversocket.send(sendPacket);
            System.out.println("Sent response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message receive() {
        byte[] receivemessage = new byte[1024];
        DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
        try {
            this.serversocket.receive(receivepacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = new String(receivepacket.getData(), 0, receivepacket.getLength());
        // poolvthreads.submit(() -> {
        // processarMensagem(message, receivepacket, serversocket);
        // });
        return new Message(receivepacket.getAddress(), receivepacket.getPort(), message);
    }

    @Override
    public void startServer(int port) {
        System.out.println("Starting UDP server on port: " + port);
        try {
            this.serversocket = new DatagramSocket(port);
        } catch ( BindException e) {
            System.out.println("Porta " + port + " Já está em uso. Por favor, escolha outra porta.");
            return;
         }
        catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
