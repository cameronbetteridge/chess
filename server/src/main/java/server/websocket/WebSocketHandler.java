package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx.session);
                case MAKE_MOVE -> makeMove(command, ctx.session);
                case RESIGN -> resign(command, ctx.session);
                case LEAVE -> leave(command, ctx.session);
            }
        } catch (IOException ex) {
            System.err.println("IO Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());

            String message = "Error: Invalid command.";
            ErrorMessage errorMessage = new ErrorMessage(message);

            try {
                connections.send(ctx.session, errorMessage);
            } catch (IOException e) {
                System.err.println("IO Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        System.out.println("Connection Command Received");
        connections.add(session, command.getGameID());
        System.out.println("Connection Added");

        String username = authDAO.getAuth(command.getAuthToken()).userName();
        GameData game = gameDAO.getGame(command.getGameID());
        LoadGameMessage loadGameMessage = new LoadGameMessage(game.game());

        if (session.isOpen()) {
            connections.send(session, loadGameMessage);
        }
        System.out.println("LoadGame Message Sent");

        String message = createConnectMessage(command, username, game);
        NotificationMessage notification = new NotificationMessage(message);

        connections.broadcast(command.getGameID(), session, notification);

        System.out.println("Connection Messages Broadcasted");
    }

    @NotNull
    private String createConnectMessage(UserGameCommand command, String username, GameData game) throws DataAccessException {
        String message;
        if (username.equals(game.whiteUsername())) {
            message = String.format("%s joined as the white player.", getUsername(command.getAuthToken()));
        } else if (username.equals(game.blackUsername())) {
            message = String.format("%s joined as the black player.", getUsername(command.getAuthToken()));
        } else {
            message = String.format("%s is observing the game.", getUsername(command.getAuthToken()));
        }
        return message;
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException, DataAccessException {
        GameData game = gameDAO.getGame(command.getGameID());
        String username = authDAO.getAuth(command.getAuthToken()).userName();
        String message = tryMove(game, username, command.getMove());

        if (message == null) {
            LoadGameMessage loadGameMessage = new LoadGameMessage(game.game());
            connections.broadcast(command.getGameID(), null, loadGameMessage);

            message = String.format("%s played %s", username, command.getMove().toString());
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(command.getGameID(), session, notification);

            sendKeyGameStateMessage(game, session);
        } else {
            ErrorMessage errorMessage = new ErrorMessage(message);
            if (session.isOpen()) {
                connections.send(session, errorMessage);
            }
        }
    }

    private String tryMove(GameData game, String username, ChessMove move) {
        if (username.equals(game.whiteUsername())) {
            if (!game.game().getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
                return "Error: It's not your turn right now.";
            }
        } else if (username.equals(game.blackUsername())) {
            if (!game.game().getTeamTurn().equals(ChessGame.TeamColor.BLACK)) {
                return "Error: It's not your turn right now.";
            }
        } else {
            return "Error: You must be a player to make a move.";
        }

        try {
            game.game().makeMove(move);
            return null;
        } catch (InvalidMoveException ex) {
            return "Error: " + move.toString() + " is an illegal move!";
        }
    }

    private void sendKeyGameStateMessage(GameData game, Session session) throws IOException {
        String message = null;

        if (game.game().gameOver()) {
            message = getGameOverMessage(game);
        } else if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            message = String.format("%s is in check!", game.whiteUsername());
        } else if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            message = String.format("%s is in check!", game.blackUsername());
        }

        if (message != null) {
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(game.gameID(), session, notification);
        }
    }

    private String getGameOverMessage(GameData game) {
        if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return String.format("%s is in checkmate! %s wins!", game.whiteUsername(), game.blackUsername());
        }
        if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return String.format("%s is in checkmate! %s wins!", game.blackUsername(), game.whiteUsername());
        }
        if (game.game().isInStalemate((ChessGame.TeamColor.WHITE))) {
            return String.format("%s is in stalemate! The game ends in a draw.", game.whiteUsername());
        }
        return String.format("%s is in stalemate! The game ends in a draw.", game.blackUsername());
    }

    private void resign(UserGameCommand command, Session session) throws IOException, DataAccessException {
        GameData game = gameDAO.getGame(command.getGameID());
        String username = authDAO.getAuth(command.getAuthToken()).userName();
        if (username.equals(game.whiteUsername())) {
            game.game().resign(ChessGame.TeamColor.WHITE);
        } else if (username.equals(game.blackUsername())) {
            game.game().resign(ChessGame.TeamColor.BLACK);
        } else {
            String message = "Only players can resign the game.";
            ErrorMessage errorMessage = new ErrorMessage(message);
            connections.send(session, errorMessage);
            return;
        }

        String message = String.format("%s resigned the game.", username);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), null, notification);
    }

    private void leave(UserGameCommand command, Session session) throws IOException, DataAccessException {
        GameData game = gameDAO.getGame(command.getGameID());
        String username = authDAO.getAuth(command.getAuthToken()).userName();
        if (username.equals(game.whiteUsername())) {
            removePlayer(command, true);
        } else if (username.equals(game.blackUsername())) {
            removePlayer(command, false);
        }

        var message = String.format("%s left the game.", getUsername(command.getAuthToken()));
        var notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), session, notification);

        connections.remove(session);
    }

    private void removePlayer(UserGameCommand command, boolean isWhitePlayer) throws DataAccessException {
        GameData oldGame = gameDAO.getGame(command.getGameID());
        GameData newGame;
        if (isWhitePlayer) {
            newGame = new GameData(command.getGameID(), null, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        } else {
            newGame = new GameData(command.getGameID(), oldGame.whiteUsername(), null, oldGame.gameName(), oldGame.game());
        }
        gameDAO.updateGame(command.getGameID(), newGame);
    }

    private String getUsername(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken).userName();
    }
}