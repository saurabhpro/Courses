package week1;

import java.util.stream.IntStream;

public class QuickUnionUnionFind {

    final int[] id;

    /**
     * set the initial ids as some index value
     *
     * @param n size of array
     */
    public QuickUnionUnionFind(int n) {
        id = new int[n];
        IntStream.range(0, n).forEach(i -> id[i] = i);
    }

    /**
     * chase parent pointers until we reach root (at most depth of i array access)
     */
    int root(int i) {
        while (i != id[i]) {
            i = id[i];
        }

        return i;
    }

    /**
     * check if p, q are connected, if root of two components are same - they are connected
     * depth of p and q access
     */
    public boolean isConnected(int p, int q) {
        return root(p) == root(q);
    }

    /**
     * change root of p to point to root of q - depth of p and q access
     */
    public void union(int p, int q) {
        int i = root(p);
        int j = root(q);

        id[i] = j;
    }
}
