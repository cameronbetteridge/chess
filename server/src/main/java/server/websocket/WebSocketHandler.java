package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect((ConnectCommand) command, ctx.session);
                case MAKE_MOVE -> makeMove((MakeMoveCommand) command, ctx.session);
                case RESIGN -> resign((ResignCommand) command, ctx.session);
                case LEAVE -> leave((LeaveCommand) command, ctx.session);
            }
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(ConnectCommand command, Session session) throws IOException {
        connections.add(session);
        String message;
        if (command.getConnectType().equals(ConnectCommand.ConnectType.OBSERVER)) {
            message = String.format("%s is observing the game.", getUsername(command.getAuthToken()));
        } else if (command.getConnectType().equals(ConnectCommand.ConnectType.WHITE_PLAYER)) {
            message = String.format("%s joined as the white player.", getUsername(command.getAuthToken()));
        } else {
            message = String.format("%s joined as the black player.", getUsername(command.getAuthToken()));
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage();
        NotificationMessage notification = new NotificationMessage(message);
        if (session.isOpen()) {
            session.getRemote().sendString(loadGameMessage.toString());
        }
        connections.broadcast(command.getGameID(), session, notification);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {

    }

    private void resign(ResignCommand command, Session session) throws IOException {

    }

    private void leave(LeaveCommand command, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.DEPARTURE, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(session);
    }

    private String getUsername(String authToken) {

    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOISE, message);
            connections.broadcast(gameID, null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}