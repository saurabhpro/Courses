package week3.miniproject_3;

import java.util.ArrayList;
import java.util.List;

/**
 * An example sequential implementation of the Sieve of Eratosthenes.
 */
public final class SieveSequential extends Sieve {
    /**
     * {@inheritDoc}
     */
    @Override
    public int countPrimes(final int limit) {
        final List<Integer> localPrimes = new ArrayList<Integer>();
        localPrimes.add(2);
        for (var i = 3; i <= limit; i += 2) {
            checkPrime(i, localPrimes);
        }

        return localPrimes.size();
    }

    /**
     * Checks if the candidate is a prime, where the possible prime factors are
     * provided in the primesList. If the provided candidate is found to be
     * prime, it is added to the primesList.
     *
     * @param candidate  Value we are checking to see if it is prime.
     * @param primesList List of already known primes that are less than
     *                   candidate.
     */
    private void checkPrime(final int candidate,
                            final List<Integer> primesList) {
        var isPrime = true;
        final var s = primesList.size();
        for (final var loopPrime : primesList) {
            if (candidate % loopPrime == 0) {
                isPrime = false;
                break;
            }
        }

        if (isPrime) {
            primesList.add(candidate);
        }
    }
}
