package edu.coursera.parallel.week1;

import java.util.Arrays;
import java.util.stream.IntStream;

import static edu.rice.pcdp.PCDP.async;
import static edu.rice.pcdp.PCDP.finish;

public class ArraySum {

    static double sum1, sum2;

    public static double seqArraySum(double[] x) {
        long startTime = System.nanoTime();
        sum1 = sum2 = 0;

        for (int i = 0; i < x.length / 2; i++) {
            sum1 += x[i];
        }
        for (int i = x.length / 2; i < x.length; i++) {
            sum2 += x[i];
        }
        double sum = sum1 + sum2;
        long timeNanos = System.nanoTime() - startTime;
        printResults("seqArraySum", timeNanos, sum);
        return sum;
    }

    public static double parArraySum(double[] x) {
        long startTime = System.nanoTime();
        sum1 = sum2 = 0;

        finish(() -> {
            // this will create a parallel task
            async(() -> IntStream.range(0, x.length / 2).forEach(i -> sum1 += x[i]));

            // will wait for this thread to end
            IntStream.range(x.length / 2, x.length).forEach(i -> sum2 += x[i]);
        });

        double sum = sum1 + sum2;
        long timeNanos = System.nanoTime() - startTime;
        printResults("parArraySum", timeNanos, sum);
        return sum;
    }

    private static void printResults(String name, long timeInNanos, double sum) {
        System.out.printf("  %s is completed is %8.3f milliseconds, with sum = 8.5%f %n", name, timeInNanos / 1e6, sum);
    }

    public static void main(String[] args) {
        double[] arr = new double[200_000_000];
        Arrays.setAll(arr, i -> Math.random());

        IntStream.range(0, 5).forEach(i -> {
            System.out.printf("Run %d%n", i);
            seqArraySum(arr);
            parArraySum(arr);
        });

    }
}
