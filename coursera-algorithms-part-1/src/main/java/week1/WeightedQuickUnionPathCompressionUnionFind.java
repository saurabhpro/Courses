package week1;

public class WeightedQuickUnionPathCompressionUnionFind extends WeightedQuickUnionUnionFind {

    /**
     * set the initial ids as some index value
     *
     * @param n size of array
     */
    public WeightedQuickUnionPathCompressionUnionFind(int n) {
        super(n);
    }

    /**
     * chase parent pointers until we reach root (at most depth of i array access)
     */
    @Override
    int root(int i) {
        while (i != id[i]) {
            // make every other node in path point to its grandparent, thereby halving path length
            id[i] = id[id[i]];  // to compress the path
            i = id[i];
        }

        return i;
    }
}
