package br.dev.danielrl.server.heartbeat;

public class ServiceInstance {
    private String id;
    private String type;
    private String host;
    private int port;
    private long lastHeartbeat;

    public ServiceInstance(String id, String type, String host, int port) {
        this.id = id;
        this.type = type;
        this.host = host;
        this.port = port;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    
}
