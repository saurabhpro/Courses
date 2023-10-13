package week2;

import java.util.Random;

import static edu.rice.pcdp.PCDP.async;
import static edu.rice.pcdp.PCDP.finish;
import static edu.rice.pcdp.PCDP.isolated;

/**
 * A thread-safe transaction implementation using global isolation.
 */
public final class BankTransactionsUsingGlobalIsolation {

    public static void main(String[] args) {
        System.out.println("BankTransactionsUsingGlobalIsolation starts...");

        final var numOfAccounts = 2000;
        final var numOfTransaction = 500_000;

        for (var i = 0; i < 5; i++) {
            System.out.println("Iteration " + i);
            for (var w = 1; w <= Runtime.getRuntime().availableProcessors(); w++) {
                benchmarkBody(numOfAccounts, numOfTransaction, w);
            }
        }
    }


    private static void benchmarkBody(final int numAccounts, final int numTransactions, final int numWorkers) {

        final var bankAccounts = new Account[numAccounts];
        for (var i = 0; i < numAccounts; i++) {
            bankAccounts[i] = new Account(i, 1000 * (randomIntValue(new Random(1000), numAccounts) + 1));
        }

        final var preSumOfBalances = sumBalances(bankAccounts);
        System.out.printf("Sum of balances before execution = %d. \n", preSumOfBalances);

        finish(() -> {
            for (var i = 0; i < numTransactions - 1; i++) {
                final var ii = i;
                async(() -> kernelBody(ii, numAccounts, bankAccounts));
            }
            // async peeling
            kernelBody(numTransactions - 1, numAccounts, bankAccounts);
        });

        final var postSumOfBalances = sumBalances(bankAccounts);
        assert (preSumOfBalances == postSumOfBalances) : ("Error in checking sum of balances");
        System.out.printf("  Sum of balances after execution  = %d. \n", postSumOfBalances);
    }

    private static int randomIntValue(final Random random, final int limit) {
        return (int) (Math.abs(random.nextDouble() * 10000) % limit);
    }

    private static long sumBalances(final Account[] bankAccounts) {
        long res = 0;
        for (final var bankAccount : bankAccounts) {
            res += bankAccount.balance();
        }
        return res;
    }

    private static void kernelBody(final int taskId, final int numAccounts, final Account[] bankAccounts) {
        final var myRandom = new Random(100L * (taskId + 1));

        final var srcIndex = randomIntValue(myRandom, numAccounts);
        final var srcAccount = bankAccounts[srcIndex];

        final var destIndex = randomIntValue(myRandom, numAccounts);
        final var destAccount = bankAccounts[destIndex];

        isolated(() -> {
            final var transferAmount = randomIntValue(myRandom, srcAccount.balance());
            final var success = srcAccount.withdraw(transferAmount);
            busyWork(srcIndex, destIndex);
            if (success) {
                destAccount.deposit(transferAmount);
            }
        });
    }

    private static void busyWork(final int srcIndex, final int destIndex) {
        for (var i = 0; i < srcIndex * 100; i++) {
        }
        for (var i = 0; i < destIndex * 100; i++) {
        }
    }
}


/*
 BankTransactionsUsingGlobalIsolation starts...
 Iteration 0
 Worker-1 Time:   1312.249 ms.
 Worker-2 Time:   1013.229 ms.
 Worker-3 Time:    919.030 ms.
 Worker-4 Time:    933.645 ms.
 Worker-5 Time:    949.927 ms.
 Worker-6 Time:    911.361 ms.
 Worker-7 Time:   1012.212 ms.
 Worker-8 Time:   1007.136 ms.
 Worker-9 Time:   1118.097 ms.
 Worker-10 Time:   1001.904 ms.
 Worker-11 Time:    938.117 ms.
 Worker-12 Time:    952.550 ms.
 Worker-13 Time:   1016.190 ms.
 Worker-14 Time:   1092.188 ms.
 Worker-15 Time:   1017.857 ms.
 Worker-16 Time:    977.444 ms.
 Iteration 1
 Worker-1 Time:    985.711 ms.
 Worker-2 Time:    940.743 ms.
 Worker-3 Time:   1154.621 ms.
 Worker-4 Time:   1096.705 ms.
 Worker-5 Time:   1020.515 ms.
 Worker-6 Time:    978.767 ms.
 Worker-7 Time:    963.232 ms.
 Worker-8 Time:    960.205 ms.
 Worker-9 Time:   1012.132 ms.
 Worker-10 Time:    931.754 ms.
 Worker-11 Time:    919.856 ms.
 Worker-12 Time:    929.318 ms.
 Worker-13 Time:    949.628 ms.
 Worker-14 Time:   1001.693 ms.
 Worker-15 Time:   1322.803 ms.
 Worker-16 Time:   1029.349 ms.
 Iteration 2
 Worker-1 Time:    981.086 ms.
 Worker-2 Time:   1015.689 ms.
 Worker-3 Time:   1103.110 ms.
 Worker-4 Time:    989.619 ms.
 Worker-5 Time:    970.933 ms.
 Worker-6 Time:    932.952 ms.
 Worker-7 Time:    945.029 ms.
 Worker-8 Time:    942.720 ms.
 Worker-9 Time:    979.249 ms.
 Worker-10 Time:    939.688 ms.
 Worker-11 Time:    941.121 ms.
 Worker-12 Time:   1015.572 ms.
 Worker-13 Time:    963.747 ms.
 Worker-14 Time:    919.279 ms.
 Worker-15 Time:    919.707 ms.
 Worker-16 Time:   1015.325 ms.
 Iteration 3
 Worker-1 Time:   1039.844 ms.
 Worker-2 Time:   1007.600 ms.
 Worker-3 Time:   1072.823 ms.
 Worker-4 Time:   1054.452 ms.
 Worker-5 Time:   1039.402 ms.
 Worker-6 Time:    995.229 ms.
 Worker-7 Time:   1087.434 ms.
 Worker-8 Time:   1008.426 ms.
 Worker-9 Time:   1095.106 ms.
 Worker-10 Time:   1203.285 ms.
 Worker-11 Time:   1238.739 ms.
 Worker-12 Time:   1352.083 ms.
 Worker-13 Time:   1300.992 ms.
 Worker-14 Time:   1084.091 ms.
 Worker-15 Time:   1060.977 ms.
 Worker-16 Time:   1100.721 ms.
 Iteration 4
 Worker-1 Time:   1107.372 ms.
 Worker-2 Time:   1007.906 ms.
 Worker-3 Time:   1184.191 ms.
 Worker-4 Time:    917.835 ms.
 Worker-5 Time:    947.937 ms.
 Worker-6 Time:    907.140 ms.
 Worker-7 Time:    921.129 ms.
 Worker-8 Time:    934.802 ms.
 Worker-9 Time:    992.279 ms.
 Worker-10 Time:    990.206 ms.
 Worker-11 Time:    916.117 ms.
 Worker-12 Time:    940.858 ms.
 Worker-13 Time:    994.699 ms.
 Worker-14 Time:   1037.933 ms.
 Worker-15 Time:    927.847 ms.
 Worker-16 Time:    928.556 ms.

 Process finished with exit code 0
 */