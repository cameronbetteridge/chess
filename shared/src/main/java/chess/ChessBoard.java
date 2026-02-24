package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard chessBoard = (ChessBoard) obj;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece thisPiece = getPiece(position);
                ChessPiece thatPiece = chessBoard.getPiece(position);

                if (thisPiece == null && thatPiece != null) {
                    return false;
                } else if (thisPiece != null && !thisPiece.equals(thatPiece)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        int sum = 0;
        int i = 0;
        for (ChessPiece[] row : board) {
            for (ChessPiece piece : row) {
                int pieceHash = piece == null ? 0 : piece.hashCode() + 1;
                sum += pieceHash * (12 ^ i);
                i++;
            }
        }
        return sum;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    private void addPawnsRow(ChessGame.TeamColor color, int row) {
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    private void addBackRow(ChessGame.TeamColor color, int row) {
        addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
    }

    private void clearRow(int row) {
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(row, col), null);
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        addBackRow(ChessGame.TeamColor.WHITE, 1);
        addBackRow(ChessGame.TeamColor.BLACK, 8);

        addPawnsRow(ChessGame.TeamColor.WHITE, 2);
        addPawnsRow(ChessGame.TeamColor.BLACK, 7);

        for (int row = 3; row < 7; row++) {
            clearRow(row);
        }
    }

    public ChessBoard copy() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                newBoard.addPiece(position, getPiece(position));
            }
        }
        return newBoard;
    }
}
