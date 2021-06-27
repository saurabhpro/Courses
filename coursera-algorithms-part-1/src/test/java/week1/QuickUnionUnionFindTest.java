package week1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickUnionUnionFindTest {
    private QuickUnionUnionFind qu;

    @BeforeEach
    void setup() {
        qu = new QuickUnionUnionFind(10);
    }

    @Test
    void isConnected() {
        qu.union(4, 3);
        qu.union(3, 8);
        qu.union(6, 5);
        qu.union(9, 4);
        qu.union(2, 1);

        assertTrue(qu.isConnected(8, 9));
        assertFalse(qu.isConnected(5, 4));

        qu.union(5, 0);
        qu.union(7, 2);
        qu.union(6, 1);
        qu.union(7, 3);

        assertTrue(qu.isConnected(5, 4));
    }
}