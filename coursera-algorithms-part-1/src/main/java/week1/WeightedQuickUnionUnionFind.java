package week1;

public class WeightedQuickUnionUnionFind extends QuickUnionUnionFind {

    final int[] size;

    /**
     * set the initial ids as some index value
     *
     * @param n size of array
     */
    public WeightedQuickUnionUnionFind(int n) {
        super(n);
        size = new int[n];
    }

    /**
     * change root of p to point to root of q - depth of p and q access (log n)
     */
    @Override
    public void union(int p, int q) {
        int i = root(p);
        int j = root(q);

        if (i == j) {
            return;
        }

        if (size[i] < size[j]) {
            id[i] = j;
            size[j] += size[i];
        } else {
            id[j] = i;
            size[i] += size[j];
        }
    }
}
