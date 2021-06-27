package week1.assignment;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * @author Saurabh Kumar
 * https://programmer.group/assignments-for-the-first-week-of-princeton-open-course-on-algorithms.html
 */
public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96;

    // Number of independent experiments on an n-by-n grid
    private final int trials;

    // Store all threshold results
    private final double[] thresholdList;

    // Perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n < 1) {
            throw new IllegalArgumentException("Grid must have at least one row and column");
        }

        if (trials < 1) {
            throw new IllegalArgumentException("You must run percolation at least once");
        }

        this.trials = trials;
        this.thresholdList = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);

            while (!percolation.percolates()) {
                // Generating Random Coordinates, gives number form [1-n]
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);

                // open Selected Lattice
                percolation.open(row, col);
            }

            // Save Threshold after Cycle Ends
            thresholdList[i] = (double) percolation.numberOfOpenSites() / (n * n);
        }
    }

    // Sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(thresholdList);
    }

    // Sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(thresholdList);
    }

    // Low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (CONFIDENCE_95 * stddev() / Math.sqrt(trials));
    }

    // High endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (CONFIDENCE_95 * stddev() / Math.sqrt(trials));
    }

    // Test client
    public static void main(String[] args) {
        int gridLength = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

//        int gridLength = StdIn.readInt();       //Enter an n and test times T 200 100
//        int trials = StdIn.readInt();

        PercolationStats stats = new PercolationStats(gridLength, trials);

        StdOut.println("mean = " + stats.mean());
        StdOut.println("stddev = " + stats.stddev());
        StdOut.println("95% confidence interval = [" + stats.confidenceLo() + "," + stats.confidenceHi() + "]");
    }
}