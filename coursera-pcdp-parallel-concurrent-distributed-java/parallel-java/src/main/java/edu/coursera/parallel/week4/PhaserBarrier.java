package edu.coursera.parallel.week4;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import static edu.rice.pcdp.PCDP.forall;

public class PhaserBarrier {

    public static void main(String[] args) {
        // initialize phaser ph	for use by n tasks ("parties")
        int n = 9;
        final Phaser ph = new Phaser(n);

        Instant start = Instant.now();
        // Create forall loop with n iterations that operate on phaser
        forall(0, n - 1, (i) -> {
                    System.out.println("HELLO, " + i);
                    int phase = ph.arrive();
                    String myId = lookup(i); // convert int to a string

                    ph.awaitAdvance(phase);
                    System.out.println("BYE, " + myId);
                }
        );

        Instant end = Instant.now();
        System.out.printf("Total Time taken: %s s ", Duration.between(start, end).toSeconds());
    }

    private static String lookup(int i) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.valueOf(i);
    }
}

/*
HELLO, 4
HELLO, 7
HELLO, 1
HELLO, 5
HELLO, 8
HELLO, 6
HELLO, 3
HELLO, 0
HELLO, 2
BYE, 1
BYE, 4
BYE, 8
BYE, 6
BYE, 7
BYE, 2
BYE, 5
BYE, 3
BYE, 0
Total Time taken: 3s   // note it actually finished all operations in 3 s instead of 30 (if running sequentially)
 */
