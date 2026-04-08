package client;

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

    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case ServerMessage.ServerMessageType.NOTIFICATION ->
                    notification((NotificationMessage) serverMessage);
            case ServerMessage.ServerMessageType.LOAD_GAME ->
                    loadGame((LoadGameMessage) serverMessage);
            case ServerMessage.ServerMessageType.ERROR ->
                    error((ErrorMessage) serverMessage);
        }
    }

    private void notification(NotificationMessage notificationMessage) {
        printMessage(notificationMessage.getMessage());
    }

    private void loadGame(LoadGameMessage loadGameMessage) {
        boardPrinter.setBoard(loadGameMessage.getGame().getBoard());
        boardPrinter.printBoard(null);
        printPrompt();
    }

    private void error(ErrorMessage errorMessage) {
        printMessage(errorMessage.getErrorMessage());
    }

    private void printMessage(String message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.println(message);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("[PLAYING CHESS] >>> ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
    }
}