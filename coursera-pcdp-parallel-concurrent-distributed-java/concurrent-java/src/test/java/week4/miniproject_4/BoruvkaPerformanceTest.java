package week4.miniproject_4;


import helper.Utils;
import org.junit.jupiter.api.Test;
import week4.miniproject_4.boruvka.BoruvkaFactory;
import week4.miniproject_4.boruvka.Component;
import week4.miniproject_4.boruvka.Edge;
import week4.miniproject_4.boruvka.parallel.ParBoruvkaFactory;
import week4.miniproject_4.boruvka.sequential.SeqBoruvka;
import week4.miniproject_4.boruvka.sequential.SeqBoruvkaFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

public class BoruvkaPerformanceTest {
    static final double expectedSpeedup = 1.7;

    private static int getNCores() {
        String ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    // From http://www.dis.uniroma1.it/challenge9/download.shtml
    public static final String[] inputs = {
            "src/main/resources/week4/boruvka/USA-road-d.FLA.gr.gz",
            "src/main/resources/week4/boruvka/USA-road-d.NE.gr.gz"
    };

    record ExperimentResults(double elapsedTime, long totalEdges, double totalWeight) {
    }

    private static <C extends Component<?>, E extends Edge<?>> ExperimentResults driver(final String fileName,
                                                                                        final BoruvkaFactory<C, E> factory,
                                                                                        final AbstractBoruvka<C> boruvkaImpl) throws InterruptedException {
        SolutionToBoruvka finalSolution = null;
        long minElapsed = 0;
        for (int r = 0; r < 5; r++) {
            final Queue<C> nodesLoaded;
            if (boruvkaImpl instanceof SeqBoruvka) {
                nodesLoaded = new LinkedList<>();
            } else {
                nodesLoaded = new ConcurrentLinkedQueue<>();
            }
            final SolutionToBoruvka solution = new SolutionToBoruvka();
            Loader.read(fileName, factory, nodesLoaded);

            final long start;
            if (boruvkaImpl instanceof SeqBoruvka) {
                start = System.currentTimeMillis();
                boruvkaImpl.computeBoruvka(nodesLoaded, solution);
            } else {
                final Thread[] threads = new Thread[getNCores()];
                for (int i = 0; i < threads.length; i++) {
                    threads[i] = new Thread(() -> {
                        boruvkaImpl.computeBoruvka(nodesLoaded, solution);
                    });
                }

                start = System.currentTimeMillis();
                for (Thread thread : threads) {
                    thread.start();
                }
                for (Thread thread : threads) {
                    thread.join();
                }
            }
            final long elapsed = System.currentTimeMillis() - start;
            System.err.println("  " + fileName + " - " + boruvkaImpl.getClass().getName() + " - " + elapsed);

            if (r == 0 || elapsed < minElapsed) {
                minElapsed = elapsed;
            }
            finalSolution = solution;
        }

        assertNotNull(finalSolution.getSolution());
        return new ExperimentResults(minElapsed, finalSolution.getSolution().totalEdges(),
                finalSolution.getSolution().totalWeight());
    }

    private void assertReasonablePercentError(final double expected, final double found) {
        final double delta = Math.abs(expected - found);
        final double percError = delta / expected;
        final double reasonablePercError = 0.2;

        assertTrue(percError <= reasonablePercError,
                String.format("Expected a percent error less than %f percent but got %f percent", reasonablePercError * 100.0, percError * 100.0));
    }

    @Test
    public void testInputUSAroadFLA() throws InterruptedException {
        final ExperimentResults seqResults = driver(inputs[0], new SeqBoruvkaFactory(), new SeqBoruvka());
        final ExperimentResults parResults = driver(inputs[0], new ParBoruvkaFactory(),
                new ParBoruvka());
        assertEquals(seqResults.totalEdges, parResults.totalEdges);
        assertReasonablePercentError(seqResults.totalWeight, parResults.totalWeight);

        final double speedup = seqResults.elapsedTime / parResults.elapsedTime;
        Utils.softAssertTrue(speedup >= expectedSpeedup,
                String.format("Expected speedup of at least %fx, but was %fx", expectedSpeedup, speedup));
    }

    @Test

    public void testInputUSAroadNE() throws InterruptedException {
        final ExperimentResults seqResults = driver(inputs[1], new SeqBoruvkaFactory(), new SeqBoruvka());
        final ExperimentResults parResults = driver(inputs[1], new ParBoruvkaFactory(),
                new ParBoruvka());
        assertEquals(seqResults.totalEdges, parResults.totalEdges);
        assertReasonablePercentError(seqResults.totalWeight, parResults.totalWeight);

        final double speedup = seqResults.elapsedTime / parResults.elapsedTime;
        Utils.softAssertTrue(speedup >= expectedSpeedup,
                String.format("Expected speedup of at least %fx, but was %fx", expectedSpeedup, speedup));

    }
}
