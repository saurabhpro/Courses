package week1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickFindUnionFindTest {

    private QuickUnionUnionFind qf;

    @BeforeEach
    void setup() {
        qf = new QuickUnionUnionFind(10);
    }

    @Test
    void isConnected() {
        qf.union(4, 3);
        qf.union(3, 8);
        qf.union(6, 5);
        qf.union(9, 4);
        qf.union(2, 1);

        assertTrue(qf.isConnected(8, 9));
        assertFalse(qf.isConnected(5, 4));

        qf.union(5, 0);
        qf.union(7, 2);
        qf.union(6, 1);
        qf.union(7, 3);

        assertTrue(qf.isConnected(5, 4));
    }

}