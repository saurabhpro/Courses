package week2;

/**
 * Represents a single account in a bank whose only contents is the balance of
 * that account.
 */
public final class Account {
    /**
     * Unique ID number for this account.
     */
    private final int id;

    /**
     * Current balance of this account.
     */
    private int balance;

    /**
     * Constructor.
     *
     * @param setId              The ID for this account
     * @param setStartingBalance The initial balance for this account.
     */
    public Account(final int setId, final int setStartingBalance) {
        this.id = setId;
        this.balance = setStartingBalance;
    }

    /**
     * Get the remaining balance in this account.
     *
     * @return The current balance.
     */
    public int balance() {
        return balance;
    }

    /**
     * Remove the specified amount from the current balance of the account.
     *
     * @param amount The amount to subtract from the current balance.
     *
     * @return true if it was possible to subtract that amount, false otherwise.
     */
    public boolean withdraw(final int amount) {
        if (amount > 0 && amount < balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    /**
     * Add the specified amount to the current balance.
     *
     * @param amount The amount to add.
     *
     * @return true if it was possible to add that amount (i.e. amount > 0),
     * false otherwise.
     */
    public boolean deposit(final int amount) {
        if (amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }
}
