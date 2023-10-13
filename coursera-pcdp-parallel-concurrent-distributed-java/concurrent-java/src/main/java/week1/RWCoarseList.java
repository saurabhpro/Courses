package week1;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class RWCoarseList implements ListSet {
    /**
     * Starting entry of this concurrent list.
     */
    private final Entry head;

    // Concurrent Reads to shared Object Can be Interleaved without data Races, Concurrent R/W or W -> data Races
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public RWCoarseList() {
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

    @Override
    public boolean add(final int key) {
        try {
            readWriteLock.writeLock().lock();
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
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(final int key) {
        try {
            readWriteLock.writeLock().lock();
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
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(final int key) {
        try {
            readWriteLock.readLock().lock();
            var pred = this.head;
            var curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            return key == curr.key;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}