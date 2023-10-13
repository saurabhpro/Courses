package week3.miniproject_3;

import edu.rice.pcdp.Actor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 * <p>
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     * <p>
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        final var sieveActorActor = new SieveActorActor[1];

        finish(() -> {
            // start with 3
            sieveActorActor[0] = new SieveActorActor(3);

            // then move to others and send
            IntStream.range(4, limit + 1)
                    .filter(i -> (i & 1) == 1)  // filter odd
                    .forEach(sieveActorActor[0]::send);
        });

        var loopActor = sieveActorActor[0];
        var numPrimes = 1;  // 1 as we already considered 2

        while (loopActor != null) {
            loopActor = loopActor.nextActor.get();
            numPrimes++;
        }

        return numPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private final int prime;
        private final AtomicReference<SieveActorActor> nextActor = new AtomicReference<>();

        SieveActorActor(final int prime) {
            this.prime = prime;
        }

        /**
         * Process a single message sent to this actor.
         * <p>
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;
            if (candidate % prime == 0) {
                return;
            }

            if (nextActor.get() == null) {
                nextActor.set(new SieveActorActor(candidate));
            } else {
                nextActor.get().send(msg);
            }
        }
    }
}
