package week2.bqueues;

import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class ResizingArrayQueue<T> {

    int n;
    int firstIdx;
    int lastIdx;
    private T[] queue;

    public ResizingArrayQueue() {
        queue = (T[]) new Object[2];
        n = 0;
        firstIdx = 0;
        lastIdx = 0;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    private void resize(int capacity) {
        var temp = (T[]) new Object[capacity];

        IntStream.range(0, n)
                .forEach(i -> temp[i] = queue[(firstIdx + i) % queue.length]);

        queue = temp;
        firstIdx = 0;
        lastIdx = n;
    }

    public void enqueue(T item) {
        if (n == queue.length) {
            resize(2 * queue.length);
        }

        queue[lastIdx++] = item;

        // if last idx is end - rotate to 0
        if (lastIdx == queue.length) {
            lastIdx = 0;
        }
        n++;
    }

    public T dequeue() {
        var item = queue[firstIdx];
        queue[firstIdx] = null;
        n--;
        firstIdx++; // this is interesting -deque here simply moves the pointer forward - hence the next statement

        // if first item is end - rotate to 0
        if (firstIdx == queue.length) {
            firstIdx = 0;
        }

        // prevents thrashing
        if (n > 0 && n == queue.length / 4) {
            resize(queue.length / 2);
        }
        return item;
    }
}
