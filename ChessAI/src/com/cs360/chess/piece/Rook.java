package com.cs360.chess.piece;

import com.cs360.chess.Board;

public final class Rook extends Piece {

    private static final int points = 5;
    private static final int id = 1;

    public Rook(int id, boolean isBlack, int column, int row) {
        super(id, isBlack, column, row);
    }

    public Rook(int id, boolean black, boolean hasMoved, int column, int row) {
        super(id, black, hasMoved, column, row);
    }

    @Override
    public Rook clone() {
        return new Rook(getUniqueId(), isBlack(), hasMoved(), getColumn(), getRow());
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public int getId() {
        return id;
    }

    //forward, backward, left, or right in any direction at any amount as long as there no pieces in the way
    @Override
    public int[][] computePossible(Board board) {
        return straightMoves(board, this);
    }

    @Override
    public String toString() {
        return "Rook[uid=" + getUniqueId() + "black=" + isBlack() + ",points=" + getPoints() + ",moved=" + hasMoved() + ",col=" + getColumn() + ",row=" + getRow() + ",BINARY=" + Integer.toBinaryString(data) + "]";
    }

}
