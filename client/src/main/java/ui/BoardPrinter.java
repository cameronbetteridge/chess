package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Arrays;
import java.util.Collections;

public class BoardPrinter {
    public void printBoard(ChessBoard board, boolean blackPlayer) {
        printColumnLabels(blackPlayer);
        finishLine();

        boolean startBlackSquare = false;
        int start = blackPlayer ? 1 : 8;
        int update = blackPlayer ? 1 : -1;

        for (int rowNum = start; rowNum > 0 && rowNum < 9; rowNum += update) {
            ChessPiece[] pieces = getPieces(board, blackPlayer, rowNum);
            printRow(pieces, startBlackSquare, rowNum);
            startBlackSquare = !startBlackSquare;
        }

        printColumnLabels(blackPlayer);
        finishLine();
    }

    private ChessPiece[] getPieces(ChessBoard board, boolean blackPlayer, int rowNum) {
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

        char[] labels = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int start = blackPlayer ? 7 : 0;
        int update = blackPlayer ? -1 : 1;
        for (int i = start; i < 9 && i >= 0; i += update) {
            System.out.print(' ' + labels[i] + ' ');
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

    }

    private void printPiece(ChessPiece piece) {

    }

    private void printRowLabel(int rowNum) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(' ' + rowNum + ' ');
    }

    private void finishLine() {
        System.out.println(EscapeSequences.RESET_BG_COLOR);
    }

    private void reversePieces(ChessPiece[] pieces) {
        Collections.reverse(Arrays.asList(pieces));
    }
}
