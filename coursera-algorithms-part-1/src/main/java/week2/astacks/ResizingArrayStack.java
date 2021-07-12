package week2.astacks;

import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class ResizingArrayStack<T> implements Iterable<T> {

    private T[] stack;
    private int size = 0;

    public ResizingArrayStack() {
        stack = (T[]) new Object[1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // resizing only takes N
    private void resize(int capacity) {
        if (capacity < size) {
            throw new IllegalArgumentException("capacity is less than size");
        }

        var temp = (T[]) new Object[capacity];
        if (size >= 0) {
            System.arraycopy(stack, 0, temp, 0, size);
        }
        stack = temp;
    }

    /**
     * create new stack with repeated doubling concept
     * since we are doubling - the runtime of push is still O(n)
     * N + (2+4+8+...+N) ~ 3N = N
     *
     * @param item the item to be added
     */
    public void push(T item) {
        if (size == stack.length) {
            resize(2 * stack.length);
        }
        stack[size++] = item;
    }

    /**
     * @return element
     */
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack empty");
        }

        var item = stack[--size];
        stack[size] = null;

        /*
         we are only reducing by 4 due to "thrashing"
         consider when the stack is full -> push = double, pop = half, push = double, pop = half
         and this is too expensive, each operation is proportional to N
         */
        if (size > 0 && size == stack.length / 4) {
            resize(stack.length / 2);
        }

        return item;
    }

    @Override
    public Iterator<T> iterator() {
        return new ReverseArrayIterator();
    }

    private class ReverseArrayIterator implements Iterator<T> {
        // start from end
        private int current = size;

        @Override
        public boolean hasNext() {
            return current > 0;
        }

        @Override
        public T next() {
            return stack[--current];
        }
    }
}
