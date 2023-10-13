package edu.coursera.parallel.week4.miniproject_4;

import java.util.concurrent.Phaser;

/**
 * Wrapper class for implementing one-dimensional iterative averaging using
 * phasers.
 */
public final class OneDimAveragingPhaser {
    /**
     * Default constructor.
     */
    private OneDimAveragingPhaser() {
    }

    /**
     * Sequential implementation of one-dimensional iterative averaging.
     *
     * @param iterations The number of iterations to run
     * @param myNew      A double array that starts as the output array
     * @param myVal      A double array that contains the initial input to the
     *                   iterative averaging problem
     * @param n          The size of this problem
     */
    public static void runSequential(final int iterations, final double[] myNew,
                                     final double[] myVal, final int n) {
        var next = myNew;
        var curr = myVal;

        for (var iter = 0; iter < iterations; iter++) {
            for (var j = 1; j <= n; j++) {
                next[j] = (curr[j - 1] + curr[j + 1]) / 2.0;
            }

            final var tmp = curr;
            curr = next;
            next = tmp;
        }
    }

    /**
     * An example parallel implementation of one-dimensional iterative averaging
     * that uses phasers as a simple barrier (arriveAndAwaitAdvance).
     *
     * @param iterations The number of iterations to run
     * @param myNew      A double array that starts as the output array
     * @param myVal      A double array that contains the initial input to the
     *                   iterative averaging problem
     * @param n          The size of this problem
     * @param tasks      The number of threads/tasks to use to compute the solution
     */
    public static void runParallelBarrier(final int iterations,
                                          final double[] myNew, final double[] myVal, final int n,
                                          final int tasks) {
        final var ph = new Phaser(0);
        ph.bulkRegister(tasks);

        final var threads = new Thread[tasks];

        for (var ii = 0; ii < tasks; ii++) {
            final var i = ii;

            threads[ii] = new Thread(() -> {
                var threadPrivateMyVal = myVal;
                var threadPrivateMyNew = myNew;

                for (var iter = 0; iter < iterations; iter++) {
                    // identify leftmost boundary element for group
                    final var left = i * (n / tasks) + 1;

                    // identify rightmost boundary element for group
                    final var right = (i + 1) * (n / tasks);

                    // iterate through all elements in group
                    for (var j = left; j <= right; j++) {
                        threadPrivateMyNew[j] = (threadPrivateMyVal[j - 1] + threadPrivateMyVal[j + 1]) / 2.0;
                    }

                    // barrier
                    ph.arriveAndAwaitAdvance();

                    final var temp = threadPrivateMyNew;
                    threadPrivateMyNew = threadPrivateMyVal;
                    threadPrivateMyVal = temp;
                }
            });

            threads[ii].start();
        }

        for (var ii = 0; ii < tasks; ii++) {
            try {
                threads[ii].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void runForAllFuzzyBarrier(final int iterations,
                                             final double[] myNew, final double[] myVal, final int n,
                                             final int tasks) {
        final var ph = new Phaser(0);
        ph.bulkRegister(tasks);

        final var threads = new Thread[tasks];

        for (var ii = 0; ii < tasks; ii++) {
            final var i = ii;

            threads[ii] = new Thread(() -> {
                var threadPrivateMyVal = myVal;
                var threadPrivateMyNew = myNew;

                for (var iter = 0; iter < iterations; iter++) {
                    // compute leftmost boundary element for group
                    final var left = i * (n / tasks) + 1;
                    threadPrivateMyNew[left] = (threadPrivateMyVal[left - 1] + threadPrivateMyVal[left + 1]) / 2.0;

                    // compute rightmost boundary element for group
                    final var right = (i + 1) * (n / tasks);
                    threadPrivateMyNew[right] = (threadPrivateMyVal[right - 1] + threadPrivateMyVal[right + 1]) / 2.0;

                    // signal arrival of phaser
                    final var currentPhase = ph.arrive();

                    // iterate through all elements in group
                    for (var j = left + 1; j <= right - 1; j++) {
                        threadPrivateMyNew[j] = (threadPrivateMyVal[j - 1] + threadPrivateMyVal[j + 1]) / 2.0;
                    }

                    // wait for previous phase to complete before advancing
                    ph.awaitAdvance(currentPhase);

                    final var temp = threadPrivateMyNew;
                    threadPrivateMyNew = threadPrivateMyVal;
                    threadPrivateMyVal = temp;
                }
            });

            threads[ii].start();
        }

        for (var ii = 0; ii < tasks; ii++) {
            try {
                threads[ii].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A parallel implementation of one-dimensional iterative averaging that
     * uses the Phaser.arrive and Phaser.awaitAdvance APIs to overlap
     * computation with barrier completion.
     * <p>
     * TODO Complete this method based on the provided runSequential and
     * runParallelBarrier methods.
     *
     * @param iterations The number of iterations to run
     * @param myNew      A double array that starts as the output array
     * @param myVal      A double array that contains the initial input to the
     *                   iterative averaging problem
     * @param n          The size of this problem
     * @param tasks      The number of threads/tasks to use to compute the solution
     */
    public static void runParallelFuzzyBarrier(final int iterations,
                                               final double[] myNew, final double[] myVal, final int n,
                                               final int tasks) {
        final var phs = new Phaser[tasks];
        for (var i = 0; i < phs.length; i++) {
            phs[i] = new Phaser(1);
        }

        final var threads = new Thread[tasks];

        for (var ii = 0; ii < tasks; ii++) {
            final var i = ii;

            threads[ii] = new Thread(() -> {
                var threadPrivateMyVal = myVal;
                var threadPrivateMyNew = myNew;

                for (var iter = 0; iter < iterations; iter++) {
                    final var left = i * (n / tasks) + 1;
                    final var right = (i + 1) * (n / tasks);

                    for (var j = left; j <= right; j++) {
                        threadPrivateMyNew[j] = (threadPrivateMyVal[j - 1] + threadPrivateMyVal[j + 1]) / 2.0;
                    }

                    // signal arrival of phaser
                    final var currentPhase = phs[i].arrive();

                    if (i - 1 >= 0) {
                        phs[i - 1].awaitAdvance(currentPhase);
                    }
                    if (i + 1 < tasks) {
                        phs[i + 1].awaitAdvance(currentPhase);
                    }

                    final var temp = threadPrivateMyNew;
                    threadPrivateMyNew = threadPrivateMyVal;
                    threadPrivateMyVal = temp;
                }
            });
            threads[ii].start();
        }

        for (var ii = 0; ii < tasks; ii++) {
            try {
                threads[ii].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}