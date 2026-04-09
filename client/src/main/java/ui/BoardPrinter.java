package ui;

import chess.*;

import java.util.*;

public class BoardPrinter {
    private ChessBoard board = null;
    private boolean blackPlayer = false;

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public void setBlackPlayer(boolean blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public void printBoard(ChessPosition highlightPosition) {
        if (board == null) {
            System.out.println("Something went wrong with loading the board...");
            return;
        }

        Collection<ChessPosition> legalEndPositions = getLegalEndPositions(highlightPosition);

        System.out.println();
        printColumnLabels();
        finishLine();

        boolean startBlackSquare = false;
        int start = blackPlayer ? 1 : 8;
        int update = blackPlayer ? 1 : -1;

        for (int rowNum = start; rowNum > 0 && rowNum < 9; rowNum += update) {
            ChessPiece[] pieces = getPieces(rowNum);
            printRow(pieces, startBlackSquare, rowNum, highlightPosition, legalEndPositions);
            startBlackSquare = !startBlackSquare;
        }

        printColumnLabels();
        finishLine();
    }

    private Collection<ChessPosition> getLegalEndPositions(ChessPosition startPosition) {
        if (startPosition == null) {
            return new ArrayList<>();
        }

        ChessGame game = new ChessGame();
        game.setBoard(board);
        Collection<ChessMove> validMoves = game.validMoves(startPosition);

        Collection<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move : validMoves) {
            endPositions.add(move.getEndPosition());
        }

        return endPositions;
    }

    private ChessPiece[] getPieces(int rowNum) {
        ChessPiece[] pieces = new ChessPiece[8];
        for (int i = 1; i < 9; i++) {
            pieces[i -1] = board.getPiece(new ChessPosition(rowNum, i));
        }

        if (blackPlayer) {
            reversePieces(pieces);
        }

        return pieces;
    }

    private void printColumnLabels() {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);

        System.out.print(EscapeSequences.EMPTY);

        String[] labels = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int start = blackPlayer ? 7 : 0;
        int update = blackPlayer ? -1 : 1;
        for (int i = start; i < 8 && i >= 0; i += update) {
            System.out.print("  " + labels[i] + "  ");
        }

        System.out.print(EscapeSequences.EMPTY);
    }

    private void printRow(ChessPiece[] pieces, boolean blackSquare, int rowNum,
                          ChessPosition startPosition, Collection<ChessPosition> legalEndPositions) {
        printRowLabel(rowNum);

        boolean highlightPosition = false;
        int start = blackPlayer ? 8 : 1;
        int update = blackPlayer ? -1 : 1;

        for (int colNum = start; colNum > 0 && colNum < 9; colNum += update) {
            boolean highlight = highlightSquare(legalEndPositions, rowNum, colNum);
            if (startPosition != null) {
                highlightPosition = startPosition.getRow() == rowNum && startPosition.getColumn() == colNum;
            }
            printSquare(pieces[colNum-1], blackSquare, highlight, highlightPosition);
            blackSquare = !blackSquare;
        }

        printRowLabel(rowNum);
        finishLine();
    }

    private boolean highlightSquare(Collection<ChessPosition> legalEndPositions, int row, int col) {
        for (ChessPosition position : legalEndPositions) {
            if (position.getColumn() == col && position.getRow() == row) {
                return true;
            }
        }
        return false;
    }

    private void printSquare(ChessPiece piece, boolean blackSquare, boolean highlight, boolean highlightPosition) {
        if (highlightPosition) {
            System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
        } else if (blackSquare && highlight) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        } else if (blackSquare) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        } else if (highlight) {
            System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
        }

        System.out.print(' ');
        printPiece(piece);
        System.out.print(' ');
    }

    private final Map<ChessPiece.PieceType, String> pieces = Map.of(
            ChessPiece.PieceType.PAWN, EscapeSequences.BLACK_PAWN,
            ChessPiece.PieceType.ROOK, EscapeSequences.BLACK_ROOK,
            ChessPiece.PieceType.KNIGHT, EscapeSequences.BLACK_KNIGHT,
            ChessPiece.PieceType.BISHOP, EscapeSequences.BLACK_BISHOP,
            ChessPiece.PieceType.QUEEN, EscapeSequences.BLACK_QUEEN,
            ChessPiece.PieceType.KING, EscapeSequences.BLACK_KING
    );

    private void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
            return;
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        } else {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        }
        System.out.print(pieces.get(piece.getPieceType()));
    }

    private void printRowLabel(int rowNum) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" " + rowNum + " ");
    }

    private void finishLine() {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.println(EscapeSequences.RESET_BG_COLOR);
    }

    private void reversePieces(ChessPiece[] pieces) {
        Collections.reverse(Arrays.asList(pieces));
    }
}
