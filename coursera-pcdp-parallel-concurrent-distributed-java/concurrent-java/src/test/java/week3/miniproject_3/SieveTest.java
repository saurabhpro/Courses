package week3.miniproject_3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SieveTest {

    static final double expectedScalability = 1.6;

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private static long driver(int limit, int ref) {
        new SieveActor().countPrimes(limit); // warmup
        System.gc();
        new SieveActor().countPrimes(limit); // warmup
        System.gc();
        new SieveActor().countPrimes(limit); // warmup
        System.gc();

        var parStart = System.currentTimeMillis();
        var parCount = new SieveActor().countPrimes(limit);
        var parElapsed = System.currentTimeMillis() - parStart;

        assertEquals(ref, parCount, "Mismatch in computed number of primes for limit " + limit);
        return parElapsed;
    }

    @Test
    public void testActorSieveOneHundredThousand() throws InterruptedException {
        final var limit = 100_000;
        var ref = new SieveSequential().countPrimes(limit);

        long prev = -1;
        var cores = 2;
        while (cores <= getNCores()) {
            edu.rice.pcdp.runtime.Runtime.resizeWorkerThreads(cores);
            var elapsed = driver(limit, ref);

            if (prev > 0) {
                var scalability = (double) prev / (double) elapsed;
                assertTrue(
                    scalability >= expectedScalability,
                    String.format("Expected scalability of %fx going from %d cores to %d cores, but found %fx",
                        expectedScalability, cores / 2, cores, scalability));
            }

            cores *= 2;
            prev = elapsed;
        }
    }

    @Test
    public void testActorSieveTwoHundredThousand() throws InterruptedException {
        final var limit = 200_000;
        var ref = new SieveSequential().countPrimes(limit);

        long prev = -1;
        var cores = 2;
        while (cores <= getNCores()) {
            edu.rice.pcdp.runtime.Runtime.resizeWorkerThreads(cores);
            var elapsed = driver(limit, ref);

            if (prev > 0) {
                var scalability = (double) prev / (double) elapsed;
                assertTrue(
                    scalability >= expectedScalability,
                    String.format("Expected scalability of %fx going from %d cores to %d cores, but found %fx",
                        expectedScalability, cores / 2, cores, scalability));
            }

            cores *= 2;
            prev = elapsed;
        }
    }
}
