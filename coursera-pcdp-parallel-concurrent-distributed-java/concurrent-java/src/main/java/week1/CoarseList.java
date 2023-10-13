package week1;

import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

/**
 * An implementation of the ListSet interface that uses Java locks to
 * protect against concurrent accesses.
 * <p>
 * correct, concurrent access to this list. Use a Java ReentrantLock object
 * to protect against those concurrent accesses. You may refer to
 * SyncList.java for help understanding the list management logic, and for
 * guidance in understanding where to place lock-based synchronization.
 */
public final class CoarseList implements ListSet {

    /**
     * Starting entry of this concurrent list.
     */
    private final Entry head;

    private final ReentrantLock lock = new ReentrantLock();

    public CoarseList() {
        this.head = new Entry(MIN_VALUE);
        this.head.next = new Entry(MAX_VALUE);
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

    @Override
    public boolean add(final int key) {
        try {
            lock.lock();
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
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(final int key) {
        try {
            lock.lock();
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
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(final int key) {
        try {
            lock.lock();
            var pred = this.head;
            var curr = pred.next;

            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            return key == curr.key;
        } finally {
            lock.unlock();
        }
    }
}