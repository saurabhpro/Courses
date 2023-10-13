package week3.miniproject_3;

import org.junit.jupiter.api.Test;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;

class SieveTest {

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

        var parStart = currentTimeMillis();
        var parCount = new SieveActor().countPrimes(limit);
        var parElapsed = currentTimeMillis() - parStart;

        assertThat(ref)
            .withFailMessage("Mismatch in computed number of primes for limit " + limit)
            .isEqualTo(parCount);
        return parElapsed;
    }

    @Test
    void testActorSieveOneHundredThousand() throws InterruptedException {
        final var limit = 100_000;
        var ref = new SieveSequential().countPrimes(limit);

        long prev = -1;
        var cores = 2;
        while (cores <= getNCores()) {
            edu.rice.pcdp.runtime.Runtime.resizeWorkerThreads(cores);
            var elapsed = driver(limit, ref);

            if (prev > 0) {
                var scalability = (double) prev / (double) elapsed;
                assertThat(scalability)
                    .withFailMessage("Expected scalability of %fx going from %d cores to %d cores, but found %fx"
                        .formatted(expectedScalability, cores / 2, cores, scalability))
                    .isGreaterThanOrEqualTo(expectedScalability);
            }

            cores *= 2;
            prev = elapsed;
        }
    }

    @Test
    void testActorSieveTwoHundredThousand() throws InterruptedException {
        final var limit = 200_000;
        var ref = new SieveSequential().countPrimes(limit);

        long prev = -1;
        var cores = 2;
        while (cores <= getNCores()) {
            edu.rice.pcdp.runtime.Runtime.resizeWorkerThreads(cores);
            var elapsed = driver(limit, ref);

            if (prev > 0) {
                var scalability = (double) prev / (double) elapsed;
                assertThat(scalability)
                    .withFailMessage("Expected scalability of %fx going from %d cores to %d cores, but found %fx"
                        .formatted(expectedScalability, cores / 2, cores, scalability))
                    .isGreaterThanOrEqualTo(expectedScalability);
            }

            cores *= 2;
            prev = elapsed;
        }
    }
}
