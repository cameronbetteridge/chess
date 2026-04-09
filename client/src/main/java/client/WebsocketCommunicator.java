package client;

import chess.ChessMove;
import com.google.gson.Gson;

import io.javalin.router.Endpoint;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.Session;
import jakarta.websocket.MessageHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    URI socketURI;

    public WebsocketCommunicator(String url, int port, ServerMessageHandler serverMessageHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            socketURI = new URI(url + ":" + port + "/ws");
            this.serverMessageHandler = serverMessageHandler;
            this.session = null;
        } catch (URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void connect(String authToken, int gameID) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            serverMessageHandler.notify(serverMessage);
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
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
}