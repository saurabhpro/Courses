package week1.assignment;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * ###Bonus
 * <p>
 * this project have a bonus, if u can only use 1 union find to solve this problem correctly.
 * so the question become how to use 1 union find to solve the backwash problem.
 * the hint is take advantage of find and another state.
 * as previous design, we only use 0 record block site, 1 record open site.
 * <p>
 * because two point will cause backwash, one point is hard to know if it is percolate.
 * so we need to find a solution how to break one of them.
 * <p>
 * use second is easier. because backwash is solved. and another is how to know it is percolate without virtual bottom line.
 * we need to know is there a cell connect any point in bottom line.
 * so when open a block site, we set 1 value, if it bottom line we set 2 value on it.
 * <p>
 * now the definition of open still is value > 0.
 * and we need to check percolate only need check the root status is 2 or not.
 * so what we need do is when union, if there is a set have 2 parent, we set new parent with 2.
 *
 * @author Saurabh Kumar
 */
public class Percolation {

    // Length of the square grid "gridLength * gridLength"
    private final int gridLength;

    // Array representing state of all sites (either it"s open or blocked)
    private final boolean[][] table;

    // Number of open sites
    private int openSitesNumber;


    // Index of the top virtual site (has value 0)
    private final int virtualTopIndex;

    // Index of the top virtual site (has value (gridLength * gridLength) + 1)
    private final int virtualBottomIndex;


    // Weighted quick union-find data structure to calculate percolation
    private final WeightedQuickUnionUF ufForPercolation;

    // Weighted quick union-find data structure to calculate fullness (without bottom virtual site)
    private final WeightedQuickUnionUF ufForFullness;

