package week4.assignment;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * create a data type that models an n-by-n board with sliding tiles. Implement an immutable data type Board
 */
public class Board {

    private final int[][] blocksBoard;
    private final int dimension;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        dimension = tiles.length;
        blocksBoard = copyBoard(tiles);
    }

    //extra method to visualize(n added in the assignment)
    public void draw(double x, double y) {
        StdDraw.text(x - .03 * dimension, y, String.valueOf(dimension));
        for (int i = 0; i < dimension; i++) {
            y -= .03;
            x -= .03 * dimension;
            for (int j = 0; j < dimension; j++) {
                StdDraw.text(x, y, String.valueOf(blocksBoard[i][j]));
                x += 0.03;
            }
        }
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        int[][] set = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                set[i][j] = StdIn.readInt();
            }
        }
        Board board = new Board(set);
        System.out.println(board.dimension());
        System.out.println(board.hamming());
        System.out.println(board.isGoal());
        System.out.println(board.manhattan());
        for (Board k : board.neighbors()) {
            System.out.println(k);
        }
        System.out.println(board.twin());
    }

    private int[][] copyBoard(int[][] tiles) {
        final int[][] blocksBoard;
        blocksBoard = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                blocksBoard[i][j] = tiles[i][j];
            }
        }
        return blocksBoard;
    }

    // string representation of this board
    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append(dimension).append("\n");
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s.append(String.format("%3d ", blocksBoard[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of tiles out of place
    public int hamming() {
        int count = 0;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if ((blocksBoard[i][j] != 0) && (blocksBoard[i][j] != expectedValueAtPosition(i, j))) {
                    count++;
                }
            }
        }
        return count;
    }

    // Get expected value that should be in provided position
    private int expectedValueAtPosition(int i, int j) {
        return dimension * i + j + 1;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int value;
        int sum = 0;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if ((blocksBoard[i][j] != 0) && (blocksBoard[i][j] != (expectedValueAtPosition(i, j)))) {
                    value = blocksBoard[i][j] - 1;

                    int expectedI = (value / dimension);
                    int expectedJ = (value % dimension);

                    sum += Math.abs(i - expectedI) + Math.abs(j - expectedJ);
                }
            }
        }
        return sum;
    }

    // is this board the goal board?
    public boolean isGoal() {
        // either hamming() or manhattan() can be used
        return hamming() == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }

        if (y == this) {
            return true;
        }

        if (y.getClass() != this.getClass()) {
            return false;
        }

        Board that = (Board) y;

        return Arrays.deepEquals(this.blocksBoard, that.blocksBoard);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {

        List<Board> boardStack = new LinkedList<>();

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {

                // we have found 0 block
                if (blocksBoard[i][j] == 0) {

                    // if 0 block is not at top position
                    if (i > 0) {
                        int[][] blocksCopy = copyBoard(blocksBoard);
                        blocksCopy[i][j] = blocksBoard[i - 1][j];
                        blocksCopy[i - 1][j] = blocksBoard[i][j];

                        boardStack.add(new Board(blocksCopy));
                    }

                    // if 0 block is not at left position
                    if (j > 0) {
                        int[][] blocksCopy = copyBoard(blocksBoard);
                        blocksCopy[i][j] = blocksBoard[i][j - 1];
                        blocksCopy[i][j - 1] = blocksBoard[i][j];

                        boardStack.add(new Board(blocksCopy));
                    }

                    // if 0 block is not at bottom position
                    if (i < dimension - 1) {
                        int[][] blocksCopy = copyBoard(blocksBoard);
                        blocksCopy[i][j] = blocksBoard[i + 1][j];
                        blocksCopy[i + 1][j] = blocksBoard[i][j];

                        boardStack.add(new Board(blocksCopy));
                    }

                    // if 0 block is not at right position
                    if (j < dimension - 1) {
                        int[][] blocksCopy = copyBoard(blocksBoard);
                        blocksCopy[i][j] = blocksBoard[i][j + 1];
                        blocksCopy[i][j + 1] = blocksBoard[i][j];

                        boardStack.add(new Board(blocksCopy));
                    }

                    break;
                }
            }
        }

        return boardStack;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

        int[][] twinall = copyBoard(blocksBoard);

        if (blocksBoard[0][0] != 0 && blocksBoard[0][1] != 0) {
            twinall[0][0] = blocksBoard[0][1];
            twinall[0][1] = blocksBoard[0][0];
        } else {
            twinall[1][0] = blocksBoard[1][1];
            twinall[1][1] = blocksBoard[1][0];
        }

        return new Board(twinall);
    }

}
