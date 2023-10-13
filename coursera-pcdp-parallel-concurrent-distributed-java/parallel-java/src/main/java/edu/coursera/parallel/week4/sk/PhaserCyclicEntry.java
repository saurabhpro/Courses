package edu.coursera.parallel.week4.sk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

import static java.lang.Thread.sleep;

public class PhaserCyclicEntry {
    private static final int PARTIES = 3;
    private static final int ITERATIONS = 3;
    private static final Logger log = LoggerFactory.getLogger(PhaserCyclicEntry.class);

    public static void main(String[] args) throws InterruptedException {
        final var phaser = new Phaser(1) {
            final private int maxPhase = ITERATIONS;

            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                return (phase >= maxPhase - 1) || (registeredParties == 0);
            }
        };
        log.info("after constructor {}", phaser);
        for (var p = 0; p < PARTIES; p++) {
            final var delay = p + 1;
            final Runnable task = new Worker(delay, phaser);
            new Thread(task).start();
        }
        log.info("all threads waiting to start {}", phaser);
        sleep(1);
        log.info("before all threads started {}", phaser);
        phaser.arriveAndDeregister();
        log.info("after all threads started {}", phaser);
        phaser.register();
        while (!phaser.isTerminated()) {
            phaser.arriveAndAwaitAdvance();
        }
        log.info("all threads finished {}", phaser);
    }

    private record Worker(int delay, Phaser phaser) implements Runnable {
        private Worker {
            phaser.register();
        }

        @Override
        public void run() {
            do {
                try {
                    work();
                    phaser.arriveAndAwaitAdvance();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!phaser.isTerminated());
        }

        void work() throws InterruptedException {
            log.info("work {} started", delay);
            sleep(delay);
            log.info("work {} finished", delay);
        }
    }
}
