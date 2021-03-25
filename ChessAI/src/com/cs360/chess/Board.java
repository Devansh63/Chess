package com.cs360.chess;

import com.cs360.chess.piece.*;

import java.io.Serializable;

/**
 * The grid of pieces which can be manipulated. This can represent a primary board, or a future board depending on its context.
 */
public class Board implements Serializable {

    private final Piece[] pieces;
    boolean whiteToMove = true; //flag that controls who's turn it is
    private int blackKing = -1; //Index of the black king
    private int whiteKing = -1; //Index of the white king
    private boolean blackInCheck = false;
    private boolean whiteInCheck = false;

    /**
     * Creates a new board instance with a fresh set of pieces
     */
    public Board() {
        pieces = new Piece[32];
        //[COLUMN][ROW]


        for (int i = 0; i < 8; i++) {
            pieces[i] = new Pawn(i, false, i, 6);
            pieces[i+8] = new Pawn(i + 8, true, i, 1);
        }

        //The black pieces (on top)
        pieces[16] = new Rook(16, true, 0, 0);
        pieces[17] = new Knight(17, true, 1, 0);
        pieces[18] = new Bishop(18, true, 2, 0);
        pieces[19] = new Queen(19, true, 3, 0);
        pieces[20] = new King(20, true, 4, 0);
        pieces[21] = new Bishop(21, true, 5, 0);
        pieces[22] = new Knight(22, true, 6, 0);
        pieces[23] = new Rook(23, true, 7, 0);

        //The white pieces (on bottom)
        pieces[24] = new Rook(24, false, 0, 7);
        pieces[25] = new Knight(25, false, 1, 7);
        pieces[26] = new Bishop(26, false, 2, 7);
        pieces[27] = new Queen(27, false, 3, 7);
        pieces[28] = new King(28, false, 4, 7);
        pieces[29] = new Bishop(29, false, 5, 7);
        pieces[30] = new Knight(30, false, 6, 7);
        pieces[31] = new Rook(31, false, 7, 7);

        blackKing = 20;
        whiteKing = 28;

//        for (Piece piece : pieces) {
//            System.out.println(piece);
//        }
    }
    
    /**
     * Creates a new board based on a previous board. (DEEP COPY)
     * @param parent The board to base the current board off of.
     */
    public Board(Board parent) {
        this.pieces = new Piece[32];
        this.whiteToMove = parent.whiteToMove;
        this.blackInCheck = parent.blackInCheck;
        this.whiteInCheck = parent.whiteInCheck;

        int i = 0; //Keeping an index of the next available position in array
        for (Piece piece : parent.pieces) {
            if (piece == null) continue;
            this.pieces[i] = piece.clone();
            if (piece instanceof King) {
                if (piece.isBlack()) this.blackKing = i;
                else this.whiteKing = i;
            }
            i++;
        }
    }

    /**
     *
     * @return Returns the value of who is next to move
     */
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    /**
     * Moves a piece from column,row to newColumn, newRow. Top left is 0, 0, bottom right is 7, 7
     * @param column The X position of the piece
     * @param row The Y position of the piece
     * @param newColumn The newColumn position of the piece
     * @param newRow The newRow position of the piece
     */
    public void movePiece(int column, int row, int newColumn, int newRow) {
        if (!isInBounds(column, row) || !isInBounds(newColumn, newRow)) throw new RuntimeException("Piece out of bounds");

        int index = 0;
        for (Piece piece : pieces) {
            if (piece != null && piece.getColumn() == column && piece.getRow() == row) {
                clearPieceAt(newColumn, newRow);

                //Handling pawn promotion here so the AI always sees it
                if (piece instanceof Pawn && (newRow == 0 || newRow == 7)) {
                    pieces[index] = null;
                    pieces[index] = new Queen(piece.getUniqueId(), piece.isBlack(), true, newColumn, newRow);
                    break;
                }

                if (piece instanceof King && !piece.hasMoved() && (newColumn == column + 2 || newColumn == column - 2) && row == newRow) {
                    Piece rook = getPieceAt(newColumn == column + 2 ? 7 : 0, row);
                    rook.setColumn(newColumn == column + 2 ? 5 : 3);
                    rook.setHasMoved(true);
                }

                piece.setColumn(newColumn);
                piece.setRow(newRow);
                piece.setHasMoved(true);
                break;
            }
            index++;
        }
        updateKingCheckStatus();
        this.whiteToMove = !whiteToMove;
    }

    private void updateKingCheckStatus() {
        this.blackInCheck = blackKing != -1 && hasIntersections(true);
        this.whiteInCheck = whiteKing != -1 && hasIntersections(false);
    }

