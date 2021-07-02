package edu.coursera.parallel.week1.miniproject_0;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetupTest {

    /*
     * A simple test case.
     */
    @Test
    void testSetup() {
        final int result = Setup.setup(42);
        assertEquals(42, result);
    }
}
