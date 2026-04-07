package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class BoardPrinter {
    private ChessBoard board = null;

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public void printBoard(boolean blackPlayer, ChessPosition highlightPosition) {
        ArrayList<ChessPosition> legalEndPositions = getLegalEndPositions(highlightPosition);

        printColumnLabels(blackPlayer);
        finishLine();

        boolean startBlackSquare = false;
        int start = blackPlayer ? 1 : 8;
        int update = blackPlayer ? 1 : -1;

        for (int rowNum = start; rowNum > 0 && rowNum < 9; rowNum += update) {
            ChessPiece[] pieces = getPieces(blackPlayer, rowNum);
            printRow(pieces, startBlackSquare, rowNum);
            startBlackSquare = !startBlackSquare;
        }

        printColumnLabels(blackPlayer);
        finishLine();
    }

    private ArrayList<ChessPosition> getLegalEndPositions(ChessPosition startPosition) {

    }

    private ChessPiece[] getPieces(boolean blackPlayer, int rowNum) {
        ChessPiece[] pieces = new ChessPiece[8];
        for (int i = 1; i < 9; i++) {
            pieces[i -1] = board.getPiece(new ChessPosition(rowNum, i));
        }

        if (blackPlayer) {
            reversePieces(pieces);
        }

        return pieces;
    }

    private void printColumnLabels(boolean blackPlayer) {
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

    private void printRow(ChessPiece[] pieces, boolean blackSquare, int rowNum) {
        printRowLabel(rowNum);

        for (int i = 0; i < 8; i++) {
            printSquare(pieces[i], blackSquare);
            blackSquare = !blackSquare;
        }

        printRowLabel(rowNum);
        finishLine();
    }

    private void printSquare(ChessPiece piece, boolean blackSquare) {
        if (blackSquare) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
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
