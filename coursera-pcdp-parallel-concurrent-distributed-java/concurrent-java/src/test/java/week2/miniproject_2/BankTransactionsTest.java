package week2.miniproject_2;

import helper.Utils;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static edu.rice.pcdp.PCDP.async;
import static edu.rice.pcdp.PCDP.finish;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankTransactionsTest {

    private final static int numAccounts = 3_000;
    private final static int numTransactions = 800_000;

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private static int randomIntValue(Random random, int limit) {
        return (int) (Math.abs(random.nextDouble() * 10000) % limit);
    }

    private static long sumBalances(Account[] bankAccounts) {
        long res = 0;
        for (var bankAccount : bankAccounts) {
            res += bankAccount.balance();
        }
        return res;
    }

    private static long testDriver(ThreadSafeBankTransaction impl) {
        var bankAccounts = new Account[numAccounts];
        for (var i = 0; i < numAccounts; i++) {
            bankAccounts[i] = new Account(
                i,
                1000 * (randomIntValue(new Random(1000), numAccounts) + 1));
        }

        var preSumOfBalances = sumBalances(bankAccounts);
        var startTime = System.currentTimeMillis();
        finish(() -> {
            for (var i = 0; i < numTransactions; i++) {
                var ii = i;

                async(() -> {
                    var myRandom = new Random(100L * (ii + 1));

                    var srcIndex = randomIntValue(myRandom, numAccounts);
                    var srcAccount = bankAccounts[srcIndex];

                    var destIndex = randomIntValue(myRandom, numAccounts);
                    var destAccount = bankAccounts[destIndex];

                    var transferAmount = randomIntValue(
                        myRandom,
                        srcAccount.balance());

                    impl.issueTransfer(transferAmount, srcAccount, destAccount);
                });
            }
        });
        var elapsed = System.currentTimeMillis() - startTime;

        System.out.println(impl.getClass().getSimpleName() + ": Performed " +
            numTransactions + " transactions with " + numAccounts +
            " accounts and " + getNCores() + " threads, in " + elapsed +
            " ms");

        var postSumOfBalances = sumBalances(bankAccounts);
        assertEquals(preSumOfBalances, postSumOfBalances, "Expected total balance before and after simulation to be " +
            "equal, but was " + preSumOfBalances + " before and " +
            postSumOfBalances + " after");

        return elapsed;
    }

    @Test
    public void testObjectIsolation() {
        // warmup
        testDriver(new BankTransactionsUsingGlobalIsolation());
        var globalTime = testDriver(
            new BankTransactionsUsingGlobalIsolation());

        // warmup
        testDriver(new BankTransactionsUsingObjectIsolation());
        var objectTime = testDriver(
            new BankTransactionsUsingObjectIsolation());
        var improvement = (double) globalTime / (double) objectTime;

        var ncores = getNCores();
        double expected;
        if (ncores == 2) {
            expected = 1.4;
        } else if (ncores == 4) {
            expected = 2.2;
        } else {
            expected = 0.7 * ncores;
        }
        var msg = String.format("Expected an improvement of at " +
                "least %fx with object-based isolation, but saw %fx", expected,
            improvement);
        Utils.softAssertTrue(improvement >= expected, msg);
    }
}
