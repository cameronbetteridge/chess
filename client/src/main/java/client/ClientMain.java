package client;

import ui.BoardPrinter;
import ui.ClientUI;

import java.net.URISyntaxException;

public class ClientMain {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade("localhost", 8080);
        BoardPrinter boardPrinter = new BoardPrinter();
        ServerMessageHandler serverMessageHandler = new ServerMessageHandler(boardPrinter);
        WebsocketCommunicator websocketCommunicator;

        try {
            websocketCommunicator = new WebsocketCommunicator("localhost", 8080, serverMessageHandler);
        } catch (URISyntaxException e) {
            System.err.println(e.getMessage());
            return;
        }

        ClientUI clientUI = new ClientUI(serverFacade, websocketCommunicator, boardPrinter);
        System.out.println("Welcome to 240 Chess! Type Help to get started.");
        clientUI.mainLoop();
    }
}
