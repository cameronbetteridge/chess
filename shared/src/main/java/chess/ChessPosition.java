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
        // 2^row * 3^col is guaranteed to produce a unique integer for every row/column combination
        return Math.powExact(2, posRow) * Math.powExact(3, posCol);
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
