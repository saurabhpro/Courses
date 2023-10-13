package week2.miniproject_2;

import static edu.rice.pcdp.PCDP.isolated;

/**
 * A thread-safe transaction implementation using global isolation.
 */
public final class BankTransactionsUsingGlobalIsolation
        implements ThreadSafeBankTransaction {
    /**
     * {@inheritDoc}
     * <p>
     * Because it's one global isolated construct, there was interference from these different transactions.
     * And that is what's causing the slowdown.
     */
    @Override
    public void issueTransfer(final int amount, final Account src,
                              final Account dst) {
        isolated(() -> src.performTransfer(amount, dst));
    }
}
