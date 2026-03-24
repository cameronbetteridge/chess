package client;

import ui.BoardPrinter;
import ui.ClientUI;

public class ClientMain {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade("localhost", 8080);
        BoardPrinter boardPrinter = new BoardPrinter();
        ClientUI clientUI = new ClientUI(serverFacade, boardPrinter);
        System.out.println("Welcome to 240 Chess! Type Help to get started.");
        clientUI.mainLoop();
    }
}
