package week3;

import edu.rice.pcdp.Actor;

import static edu.rice.pcdp.PCDP.finish;

/**
 * <p>SieveActorMain class.</p>
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class SieveActorMain {

    public static void main() {
        final var limit = 500_000;
        System.out.println("SieveActorMain.main: limit = " + limit);

        for (var iter = 0; iter < 2; iter++) {
            System.out.printf("Run %d\n", iter);

            for (var w = 1; w <= Runtime.getRuntime().availableProcessors(); w++) {


                final var parStartTime = System.nanoTime();

                final var sieveActor = new SieveActor(2);
                finish(() -> {
                    /// sieveActor.start();
                    for (var i = 3; i <= limit; i += 2) {
                        ///   sieveActor.send(i);
                    }
                    ///  sieveActor.send(0);
                });

                final var parExecTime = System.nanoTime() - parStartTime;
                final var execTime = parExecTime / 1e6;

                var numPrimes = 0;
                var loopActor = sieveActor;
                while (loopActor != null) {
                    numPrimes += loopActor.numLocalPrimes();
                    loopActor = loopActor.nextActor();
                }

                System.out.printf("  Workers-%d Completed in %9.2f ms with %d primes \n", w, execTime, numPrimes);

            }
        }
    }

    private static class SieveActor extends Actor {

        private static final int MAX_LOCAL_PRIMES = 10_000;
        private final int[] localPrimes;
        private int numLocalPrimes;
        private SieveActor nextActor;

        SieveActor(final int localPrime) {
            this.localPrimes = new int[MAX_LOCAL_PRIMES];
            this.localPrimes[0] = localPrime;
            this.numLocalPrimes = 1;
            this.nextActor = null;
        }

        public SieveActor nextActor() {
            return nextActor;
        }

        public int numLocalPrimes() {
            return numLocalPrimes;
        }

        @Override
        public void process(final Object theMsg) {
            final int candidate = (Integer) theMsg;
            if (candidate <= 0) {
                if (nextActor != null) {
                    nextActor.send(theMsg);
                }
                /// exit();
            } else {
                final var locallyPrime = isLocallyPrime(candidate);
                if (locallyPrime) {
                    if (numLocalPrimes < MAX_LOCAL_PRIMES) {
                        localPrimes[numLocalPrimes] = candidate;
                        numLocalPrimes += 1;
                    } else if (nextActor == null) {
                        nextActor = new SieveActor(candidate);
                        ///     nextActor.start();
                    } else {
                        nextActor.send(theMsg);
                    }
                }
            }
        }

        private boolean isLocallyPrime(final int candidate) {
            final var isPrime = new boolean[]{true};
            checkPrimeKernel(candidate, isPrime, 0, numLocalPrimes);
            return isPrime[0];
        }

        private void checkPrimeKernel(final int candidate, final boolean[] isPrime, final int startIndex, final int endIndex) {
            for (var i = startIndex; i < endIndex; i++) {
                if (candidate % localPrimes[i] == 0) {
                    isPrime[0] = false;
                    break;
                }
            }
        }
    }
}