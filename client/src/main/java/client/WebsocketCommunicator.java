package client;

import chess.ChessMove;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    URI socketURI;

    public WebsocketCommunicator(String host, int port, ServerMessageHandler serverMessageHandler) throws URISyntaxException {
        socketURI = new URI("ws://" + host + ":" + port + "/ws");
        this.serverMessageHandler = serverMessageHandler;
        session = null;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, socketURI);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                serverMessageHandler.notify(message);
            }
        });

        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendCommand(command);
    }

    public void makeMove(ChessMove move, String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        command.setMove(move);
        sendCommand(command);
    }

    public void resign(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        sendCommand(command);
    }

    public void leave(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendCommand(command);
        session = null;
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }
}