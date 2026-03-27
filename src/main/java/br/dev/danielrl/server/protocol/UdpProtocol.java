package br.dev.danielrl.server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpProtocol implements CommunicationProtocol {

    ExecutorService poolvthreads = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void send(String message) {
        // Implement UDP send logic here
        System.out.println("Sending via UDP: " + message);
    }

    @Override
    public String receive() {
        // Implement UDP receive logic here
        String receivedMessage = "Received message via UDP";
        System.out.println(receivedMessage);
        return receivedMessage;
    }

    @Override
    public void startServer(int port) {
        // Implement UDP server start logic here
        System.out.println("Starting UDP server on port: " + port);
        		try {
			DatagramSocket serversocket = new DatagramSocket(port);
			while (true) {
				byte[] receivemessage = new byte[1024];
				DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
				serversocket.receive(receivepacket);
				String message = new String(receivepacket.getData(), 0, receivepacket.getLength());
				poolvthreads.submit(() -> {
					processarMensagem(message, receivepacket, serversocket);
				});

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException nfe) {
			System.out.println("Erro ao converter numero: " + nfe.getMessage());

		} catch (Exception e) {
			System.out.println("Erro inesperado: " + e.getMessage());
		}
		System.out.println("UDP Bank server terminating");
    }

    private void processarMensagem(String message, DatagramPacket receivepacket, DatagramSocket serversocket) {
        System.out.println("Processing message: " + message);
        // Here you can add logic to process the received message and send a response if needed
        String response = "Processed: " + message;
        byte[] sendData = response.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivepacket.getAddress(), receivepacket.getPort());
        try {
            serversocket.send(sendPacket);
            System.out.println("Sent response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
