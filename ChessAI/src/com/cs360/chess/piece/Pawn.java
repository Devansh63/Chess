package com.cs360.chess.piece;

import com.cs360.chess.Board;

public final class Pawn extends Piece {

    private static final int points = 1;
    private static final int id = 0;

    public Pawn(int id, boolean isBlack, int column, int row) {
        super(id, isBlack, column, row);
    }

    public Pawn(int id, boolean black, boolean hasMoved, int column, int row) {
        super(id, black, hasMoved, column, row);
    }

    @Override
    public Pawn clone() {
        return new Pawn(getUniqueId(), isBlack(), hasMoved(), getColumn(), getRow());
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public int getId() {
        return id;
    }

    //2 spaces forward first shot if clear
    //1 space forward from then on if clear
    //can move diagonally 1 space (forward) only if there is a piece in the said diagonal spot
    @Override
    public int[][] computePossible(Board board) {
        int direction = isBlack() ? 1 : -1;
        int[][] moves = new int[4][2];
        int index = 0;

        //No attack (move forward 1 space)
        if (board.isInBounds(getColumn(), getRow() + direction) && !board.isPieceAt(getColumn(), getRow() + direction)) {
            moves[index] = new int[]{getColumn(), getRow() + direction};
            index++;
        }
        
        //attack left (diagonal)
        if (board.isInBounds(getColumn() - 1, getRow() + direction) && board.isPieceAt(getColumn() - 1, getRow() + direction) && (board.getPieceAt(getColumn() - 1, getRow() + direction).isBlack() != isBlack())) {
            moves[index] = new int[]{getColumn() - 1, getRow() + direction}; //attack left
            index++;
        }
        
        //attack right (diagonal)
        if (board.isInBounds(getColumn() + 1, getRow() + direction) && board.isPieceAt(getColumn() + 1, getRow() + direction) && (board.getPieceAt(getColumn() + 1, getRow() + direction).isBlack() != isBlack())) {
            moves[index] = new int[]{getColumn() + 1, getRow() + direction}; //attack right
            index++;
        }
        
        //No attack, move forward 2 spaces if the piece has not moved ever
        if (!hasMoved() && board.isInBounds(getColumn(), getRow() + (2 * direction)) && !board.isPieceAt(getColumn(), getRow() + (2 * direction)) && !board.isPieceAt(getColumn(), getRow() + direction)) {
            moves[index] = new int[]{getColumn(), getRow() + (2 * direction)};
            index++;
        }

        int[][] trimmedMoves = new int[index][2];
        System.arraycopy(moves, 0, trimmedMoves, 0, index);
        return trimmedMoves;
    }

    @Override
    public String toString() {
        return "Pawn[uid=" + getUniqueId() + "black=" + isBlack() + ",points=" + getPoints() + ",moved=" + hasMoved() + ",col=" + getColumn() + ",row=" + getRow() + ",BINARY=" + Integer.toBinaryString(data) + "]";
    }

}
