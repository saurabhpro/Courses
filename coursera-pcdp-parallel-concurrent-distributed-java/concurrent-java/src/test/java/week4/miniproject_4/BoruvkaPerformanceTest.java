package week4.miniproject_4;

import org.junit.jupiter.api.Test;
import week4.miniproject_4.boruvka.BoruvkaFactory;
import week4.miniproject_4.boruvka.Component;
import week4.miniproject_4.boruvka.Edge;
import week4.miniproject_4.boruvka.parallel.ParBoruvkaFactory;
import week4.miniproject_4.boruvka.sequential.SeqBoruvka;
import week4.miniproject_4.boruvka.sequential.SeqBoruvkaFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static helper.Utils.softAssertTrue;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;

public class BoruvkaPerformanceTest {

    // From http://www.dis.uniroma1.it/challenge9/download.shtml
    public static final String[] inputs = {
        "src/main/resources/week4/boruvka/USA-road-d.FLA.gr.gz",
        "src/main/resources/week4/boruvka/USA-road-d.NE.gr.gz"
    };
    static final double expectedSpeedup = 1.7;

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private static <C extends Component<?>, E extends Edge<?>> ExperimentResults driver(String fileName,
                                                                                        BoruvkaFactory<C, E> factory,
                                                                                        AbstractBoruvka<C> boruvkaImpl) throws InterruptedException {
        SolutionToBoruvka finalSolution = null;
        long minElapsed = 0;
        for (var r = 0; r < 5; r++) {
            Queue<C> nodesLoaded;
            if (boruvkaImpl instanceof SeqBoruvka) {
                nodesLoaded = new LinkedList<>();
            } else {
                nodesLoaded = new ConcurrentLinkedQueue<>();
            }
            var solution = new SolutionToBoruvka();
            Loader.read(fileName, factory, nodesLoaded);

            long start;
            if (boruvkaImpl instanceof SeqBoruvka) {
                start = currentTimeMillis();
                boruvkaImpl.computeBoruvka(nodesLoaded, solution);
            } else {
                var threads = new Thread[getNCores()];
                for (var i = 0; i < threads.length; i++) {
                    threads[i] = new Thread(() -> boruvkaImpl.computeBoruvka(nodesLoaded, solution));
                }

                start = currentTimeMillis();
                for (var thread : threads) {
                    thread.start();
                }
                for (var thread : threads) {
                    thread.join();
                }
            }
            var elapsed = currentTimeMillis() - start;
            System.err.println("  " + fileName + " - " + boruvkaImpl.getClass().getName() + " - " + elapsed);

            if (r == 0 || elapsed < minElapsed) {
                minElapsed = elapsed;
            }
            finalSolution = solution;
        }

        assertThat(finalSolution.getSolution()).isNotNull();
        return new ExperimentResults(minElapsed, finalSolution.getSolution().totalEdges(),
            finalSolution.getSolution().totalWeight());
    }

    private void assertReasonablePercentError(double expected, double found) {
        var delta = Math.abs(expected - found);
        var percError = delta / expected;
        final var reasonablePercError = 0.2;

        assertThat(percError)
            .withFailMessage("Expected a percent error less than %f percent but got %f percent"
                .formatted(reasonablePercError * 100.0, percError * 100.0))
            .isGreaterThanOrEqualTo(reasonablePercError);
    }

    @Test
    void testInputUSAroadFLA() throws InterruptedException {
        var seqResults = driver(inputs[0], new SeqBoruvkaFactory(), new SeqBoruvka());
        var parResults = driver(inputs[0], new ParBoruvkaFactory(),
            new ParBoruvka());
        assertThat(seqResults.totalEdges).isEqualTo(parResults.totalEdges);
        assertReasonablePercentError(seqResults.totalWeight, parResults.totalWeight);

        var speedup = seqResults.elapsedTime / parResults.elapsedTime;
        softAssertTrue(
            speedup >= expectedSpeedup,
            String.format("Expected speedup of at least %fx, but was %fx", expectedSpeedup, speedup));
    }

    @Test
    void testInputUSAroadNE() throws InterruptedException {
        var seqResults = driver(inputs[1], new SeqBoruvkaFactory(), new SeqBoruvka());
        var parResults = driver(inputs[1], new ParBoruvkaFactory(),
            new ParBoruvka());
        assertThat(seqResults.totalEdges).isEqualTo(parResults.totalEdges);

        assertReasonablePercentError(seqResults.totalWeight, parResults.totalWeight);

        var speedup = seqResults.elapsedTime / parResults.elapsedTime;
        softAssertTrue(
            speedup >= expectedSpeedup,
            String.format("Expected speedup of at least %fx, but was %fx", expectedSpeedup, speedup));

    }

    record ExperimentResults(double elapsedTime, long totalEdges, double totalWeight) {

    }
}
