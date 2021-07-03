package edu.coursera.parallel.week2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FutureReciprocalArraySumTest {

    @Test
    void seqArraySum() {
        final var arraySum = FutureReciprocalArraySum.seqArraySum(new double[]{1, 2}, 0, 2);
        assertEquals(1.5, arraySum);
    }

    @Test
    void parArraySumFuture() {
        // 1 + 1/2
        final var arraySum = FutureReciprocalArraySum.parArraySumFuture(new double[]{1, 2});
        assertEquals(1.5, arraySum);
    }
}