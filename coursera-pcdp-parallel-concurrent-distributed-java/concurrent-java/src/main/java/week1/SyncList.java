package week1;

/**
 * Class SyncList implements a thread-safe sorted list data structure that
 * supports contains(), add() and remove() methods.
 * <p>
 * Thread safety is guaranteed by declaring each of the methods to be
 * synchronized.
 */
public final class SyncList implements ListSet {
    /**
     * Starting entry of this concurrent list.
     */
    private final Entry head;

    /**
     * Constructor.
     */
    public SyncList() {
        this.head = new Entry(Integer.MIN_VALUE);
        this.head.next = new Entry(Integer.MAX_VALUE);
    }

    /**
     * Getter for the head of the list.
     *
     * @return The head of this list.
     */
    @Override
    public Entry getHead() {
        return head;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean contains(final int key) {
        var pred = this.head;
        var curr = pred.next;

        while (curr.key < key) {
            pred = curr;
            curr = curr.next;
        }

        return key == curr.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean add(final int key) {
        var pred = this.head;
        var curr = pred.next;

        while (curr.key < key) {
            pred = curr;
            curr = curr.next;
        }

        if (key == curr.key) {
            return false;
        } else {
            final var entry = new Entry(key);
            entry.next = curr;
            pred.next = entry;
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean remove(final int key) {
        var pred = this.head;
        var curr = pred.next;

        while (curr.key < key) {
            pred = curr;
            curr = curr.next;
        }

        if (key == curr.key) {
            pred.next = curr.next;
            return true;
        } else {
            return false;
        }
    }
}
