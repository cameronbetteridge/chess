package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean equals(Object obj) {
        if (obj == null | getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition position = (ChessPosition) obj;

        return row == position.getRow() && col == position.getColumn();
    }

    public int hashCode() {
        return (row * 8) + col;
    }

    @Override
    public String toString() {
        char columnName = (char) (col + 97);
        return String.format("%c%d", columnName, row);
    }

    public static ChessPosition fromString(String str) {
        char columnName = str.charAt(0);
        int col = columnName - 97;
        int row = str.charAt(1);
        return new ChessPosition(row, col);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }
}
