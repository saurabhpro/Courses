package edu.coursera.parallel.week4.miniproject_4;

import edu.coursera.parallel.helper.Utils;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Phaser;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OneDimAveragingPhaserTest {

    // Number of times to repeat each test, for consistent timing results.
    final static private int niterations = 40000;

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private static void runParallelBarrier(int iterations,
                                           double[] myNew,
                                           double[] myVal,
                                           int n,
                                           int tasks) {
        var ph = new Phaser(0);
        ph.bulkRegister(tasks);

        var threads = new Thread[tasks];

        for (var ii = 0; ii < tasks; ii++) {
            var i = ii;

            threads[ii] = new Thread(() -> {
                var threadPrivateMyVal = myVal;
                var threadPrivateMyNew = myNew;

                var chunkSize = (n + tasks - 1) / tasks;
                var left = (i * chunkSize) + 1;
                var right = (left + chunkSize) - 1;
                if (right > n) right = n;

                for (var iter = 0; iter < iterations; iter++) {

                    for (var j = left; j <= right; j++) {
                        threadPrivateMyNew[j] = (threadPrivateMyVal[j - 1] + threadPrivateMyVal[j + 1]) / 2.0;
                    }
                    ph.arriveAndAwaitAdvance();

                    var temp = threadPrivateMyNew;
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

    private double[] createArray(int N, int iterations) {
        var input = new double[N + 2];
        var index = N + 1;
        while (index > 0) {
            input[index] = 1.0;
            index -= (iterations / 4);
        }
        return input;
    }

    /**
     * A reference implementation of runSequential, in case the one in the main source file is accidentally modified.
     */
    public void runSequential(int iterations, double[] myNew, double[] myVal, int n) {
        for (var iter = 0; iter < iterations; iter++) {
            for (var j = 1; j <= n; j++) {
                myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0;
            }
            var tmp = myNew;
            myNew = myVal;
            myVal = tmp;
        }
    }

    private void checkResult(double[] ref, double[] output) {
        for (var i = 0; i < ref.length; i++) {
            var msg = "Mismatch on output at element " + i;
            assertEquals(ref[i], output[i], msg);
        }
    }

    /**
     * A helper function for tests of the two-task parallel implementation.
     *
     * @param N The size of the array to test
     * @return The speedup achieved, not all tests use this information
     */
    private double parTestHelper(int N, int ntasks) {
        // Create a random input
        var myNew = createArray(N, niterations);
        var myVal = createArray(N, niterations);
        var myNewRef = createArray(N, niterations);
        var myValRef = createArray(N, niterations);

        long barrierTotalTime = 0;
        long fuzzyTotalTime = 0;

        for (var r = 0; r < 3; r++) {
            var barrierStartTime = System.currentTimeMillis();
            runParallelBarrier(niterations, myNew, myVal, N, ntasks);
            var barrierEndTime = System.currentTimeMillis();

            var fuzzyStartTime = System.currentTimeMillis();
            OneDimAveragingPhaser.runParallelFuzzyBarrier(niterations, myNewRef, myValRef, N, ntasks);
            var fuzzyEndTime = System.currentTimeMillis();

            if (niterations % 2 == 0) {
                checkResult(myNew, myNewRef);
            } else {
                checkResult(myVal, myValRef);
            }

            barrierTotalTime += (barrierEndTime - barrierStartTime);
            fuzzyTotalTime += (fuzzyEndTime - fuzzyStartTime);
        }

        return (double) barrierTotalTime / (double) fuzzyTotalTime;
    }

    /**
     * Test on large input.
     */
    @Test
    void testFuzzyBarrier() {
        final var expected = 1.05;
        var speedup = parTestHelper(2 * 1024 * 1024, getNCores() * 1);
        var errMsg = String.format("It was expected that the fuzzy barrier parallel implementation would " +
            "run %fx faster than the barrier implementation, but it only achieved %fx speedup", expected, speedup);
        Utils.softAssertTrue(speedup >= expected, errMsg);
        var successMsg = String.format("Fuzzy barrier parallel implementation " +
            "ran %fx faster than the barrier implementation", speedup);
        System.out.println(successMsg);
    }
}
