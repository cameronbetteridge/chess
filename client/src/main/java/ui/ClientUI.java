package ui;

import chess.ChessBoard;
import client.ServerFacade;

import java.util.Scanner;

public class ClientUI {
    ServerFacade serverFacade;
    BoardPrinter boardPrinter;
    String authToken;

    public ClientUI(ServerFacade serverFacade, BoardPrinter boardPrinter) {
        this.serverFacade = serverFacade;
        this.boardPrinter = boardPrinter;
        authToken = null;
    }

    public void mainLoop() {
        boolean done = false;
        while (true) {
            String[] args = getInput(authToken != null);
            if (authToken == null) {
                done = preLogin(args);
            } else {
                postLogin(authToken, args);
            }
            if (done) {
                return;
            }
        }
    }

    private String[] getInput(boolean loggedIn) {
        Scanner scanner = new Scanner(System.in);
        if (loggedIn) {
            System.out.print("[LOGGED IN] >>> ");
        } else {
            System.out.print("[LOGGED OUT] >>> ");
        }
        String command = scanner.nextLine().toLowerCase();
        return command.split(" ");
    }

    private boolean preLogin(String[] args) {
        switch (args[0]) {
            case "help":
                help(false);
            case "quit":
                System.out.println("Goodbye!");
                return true;
            case "login":
                authToken = serverFacade.login(args[1], args[2]);
                System.out.println("Logged in as " + args[1]);
            case "register":
                authToken = serverFacade.register(args[1], args[2], args[3]);
                System.out.println("Logged in as " + args[1]);
            default:
                System.out.println("'" + args[0] + "' is not an option. Type Help for more information.");
        }

        System.out.println();
        return false;
    }

    private void postLogin(String authToken, String[] args) {

    }

    private void gameplay() {
        boardPrinter.printBoard(new ChessBoard());
    }

    private void help(boolean loggedIn) {

    }
}
