package br.dev.danielrl.domain;

public class PlayerEliminationEvent extends GameLogEvent {
    private String killerPlayerId;
    private String victimPlayerId;
    private EliminationMethod method;

    public PlayerEliminationEvent() {
        super();
    }

    public PlayerEliminationEvent(String eventId, long timestamp, String killerPlayerId, String victimPlayerId,
            EliminationMethod method) {
        super(eventId, timestamp, LogEventType.PLAYER_ELIMINATION);
        this.killerPlayerId = killerPlayerId;
        this.victimPlayerId = victimPlayerId;
        this.method = method;
    }

    public String getKillerPlayerId() {
        return killerPlayerId;
    }

    public void setKillerPlayerId(String killerPlayerId) {
        this.killerPlayerId = killerPlayerId;
    }

    public String getVictimPlayerId() {
        return victimPlayerId;
    }

    public void setVictimPlayerId(String victimPlayerId) {
        this.victimPlayerId = victimPlayerId;
    }

    public EliminationMethod getMethod() {
        return method;
    }

    public void setMethod(EliminationMethod method) {
        this.method = method;
    }
}