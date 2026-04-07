package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    private final ConnectCommand.ConnectType connectType;

    public LeaveCommand(String authToken, int gameID, ConnectCommand.ConnectType connectType) {
        super(CommandType.LEAVE, authToken, gameID);
        this.connectType = connectType;
    }

    public ConnectCommand.ConnectType getConnectType() {
        return connectType;
    }
}
