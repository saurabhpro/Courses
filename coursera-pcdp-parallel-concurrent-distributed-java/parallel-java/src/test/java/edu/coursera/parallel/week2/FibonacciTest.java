package edu.coursera.parallel.week2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FibonacciTest {

    @Test
    void compute() {
        var fibonacci = new Fibonacci(15);
        Assertions.assertEquals(610, fibonacci.compute());
    }
}