package week2.miniproject_2;

import static edu.rice.pcdp.PCDP.isolated;

/**
 * A thread-safe transaction implementation using object-based isolation.
 */
public final class BankTransactionsUsingObjectIsolation
        implements ThreadSafeBankTransaction {
    /**
     * {@inheritDoc}
     */
    @Override
    public void issueTransfer(final int amount, final Account src,
                              final Account dst) {
        /*
         * TODO implement issueTransfer using object-based isolation instead of
         * global isolation, based on the reference code provided in
         * BankTransactionsUsingGlobalIsolation. Keep in mind that isolation
         * must be applied to both src and dst.
         *
         * use object based isolation. And in object based isolation, we have the same set up. But in the body
         * we're going to use an isolated construct, which only isolates the source and the destination account,
         * rather than a global isolation.
         */
        isolated(src, dst, () -> {
            final var success = src.withdraw(amount);
            if (success) {
                dst.deposit(amount);
            }
        });
    }
}
