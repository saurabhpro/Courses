package week3.miniproject_3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import week3.miniproject_3.util.MPI;
import week3.miniproject_3.util.MPI.MPIException;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class MpiJavaTest {

    private static MPI mpi = null;

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            ncoresStr = System.getProperty("COURSERA_GRADER_NCORES");
        }

        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private Matrix createRandomMatrix(int rows, int cols) {
        var matrix = new Matrix(rows, cols);
        var rand = new Random(314);

        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                matrix.set(i, j, rand.nextInt(100));
            }
        }

        return matrix;
    }

    private Matrix copyMatrix(Matrix input) {
        return new Matrix(input);
    }

    private void seqMatrixMultiply(Matrix a, Matrix b, Matrix c) {
        for (var i = 0; i < c.getNRows(); i++) {
            for (var j = 0; j < c.getNCols(); j++) {
                c.set(i, j, 0.0);

                for (var k = 0; k < b.getNRows(); k++) {
                    c.incr(i, j, a.get(i, k) * b.get(k, j));
                }
            }
        }
    }

    @BeforeEach
    protected void setUp() throws Exception {
        assert (mpi == null);
        mpi = new MPI();
        mpi.MPI_Init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        assert (mpi != null);
        mpi.MPI_Finalize();
    }

    private void testDriver(int M, int N, int P)
        throws MPIException {
        var myrank = mpi.MPI_Comm_rank(mpi.MPI_COMM_WORLD);

        Matrix a, b, c;
        if (myrank == 0) {
            a = createRandomMatrix(M, N);
            b = createRandomMatrix(N, P);
            c = createRandomMatrix(M, P);
        } else {
            a = new Matrix(M, N);
            b = new Matrix(N, P);
            c = new Matrix(M, P);
        }

        var copy_a = copyMatrix(a);
        var copy_b = copyMatrix(b);
        var copy_c = copyMatrix(c);

        if (myrank == 0) {
            System.err.println("Testing matrix multiply: [" + M + " x " + N +
                "] * [" + N + " x " + P + "] = [" + M + " x " + P + "]");
        }

        var seqStart = System.currentTimeMillis();
        seqMatrixMultiply(copy_a, copy_b, copy_c);
        var seqElapsed = System.currentTimeMillis() - seqStart;

        if (myrank == 0) {
            System.err.println("Sequential implementation ran in " +
                seqElapsed + " ms");
        }

        mpi.MPI_Barrier(mpi.MPI_COMM_WORLD);

        var parallelStart = System.currentTimeMillis();
        MatrixMult.parallelMatrixMultiply(a, b, c, mpi);
        var parallelElapsed = System.currentTimeMillis() - parallelStart;

        if (myrank == 0) {
            var speedup = (double) seqElapsed / (double) parallelElapsed;
            System.err.println("MPI implementation ran in " + parallelElapsed +
                " ms, yielding a speedup of " + speedup + "x");
            System.err.println();

            for (var i = 0; i < c.getNRows(); i++) {
                for (var j = 0; j < c.getNCols(); j++) {
                    var msg = "Expected " + copy_c.get(i, j)
                        + " at (" + i + ", " + j + ") but found " + c.get(i, j);
                    assertEquals(copy_c.get(i, j), c.get(i, j), msg);
                }
            }

            var expectedSpeedup = 0.75 * getNCores();
            var msg = "Expected a speedup of at least " + expectedSpeedup
                + ", but saw " + speedup;
            assertTrue(speedup >= expectedSpeedup, msg);
        }
    }

    @Test
    @Disabled("To run this we need to setup open MPI")
    public void testMatrixMultiplySquareSmall() throws MPIException {
        testDriver(800, 800, 800);
    }

    @Test
    @Disabled("To run this we need to setup open MPI")
    public void testMatrixMultiplySquareLarge() throws MPIException {
        testDriver(1200, 1200, 1200);
    }

    @Test
    @Disabled
    public void testMatrixMultiplyRectangular1Small() throws MPIException {
        testDriver(800, 1600, 500);
    }

    @Test
    @Disabled
    public void testMatrixMultiplyRectangular2Small() throws MPIException {
        testDriver(1600, 800, 500);
    }

    @Test
    @Disabled
    public void testMatrixMultiplyRectangularLarge() throws MPIException {
        testDriver(1800, 1400, 1000);
    }
}
