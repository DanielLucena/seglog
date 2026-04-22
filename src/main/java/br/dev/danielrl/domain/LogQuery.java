package br.dev.danielrl.domain;

public class LogQuery {
    private long startTimestamp;
    private long endTimestamp;
    private String playerId;
    private String lobbyId;
    private LogEventType eventType;

    public LogQuery() {
    }

    public LogQuery(long startTimestamp, long endTimestamp, String playerId, String lobbyId, LogEventType eventType) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.playerId = playerId;
        this.lobbyId = lobbyId;
        this.eventType = eventType;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public LogEventType getEventType() {
        return eventType;
    }

    public void setEventType(LogEventType eventType) {
        this.eventType = eventType;
    }
}