package week1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightedQuickUnionPathCompressionUnionFindTest {
    private WeightedQuickUnionPathCompressionUnionFind wqu;

    @BeforeEach
    void setup() {
        wqu = new WeightedQuickUnionPathCompressionUnionFind(10);
    }

    @Test
    void isConnected() {
        wqu.union(4, 3);
        wqu.union(3, 8);
        wqu.union(6, 5);
        wqu.union(9, 4);
        wqu.union(2, 1);

        assertTrue(wqu.isConnected(8, 9));
        assertFalse(wqu.isConnected(5, 4));

        wqu.union(5, 0);
        wqu.union(7, 2);
        wqu.union(6, 1);
        wqu.union(7, 3);

        assertTrue(wqu.isConnected(5, 4));
    }
}