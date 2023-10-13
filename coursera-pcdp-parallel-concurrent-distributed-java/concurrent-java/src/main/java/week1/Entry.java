package week1;

/**
 * A single element in any of the list implementations.
 */
public final class Entry {

    /**
     * The value stored in this list entry.
     */
    public final int key;

    /**
     * The next element in this singly linked list.
     */
    public Entry next;

    /**
     * The general constructor used when creating a new list entry.
     *
     * @param setObject Value to store in this item
     */
    Entry(int setObject) {
        this.key = setObject;
    }
}