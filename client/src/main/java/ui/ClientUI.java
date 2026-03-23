package ui;

import chess.ChessBoard;
import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientUI {
    ServerFacade serverFacade;
    BoardPrinter boardPrinter;
    String authToken;
    Map<Integer,Integer> gameIDs;

    public ClientUI(ServerFacade serverFacade, BoardPrinter boardPrinter) {
        this.serverFacade = serverFacade;
        this.boardPrinter = boardPrinter;
        authToken = null;
        gameIDs = new HashMap<>();
    }

    public void mainLoop() {
        boolean done = false;
        while (true) {
            String[] args = getInput(authToken != null);
            if (authToken == null) {
                done = preLogin(args);
            } else {
                postLogin(args);
            }
            if (done) {
                return;
            }
            System.out.println();
        }
    }

    private String[] getInput(boolean loggedIn) {
        Scanner scanner = new Scanner(System.in);
        if (loggedIn) {
            System.out.print("[LOGGED IN] >>> ");
        } else {
            System.out.print("[LOGGED OUT] >>> ");
        }
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
        String command = scanner.nextLine().toLowerCase();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        return command.split(" ");
    }

    private boolean preLogin(String[] args) {
        switch (args[0]) {
            case "help" ->
                help(false);
            case "quit" -> {
                System.out.println("Goodbye!");
                return true;
            }
            case "login" -> {
                authToken = serverFacade.login(args[1], args[2]);
                System.out.println("Logged in as " + args[1]);
            }
            case "register" -> {
                authToken = serverFacade.register(args[1], args[2], args[3]);
                System.out.println("Logged in as " + args[1]);
            }
            default ->
                System.out.println("'" + args[0] + "' is not an option. Type Help for more information.");
        }
        return false;
    }

    private void postLogin(String[] args) {
        switch (args[0]) {
            case "help" ->
                help(true);
            case "logout" -> {
                serverFacade.logout(authToken);
                authToken = null;
                System.out.println("Logged out successfully.");
            }
            case "create" -> {
                int gameID = serverFacade.createGame(authToken, args[1]);
                gameIDs.put(gameIDs.size() + 1, gameID);
                System.out.println("Created game '" + args[1] + "'.");
            }
            case "list" ->
                list();
            case "join" ->
                gameplay(args[2].equals("black"));
            case "observe" ->
                gameplay(false);
            default ->
                System.out.println("'" + args[0] + "' is not an option. Type Help for more information.");
        }
    }

    private void gameplay(boolean blackPlayer) {
        boardPrinter.printBoard(new ChessBoard(), blackPlayer);
    }

    private void help(boolean loggedIn) {
        if (loggedIn) {
            printCommand("create <NAME>", "to start a chess game");
            printCommand("list", "to list chess games");
            printCommand("join <GAME ID> [WHITE/BLACK]", "to join a game");
            printCommand("observe <GAME ID>", "to observe a game");
            printCommand("logout", "when you are done");
        } else {
            printCommand("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account");
            printCommand("login <USERNAME> <PASSWORD>", "to play chess");
        }

        printCommand("quit", "to exit the game");
        printCommand("help", "for more information");
    }

    private void list() {
        ArrayList<GameData> chessGames = serverFacade.listGames(authToken);
        for (int gameNum : gameIDs.keySet()) {
            GameData gameData = chessGames.get(gameIDs.get(gameNum));
            printGame(gameNum, gameData);
        }
    }

    private void printGame(int gameNum, GameData gameData) {
        StringBuilder builder = new StringBuilder();
        builder.append(gameNum);
        builder.append(". ");
        builder.append(gameData.gameName());

        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        System.out.print(builder);
        builder = new StringBuilder();

        builder.append(" - ");
        if (gameData.whiteUsername() == null) {
            builder.append("<available>");
        } else {
            builder.append(gameData.whiteUsername());
        }
        builder.append(" versus ");
        if (gameData.blackUsername() == null) {
            builder.append("<available>");
        } else {
            builder.append(gameData.blackUsername());
        }

        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.print(builder);
        System.out.println(EscapeSequences.RESET_TEXT_COLOR);
    }

    private void printCommand(String command, String description) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        System.out.print(command);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.print(" - " + description);
        System.out.println(EscapeSequences.RESET_TEXT_COLOR);
    }
}
