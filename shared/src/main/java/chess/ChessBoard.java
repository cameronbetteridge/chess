package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] boardPieces = new ChessPiece[8][8];

    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    private void initializeBackRow(int row, ChessGame.TeamColor color) {
        row--;

        addPiece(new ChessPosition(row, 0), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.ROOK));
    }

    private void initializePawnsRow(int row, ChessGame.TeamColor color) {
        row--;

        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(row, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        initializeBackRow(1, ChessGame.TeamColor.WHITE);
        initializeBackRow(8, ChessGame.TeamColor.BLACK);
        initializePawnsRow(2, ChessGame.TeamColor.WHITE);
        initializePawnsRow(7, ChessGame.TeamColor.BLACK);

        for (int row = 3; row < 7; row++) {
            for (int col = 1; col < 9; col++) {
                addPiece(new ChessPosition(row, col), null);
            }
        }
    }
}
