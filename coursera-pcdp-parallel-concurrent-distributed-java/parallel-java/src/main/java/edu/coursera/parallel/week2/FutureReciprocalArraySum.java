package edu.coursera.parallel.week2;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class FutureReciprocalArraySum {
    /**
     * @param x an array of double
     *
     * @return sum of 1/x[i] for 0 <= i <= x.length
     */
    public static double seqArraySum(double[] x, int start, int end) {
        double sum = 0;
        for (int i = start; i < end; i++) {
            double v = x[i];
            sum += 1 / v;
        }

        return sum;
    }

    /**
     * @param x an array of double
     *
     * @return sum of 1/x[i] for 0 <= i <= x.length
     */
    public static double parArraySumFuture(double[] x) {
        final long startTime = System.nanoTime();

        // special class to work with RecursiveTask
        final var t = new SumArray(x, 0, x.length);
        final double sum = ForkJoinPool.commonPool().invoke(t);

        final long timeNanos = System.nanoTime() - startTime;
        printResults("parArraySumFuture", timeNanos, sum);

        return sum;
    }

    private static void printResults(String name, long timeInNanos, double sum) {
        System.out.printf("  %s is completed is %8.3f milliseconds, with sum = 8.5%f %n", name, timeInNanos / 1e6, sum);
    }

    public static void main(String[] args) {
        // note : for a small array size, seq is very fast, hence i need this massive thing on a top mac config
        double[] arr = new double[200_000_000];

        // making an array smartly
        Arrays.setAll(arr, i -> i + 1);

        // the degree which makes use of your cpu's, more usually gives you better performance
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");

        IntStream.range(0, 5).forEach(i -> {
            System.out.printf("Run %d%n", i);

            final long startTime = System.nanoTime();
            final var sum = seqArraySum(arr, 0, arr.length);
            final long timeNanos = System.nanoTime() - startTime;
            printResults("seqArraySum", timeNanos, sum);

            parArraySumFuture(arr);
        });

    }

    private static class SumArray extends RecursiveTask<Double> {
        // this threshold basically denotes the splitting, higher means less time for join
        private static final int SEQUENTIAL_THRESHOLD = 10_000;

        private final int lo;
        private final int hi;
        private final double[] arr;

        public SumArray(double[] arr, int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
            this.arr = arr;
        }

        @Override
        protected Double compute() {
            // for anything having size < some value  we will prefer sequential approach only
            if (hi - lo < SEQUENTIAL_THRESHOLD) {
                return seqArraySum(arr, lo, hi);
            } else {
                final int mid = lo + (hi - lo) / 2;
                var left = new SumArray(arr, lo, mid);
                var right = new SumArray(arr, mid, this.hi);

                left.fork();    // future async
                final var rightSum = right.compute();
                final var leftSum = left.join();// future get

                return leftSum + rightSum;
            }
        }
    }
}
