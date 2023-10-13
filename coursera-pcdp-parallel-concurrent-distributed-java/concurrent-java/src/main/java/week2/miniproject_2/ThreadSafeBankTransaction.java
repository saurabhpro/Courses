package week2.miniproject_2;

/**
 * An abstract interface for all thread-safe bank transaction implementations to
 * extend.
 */
public interface ThreadSafeBankTransaction {
    /**
     * Transfer the specified amount from src to dst.
     *
     * @param amount Amount to transfer
     * @param src    Source account
     * @param dst    Destination account
     */
    void issueTransfer(int amount, Account src, Account dst);
}
