package br.dev.danielrl.domain;

public abstract class GameLogEvent {
    private String eventId;
    private long timestamp;
    private LogEventType type;

    public GameLogEvent() {
    }

    public GameLogEvent(String eventId, long timestamp, LogEventType type) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public LogEventType getType() {
        return type;
    }

    public void setType(LogEventType type) {
        this.type = type;
    }
}