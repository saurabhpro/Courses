package week1.assignment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PercolationTest {

    @Test
    void percolates() {
        Percolation percolation = new Percolation(1);
        percolation.open(1, 1);
        assertTrue(percolation.isOpen(1, 1));

        percolation.percolates();
        assertEquals(1, percolation.numberOfOpenSites());
        assertTrue(percolation.isFull(1, 1));
    }
}