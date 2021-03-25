package com.cs360.chess.piece;

import com.cs360.chess.Board;

public final class Bishop extends Piece {

    private static final int points = 3;
    private static final int id = 3;

    public Bishop(int id, boolean isBlack, int column, int row) {
        super(id, isBlack, column, row);
    }

    public Bishop(int id, boolean black, boolean hasMoved, int column, int row) {
        super(id, black, hasMoved, column, row);
    }

    @Override
    public Bishop clone() {
        return new Bishop(getUniqueId(), isBlack(), hasMoved(), getColumn(), getRow());
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int[][] computePossible(Board board) {
        return diagonalMoves(board, this);
    }

    @Override
    public String toString() {
        return "Bishop[uid=" + getUniqueId() + "black=" + isBlack() + ",points=" + getPoints() + ",moved=" + hasMoved() + ",col=" + getColumn() + ",row=" + getRow() + ",BINARY=" + Integer.toBinaryString(data) + "]";
    }
}
