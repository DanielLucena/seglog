package br.dev.danielrl.server.protocol;

import java.net.InetAddress;

public class Message {
    private InetAddress address;
    private int port;
    private String messageText;

    public Message(InetAddress address, int port, String messageText) {
        this.address = address;
        this.port = port;
        this.messageText = messageText;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    
}
