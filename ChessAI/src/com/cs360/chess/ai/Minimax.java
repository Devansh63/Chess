package com.cs360.chess.ai;

import com.cs360.chess.Board;
import com.cs360.chess.piece.King;
import com.cs360.chess.piece.Piece;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Minimax {

    private Node root;
    private int depth;

    /**
     * Generate a minimax tree
     * @param board The board after the opponents last move
     * @param depth How deep the minimax tree looks ahead
     */
    public Minimax(Board board, int depth) {
        this.depth = depth;
        this.root = new Node(board);
    }

    public int minmax(Node node, int depth, int alpha, int beta){
        Board board = node.nodeBoard;

        node.calculateChildren();

        if(depth == 1){
            return board.calcBoardScore();
        }

        if (board.isWhiteToMove()) {
            int maxScore = -10000;
            for(Node child: node.children) {
                child.score = minmax(child, depth-1, alpha, beta);
                maxScore = Math.max(maxScore, child.score);
                alpha = Math.max(alpha, maxScore);
                if(beta <= alpha)break;//pruning
            }
            //if AI white, move white here
            return maxScore;
        } else{
            int minScore = 10000;
            for(Node child: node.children){
                child.score = minmax(child, depth-1, alpha, beta);
                minScore = Math.min(minScore, child.score);
                beta = Math.min(beta,minScore);
                if(beta <= alpha)break;//pruning
            }
            return minScore;
        }

    }

    /**
     * Only Call after MiniMax Has been Called or error
     * @return Board that represents the AI's next best move
     */
    public Board bestMove(){
        System.out.println("Starting best move search");
        long start = System.currentTimeMillis();

        minmax(root, depth, -2000, 2000);

        Node node = minNode(root);
        System.out.println("Search finished. Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
        if (node == null) return null;
        else return node.nodeBoard;

    }

    /**
     *
     * @return the node with the lowest score
     */
    private Node minNode(Node parent){
        List<Node> mins = new ArrayList<>();
        int min = 2000;
        for (Node child : parent.children) {
            System.out.print(child.getScore()+" ");
            if (child.getScore() <= min) {
                if (child.getScore() < min) {
                    mins.clear();
                    mins.add(child);
                    min = child.getScore();
                } else mins.add(child);
            }
        }
        System.out.println();
        for(Node temp : mins){
            System.out.print(temp.score + " ");
        }
        System.out.println();
        if (mins.size() == 0) return null;
        return mins.get(new Random().nextInt(mins.size()));
    }

    public void changeDepth(int x){
        this.depth = x;
    }

    public void killTree() {
        root.children.clear();
        root = null;
    }

    public void newRoot(Board board) {
        this.root = new Node(board);
    }

    public Node getRoot() {
        return root;
    }

    public static class Node {

        private int score;
        private final Board nodeBoard;
        private List<Node> children;

        public Node(Board board) {
            nodeBoard = board;
        }

        public int getScore(){
            return score;
        }


        public List<Node> getChildren() {
            return children;
        }

        //To maximize MiniMax gains, Order the lists from best move to worst move for p

        /**
         * generates and populates the children nodes
         */
        public void calculateChildren(){
            children = new ArrayList<>();
            for (Piece piece : nodeBoard.getPieces()) {
                if (piece != null && piece.isBlack() != nodeBoard.isWhiteToMove()) {
                    int[][] possible = piece instanceof King ? piece.findNonIntersecting(nodeBoard) : piece.computePossible(nodeBoard);
                    for (int[] coord : possible) {
                        Board childBoard = new Board(nodeBoard);//clone
                        childBoard.movePiece(piece.getColumn(),piece.getRow(),coord[0],coord[1]);
                        if (!nodeBoard.isWhiteToMove() && childBoard.isBlackInCheck()) continue;
                        else if (nodeBoard.isWhiteToMove() && childBoard.isWhiteInCheck()) continue;
                        Node childNode = new Node(childBoard);
                        children.add(childNode);
                    }
                }
            }
            if(nodeBoard.isWhiteToMove()) {//opposite of it's children, so in this case if black is to move
                children.sort(Comparator.comparingInt(n -> ((Node) n).nodeBoard.calcBoardScore()).reversed());
            }
            else{
                children.sort(Comparator.comparingInt(n -> n.nodeBoard.calcBoardScore()));
            }

            //sort here depending on white or black top optimize min max
            //children.sort();
        }
    }
}
