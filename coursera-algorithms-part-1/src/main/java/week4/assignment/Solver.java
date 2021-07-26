package week4.assignment;

import edu.princeton.cs.algs4.*;

import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;

/**
 * implement A* search to solve n-by-n slider puzzles. Create an immutable data type Solver
 */
public class Solver {

    private final SearchNode node;

    private static class SearchNode implements Comparable<SearchNode> {
        public final Board board;
        public final SearchNode previous;
        public final int moves;
        public final int priority;

        public SearchNode(Board board, SearchNode previous) {
            this.board = board;
            this.previous = previous;

            if (previous != null) {
                this.moves = previous.moves + 1;
            } else {
                this.moves = 0;
            }

            this.priority = this.board.manhattan() + this.moves;
        }

        @Override
        public int compareTo(SearchNode that) {
            return Integer.compare(this.priority, that.priority);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("board is null");
        }

        MinPQ<SearchNode> pqOriginal = new MinPQ<>();
        MinPQ<SearchNode> pqTwin = new MinPQ<>();

        pqOriginal.insert(new SearchNode(initial, null));
        pqTwin.insert(new SearchNode(initial.twin(), null));

        while (true) {
            SearchNode originalNode = pqOriginal.delMin();
            SearchNode twinNode = pqTwin.delMin();

            if (originalNode.board.isGoal()) {
                this.node = originalNode;
                return;
            } else if (twinNode.board.isGoal()) {
                this.node = null;
                return;
            }

            for (Board neighbour : originalNode.board.neighbors()) {
                if (originalNode.previous != null && originalNode.previous.board.equals(neighbour)) {
                    continue;
                }
                pqOriginal.insert(new SearchNode(neighbour, originalNode));
            }

            for (Board neighbour : twinNode.board.neighbors()) {
                if (twinNode.previous != null && twinNode.previous.board.equals(neighbour)) {
                    continue;
                }
                pqTwin.insert(new SearchNode(neighbour, twinNode));
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return node != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) {
            return -1;
        }
        return node.moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }

        Deque<Board> solution = new LinkedList<>();
        SearchNode solutionnode = node;   // to make node immutable,we use an extra searchNode here

        while (solutionnode != null) {
            solution.addFirst(solutionnode.board);
            solutionnode = solutionnode.previous;
        }

        return solution;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(StdIn.readString());
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        StdDraw.setCanvasSize(600, 600);
        StdDraw.clear();

        double x = 0;
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
                StdDraw.setPenColor(Color.red);
                board.draw(.5, .9 - 5 * x);
                StdDraw.show();
                StdDraw.pause(20);
                x += .03;
            }
        }
    }

}