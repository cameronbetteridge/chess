package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int posRow;
    private int posCol;

    public ChessPosition(int row, int col) {
        posRow = row;
        posCol = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition position = (ChessPosition) obj;

        return posRow == position.posRow && posCol == position.posCol;
    }

    @Override
    public int hashCode() {
        // The square are numbered bottom to top, left to right
        return ((posRow - 1) * 8) + posCol;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return posRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return posCol;
    }
}
