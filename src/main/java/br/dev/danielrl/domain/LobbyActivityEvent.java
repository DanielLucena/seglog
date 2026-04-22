package br.dev.danielrl.domain;

public class LobbyActivityEvent extends GameLogEvent {
    private String lobbyId;
    private String playerId;
    private LobbyAction action;

    public LobbyActivityEvent() {
        super();
    }

    public LobbyActivityEvent(String eventId, long timestamp, String lobbyId, String playerId, LobbyAction action) {
        super(eventId, timestamp, LogEventType.LOBBY_ACTIVITY);
        this.lobbyId = lobbyId;
        this.playerId = playerId;
        this.action = action;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public LobbyAction getAction() {
        return action;
    }

    public void setAction(LobbyAction action) {
        this.action = action;
    }
}