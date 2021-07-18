package edu.coursera.parallel.week4.sk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

public class CountdownLatchOneTimeEntryAndExitBarriers {
    private static final int PARTIES = 3;
    private static final Logger LOG = LoggerFactory.getLogger(CountdownLatchOneTimeEntryAndExitBarriers.class);

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch entryBarrier = new CountDownLatch(1);
        CountDownLatch exitBarrier = new CountDownLatch(PARTIES);

        for (int p = 0; p < PARTIES; p++) {
            int delay = p + 1;
            Runnable task = new Worker(delay, entryBarrier, exitBarrier);
            new Thread(task).start();
        }

        LOG.info("all threads waiting to start");
        sleep(1);
        entryBarrier.countDown();
        LOG.info("all threads started");
        exitBarrier.await();    // releasing all waiting threads if the count reaches 0.
        LOG.info("all threads finished");
    }

    public record Worker(int delay,
                         CountDownLatch entryBarrier,
                         CountDownLatch exitBarrier) implements Runnable {

        @Override
        public void run() {
            try {
                entryBarrier.await();
                work();
                exitBarrier.countDown();
            } catch (InterruptedException e) {
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