    // Create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Grid must have at least one row and column");
        }

        this.gridLength = n;
        int gridSize = (n * n) + 2; // with two virtual sites (includes virtual top bottom)
        this.table = new boolean[gridSize][gridSize];
        this.openSitesNumber = 0;

        // init virtual sites
        virtualBottomIndex = n * n + 1;
        virtualTopIndex = n * n;

        this.ufForPercolation = new WeightedQuickUnionUF(gridSize);
        this.ufForFullness = new WeightedQuickUnionUF(gridSize - 1);
    }

    // Open site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isOpen(row, col)) {
            return;
        }

        // mark it opened
        table[row - 1][col - 1] = true;
        openSitesNumber++;

        final int flatIndex = flattenGridIndex(row, col) - 1;

        // connect with top row
        if (row == 1) {
            ufForPercolation.union(virtualTopIndex, flatIndex);
            ufForFullness.union(virtualTopIndex, flatIndex);
        }

        // connect with bottom row
        if (row == gridLength) {
            ufForPercolation.union(virtualBottomIndex, flatIndex);
        }

        // connect with left neighbor
        if (col > 1 && isOpen(row, col - 1)) {
            final int leftIdx = flattenGridIndex(row, col - 1) - 1;
            ufForPercolation.union(flatIndex, leftIdx);
            ufForFullness.union(flatIndex, leftIdx);
        }

        // connect with right neighbor
        if (col < gridLength && isOpen(row, col + 1)) {
            final int rightIdx = flattenGridIndex(row, col + 1) - 1;
            ufForPercolation.union(flatIndex, rightIdx);
            ufForFullness.union(flatIndex, rightIdx);
        }

        // connect with top neighbor
        if (row > 1 && isOpen(row - 1, col)) {
            final int topIdx = flattenGridIndex(row - 1, col) - 1;
            ufForPercolation.union(flatIndex, topIdx);
            ufForFullness.union(flatIndex, topIdx);
        }

        // connect with bottom neighbor
        if (row < gridLength && isOpen(row + 1, col)) {
            final int bottomIdx = flattenGridIndex(row + 1, col) - 1;
            ufForPercolation.union(flatIndex, bottomIdx);
            ufForFullness.union(flatIndex, bottomIdx);
        }

        // debug
        runTests();

    }

    // If site (row, col) open
    public boolean isOpen(int row, int col) {
        validateBounds(row, col);
        return table[row - 1][col - 1];
    }

    // If site (row, col) full
    public boolean isFull(int row, int col) {
        validateBounds(row, col);
        return ufForFullness.find(flattenGridIndex(row, col) - 1) == ufForFullness.find(virtualTopIndex);
    }

    // Number of open sites
    public int numberOfOpenSites() {
        return openSitesNumber;
    }

    // If the system percolate
    public boolean percolates() {
        return ufForPercolation.find(virtualTopIndex) == ufForPercolation.find(virtualBottomIndex);
    }

    // Get site"s index to be represented in array
    private int flattenGridIndex(int row, int col) {
        return ((row - 1) * gridLength) + col;
    }

    // Check if row and column values are in range of grid size
    private void validateBounds(int row, int col) {
        if (!isOnGrid(row, col)) {
            throw new IllegalArgumentException("Row/Col index is out of bounds");
        }
    }

    private boolean isOnGrid(int row, int col) {
        int shiftRow = row - 1;
        int shiftCol = col - 1;

        return (shiftRow >= 0 && shiftCol >= 0
                && shiftRow < gridLength && shiftCol < gridLength);
    }

    // Test client (optional)
    public static void main(String[] args) {
        Percolation percolation = new Percolation(2);

        StdOut.println("percolates = " + percolation.percolates());

        StdOut.println("isOpen(1, 2) = " + percolation.isOpen(1, 2));
        StdOut.println("isFull(1, 2) = " + percolation.isFull(1, 2));
        StdOut.println("open(1, 2)");
        percolation.open(1, 2);
        StdOut.println("isOpen(1, 2) = " + percolation.isOpen(1, 2));
        StdOut.println("isFull(1, 2) = " + percolation.isFull(1, 2));
        StdOut.println("numberOfOpenSites() = " + percolation.numberOfOpenSites());
        StdOut.println("percolates() = " + percolation.percolates());

        StdOut.println("isOpen(2, 1) = " + percolation.isOpen(2, 1));
        StdOut.println("isFull(2, 1) = " + percolation.isFull(2, 1));
        StdOut.println("open(2, 1)");
        percolation.open(2, 1);
        StdOut.println("isOpen(2, 1) = " + percolation.isOpen(2, 1));
        StdOut.println("isFull(2, 1) = " + percolation.isFull(2, 1));
        StdOut.println("numberOfOpenSites() = " + percolation.numberOfOpenSites());
        StdOut.println("percolates() = " + percolation.percolates());

        StdOut.println("isOpen(1, 1) = " + percolation.isOpen(1, 1));
        StdOut.println("isFull(1, 1) = " + percolation.isFull(1, 1));
        StdOut.println("open(1, 1)");
        percolation.open(1, 1);
        StdOut.println("isOpen(1, 1) = " + percolation.isOpen(1, 1));
        StdOut.println("isFull(1, 1) = " + percolation.isFull(1, 1));
        StdOut.println("numberOfOpenSites() = " + percolation.numberOfOpenSites());
        StdOut.println("percolates() = " + percolation.percolates());
    }

    private void runTests() {
        for (int row = 1; row <= gridLength; row++) {
            for (int col = 1; col <= gridLength; col++) {
                if (isOpen(row, col)) {
                    StdOut.printf("Row: %d Col: %d is Open %n", row, col);
                } else {
                    StdOut.printf("Row: %d Col: %d is not Open %n", row, col);
                }
                if (isFull(row, col)) {
                    StdOut.printf("Row: %d Col: %d is Full %n", row, col);
                } else {
                    StdOut.printf("Row: %d Col: %d is not Full %n", row, col);
                }
            }
        }
        StdOut.printf("Sites Open: %d %n", numberOfOpenSites());
        if (percolates()) {
            StdOut.printf("Percolates %n");
        } else {
            StdOut.printf("Does not Percolate %n");
        }
    }
}


/*
percolates = false
 isOpen(1, 2) = false
 isFull(1, 2) = false
 open(1, 2)
 isOpen(1, 2) = true
 isFull(1, 2) = true
 numberOfOpenSites() = 1
 percolates() = false
 isOpen(2, 1) = false
 isFull(2, 1) = false
 open(2, 1)
 isOpen(2, 1) = true
 isFull(2, 1) = false
 numberOfOpenSites() = 2
 percolates() = false
 isOpen(1, 1) = false
 isFull(1, 1) = false
 open(1, 1)
 isOpen(1, 1) = true
 isFull(1, 1) = true
 numberOfOpenSites() = 3
 percolates() = true

 Process finished with exit code 0

 */