package week4.heap;

public class MinPQBinaryHeap<Key extends Comparable<Key>> {

    private final Key[] minpq;
    private int n;

    public MinPQBinaryHeap(int capacity) {
        minpq = (Key[]) new Comparable[capacity + 1];
        n = 0;
    }

    public boolean isEmpty() {
        return n == 0;
    }


    // swim until we get to point where parent is larger than all child
    private void swim(int k) {
        while (k > n) {
            final int parent = k / 2;
            final int child = k;

            if (!greater(parent, child)) break;
            swap(k, parent);
            k = parent; // parent is at k/2
        }
    }

    //  if parent is smaller than child -> pick next bigger child and keep going down heap
    private void sink(int k) {
        while (2 * k <= n) {
            int child = 2 * k;

            // both children compared
            if (child < n && greater(child, child + 1)) {
                child++;
            }

            if (!greater(k, child)) {
                break;
            }

            swap(k, child);
            k = child;
        }
    }


    // 1 + lgN compares
    public void insert(Key item) {
        minpq[++n] = item;
        swim(n);
    }

    // a[1] is largest key, logN
    public Key delMIN() {
        Key item = minpq[1];
        swap(1, n--); // move the top to end

        sink(1); // keep sinking from node 1
        minpq[n + 1] = null; // fix loitering
        return item;
    }

    private boolean greater(int i, int j) {
        return minpq[i].compareTo(minpq[j]) > 0;
    }

    private void swap(int i, int j) {
        Key temp = minpq[i];
        minpq[i] = minpq[j];
        minpq[j] = temp;
    }
}
