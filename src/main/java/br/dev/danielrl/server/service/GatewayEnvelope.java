package br.dev.danielrl.server.service;

public class GatewayEnvelope {
    private String payload;
    private String requestId;
    private String replyAddress;
    private int replyPort;

    public GatewayEnvelope() {
    }

    public GatewayEnvelope(String payload, String replyAddress, int replyPort) {
        this.payload = payload;
        this.replyAddress = replyAddress;
        this.replyPort = replyPort;
    }

    public GatewayEnvelope(String payload, String requestId) {
        this.payload = payload;
        this.requestId = requestId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReplyAddress() {
        return replyAddress;
    }

    public void setReplyAddress(String replyAddress) {
        this.replyAddress = replyAddress;
    }

    public int getReplyPort() {
        return replyPort;
    }

    public void setReplyPort(int replyPort) {
        this.replyPort = replyPort;
    }
}