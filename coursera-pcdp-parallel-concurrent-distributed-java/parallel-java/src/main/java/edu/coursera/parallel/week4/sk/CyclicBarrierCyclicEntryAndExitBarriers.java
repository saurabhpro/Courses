package edu.coursera.parallel.week4.sk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.lang.Thread.sleep;

public class CyclicBarrierCyclicEntryAndExitBarriers {
    private static final int PARTIES = 3;
    private static final int ITERATIONS = 3;
    private static final Logger LOG = LoggerFactory.getLogger(CyclicBarrierCyclicEntryAndExitBarriers.class);

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        final var entryBarrier = new CyclicBarrier(PARTIES + 1, () -> LOG.info("iteration started"));
        final var exitBarrier = new CyclicBarrier(PARTIES + 1, () -> LOG.info("iteration finished"));

        for (var i = 0; i < ITERATIONS; i++) {
            for (var p = 0; p < PARTIES; p++) {
                final var delay = p + 1;
                final Runnable task = new Worker(delay, entryBarrier, exitBarrier);
                new Thread(task).start();
            }

            LOG.info("all threads waiting to start: iteration {}", i);
            sleep(1);
            entryBarrier.await();
            LOG.info("all threads started: iteration {}", i);
            exitBarrier.await();
            LOG.info("all threads finished: iteration {}", i);
        }
    }

    private record Worker(int delay,
                          CyclicBarrier entryBarrier,
                          CyclicBarrier exitBarrier) implements Runnable {

        @Override
        public void run() {
            try {
                entryBarrier.await();
                work();
                exitBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

        private void work() throws InterruptedException {
            LOG.info("work {} started", delay);
            sleep(delay);
            LOG.info("work {} finished", delay);
        }
    }
}
