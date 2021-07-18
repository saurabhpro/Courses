package edu.coursera.parallel.week3.miniproject_3;

import edu.coursera.parallel.week3.miniproject_3.MatrixMultiply;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatrixMultiplyTest {

    // Number of times to repeat each test, for consistent timing results.
    final static private int REPEATS = 20;

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
    private double[][] createMatrix(final int N) {
        final double[][] input = new double[N][N];
        final Random rand = new Random(314);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                input[i][j] = rand.nextInt(100);
            }
        }

        return input;
    }

    /**
     * Check if there is any difference in the correct and generated outputs.
     */
    private void checkResult(final double[][] ref, final double[][] output, final int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                String msg = "Error detected on cell (" + i + ", " + j + ")";
                assertEquals(ref[i][j], output[i][j], msg);
            }
        }
    }

    /**
     * A reference implementation of seqMatrixMultiply, in case the one in the main
     * source file is accidentally modified.
     */
    public void seqMatrixMultiply(final double[][] A, final double[][] B, final double[][] C, final int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                C[i][j] = 0.0;
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
    }

    /**
     * A helper function for tests of the two-task parallel implementation.
     *
     * @param N The size of the array to test
     *
     * @return The speedup achieved, not all tests use this information
     */
    private double parTestHelper(final int N) {
        // Create a random input
        final double[][] A = createMatrix(N);
        final double[][] B = createMatrix(N);
        final double[][] C = new double[N][N];
        final double[][] refC = new double[N][N];

        // Use a reference sequential version to compute the correct result
        MatrixMultiply.seqMatrixMultiply(A, B, refC, N);

        // Use the parallel implementation to compute the result
        MatrixMultiply.parMatrixMultiply(A, B, C, N);

        checkResult(refC, C, N);

        /*
         * Run several repeats of the sequential and parallel versions to get an
         * accurate measurement of parallel performance.
         */
        final long seqStartTime = System.currentTimeMillis();
        IntStream.range(0, REPEATS).forEach(r -> MatrixMultiply.seqMatrixMultiply(A, B, C, N));
        final long seqEndTime = System.currentTimeMillis();

        final long parStartTime = System.currentTimeMillis();
        IntStream.range(0, REPEATS).forEach(r -> MatrixMultiply.parMatrixMultiply(A, B, C, N));
        final long parEndTime = System.currentTimeMillis();

        final long seqTime = (seqEndTime - seqStartTime) / REPEATS;
        final long parTime = (parEndTime - parStartTime) / REPEATS;

        return (double) seqTime / (double) parTime;
    }

    /**
     * Tests the performance of the parallel implementation on a 512x512 matrix.
     */
    @Test
    void testPar512_x_512() {
        final int ncores = getNCores();
        double speedup = parTestHelper(512);
        double minimalExpectedSpeedup = (double) ncores * 0.6;
        final String errMsg = String.format("It was expected that the parallel implementation would run at "
                + "least %fx faster, but it only achieved %fx speedup", minimalExpectedSpeedup, speedup);
        assertTrue(speedup >= minimalExpectedSpeedup, errMsg);
    }

    /**
     * Tests the performance of the parallel implementation on a 768x768 matrix.
     */
    @Test
    void testPar768_x_768() {
        final int ncores = getNCores();
        double speedup = parTestHelper(768);
        double minimalExpectedSpeedup = (double) ncores * 0.6;
        final String errMsg = String.format("It was expected that the parallel implementation would run at "
                + "least %fx faster, but it only achieved %fx speedup", minimalExpectedSpeedup, speedup);
        assertTrue(speedup >= minimalExpectedSpeedup, errMsg);
    }

}