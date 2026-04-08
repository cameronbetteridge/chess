package ui;

import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import client.WebsocketCommunicator;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientUI {
    ServerFacade serverFacade;
    WebsocketCommunicator websocketCommunicator;
    BoardPrinter boardPrinter;
    String authToken;
    Map<Integer,Integer> gameIDs;
    int currentGameID;

    public ClientUI(ServerFacade serverFacade, WebsocketCommunicator websocketCommunicator, BoardPrinter boardPrinter) {
        this.serverFacade = serverFacade;
        this.websocketCommunicator = websocketCommunicator;
        this.boardPrinter = boardPrinter;
        authToken = null;
        gameIDs = new HashMap<>();
        currentGameID = -1;
    }

    public void mainLoop() {
        boolean done = false;
        while (true) {
            String[] args = getInput(authToken != null);
            if (authToken == null) {
                done = preLogin(args);
            } else if (currentGameID == -1) {
                done = postLogin(args);
            } else {
                gameplay(args);
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
                help(false, false);
            case "quit" -> {
                System.out.println("Goodbye!");
                return true;
            }
            case "login" ->
                login(args);
            case "register" ->
                register(args);
            default ->
                System.out.println("'" + args[0] + "' is not an option. Type Help for more information.");
        }
        return false;
    }

    private void login(String[] args) {
        if (notEnoughArgs(args.length, 3)) {
            return;
        }
        try {
            authToken = serverFacade.login(args[1], args[2]).authToken();
            System.out.println("Logged in as " + args[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void register(String[] args) {
        if (notEnoughArgs(args.length, 4)) {
            return;
        }
        UserData userData = new UserData(args[1], args[2], args[3]);
        try {
            authToken = serverFacade.register(userData).authToken();
            System.out.println("Logged in as " + args[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean postLogin(String[] args) {
        switch (args[0]) {
            case "help" ->
                help(true, false);
            case "quit" -> {
                logout();
                System.out.println("Goodbye!");
                return true;
            }
            case "logout" ->
                logout();
            case "create" ->
                create(args);
            case "list" ->
                list();
            case "join" ->
                join(args);
            case "observe" ->
                observe(args);
            default ->
                System.out.println("'" + args[0] + "' is not an option. Type Help for more information.");
        }
        return false;
    }

    private void gameplay(String[] args) {
        switch (args[0]) {
            case "help" ->
                help(true, true);
            case "redraw" ->
                boardPrinter.printBoard(null);
            case "move" ->
                makeMove(args);
            case "legal" -> {
                ChessPosition highlightPosition = ChessPosition.fromString(args[1]);
                boardPrinter.printBoard(highlightPosition);
            }
            case "resign" ->
                resign();
            case "leave" ->
                leaveGame();
        }
    }

    private void resign() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign? (y or n): ");
        String confirmation = scanner.nextLine().toLowerCase();

        if (!confirmation.equals("y")) {
            return;
        }

        try {
            websocketCommunicator.resign(authToken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void leaveGame() {
        try {
            websocketCommunicator.leave(authToken);
            System.out.println("You left the game.");
            currentGameID = -1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void makeMove(String[] args) {
        ChessMove chessMove = ChessMove.fromString(args[1]);
        try {
            websocketCommunicator.makeMove(authToken, chessMove);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void join(String[] args) {
        if (notEnoughArgs(args.length, 3)) {
            return;
        }
        if (!args[2].equals("white") && !args[2].equals("black")) {
            System.out.println("Must join as white or black.");
            return;
        }
        if (goodGameNum(args[1])) {
            try {
                int gameNum = Integer.parseInt(args[1]);
                serverFacade.joinGame(authToken, gameIDs.get(gameNum), args[2].toUpperCase());
                websocketCommunicator.connect(authToken, gameIDs.get(gameNum));
                currentGameID = gameIDs.get(gameNum);
                boardPrinter.setBlackPlayer(args[2].equals("black"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void observe(String[] args) {
        if (notEnoughArgs(args.length, 2)) {
            return;
        }
        if (goodGameNum(args[1])) {
            try {
                int gameNum = Integer.parseInt(args[1]);
                websocketCommunicator.connect(authToken, gameIDs.get(gameNum));
                currentGameID = gameIDs.get(gameNum);
                boardPrinter.setBlackPlayer(false);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean goodGameNum(String gameNum) {
        int num;
        try {
            num = Integer.parseInt(gameNum);
        } catch (Exception e) {
            System.out.println("Game " + gameNum + " doesn't exist.");
            return false;
        }
        if (num > gameIDs.size() || num < 0) {
            System.out.println("Game " + num + " doesn't exist.");
            return false;
        }
        return true;
    }

    private void create(String[] args) {
        if (notEnoughArgs(args.length, 2)) {
            return;
        }
        try {
            int gameID = serverFacade.createGame(authToken, args[1]).gameID();
            gameIDs.put(gameIDs.size() + 1, gameID);
            System.out.println("Created game '" + args[1] + "'.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void logout() {
        try {
            serverFacade.logout(authToken);
            authToken = null;
            System.out.println("Logged out successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void help(boolean loggedIn, boolean inGameplay) {
        if (loggedIn && inGameplay) {
            printCommand("redraw", "to redraw the chess board");
            printCommand("move <MOVE>", "to make a move");
            printCommand("legal <PIECE>", "to see a piece's legal moves");
            printCommand("resign", "to resign the game");
            printCommand("leave", "to leave the game");
        } else if (loggedIn) {
            printCommand("create <NAME>", "to start a chess game");
            printCommand("list", "to list chess games");
            printCommand("join <GAME ID> [WHITE/BLACK]", "to join a game");
            printCommand("observe <GAME ID>", "to observe a game");
            printCommand("logout", "when you are done");
            printCommand("quit", "to exit the game");
        } else {
            printCommand("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account");
            printCommand("login <USERNAME> <PASSWORD>", "to play chess");
            printCommand("quit", "to exit the game");
        }

        printCommand("help", "for more information");
    }

    private void list() {
        try {
            ArrayList<GameData> chessGames = serverFacade.listGames(authToken).games();
            if (chessGames.isEmpty()) {
                System.out.println("There are no current games.");
            } else {
                System.out.println("Current Games:");
            }
            updateGameIDs(chessGames);
            for (int gameNum : gameIDs.keySet()) {
                GameData gameData = getGameByID(chessGames, gameIDs.get(gameNum));
                assert gameData != null;
                printGame(gameNum, gameData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    private boolean notEnoughArgs(int numArgs, int requiredArgs) {
        if (numArgs < requiredArgs) {
            System.out.println("That's not right. Type Help for more information.");
            return true;
        }
        return false;
    }

    private void updateGameIDs(ArrayList<GameData> chessGames) {
        gameIDs.clear();
        for (GameData gameData : chessGames) {
            gameIDs.put(gameIDs.size() + 1, gameData.gameID());
        }
    }

    private GameData getGameByID(ArrayList<GameData> chessGames, int gameID) {
        for (GameData game : chessGames) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }
}
