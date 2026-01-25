package chess;

import javax.management.RuntimeErrorException;
import java.util.Collection;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private ChessPosition enPassantPosition;
    private boolean whiteCanCastleKingside;
    private boolean whiteCanCastleQueenside;
    private boolean blackCanCastleKingside;
    private boolean blackCanCastleQueenside;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        enPassantPosition = null;
        whiteCanCastleKingside = true;
        whiteCanCastleQueenside = true;
        blackCanCastleKingside = true;
        blackCanCastleQueenside = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

//    private Collection<ChessMove> legalCastlingMoves(TeamColor teamColor) {
//        if (teamColor.equals(TeamColor.WHITE)) {
//            return whiteCastlingMoves();
//        } else {
//            return blackCastlingMoves();
//        }
//    }

    private ChessMove whiteEnPassant(ChessPosition startPosition) {
        if (startPosition.getRow() != 5) {
            return null;
        }
        if (Math.abs(enPassantPosition.getColumn() - startPosition.getColumn()) != 1) {
            return null;
        }
        return new ChessMove(startPosition, enPassantPosition, null);
    }

    private ChessMove blackEnPassant(ChessPosition startPosition) {
        if (startPosition.getRow() != 4) {
            return null;
        }
        if (Math.abs(enPassantPosition.getColumn() - startPosition.getColumn()) != 1) {
            return null;
        }
        return new ChessMove(startPosition, enPassantPosition, null);
    }

    private ChessMove getEnPassant(ChessPosition startPosition) {
        if (enPassantPosition == null) { return null; }

        ChessPiece piece = board.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();
        return color == TeamColor.WHITE ? whiteEnPassant(startPosition) : blackEnPassant(startPosition);
    }

    private ChessBoard tryMove(ChessBoard board, ChessMove move) {
        ChessBoard resultingBoard = board.copy();
        ChessPiece movingPiece = resultingBoard.getPiece(move.getStartPosition());
        resultingBoard.addPiece(move.getEndPosition(), movingPiece);
        resultingBoard.addPiece(move.getStartPosition(), null);
        return resultingBoard;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) { return new ArrayList<ChessMove>(); }
        TeamColor color = piece.getTeamColor();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<ChessMove>();
        for (ChessMove move : moves) {
            ChessBoard resultingBoard = tryMove(board, move);
            if (!isInCheck(color, resultingBoard)) {
                legalMoves.add(move);
            }
        }

//        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
//            legalMoves.addAll(legalCastlingMoves(color));
//        } else
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessMove enPassant = getEnPassant(startPosition);
            if (enPassant != null) {
                legalMoves.add(enPassant);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    private boolean canMoveTo(ChessPosition startPosition, ChessPosition endPosition, ChessBoard board) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return false;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(endPosition)) {
                return true;
            }
        }
        return false;
    }

    private ChessPosition getKingPosition(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (piece.getTeamColor() == teamColor) {
                        return position;
                    }
                }
            }
        }
        throw new RuntimeException("Board does not have a king of the correct color.");
    }

    private Collection<ChessMove> getAllLegalMoves(TeamColor teamColor, ChessBoard board) {
        ArrayList<ChessMove> allMoves = new ArrayList<ChessMove>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <=8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null) { continue; }
                if (piece.getTeamColor() != teamColor) { continue; }

                allMoves.addAll(validMoves(position));
            }
        }
        return allMoves;
    }

    private boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPosition = getKingPosition(teamColor, board);
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (canMoveTo(position, kingPosition, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    private boolean noLegalMoves(TeamColor teamColor) {
        return getAllLegalMoves(teamColor, board).isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noLegalMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return noLegalMoves(teamColor) && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
