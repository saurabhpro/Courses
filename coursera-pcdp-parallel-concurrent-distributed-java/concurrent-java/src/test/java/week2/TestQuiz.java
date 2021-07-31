package week2;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static edu.rice.pcdp.PCDP.*;

public class TestQuiz {

    @Test
    void t4() {

        //4.
        //Question 4
        //Assume we are using the provided bank transaction code. We have six accounts: A, B, C, D, E, F We make eight transfers, all asynchronously:
        //
        //1.     $100 from A to B
        //
        //2.     $50 from B to C
        //
        //3.     $30 from B to C
        //
        //4.     $20 from B to A
        //
        //5.     $70 from D to A
        //
        //6.     $40 from B to D
        //
        //7.     $30 from E to D
        //
        //8.     $20 from D to F
    }

    @Test
    void t7() {
        finish(() -> {
            var r = new AtomicReference<Integer>();
            async(() -> System.out.println(r.get()));

            forall(0, 1000, (i) -> r.compareAndSet(null, i));

        });
    }

    @Test
    void t8() {
        finish(() -> {
            var r = new AtomicReference<Integer>();

            forall(0, 1000, (i) -> r.compareAndSet(null, i));

            async(() -> System.out.println(r.get()));
        });
    }
}