    /**
     * Checks if there are path intersections at a given spot from the opposing team.
     * @param checkBlack The color of the king you want to check
     * @return True if there are any intersections at all.
     */
    public boolean hasIntersections(boolean checkBlack) {
        if (checkBlack && blackKing == -1) return true;
        else if (!checkBlack && whiteKing == -1) return true;
        int row =  pieces[checkBlack ? blackKing : whiteKing].getRow();
        int column =  pieces[checkBlack ? blackKing : whiteKing].getColumn();
        for (Piece piece : pieces) {
            if (piece != null && checkBlack != piece.isBlack()) {
                for (int[] coord : piece.computePossible(this)) {
                    if (coord[0] == column && coord[1] == row) return true;
                }
            }
        }
        return false;
    }

    public boolean isBlackInCheck() {
        return blackKing == -1 || blackInCheck;
    }

    public boolean isWhiteInCheck() {
        return whiteKing == -1 || whiteInCheck;
    }

    public Piece getBlackKing() {
        return blackKing == -1 ? null : pieces[blackKing];
    }

    public Piece getWhiteKing() {
        return whiteKing == -1 ? null : pieces[whiteKing];
    }

    /**
     * Get a piece at a specific location on the board.
     * @param column The column of the piece you want
     * @param row The row of the piece you want
     * @return The piece at the given spot, or null if there is no piece in the spot.
     */
    public Piece getPieceAt(int column, int row) {
        for (Piece piece : pieces) {
            if (piece != null && piece.getColumn() == column && piece.getRow() == row) return piece;
        }
        return null;
    }

    /**
     * Removes a piece from this board at a given location
     * @param column Column of the piece you want to remove
     * @param row Row of the piece you want to remove
     */
    public void clearPieceAt(int column, int row) {
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i] != null && pieces[i].getColumn() == column && pieces[i].getRow() == row) {
                if (pieces[i] instanceof King) {
                    if (pieces[i].isBlack()) blackKing = -1;
                    else whiteKing = -1;
                }
                pieces[i] = null;
                break;
            }
        }
    }

    /**
     * Check if a piece is at a given location on the board
     * @param column The column of the piece you want to check
     * @param row The row of the piece you want to check
     * @return True if a piece was at the given location, false otherwise
     */
    public boolean isPieceAt(int column, int row) {
        return getPieceAt(column, row) != null;
    }

    /**
     * Check if the given column and row is within the bounds of the board
     * @return True if the location is within the board, false otherwise.
     */
    public boolean isInBounds(int column, int row) {
        return column <= 7 && row <= 7 && column >= 0 && row >= 0;
    }

    /**
     *
     * @return The piece array board
     */
    public Piece[] getPieces() {
        return pieces;
    }

    public int calcBoardScore(){
        int whiteScore =0;
        int blackScore =0;

        for (Piece piece : pieces) {
            if (piece != null) {
                if (piece.isBlack()) blackScore += piece.getPoints();
                else whiteScore += piece.getPoints();
            }
        }
        
        return whiteScore-blackScore;
    }

    /**
     * Gets an array of all the changed coordinates from the current board to the given board
     * @param oldBoard The board to compare it against
     * @return An integer array of all the pieces changed. (Their from locations to their to locations)
     */
    public int[][] getChangedPieceCoordinates(Board oldBoard) {
        int[][] changes = new int[4][4];
        int index = 0;
        for (Piece oldPiece : oldBoard.pieces) {
            if (oldPiece != null) {
                for (Piece piece : pieces) {
                    if (piece != null && oldPiece.getUniqueId() == piece.getUniqueId()) {
                        if (piece.getColumn() != oldPiece.getColumn() || piece.getRow() != oldPiece.getRow()) { //The piece moved
                            changes[index] = new int[]{oldPiece.getColumn(), oldPiece.getRow(), piece.getColumn(), piece.getRow()};
                            index++;
                        }
                        break;
                    }
                }
            }
        }
        return changes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("--------\n--------\n--------\n--------\n--------\n--------\n--------\n--------\n");
        for (Piece piece : pieces) {
            if (piece == null) continue;
            char character = '-';
            if (piece instanceof Bishop) character = 'B';
            else if (piece instanceof King) character = 'K';
            else if (piece instanceof Knight) character = 'N';
            else if (piece instanceof Pawn) character = 'P';
            else if (piece instanceof Queen) character = 'Q';
            else if (piece instanceof Rook) character = 'R';
            builder.setCharAt(piece.getRow() * 8 + piece.getColumn() + (piece.getRow()), character);
        }
        return builder.toString();
    }
}
