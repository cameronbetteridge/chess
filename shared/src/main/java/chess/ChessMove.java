package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public boolean equals(Object obj) {
        if (obj == null | getClass() != obj.getClass()) {
            return false;
        }
        ChessMove move = (ChessMove) obj;

        if (promotionPiece == null) {
            if (move.promotionPiece != null) {
                return false;
            }
        } else {
            if (!promotionPiece.equals(move.promotionPiece)) {
                return false;
            }
        }

        return startPosition.equals(move.getStartPosition()) && endPosition.equals(move.getEndPosition());
    }

    public int hashCode() {
        int promotionHash;
        if (promotionPiece == null) {
            promotionHash = 0;
        } else {
            promotionHash = promotionPiece.ordinal() + 1;
        }

        return (promotionHash * 5 * 64) + (startPosition.hashCode() * 64) + endPosition.hashCode();
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
