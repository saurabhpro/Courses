package week3;

import java.util.stream.IntStream;

public class SieveOfEratosthenes {

    // Driver Program to test above function
    public static void main(String[] args) {
        final var n = 130_000_000;
        System.out.print("Following are the prime numbers ");
        System.out.println("smaller than or equal to " + n);
        final var g = new SieveOfEratosthenes();
        g.sieveOfEratosthenes(n);
    }

    void sieveOfEratosthenes(int n) {
        // Create a boolean array "prime[0..n]" and initialize
        // all entries it as true. A value in prime[i] will
        // finally be false if i is Not a prime, else true.
        final var prime = new boolean[n + 1];

        IntStream.rangeClosed(0, n)
                .parallel()
                .forEach(i -> prime[i] = true);

        final var sqrt = Math.sqrt(n);
        IntStream.iterate(2, p -> p < sqrt, p -> p + 1)
                .filter(p -> prime[p]) // If prime[p] is not changed, then it is a prime
                .flatMap(p -> IntStream.iterate(p * p, i -> i <= n, i -> i + p))
                .forEach(i -> prime[i] = false); // Update all multiples of p

        // Print all prime numbers
        IntStream.rangeClosed(2, n)
                .filter(i -> prime[i])
                .forEachOrdered(i -> System.out.print(i + " "));
    }
}
