package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition start;
    private ChessPosition end;
    private ChessPiece.PieceType promotion;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        start = startPosition;
        end = endPosition;
        promotion = promotionPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChessMove move = (ChessMove) obj;

        if (promotion == null && move.promotion == null) {
            return start.equals(move.start) && end.equals(move.end);
        } else if (promotion == null && move.promotion != null) {
            return false;
        } else if (promotion != null && move.promotion == null) {
            return false;
        } else {
            return start.equals(move.start) && end.equals(move.end) && promotion.equals(move.promotion);
        }
    }

    private int promotionHashCode() {
        if (promotion == null) {
            return 0;
        }
        return promotion.ordinal() + 1;
    }

    @Override
    public int hashCode() {
        return (promotionHashCode() * 4096) + ((start.hashCode() - 1) * 64) + end.hashCode();
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }
}
