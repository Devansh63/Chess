package com.cs360.chess.piece;

import com.cs360.chess.Board;

public final class Knight extends Piece {

    private static final int points = 3;
    private static final int id = 2;

    public Knight(int id, boolean isBlack, int column, int row) {
        super(id, isBlack, column, row);
    }

    public Knight(int id, boolean black, boolean hasMoved, int column, int row) {
        super(id, black, hasMoved, column, row);
    }

    @Override
    public Knight clone() {
        return new Knight(getUniqueId(), isBlack(), hasMoved(), getColumn(), getRow());
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public int getId() {
        return id;
    }

    // L L L L L L L L L
    //can skip
    @Override
    public int[][] computePossible(Board board) {
        int[][] moves = new int[8][2];
        int index = 0;
        /*

        This is the diagram of the pieces in this. It is all hard coded and each point is marked by the respective letter.

          H   A
        G       B
            X
        F       C
          E   D

         */

        //Spot A
        if (board.isInBounds(getColumn() + 1, getRow() - 2)) {
            //If the spot is empty or if the piece at the given spot is the opposite color, we can go there.
            if (!board.isPieceAt(getColumn() + 1, getRow() - 2) || board.getPieceAt(getColumn() + 1, getRow() - 2).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() + 1, getRow() - 2};
                index++;
            }
        }

        //Spot B
        if (board.isInBounds(getColumn() + 2, getRow() - 1)) {
            if (!board.isPieceAt(getColumn() + 2, getRow() - 1) || board.getPieceAt(getColumn() + 2, getRow() - 1).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() + 2, getRow() - 1};
                index++;
            }
        }

        //Spot C
        if (board.isInBounds(getColumn() + 2, getRow() + 1)) {
            if (!board.isPieceAt(getColumn() + 2, getRow() + 1) || board.getPieceAt(getColumn() + 2, getRow() + 1).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() + 2, getRow() + 1};
                index++;
            }
        }

        //Spot D
        if (board.isInBounds(getColumn() + 1, getRow() + 2)) {
            if (!board.isPieceAt(getColumn() + 1, getRow() + 2) || board.getPieceAt(getColumn() + 1, getRow() + 2).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() + 1, getRow() + 2};
                index++;
            }
        }

        //Spot E
        if (board.isInBounds(getColumn() - 1, getRow() + 2)) {
            if (!board.isPieceAt(getColumn() - 1, getRow() + 2) || board.getPieceAt(getColumn() - 1, getRow() + 2).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() - 1, getRow() + 2};
                index++;
            }
        }

        //Spot F
        if (board.isInBounds(getColumn() - 2, getRow() + 1)) {
            if (!board.isPieceAt(getColumn() - 2, getRow() + 1) || board.getPieceAt(getColumn() - 2, getRow() + 1).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() - 2, getRow() + 1};
                index++;
            }
        }

        //Spot G
        if (board.isInBounds(getColumn() - 2, getRow() - 1)) {
            if (!board.isPieceAt(getColumn() - 2, getRow() - 1) || board.getPieceAt(getColumn() - 2, getRow() - 1).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() - 2, getRow() - 1};
                index++;
            }
        }

        //Spot H
        if (board.isInBounds(getColumn() - 1, getRow() - 2)) {
            if (!board.isPieceAt(getColumn() - 1, getRow() - 2) || board.getPieceAt(getColumn() - 1, getRow() - 2).isBlack() != isBlack()) {
                moves[index] = new int[]{getColumn() - 1, getRow() - 2};
                index++;
            }
        }

        int[][] trimmedMoves = new int[index][2];
        System.arraycopy(moves, 0, trimmedMoves, 0, index);
        return trimmedMoves;
    }

    @Override
    public String toString() {
        return "Knight[uid=" + getUniqueId() + "black=" + isBlack() + ",points=" + getPoints() + ",moved=" + hasMoved() + ",col=" + getColumn() + ",row=" + getRow() + ",BINARY=" + Integer.toBinaryString(data) + "]";
    }

}
