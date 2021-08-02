package week2;

import java.util.Random;

import static edu.rice.pcdp.PCDP.async;
import static edu.rice.pcdp.PCDP.finish;
import static edu.rice.pcdp.PCDP.isolated;

/**
 * A thread-safe transaction implementation using object-based isolation.
 */
public final class BankTransactionsUsingObjectIsolation {
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) {

        System.out.println("BankTransactionsUsingObjectIsolation starts...");

        final int numAccounts = 2_000;
        final int numTransactions = 500_000;

        for (int i = 0; i < 5; i++) {
            System.out.println("Iteration-" + i);

            for (int w = 1; w <= Runtime.getRuntime().availableProcessors(); w++) {
                benchmarkBody(numAccounts, numTransactions, w);
            }
        }
    }

    private static void benchmarkBody(final int numAccounts, final int numTransactions, final int numWorkers) {

        final Account[] bankAccounts = new Account[numAccounts];
        for (int i = 0; i < numAccounts; i++) {
            bankAccounts[i] = new Account(i, 1000 * (randomIntValue(new Random(1000), numAccounts) + 1));
        }

        final long preSumOfBalances = sumBalances(bankAccounts);
        // System.out.printf("Sum of balances before execution = %d. \n", preSumOfBalances);

        finish(() -> {
            for (int i = 0; i < numTransactions - 1; i++) {
                final int ii = i;
                async(() -> kernelBody(ii, numAccounts, bankAccounts));
            }
            // async peeling
            kernelBody(numTransactions - 1, numAccounts, bankAccounts);
        });

        final long postSumOfBalances = sumBalances(bankAccounts);
        assert (preSumOfBalances == postSumOfBalances) : ("Error in checking sum of balances");
        System.out.printf("  Sum of balances after execution  = %d. \n", postSumOfBalances);
    }

    private static int randomIntValue(final Random random, final int limit) {
        return (int) (Math.abs(random.nextDouble() * 10000) % limit);
    }

    private static long sumBalances(final Account[] bankAccounts) {
        long res = 0;
        for (final Account bankAccount : bankAccounts) {
            res += bankAccount.balance();
        }
        return res;
    }

    private static void kernelBody(final int taskId, final int numAccounts, final Account[] bankAccounts) {
        final Random myRandom = new Random(100L * (taskId + 1));

        final int srcIndex = randomIntValue(myRandom, numAccounts);
        final Account srcAccount = bankAccounts[srcIndex];

        final int destIndex = randomIntValue(myRandom, numAccounts);
        final Account destAccount = bankAccounts[destIndex];


        // local isolation
        isolated(srcAccount, destAccount, () -> {
            busyWork(srcIndex, destIndex);
            final int transferAmount = randomIntValue(myRandom, srcAccount.balance());
            final boolean success = srcAccount.withdraw(transferAmount);
            if (success) {
                destAccount.deposit(transferAmount);
            }
        });
    }

    private static void busyWork(final int srcIndex, final int destIndex) {
        for (int i = 0; i < srcIndex * 100; i++) {

        }
        for (int i = 0; i < destIndex * 100; i++) {

        }
    }
}

/*
BankTransactionsUsingObjectIsolation starts...
Iteration-0
                   Worker-1 Time:    417.588 ms.
                   Worker-2 Time:    225.150 ms.
                   Worker-3 Time:    192.176 ms.
                   Worker-4 Time:    219.536 ms.
                   Worker-5 Time:    172.116 ms.
                   Worker-6 Time:    246.680 ms.
                   Worker-7 Time:    226.995 ms.
                   Worker-8 Time:    183.630 ms.
                   Worker-9 Time:    170.442 ms.
                  Worker-10 Time:    155.077 ms.
                  Worker-11 Time:    147.340 ms.
                  Worker-12 Time:    160.830 ms.
                  Worker-13 Time:    141.460 ms.
                  Worker-14 Time:    189.924 ms.
                  Worker-15 Time:    210.972 ms.
                  Worker-16 Time:    147.531 ms.
Iteration-1
                   Worker-1 Time:    190.970 ms.
                   Worker-2 Time:    136.321 ms.
                   Worker-3 Time:    145.293 ms.
                   Worker-4 Time:    126.466 ms.
                   Worker-5 Time:    160.617 ms.
                   Worker-6 Time:    207.162 ms.
                   Worker-7 Time:    180.500 ms.
                   Worker-8 Time:    136.409 ms.
                   Worker-9 Time:    102.200 ms.
                  Worker-10 Time:    143.898 ms.
                  Worker-11 Time:    143.642 ms.
                  Worker-12 Time:    129.553 ms.
                  Worker-13 Time:    149.697 ms.
                  Worker-14 Time:    201.300 ms.
                  Worker-15 Time:    130.062 ms.
                  Worker-16 Time:    142.669 ms.
Iteration-2
                   Worker-1 Time:    129.173 ms.
                   Worker-2 Time:    144.715 ms.
                   Worker-3 Time:    130.477 ms.
                   Worker-4 Time:    241.119 ms.
                   Worker-5 Time:    112.677 ms.
                   Worker-6 Time:    145.638 ms.
                   Worker-7 Time:    130.004 ms.
                   Worker-8 Time:    132.645 ms.
                   Worker-9 Time:    138.628 ms.
                  Worker-10 Time:    168.822 ms.
                  Worker-11 Time:    170.596 ms.
                  Worker-12 Time:    135.982 ms.
                  Worker-13 Time:    124.353 ms.
                  Worker-14 Time:    118.422 ms.
                  Worker-15 Time:    117.624 ms.
                  Worker-16 Time:    153.950 ms.
Iteration-3
                   Worker-1 Time:    158.896 ms.
                   Worker-2 Time:    168.945 ms.
                   Worker-3 Time:    117.463 ms.
                   Worker-4 Time:    133.187 ms.
                   Worker-5 Time:    146.527 ms.
                   Worker-6 Time:    121.000 ms.
                   Worker-7 Time:    131.082 ms.
                   Worker-8 Time:    125.467 ms.
                   Worker-9 Time:    125.707 ms.
                  Worker-10 Time:    142.571 ms.
                  Worker-11 Time:    120.637 ms.
                  Worker-12 Time:    146.853 ms.
                  Worker-13 Time:    117.226 ms.
                  Worker-14 Time:    123.977 ms.
                  Worker-15 Time:    116.113 ms.
                  Worker-16 Time:    173.455 ms.
Iteration-4
                   Worker-1 Time:    151.144 ms.
                   Worker-2 Time:    129.210 ms.
                   Worker-3 Time:    156.467 ms.
                   Worker-4 Time:    187.502 ms.
                   Worker-5 Time:    215.198 ms.
                   Worker-6 Time:    279.668 ms.
                   Worker-7 Time:    183.302 ms.
                   Worker-8 Time:    221.396 ms.
                   Worker-9 Time:    150.924 ms.
                  Worker-10 Time:    151.911 ms.
                  Worker-11 Time:    122.766 ms.
                  Worker-12 Time:    140.519 ms.
                  Worker-13 Time:    157.638 ms.
                  Worker-14 Time:    136.069 ms.
                  Worker-15 Time:    155.729 ms.
                  Worker-16 Time:    147.735 ms.

 */