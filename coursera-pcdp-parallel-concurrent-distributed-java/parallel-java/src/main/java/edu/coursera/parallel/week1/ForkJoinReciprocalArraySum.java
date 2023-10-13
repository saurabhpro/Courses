package edu.coursera.parallel.week1;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

public class ForkJoinReciprocalArraySum {
    public static double seqArraySum(double[] x) {
        final var startTime = System.nanoTime();
        double sum = 0;
        for (final var v : x) {
            sum += 1 / v;
        }

        final var timeNanos = System.nanoTime() - startTime;
        printResults("seqArraySum", timeNanos, sum);

        return sum;
    }

    /**
     * @param x an array of double
     *
     * @return sum of 1/x[i] for 0 <= i <= x.length
     */
    public static double parArraySum(double[] x) {
        final var startTime = System.nanoTime();

        // special class to work with RecursiveTask
        final var t = new SumArray(x, 0, x.length);
        ForkJoinPool.commonPool().invoke(t);

        final var sum = t.ans;
        final var timeNanos = System.nanoTime() - startTime;
        printResults("parArraySum", timeNanos, sum);

        return sum;
    }

    private static void printResults(String name, long timeInNanos, double sum) {
        System.out.printf("  %s is completed is %8.3f milliseconds, with sum = 8.5%f %n", name, timeInNanos / 1e6, sum);
    }

    public static void main(String[] args) {
        // note : for a small array size, seq is very fast, hence i need this massive thing on a top mac config
        final var arr = new double[200_000_000];

        // making an array smartly
        Arrays.setAll(arr, i -> i + 1);

        // the degree which makes use of your cpu's, more usually gives you better performance
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");

        IntStream.range(0, 5).forEach(i -> {
            System.out.printf("Run %d%n", i);
            seqArraySum(arr);
            parArraySum(arr);
        });

    }

    private static class SumArray extends RecursiveAction {
        // this threshold basically denotes the splitting, higher means less time for join
        private static final int SEQUENTIAL_THRESHOLD = 10_000;

        private final int lo;
        private final int hi;
        private final double[] arr;
        private double ans;

        public SumArray(double[] arr, int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
            this.arr = arr;
        }

        @Override
        protected void compute() {
            if (hi - lo < SEQUENTIAL_THRESHOLD) {
                for (var i = lo; i < hi; i++) {
                    ans += 1 / arr[i];
                }
            } else {
                final var left = new SumArray(arr, lo, (hi + lo) / 2);
                final var right = new SumArray(arr, (hi + lo) / 2, hi);

                left.fork();
                right.compute();
                left.join();

                ans = left.ans + right.ans;
            }
        }
    }
}
