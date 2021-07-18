package edu.coursera.parallel.week4.sk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

import static java.lang.Thread.sleep;

public class PhaserCyclicEntryAndExitBarriers {

    private static final int PARTIES = 3;
    private static final int ITERATIONS = 3;
    private static final Logger logger = LoggerFactory.getLogger(PhaserCyclicEntryAndExitBarriers.class);

    public static void main(String[] args) throws InterruptedException {
        Phaser phaser = new Phaser(1) {
            final private int maxPhase = ITERATIONS;

            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                return (phase >= maxPhase - 1) || (registeredParties == 0);
            }
        };
        log("after constructor", phaser);

        for (int p = 0; p < PARTIES; p++) {
            int delay = p + 1;
            Runnable task = new Worker(delay, phaser);
            new Thread(task).start();
        }

        log("all threads waiting to start", phaser);
        sleep(1);

        log("before all threads started", phaser);
        phaser.arriveAndDeregister();
        log("after all threads started", phaser);

        phaser.register();
        while (!phaser.isTerminated()) {
            phaser.arriveAndAwaitAdvance();
        }

        log("all threads finished", phaser);
    }

    private static void log(String message, Phaser phaser) {
        logger.info("{} phase: {}, registered/arrived/unarrived: {}={}+{}, terminated: {}",
                String.format("%-40s", message),
                phaser.getPhase(),
                phaser.getRegisteredParties(),
                phaser.getArrivedParties(),
                phaser.getUnarrivedParties(),
                phaser.isTerminated());
    }

    private record Worker(int delay, Phaser phaser) implements Runnable {

        private Worker {
            phaser.register();

        }

        @Override
        public void run() {
            log("before arriveAndAwaitAdvance()", phaser);
            phaser.arriveAndAwaitAdvance();
            log("after arriveAndAwaitAdvance()", phaser);

            work();

            log("before arriveAndDeregister()", phaser);
            phaser.arriveAndDeregister();
            log("after arriveAndDeregister()", phaser);
        }

        private void work() {
            logger.info("work {} started", delay);
            try {
                sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("work {} finished", delay);
        }
    }
}