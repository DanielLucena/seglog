package br.dev.danielrl.domain;

public class PlayerProgressEvent extends GameLogEvent {
    private String playerId;
    private ProgressType progressType;
    private String value;

    public PlayerProgressEvent() {
        super();
    }

    public PlayerProgressEvent(String eventId, long timestamp, String playerId, ProgressType progressType,
            String value) {
        super(eventId, timestamp, LogEventType.PLAYER_PROGRESS);
        this.playerId = playerId;
        this.progressType = progressType;
        this.value = value;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public ProgressType getProgressType() {
        return progressType;
    }

    public void setProgressType(ProgressType progressType) {
        this.progressType = progressType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}