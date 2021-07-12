package week2.assignment;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A randomized queue is similar to a stack or queue, except that the item removed
 * is chosen uniformly at random among items in the data structure
 *
 * @author Saurabh Kumar
 */
public class RandomizedQueue<Item> implements Iterable<Item> {
    // Ts in queue
    private Item[] values;

    // week2.assignment.RandomizedQueue size
    private int size;

    // Construct an empty randomized queue
    public RandomizedQueue() {
        final int defaultTsSize = 2;

        values = (Item[]) new Object[defaultTsSize];
        size = 0;
    }

    // Unit testing
    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<>();

        String text = "A";
        queue.enqueue(text);
        StdOut.println("enqueue() with: '" + text + "'");

        text = "B";
        queue.enqueue(text);
        StdOut.println("enqueue() with: '" + text + "'");

        text = "C";
        queue.enqueue(text);
        StdOut.println("enqueue() with: '" + text + "'");

        text = "D";
        queue.enqueue(text);
        StdOut.println("enqueue() with: '" + text + "'");

        text = "E";
        queue.enqueue(text);
        StdOut.println("enqueue() with: '" + text + "'");

        queue.dequeue();
        StdOut.println("dequeue()");

        queue.sample();
        StdOut.println("sample()");

        text = "F";
        queue.enqueue(text);
        StdOut.println("enqueue() with: '" + text + "'");

        StdOut.println("Iterating queue...");
        for (String str : queue) {
            StdOut.println("Iterate element: " + str);
        }
    }

    // If the queue empty
    public boolean isEmpty() {
        return (size == 0);
    }

    // Return the number of Ts on the queue
    public int size() {
        return size;
    }

    // Add the value
    public void enqueue(Item value) {
        if (value == null) {
            throw new IllegalArgumentException("Can't add empty element to queue");
        }

        if (size == values.length) {
            resizeTs(values.length * 2);
        }

        values[size] = value;
        size++;
    }

    // Remove and return a random T
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }

        int index = StdRandom.uniform(size);
        Item value = values[index];

        // replace taken element with last one
        values[index] = values[size - 1];
        values[size - 1] = null;
        size--;

        if (size > 0 && (size == values.length / 4)) {
            resizeTs(values.length / 2);
        }

        return value;
    }

    // Return (but do not remove) a random T
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }

        int index = StdRandom.uniform(size);

        return values[index];
    }

    // Return an independent iterator over Ts in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    // Resize Ts array
    private void resizeTs(int length) {
        Item[] newTs = (Item[]) new Object[length];

        if (size >= 0) System.arraycopy(values, 0, newTs, 0, size);

        values = newTs;
    }

    // week2.assignment.RandomizedQueue Iterator
    private class RandomizedQueueIterator implements Iterator<Item> {
        // Ts in queue during iteration
        private final Item[] valuesCopy;

        // week2.assignment.RandomizedQueue size during iteration
        private int sizeCopy;

        // Init with coping Ts and size
        private RandomizedQueueIterator() {
            sizeCopy = size;
            valuesCopy = (Item[]) new Object[sizeCopy];

            System.arraycopy(values, 0, valuesCopy, 0, sizeCopy);
        }

        public boolean hasNext() {
            return (sizeCopy > 0);
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }

            int index = StdRandom.uniform(sizeCopy);
            Item value = valuesCopy[index];

            // replace taken element with last one
            valuesCopy[index] = valuesCopy[sizeCopy - 1];
            valuesCopy[sizeCopy - 1] = null;
            sizeCopy--;

            return value;
        }
    }
}