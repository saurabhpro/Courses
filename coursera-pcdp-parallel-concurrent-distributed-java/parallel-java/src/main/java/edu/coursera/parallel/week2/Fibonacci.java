package edu.coursera.parallel.week2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RecursiveTask;

class Fibonacci extends RecursiveTask<Integer> {
    public static final Logger LOG = LoggerFactory.getLogger(Fibonacci.class);
    private final int n;

    Fibonacci(int n) {
        this.n = n;
    }

    public static void main(String[] args) {
        final var fibonacci = new Fibonacci(118);
        LOG.info(String.valueOf(fibonacci.compute()));
    }

    /**
     * A future task extends the RecursiveTask class in the FJ framework, instead of RecursiveAction as in regular tasks.
     * <p>
     * 2.   The {@code compute()} method of a future task must have a non-void return type, whereas it has a
     * void return type  for  regular tasks.
     * <p>
     * 3.   A method call like {@code left.join()} waits for the task referred to by object {@code left}
     * in both cases, but also provides the task’s return value  in the case of future    task
     *
     * @return the result of compute
     */
    @Override
    public Integer compute() {
        if (n <= 1) {
            return n;
        }

        final var f1 = new Fibonacci(n - 1);
        f1.fork();
        final var f2 = new Fibonacci(n - 2);

        // since f1 ws forked, it would join the main thread here
        // and we call f2 recursively
        return f2.compute() + f1.join();
    }
}