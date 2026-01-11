package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public enum RelativeTeamColor {
        ALLY,
        ENEMY
    }

    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) obj;

        return color.equals(piece.color) && pieceType.equals(piece.pieceType);
    }

    public int hashCode() {
        return (color.ordinal() * 6) + pieceType.ordinal();
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    private RelativeTeamColor getRelativeColor(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            return null;
        }

        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if (color.equals(pieceColor)) {
            return RelativeTeamColor.ALLY;
        } else {
            return RelativeTeamColor.ENEMY;
        }
    }

    private boolean validPosition(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        return col <= 8 && col >= 1 && row <= 8 && row >= 1;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();

        for (int horizontalChange = -1; horizontalChange <= 1; horizontalChange++) {
            for (int verticalChange = -1; verticalChange <= 1; verticalChange++) {
                if (horizontalChange == 0 && verticalChange == 0) {
                    continue;
                }

                int newRow = myPosition.getRow() + horizontalChange;
                int newCol = myPosition.getColumn() + verticalChange;
                ChessPosition newPosition = new ChessPosition(newRow, newCol);

                if (validPosition(newPosition)) {
                    if (getRelativeColor(board, newPosition) != RelativeTeamColor.ALLY) {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        movesList.add(move);
                    }
                }
            }
        }

        return movesList;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesList = (ArrayList<ChessMove>) bishopMoves(board, myPosition);
        movesList.addAll(rookMoves(board, myPosition));
        return movesList;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();

        for (int horizontalChange = -1; horizontalChange <= 1; horizontalChange+=2) {
            for (int verticalChange = -1; verticalChange <= 1; verticalChange+=2) { // Each of the four diagonal directions
                int newRow = myPosition.getRow() + horizontalChange;
                int newCol = myPosition.getColumn() + verticalChange;
                ChessPosition newPosition = new ChessPosition(newRow, newCol);

                while (validPosition(newPosition) && getRelativeColor(board, newPosition) == null) {
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    movesList.add(move);
                    newRow += horizontalChange;
                    newCol += verticalChange;
                    newPosition = new ChessPosition(newRow, newCol);
                }

                if (validPosition(newPosition) && !getRelativeColor(board, newPosition).equals(RelativeTeamColor.ALLY)) {
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    movesList.add(move);
                }
            }
        }

        return movesList;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (pieceType) {
            case KING:
                return kingMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case BISHOP:
                return bishopMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case PAWN:
                return pawnMoves(board, myPosition);
            default:
                throw new RuntimeException("Invalid piece type");
        }
    }
}
