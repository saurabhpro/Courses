package week4.array;

import edu.princeton.cs.algs4.StdOut;

public class OrderedArrayImplementation<Key extends Comparable<Key>> {

    private final Key[] pq;
    private int n;

    public OrderedArrayImplementation(int capacity) {
        pq = (Key[]) new Comparable[capacity];
        n = 0;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    // O(N)
    public void insert(Key item) {
        int i = n - 1;  // end

        while (i >= 0 && less(item, pq[i])) {
            pq[i + 1] = pq[i];
            i--;
        }

        pq[i + 1] = item;
        n++;
    }

    // O(1)
    public Key delMAX() {
        return pq[--n];
    }

    private boolean less(Comparable<Key> a, Comparable<Key> b) {
        return a.compareTo((Key) b) < 0;
    }

    private void swap(int i, int j) {
        Key temp = pq[i];
        pq[i] = pq[j];
        pq[j] = temp;
    }

    public static void main(String[] args) {
        OrderedArrayImplementation<String> pq = new OrderedArrayImplementation<>(10);
        pq.insert("this");
        pq.insert("is");
        pq.insert("a");
        pq.insert("test");

        while (!pq.isEmpty()) {
            StdOut.println(pq.delMAX());
        }
    }
}
