package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] boardPieces = new ChessPiece[8][8];

    public ChessBoard() {
    }

    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard board = (ChessBoard) obj;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = getPiece(new ChessPosition(i, j));
                ChessPiece otherPiece = board.getPiece(new ChessPosition(i, j));

                boolean condition;
                if (piece == null) {
                    condition = otherPiece == null;
                } else {
                    condition = otherPiece != null && piece.equals(otherPiece);
                }

                if (!condition) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardPieces[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardPieces[position.getRow() - 1][position.getColumn() - 1];
    }

    private void initializeBackRow(int row, ChessGame.TeamColor color) {
        addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
    }

    private void initializePawnsRow(int row, ChessGame.TeamColor color) {
        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(row, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        initializeBackRow(1, ChessGame.TeamColor.WHITE);
        initializePawnsRow(2, ChessGame.TeamColor.WHITE);
        initializePawnsRow(7, ChessGame.TeamColor.BLACK);
        initializeBackRow(8, ChessGame.TeamColor.BLACK);

        for (int row = 3; row < 7; row++) {
            for (int col = 1; col < 9; col++) {
                addPiece(new ChessPosition(row, col), null);
            }
        }
    }
}
