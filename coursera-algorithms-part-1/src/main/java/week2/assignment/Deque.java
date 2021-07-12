package week2.assignment;

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A double-ended queue or deque (pronounced “deck”) is a generalization of a stack and a queue
 * that supports adding and removing items from either the front or the back of the data structure.
 *
 * @author Saurabh Kumar
 */
public class Deque<Item> implements Iterable<Item> {
    // week2.assignment.Deque size
    private int size;

    // First element of the week2.assignment.Deque
    private Node<Item> first;

    // Last element of the week2.assignment.Deque
    private Node<Item> last;

    // Construct an empty deque
    public Deque() {
        size = 0;
        first = null;
        last = null;
    }

    // Unit testing
    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();

        String text = "World";
        deque.addFirst(text);
        StdOut.println("addFirst() with: '" + text + "'");

        text = ", ";
        deque.addFirst(text);
        StdOut.println("addFirst() with: '" + text + "'");

        text = "Hello";
        deque.addFirst(text);
        StdOut.println("addFirst() with: '" + text + "'");

        text = "Meow, ";
        deque.addFirst(text);
        StdOut.println("addFirst() with: '" + text + "'");

        text = "^^";
        deque.addLast(text);
        StdOut.println("addLast() with: '" + text + "'");

        deque.removeFirst();
        StdOut.println("removeFirst()");

        deque.removeLast();
        StdOut.println("removeLast()");

        text = "!";
        deque.addLast(text);
        StdOut.println("addLast() with: '" + text + "'");

        StdOut.println("Iterating deque...");
        for (String str : deque) {
            StdOut.println("Iterate element: " + str);
        }
    }

    // If the deque empty
    public boolean isEmpty() {
        return (size == 0);
    }

    // Return the number of Ts on the deque
    public int size() {
        return size;
    }

    // Add the value to the front
    public void addFirst(Item value) {
        if (value == null) {
            throw new IllegalArgumentException("Can't add empty element to deque");
        }

        Node<Item> newT = new Node<>();
        newT.value = value;
        newT.next = first;
        newT.previous = null;

        if (isEmpty()) {
            last = newT;
        } else {
            first.previous = newT;
        }

        first = newT;
        size++;
    }

    // Add the value to the end
    public void addLast(Item value) {
        if (value == null) {
            throw new IllegalArgumentException("Can't add empty element to deque");
        }

        Node<Item> newT = new Node<>();
        newT.value = value;
        newT.next = null;
        newT.previous = last;

        if (isEmpty()) {
            first = newT;
        } else {
            last.next = newT;
        }

        last = newT;
        size++;
    }

    // Remove and return the T from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("week2.assignment.Deque is empty");
        }

        Item node = first.value;
        first = first.next;
        size--;

        if (isEmpty()) {
            last = null;
        } else {
            first.previous = null;
        }

        return node;
    }

    // Remove and return the T from the end
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("week2.assignment.Deque is empty");
        }

        Item node = last.value;
        last = last.previous;
        size--;

        if (isEmpty()) {
            first = null;
        } else {
            last.next = null;
        }

        return node;
    }

    // Return an iterator over Ts in order from front to end
    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator<>(first);
    }

    // week2.assignment.Deque element/T
    private static class Node<T> {
        T value;
        Node<T> next;
        Node<T> previous;
    }

    // week2.assignment.Deque iterator
    private static class DequeIterator<T> implements Iterator<T> {
        // Current iterable element
        private Node<T> current;

        // Init with first element to start from
        private DequeIterator(Node<T> node) {
            current = node;
        }

        public boolean hasNext() {
            return (current != null);
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }

            T node = current.value;
            current = current.next;

            return node;
        }
    }
}