package chess;
import java.util.Collection;
import java.util.ArrayList;

public class PieceMovesCalculator {
    private ChessBoard board;
    private ChessPosition myPosition;
    private ChessGame.TeamColor myColor;
    private ChessPiece.PieceType myType;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor, ChessPiece.PieceType myType) {
        this.board = board;
        this.myPosition = myPosition;
        this.myColor = myColor;
        this.myType = myType;
    }

    private ChessPiece.PieceType[] promotionTypes = {
        ChessPiece.PieceType.QUEEN,
        ChessPiece.PieceType.ROOK,
        ChessPiece.PieceType.BISHOP,
        ChessPiece.PieceType.KNIGHT
    };

    private boolean hasPiece(ChessPosition position) {
        return board.getPiece(position) != null;
    }

    private boolean isAlly(ChessPosition position) {
        if (!hasPiece(position)) {
            return false;
        }
        return board.getPiece(position).getTeamColor().equals(myColor);
    }

    private boolean isEnemy(ChessPosition position) {
        if (!hasPiece(position)) {
            return false;
        }
        return !board.getPiece(position).getTeamColor().equals(myColor);
    }

    private boolean validPosition(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    private boolean willPromote(ChessPosition position) {
        if (myColor.equals(ChessGame.TeamColor.WHITE)) {
            return position.getRow() == 8;
        }
        return position.getRow() == 1;
    }

    private boolean canMoveTwo() {
        if (myColor.equals(ChessGame.TeamColor.WHITE)) {
            return myPosition.getRow() == 2;
        } else {
            return myPosition.getRow() == 7;
        }
    }

    private ChessPosition constructPosition(int rowChange, int colChange) {
        int newRow = myPosition.getRow() + rowChange;
        int newCol = myPosition.getColumn() + colChange;
        return new ChessPosition(newRow, newCol);
    }

    private ChessMove constructMove(int rowChange, int colChange, ChessPiece.PieceType promotionPiece) {
        ChessPosition position = constructPosition(rowChange, colChange);
        if (!validPosition(position)) {
            return null;
        } else if (isAlly(position)) {
            return null;
        }
        return new ChessMove(myPosition, position, promotionPiece);
    }

    private ArrayList<ChessMove> moveUntilAlly(int rowDirection, int colDirection) {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        int rowChange = rowDirection;
        int colChange = colDirection;
        ChessMove move = constructMove(rowChange, colChange, null);

        while (move != null) {
            movesList.add(move);
            ChessPosition newPosition = constructPosition(rowChange, colChange);
            if (hasPiece(newPosition)) {
                break;
            }
            rowChange += rowDirection;
            colChange += colDirection;
            move = constructMove(rowChange, colChange, null);
        }

        return movesList;
    }

    private ArrayList<ChessMove> moveDiagonally() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        for (int rowDirection = -1; rowDirection <= 1; rowDirection+=2) {
            for (int colDirection = -1; colDirection <= 1; colDirection+=2) {
                movesList.addAll(moveUntilAlly(rowDirection, colDirection));
            }
        }
        return movesList;
    }

    private ArrayList<ChessMove> moveStraight() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        for (int rowDirection = -1; rowDirection <= 1; rowDirection+=2) {
            movesList.addAll(moveUntilAlly(rowDirection, 0));
        }
        for (int colDirection = -1; colDirection <= 1; colDirection+=2) {
                movesList.addAll(moveUntilAlly(0, colDirection));
            }
        return movesList;
    }

    private ArrayList<ChessMove> constructPawnMoves(int rowChange, int colChange) {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        ChessPosition position = constructPosition(rowChange, colChange);
        if (!validPosition(position)) {
            return movesList;
        }

        if (colChange == 0) {
            if (hasPiece(position)) {
                return movesList;
            }
        } else {
            if (!isEnemy(position)) {
                return movesList;
            }
        }

        if (willPromote(position)) {
            for (ChessPiece.PieceType piecetype : promotionTypes) {
                ChessMove move = constructMove(rowChange, colChange, piecetype);
                if (move != null) {
                    movesList.add(move);
                }
            }
        } else {
            ChessMove move = constructMove(rowChange, colChange, null);
            if (move != null) {
                movesList.add(move);
            }
        }

        return movesList;
    }

    private ArrayList<ChessMove> getKingMoves() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        for (int rowChange = -1; rowChange <= 1; rowChange++) {
            for (int colChange = -1; colChange <= 1; colChange++) {
                if (rowChange == 0 && colChange == 0) {
                    continue;
                }

                ChessMove move = constructMove(rowChange, colChange, null);
                if (move != null) {
                    movesList.add(move);
                }
            }
        }
        return movesList;
    }

    private ArrayList<ChessMove> getQueenMoves() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        movesList.addAll(moveStraight());
        movesList.addAll(moveDiagonally());
        return movesList;
    }

    private ArrayList<ChessMove> getRookMoves() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        movesList.addAll(moveStraight());
        return movesList;
    }

    private ArrayList<ChessMove> getBishopMoves() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        movesList.addAll(moveDiagonally());
        return movesList;
    }

    private ArrayList<ChessMove> getKnightMoves() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        for (int rowChange = -2; rowChange <= 2; rowChange++) {
            if (rowChange == 0) {
                continue;
            }
            for (int colDirection = -1; colDirection <= 1; colDirection+=2) {
                int colChange = Math.abs(rowChange) == 1 ? colDirection*2 : colDirection;

                ChessMove move = constructMove(rowChange, colChange, null);
                if (move != null) {
                    movesList.add(move);
                }
            }
        }
        return movesList;
    }

    private ArrayList<ChessMove> getPawnMoves() {
        ArrayList<ChessMove> movesList = new ArrayList<ChessMove>();
        int rowDirection = myColor.equals(ChessGame.TeamColor.WHITE) ? 1 : -1;

        boolean success = movesList.addAll(constructPawnMoves(rowDirection, 0));
        if (success && canMoveTwo()) {
            movesList.addAll(constructPawnMoves(rowDirection*2, 0));
        }

        movesList.addAll(constructPawnMoves(rowDirection, 1));
        movesList.addAll(constructPawnMoves(rowDirection, -1));

        return movesList;
    }

    public Collection<ChessMove> getMoves() {
        switch (myType) {
            case ChessPiece.PieceType.KING:
                return getKingMoves();
            case ChessPiece.PieceType.QUEEN:
                return getQueenMoves();
            case ChessPiece.PieceType.ROOK:
                return getRookMoves();
            case ChessPiece.PieceType.BISHOP:
                return getBishopMoves();
            case ChessPiece.PieceType.KNIGHT:
                return getKnightMoves();
            case ChessPiece.PieceType.PAWN:
                return getPawnMoves();
            default:
                throw new RuntimeException("Piece Type is incorrect.");
        }
    }
}
