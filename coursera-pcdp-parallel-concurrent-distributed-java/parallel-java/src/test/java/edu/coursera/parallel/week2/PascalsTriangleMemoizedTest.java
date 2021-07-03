package edu.coursera.parallel.week2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static edu.rice.pcdp.PCDP.finish;

class PascalsTriangleMemoizedTest {

    @Test
    void chooseMemoizedPar() {
        finish(() -> {
            try {
                final var i = PascalsTriangleMemoized.chooseMemoizedPar(5, 2);
                Assertions.assertEquals(10, i);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}