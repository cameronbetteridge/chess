package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whiteCanCastleKingside == chessGame.whiteCanCastleKingside
                && whiteCanCastleQueenside == chessGame.whiteCanCastleQueenside
                && blackCanCastleKingside == chessGame.blackCanCastleKingside
                && blackCanCastleQueenside == chessGame.blackCanCastleQueenside
                && Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn
                && Objects.equals(enPassantPosition, chessGame.enPassantPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, enPassantPosition, whiteCanCastleKingside,
                whiteCanCastleQueenside, blackCanCastleKingside, blackCanCastleQueenside);
    }

    private boolean passesThroughCheck(int row, int middle) {
        TeamColor color = row == 1 ? TeamColor.WHITE : TeamColor.BLACK;
        ChessPosition startPosition = new ChessPosition(row, 5);
        ChessPosition endPosition = new ChessPosition(row, middle);
        ChessBoard hypotheticalBoard = tryMove(board, new ChessMove(startPosition, endPosition, null));
        return isInCheck(color, hypotheticalBoard);
    }

    private boolean validCastle(int row, int middle, int endCol) {
        TeamColor color = row == 1 ? TeamColor.WHITE : TeamColor.BLACK;
        if (board.getPiece(new ChessPosition(row, middle)) != null) {
            return false;
        } else if (board.getPiece(new ChessPosition(row, endCol)) != null) {
            return false;
        } else if (isInCheck(color)) {
            return false;
        } else {
            return !passesThroughCheck(row, middle);
        }
    }

    private ChessMove castle(int row, int middle, int endCol) {
        if (!validCastle(row, middle, endCol)) { return null; }

        ChessPosition startPosition = new ChessPosition(row, 5);
        ChessPosition endPosition = new ChessPosition(row, endCol);
        return new ChessMove(startPosition, endPosition, null);
    }

    private Collection<ChessMove> legalCastlingMoves(TeamColor teamColor, ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        boolean isWhite = teamColor == TeamColor.WHITE;
        int row = isWhite ? 1 : 8;
        boolean canCastleKingside = isWhite ? whiteCanCastleKingside : blackCanCastleKingside;
        boolean canCastleQueenside = isWhite ? whiteCanCastleQueenside : blackCanCastleQueenside;
        ChessPosition validStartPosition = isWhite ? new ChessPosition(1, 5) : new ChessPosition(8, 5);

        if (!startPosition.equals(validStartPosition)) { return moves; }

        if (canCastleKingside) {
            ChessMove move = castle(row, 6, 7);
            if (move != null) { moves.add(move); }
        }
        if (canCastleQueenside) {
            ChessMove move = castle(row, 4, 3);
            if (move != null) { moves.add(move); }
        }
        return moves;
    }

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

        TeamColor color = board.getPiece(startPosition).getTeamColor();
        return color == TeamColor.WHITE ? whiteEnPassant(startPosition) : blackEnPassant(startPosition);
    }

    private ChessBoard tryMove(ChessBoard board, ChessMove move) {
        ChessBoard resultingBoard = board.copy();
        ChessPiece movingPiece = resultingBoard.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }
        resultingBoard.addPiece(move.getEndPosition(), movingPiece);
        resultingBoard.addPiece(move.getStartPosition(), null);
        return resultingBoard;
    }

    private void addSpecialMoves(Collection<ChessMove> moves, ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            moves.addAll(legalCastlingMoves(color, startPosition));
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessMove enPassant = getEnPassant(startPosition);
            if (enPassant != null) {
                moves.add(enPassant);
            }
        }
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
        if (piece == null) { return new ArrayList<>(); }
        TeamColor color = piece.getTeamColor();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        addSpecialMoves(moves, startPosition);

        for (ChessMove move : moves) {
            ChessBoard resultingBoard = tryMove(board, move);
            if (!isInCheck(color, resultingBoard)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    private boolean isLegalMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) { return false; }
        TeamColor color = piece.getTeamColor();

        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        boolean moveIsValid = false;

        for (ChessMove legalMove : legalMoves) {
            if (legalMove.equals(move)) {
                moveIsValid = true;
                break;
            }
        }
        return moveIsValid && color == teamTurn;
    }

    private void updateEnPassantPosition(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            ChessPosition startPosition = move.getStartPosition();
            if (startPosition.getRow() - move.getEndPosition().getRow() == 2) {
                enPassantPosition = new ChessPosition(startPosition.getRow()-1, startPosition.getColumn());
            } else if (startPosition.getRow() - move.getEndPosition().getRow() == -2) {
                enPassantPosition = new ChessPosition(startPosition.getRow()+1, startPosition.getColumn());
            } else { enPassantPosition = null; }
        } else { enPassantPosition = null; }
    }

    private void updateCastlingRights(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
            if (piece.getTeamColor().equals(TeamColor.WHITE)) {
                whiteCanCastleKingside = false;
                whiteCanCastleQueenside = false;
            } else {
                blackCanCastleKingside = false;
                blackCanCastleQueenside = false;
            }
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
            if (piece.getTeamColor().equals(TeamColor.WHITE)) {
                if (move.getStartPosition().equals(new ChessPosition(1, 1))) {
                    whiteCanCastleQueenside = false;
                } else if (move.getStartPosition().equals(new ChessPosition(1, 8))) {
                    whiteCanCastleKingside = false;
                }
            } else {
                if (move.getStartPosition().equals(new ChessPosition(8, 1))) {
                    blackCanCastleQueenside = false;
                } else if (move.getStartPosition().equals(new ChessPosition(8, 8))) {
                    blackCanCastleKingside = false;
                }
            }
        }
    }

    private boolean isCastlingMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        if (board.getPiece(startPosition).getPieceType() != ChessPiece.PieceType.KING) {
            return false;
        } else {
            return Math.abs(startPosition.getColumn() - move.getEndPosition().getColumn()) == 2;
        }
    }

    private void castleRook(ChessMove move) {
        ChessPosition kingPosition = move.getEndPosition();
        int row = kingPosition.getRow();
        int endCol = kingPosition.getColumn() == 3 ? 4 : 6;
        int startCol = endCol == 4 ? 1 : 8;
        ChessPosition endPosition = new ChessPosition(row, endCol);
        ChessPosition startPosition = new ChessPosition(row, startCol);
        board.addPiece(endPosition, board.getPiece(startPosition));
        board.addPiece(startPosition, null);
    }

    private boolean isEnPassantMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) { return false; }
        if (move.getStartPosition().getColumn() == move.getEndPosition().getColumn()) { return false; }
        return board.getPiece(move.getEndPosition()) == null;
    }

    private void captureEnPassant(ChessMove move) {
        int row = move.getStartPosition().getRow();
        int col = move.getEndPosition().getColumn();
        board.addPiece(new ChessPosition(row, col), null);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!isLegalMove(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece piece = board.getPiece(move.getStartPosition());
        boolean moveIsCastling = isCastlingMove(move);
        boolean moveIsEnPassant = isEnPassantMove(move);

        updateEnPassantPosition(move);
        updateCastlingRights(move);
        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;

        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), piece);
        if (moveIsCastling) { castleRook(move); }
        if (moveIsEnPassant) { captureEnPassant(move); }
    }

    private boolean canMoveTo(ChessPosition startPosition, ChessPosition endPosition, ChessBoard board) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) { return false; }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(endPosition)) { return true; }
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
        ArrayList<ChessMove> allMoves = new ArrayList<>();
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
