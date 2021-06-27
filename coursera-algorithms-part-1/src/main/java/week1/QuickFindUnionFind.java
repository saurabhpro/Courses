package week1;

import java.util.stream.IntStream;

public class QuickFindUnionFind {

    private final int[] id;

    /**
     * set the initial ids as some index value
     *
     * @param n size of array
     */
    public QuickFindUnionFind(int n) {
        id = new int[n];
        IntStream.range(0, n).forEach(i -> id[i] = i);
    }

    /**
     * 2 array access - check if p, q are connected
     */
    public boolean isConnected(int p, int q) {
        return id[p] == id[q];
    }

    /**
     * change all id[p] to id[q] - at most 2n+2 array access
     */
    public void union(int p, int q) {
        int pId = id[p];
        int qId = id[q];

        IntStream.range(0, id.length)
                .filter(i -> id[i] == pId)
                .forEach(i -> id[i] = qId);
    }
}
