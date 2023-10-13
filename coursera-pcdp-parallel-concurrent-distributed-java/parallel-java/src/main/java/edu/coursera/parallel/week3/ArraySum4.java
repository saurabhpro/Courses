package edu.coursera.parallel.week3;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

/**
 * ArraySum4.java --- Parallel recursive example program for computing the sum of an array using futures
 * with variable execution times for add operations.
 *
 * @author Vivek Sarkar (vsarkar@rice.edu)
 * <p>
 * This example program creates an array of n random int's, and computes their sum in parallel.
 * The default value of n is 128, but any size can be provided as argv[0]
 * <p>
 * NOTE: this example program is intended for illustrating abstract performance metrics,
 * and is not intended to be used as a benchmark for real performance.
 */
public class ArraySum4 {
    static final int default_n = 128;
    static final String err = "Incorrect argument for array size (should be > 0), assuming n = " + default_n;

    /**
     * Computes the sum of an array of integers between two indexes
     *
     * @param X  the array to sum up
     * @param lo The leftmost index to use in the sum
     * @param hi the rightmost index to use in the sum
     *
     * @return - the sum of all elements in between the two indexes
     */
    static int computeSum(int[] X, int lo, int hi) throws ExecutionException, InterruptedException {
        if (lo > hi) {
            return 0;
        } else if (lo == hi) {
            return X[lo];
        } else {
            final var mid = (lo + hi) / 2;
            final var sum1 = CompletableFuture.supplyAsync(() -> {
                try {
                    return computeSum(X, lo, mid);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return 0;
            });
            final var sum2 = CompletableFuture.supplyAsync(() -> {
                try {
                    computeSum(X, mid + 1, hi);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return 0;
            });

            var local_sum = 0;

            while (!sum1.isDone() && !sum2.isDone()) {
                local_sum = sum1.get() + sum2.get();
            }
            // Variant of ArraySum1 -- count work as sum of the bits in input operands,
            // and assume that everything else is free
            countBits(sum1.get());
            countBits(sum2.get());

            return local_sum;
        }
    } // computeSum

    /**
     * Main function. Input can take one parameter, which is size
     * of the array. If there are no input parameters, use a default size
     *
     * @param argv - the input parameter
     */
    public static void main(String[] argv) throws ExecutionException, InterruptedException {
        // Initialization
        final var n = 10;
        final var X = IntStream.range(0, n).toArray();

        // Recursive parallel computation
        final var sum = computeSum(X, 0, n - 1);

        // Output
        System.out.println("Sum of " + n + " elements = " + sum);
    }

    /*
     * Return the number of bits in x for use in abstract performance metrics.
     * If x is negative, return the number of bits in -x.
     * @param x an integer
     * @return the number of bits in the input
     */
    static int countBits(int x) {
        // Convert x to a positive value
        if (x == Integer.MIN_VALUE) x = Integer.MAX_VALUE;
        else if (x < 0) x = -x;
        return 32 - Integer.numberOfLeadingZeros(x);
    }
}