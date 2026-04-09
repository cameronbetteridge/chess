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

    @Override
    public String toString() {
        String str = startPosition.toString() + "-" + endPosition.toString();
        if (promotionPiece != null) {
            str += switch (promotionPiece) {
                case ChessPiece.PieceType.QUEEN -> "=Q";
                case ChessPiece.PieceType.ROOK -> "=R";
                case ChessPiece.PieceType.BISHOP -> "=B";
                case ChessPiece.PieceType.KNIGHT -> "=N";
                default -> "";
            };
        }
        return str;
    }

    public static ChessMove fromString(String str) throws Exception{
        ChessPiece.PieceType promotionPiece = null;
        if (str.indexOf('=') != -1) {
            char promotionChar = str.charAt(str.indexOf('=') + 1);
            promotionPiece = switch (promotionChar) {
                case 'Q' -> ChessPiece.PieceType.QUEEN;
                case 'R' -> ChessPiece.PieceType.ROOK;
                case 'B' -> ChessPiece.PieceType.BISHOP;
                case 'N' -> ChessPiece.PieceType.KNIGHT;
                default -> null;
            };
        }

        String[] positions = str.split("-");
        if (positions.length != 2) {
            throw new Exception("Error: Invalid move.");
        }

        ChessPosition startPosition = ChessPosition.fromString(positions[0]);
        ChessPosition endPosition = ChessPosition.fromString(positions[1]);

        return new ChessMove(startPosition, endPosition, promotionPiece);
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
