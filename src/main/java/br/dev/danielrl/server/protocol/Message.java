package br.dev.danielrl.server.protocol;

import java.net.InetAddress;

public class Message {
    private InetAddress address;
    private int port;
    private String endpoint;
    private String rawText;
    private String body;

    public Message(InetAddress address, int port, String rawText) {
        this.address = address;
        this.port = port;
        this.rawText = rawText;
        int separatorIndex = rawText.indexOf('\n');
        if (separatorIndex < 0) {
            separatorIndex = rawText.indexOf('\r');
        }

        // caso com body separa o endpoint do body pelo primeiro \n ou \r 
        if (separatorIndex >= 0) {
            this.endpoint = rawText.substring(0, separatorIndex).trim();

            int bodyStart = separatorIndex + 1;
            if (rawText.charAt(separatorIndex) == '\r' && bodyStart < rawText.length()
                    && rawText.charAt(bodyStart) == '\n') {
                bodyStart++;
            }
            this.body = rawText.substring(bodyStart);
        } else {
            // caso sem body
            this.endpoint = rawText.trim();
            this.body = "";
        }
    }

    public Message(InetAddress address, int port, String endpoint, String body) {
        this.address = address;
        this.port = port;
        this.endpoint = endpoint;
        this.body = body;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getRawText() {
        if (rawText != null) {
            return rawText;
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(endpoint);
            sb.append("\r\n");
            sb.append(body);
            return sb.toString();
        }
        
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    
}
