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

    private ArrayList<ChessMove> constructChessMoves(ChessBoard board, ChessPosition myPosition, int horizontalChange, int verticalChange) {
        int newRow = myPosition.getRow() + verticalChange;
        int newCol = myPosition.getColumn() + horizontalChange;
        ChessPosition newPosition = new ChessPosition(newRow, newCol);

        if (!validPosition(newPosition)) {
            return null;
        }
        if (getRelativeColor(board, newPosition).equals(RelativeTeamColor.ALLY)) {
            return null;
        }

        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();

        if (pieceType.equals(PieceType.PAWN) && newRow == 8) {
            for (PieceType type : PieceType.values()) {
                movesList.add(new ChessMove(myPosition, newPosition, type));
            }
        } else {
            movesList.add(new ChessMove(myPosition, newPosition, null));
        }

        return movesList;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();

        for (int horizontalChange = -1; horizontalChange <= 1; horizontalChange++) {
            for (int verticalChange = -1; verticalChange <= 1; verticalChange++) {
                if (horizontalChange == 0 && verticalChange == 0) {
                    continue;
                }

                ArrayList<ChessMove> moves = constructChessMoves(board, myPosition, horizontalChange, verticalChange);
                if (moves != null) {
                    movesList.addAll(moves);
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

        for (int horizontalDirection = -1; horizontalDirection <= 1; horizontalDirection+=2) {
            for (int verticalDirection = -1; verticalDirection <= 1; verticalDirection+=2) { // Each of the four diagonal directions
                int verticalChange = verticalDirection;
                int horizontalChange = horizontalDirection;
                ArrayList<ChessMove> moves = constructChessMoves(board, myPosition, horizontalChange, verticalChange);

                while (moves != null) {
                    movesList.addAll(moves);
                    verticalChange += verticalDirection;
                    horizontalChange += horizontalDirection;
                    moves = constructChessMoves(board, myPosition, horizontalChange, verticalChange);
                }
            }
        }

        return movesList;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();

        for (int horizontalChange = -2; horizontalChange <= 2; horizontalChange++) {
            if (horizontalChange == 0) {
                continue;
            }

            for (int verticalDirection = -1; verticalDirection <= 1; verticalDirection+=2) {
                int verticalChange = verticalDirection;
                if (Math.abs(horizontalChange) == 1) {
                    verticalChange *= 2;
                }

                ArrayList<ChessMove> moves = constructChessMoves(board, myPosition, horizontalChange, verticalChange);
                if (moves != null) {
                    movesList.addAll(moves);
                }
            }
        }

        return movesList;
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
