package edu.coursera.parallel.week1.miniproject_1;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ReciprocalArraySumTest {
    // Number of times to repeat each test, for consistent timing results.
    final static private int REPEATS = 60;

    private static int getNCores() {
        String ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    /**
     * Create a double[] of length N to use as input for the tests.
     *
     * @param N Size of the array to create
     *
     * @return Initialized double array of length N
     */
    private double[] createArray(final int N) {
        final double[] input = new double[N];
        final Random rand = new Random(314);

        for (int i = 0; i < N; i++) {
            input[i] = rand.nextInt(100);
            // Don't allow zero values in the input array to prevent divide-by-zero
            if (input[i] == 0.0) {
                i--;
            }
        }

        return input;
    }

    /**
     * A reference implementation of seqArraysum, in case the one in the main source file is accidentally modified.
     *
     * @param input Input to sequentially compute a reciprocal sum over
     *
     * @return Reciprocal sum of input
     */
    private double seqArraySum(final double[] input) {
        // Compute sum of reciprocals of array elements
        return Arrays.stream(input).map(v -> 1 / v).sum();
    }

    /**
     * A helper function for tests of the two-task parallel implementation.
     *
     * @param N                  The size of the array to test
     * @param useManyTaskVersion Switch between two-task and many-task versions of the code
     * @param ntasks             Number of tasks to use
     *
     * @return The speedup achieved, not all tests use this information
     */
    private double parTestHelper(final int N, final boolean useManyTaskVersion, final int ntasks) {
        // Create a random input
        final double[] input = createArray(N);
        // Use a reference sequential version to compute the correct result
        final double correct = seqArraySum(input);
        // Use the parallel implementation to compute the result
        double sum;
        if (useManyTaskVersion) {
            sum = ReciprocalArraySumOriginal.parManyTaskArraySum(input, ntasks);
        } else {
            assert ntasks == 2;
            sum = ReciprocalArraySumOriginal.parArraySum(input);
        }
        final double err = Math.abs(sum - correct);
        // Assert the expected output was produced
        final String errMsg = String.format("Mismatch in result for N = %d, expected = %f, computed = %f, absolute " +
                "error = %f", N, correct, sum, err);
        assertTrue(err < 1E-2, errMsg);

        /*
         * Run several repeats of the sequential and parallel versions to get an accurate measurement of parallel
         * performance.
         */
        final long seqStartTime = System.currentTimeMillis();
        for (int r = 0; r < REPEATS; r++) {
            seqArraySum(input);
        }
        final long seqEndTime = System.currentTimeMillis();

        final long parStartTime = System.currentTimeMillis();
        for (int r = 0; r < REPEATS; r++) {
            if (useManyTaskVersion) {
                ReciprocalArraySumOriginal.parManyTaskArraySum(input, ntasks);
            } else {
                assert ntasks == 2;
                ReciprocalArraySumOriginal.parArraySum(input);
            }
        }
        final long parEndTime = System.currentTimeMillis();

        final long seqTime = (seqEndTime - seqStartTime) / REPEATS;
        final long parTime = (parEndTime - parStartTime) / REPEATS;

        return (double) seqTime / (double) parTime;
    }

    /**
     * Test that the two-task parallel implementation properly computes the results for a million-element array.
     */
    @Test
    void testParSimpleTwoMillion() {
        final double minimalExpectedSpeedup = 1.5;
        final double speedup = parTestHelper(2_000_000, false, 2);
        final String errMsg = String.format("It was expected that the two-task parallel implementation would run at " +
                "least %fx faster, but it only achieved %fx speedup", minimalExpectedSpeedup, speedup);
        assertTrue(speedup >= minimalExpectedSpeedup, errMsg);
    }

    /**
     * Test that the two-task parallel implementation properly computes the results for a hundred million-element array.
     */
    @Test
    void testParSimpleTwoHundredMillion() {
        final double speedup = parTestHelper(200_000_000, false, 2);
        final double minimalExpectedSpeedup = 1.5;
        final String errMsg = String.format("It was expected that the two-task parallel implementation would run at " +
                "least %fx faster, but it only achieved %fx speedup", minimalExpectedSpeedup, speedup);
        assertTrue(speedup >= minimalExpectedSpeedup, errMsg);
    }

    /**
     * Test that the many-task parallel implementation properly computes the results for a million-element array.
     */
    @Test
    void testParManyTaskTwoMillion() {
        final int ncores = getNCores();
        final double minimalExpectedSpeedup = (double) ncores * 0.6;
        final double speedup = parTestHelper(2_000_000, true, ncores);
        final String errMsg = String.format("It was expected that the many-task parallel implementation would run at " +
                "least %fx faster, but it only achieved %fx speedup", minimalExpectedSpeedup, speedup);
        assertTrue(speedup >= minimalExpectedSpeedup, errMsg);
    }

    /**
     * Test that the many-task parallel implementation properly computes the results for a hundred million-element array.
     */
    @Test
    void testParManyTaskTwoHundredMillion() {
        final int ncores = getNCores();
        final double speedup = parTestHelper(200_000_000, true, ncores);
        final double minimalExpectedSpeedup = (double) ncores * 0.8;
        final String errMsg = String.format("It was expected that the many-task parallel implementation would run at " +
                "least %fx faster, but it only achieved %fx speedup", minimalExpectedSpeedup, speedup);
        assertTrue(speedup >= minimalExpectedSpeedup, errMsg);
    }
}
