package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final ConnectType connectType;

    public ConnectCommand(String authToken, int gameID, ConnectType connectType) {
        super(CommandType.RESIGN, authToken, gameID);
        this.connectType = connectType;
    }

    public ConnectType getConnectType() {
        return connectType;
    }

    public enum ConnectType {
        WHITE_PLAYER,
        BLACK_PLAYER,
        OBSERVER
    }
}
