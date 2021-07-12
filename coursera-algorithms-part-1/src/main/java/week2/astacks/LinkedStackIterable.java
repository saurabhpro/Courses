package week2.astacks;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedStackIterable<T> implements Iterable<T> {

    private Node<T> first = null;

    public static void main(String[] args) {
        var list = new LinkedStackIterable<String>();
        list.push("A");
        list.push("B");
        list.push("C");
        list.push("D");
        list.push("E");

        // all thanks to the iterable and iterator combinations
        list.forEach(System.out::println);
    }

    @Override
    public Iterator<T> iterator() {
        return new ListIterator();
    }

    public boolean isEmpty() {
        return first == null;
    }

    public void push(T item) {
        Node<T> oldFirst = first;
        first = new Node<>();
        first.item = item;
        first.next = oldFirst;
    }

    public T pop() {
        var item = first.item;
        first = first.next;
        return item;
    }

    public static class Node<T> {
        T item;
        Node<T> next;
    }

    /**
     * Iterator which must be returned for Iteration
     */
    public class ListIterator implements Iterator<T> {
        private Node<T> current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            // guard against manual access
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            Iterator.super.remove();
        }
    }
}
