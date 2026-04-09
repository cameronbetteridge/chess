package client;

import com.google.gson.Gson;
import ui.BoardPrinter;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ServerMessageHandler {
    BoardPrinter boardPrinter;

    public ServerMessageHandler(BoardPrinter boardPrinter) {
        this.boardPrinter = boardPrinter;
    }

    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case ServerMessage.ServerMessageType.NOTIFICATION ->
                    notification(message);
            case ServerMessage.ServerMessageType.LOAD_GAME ->
                    loadGame(message);
            case ServerMessage.ServerMessageType.ERROR ->
                    error(message);
        }
    }

    private void notification(String message) {
        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
        printMessage(notificationMessage.getMessage());
    }

    private void loadGame(String message) {
        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
        boardPrinter.setBoard(loadGameMessage.getGame().getBoard());
        boardPrinter.printBoard(null);
        printPrompt();
    }

    private void error(String message) {
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        printMessage(errorMessage.getErrorMessage());
    }

    private void printMessage(String message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.println(message);
        System.out.println();
        printPrompt();
    }

    private void printPrompt() {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print("[PLAYING CHESS] >>> ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
    }
}