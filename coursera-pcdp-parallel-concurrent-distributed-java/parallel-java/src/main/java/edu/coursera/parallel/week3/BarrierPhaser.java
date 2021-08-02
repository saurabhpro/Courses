package edu.coursera.parallel.week3;

import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/**
 * Barrier synchronizers (barriers) are a kind of synchronizer that ensures that
 * any threads must stop at a certain point and cannot proceed further until all other threads reach this point.
 * <p>
 * <p>
 * By purpose, barriers can be grouped into the following categories:
 * <ul>
 *     <li> entry barriers, that prevents threads from starting processing</li>
 *     <li> exit barriers, that waiting for all threads to finish processing</li>
 * </ul>
 * <p>
 * Barriers also can be grouped
 * <ul>
 *     <li>by the number of iterations (one-time or cyclic)</li>
 *     <li>and by the number of parties/threads (fixed or variable).</li>
 * </ul>
 * In Java 7+ there are 3 predefined barrier classes: CountDownLatch, CyclicBarrier, Phaser.
 */
public class BarrierPhaser {
    public static void main(String[] args) throws InterruptedException {
        Phaser phaser = new Phaser();
        final var executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> compute("A", phaser));
        executorService.submit(() -> compute("B", phaser));
        executorService.submit(() -> compute("C", phaser));

        executorService.shutdown();
    }

    static void compute(String name, Phaser phaser) {
        System.out.println("Hello " + name);

        phaser.awaitAdvance(1); // todo wrong order

        System.out.println("Bye " + name);
    }
}
