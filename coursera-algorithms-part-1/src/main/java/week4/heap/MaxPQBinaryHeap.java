package week4.heap;

public class MaxPQBinaryHeap<Key extends Comparable<Key>> {

    private final Key[] maxpq;
    private int n;

    public MaxPQBinaryHeap(int capacity) {
        maxpq = (Key[]) new Comparable[capacity + 1];
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

            if (!less(parent, child)) break;
            swap(k, parent);
            k = parent; // parent is at k/2
        }
    }

    //  if parent is smaller than child -> pick next bigger child and keep going down heap
    private void sink(int k) {
        while (2 * k <= n) {
            int child = 2 * k;

            // both children compared
            if (child < n && less(child, child + 1)) {
                child++;
            }

            if (!less(k, child)) {
                break;
            }

            swap(k, child);
            k = child;
        }
    }


    // 1 + lgN compares
    public void insert(Key item) {
        maxpq[++n] = item;
        swim(n);
    }

    // a[1] is largest key, logN
    public Key delMAX() {
        Key item = maxpq[1];
        swap(1, n--); // move the top to end

        sink(1); // keep sinking from node 1
        maxpq[n + 1] = null; // fix loitering
        return item;
    }

    private boolean less(int i, int j) {
        return maxpq[i].compareTo(maxpq[j]) < 0;
    }

    private void swap(int i, int j) {
        Key temp = maxpq[i];
        maxpq[i] = maxpq[j];
        maxpq[j] = temp;
    }
}
