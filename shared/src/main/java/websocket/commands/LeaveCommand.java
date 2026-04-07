package websocket.commands;

public class LeaveCommand extends UserGameCommand {

    public LeaveCommand(String authToken, int gameID, ConnectCommand.ConnectType connectType) {
        super(CommandType.LEAVE, authToken, gameID);
    }
}
