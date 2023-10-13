package week1.miniproject_1;

import org.junit.jupiter.api.Test;
import week1.miniproject_1.CoarseLists.CoarseList;
import week1.miniproject_1.CoarseLists.RWCoarseList;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import static helper.Utils.softAssertTrue;
import static java.lang.Math.random;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.MAX_PRIORITY;
import static org.assertj.core.api.Assertions.assertThat;

class ListSetTest {

    private final int randNumsLength = 10_000;
    private final int randNumRange = 80_000;

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private static void printStats(TestResults ref, TestResults test,
                                   SequenceGenerator seq, String datasetName) {
        System.out.println("=========================================================");
        System.out.println(test.lbl + " vs. " + ref.lbl + " (" + datasetName + " " + seq.getLabel() + ")");
        System.out.println("=========================================================");
        System.out.println("# threads = " + getNCores());
        System.out.println((test.addRate / ref.addRate) + "x improvement in add throughput (" + ref.addRate + " -> " + test.addRate + ")");
        System.out.println((test.containsRate / ref.containsRate) + "x improvement in contains throughput (" + ref.containsRate + " -> " + test.containsRate + ")");
        System.out.println((test.removeRate / ref.removeRate) + "x improvement in remove throughput (" + ref.removeRate + " -> " + test.removeRate + ")");
        System.out.println("=========================================================");
    }

    private static TestResultsPair runKernel(ListFactory factoryA,
                                             String lblA,
                                             ListFactory factoryB,
                                             String lblB,
                                             SequenceGenerator addSeq,
                                             SequenceGenerator containsSeq,
                                             SequenceGenerator removeSeq) throws InterruptedException {
        var numThreads = getNCores();

        /*
         * Require several warm-ups to ensure JIT does not interfere with thread
         * scheduling. We interleave runs of the baseline and the implementation
         * we're testing to try to make any interference on Coursera's
         * autograding platform the same across both.
         */
        TestResults resultsA = null;
        TestResults resultsB = null;
        for (var r = 0; r < 5; r++) {
            addSeq.reset();
            containsSeq.reset();
            removeSeq.reset();

            var newResults = mainKernel(numThreads, factoryA.construct(),
                lblA, addSeq, containsSeq, removeSeq);
            if (resultsA == null ||
                newResults.containsRate > resultsA.containsRate) {
                resultsA = newResults;
            }

            addSeq.reset();
            containsSeq.reset();
            removeSeq.reset();

            newResults = mainKernel(numThreads, factoryB.construct(),
                lblB, addSeq, containsSeq, removeSeq);
            if (resultsB == null ||
                newResults.containsRate > resultsB.containsRate) {
                resultsB = newResults;
            }
        }

        return new TestResultsPair(resultsA, resultsB);
    }

    private static long launchAndJoinAll(Runnable[] runners)
        throws InterruptedException {
        var elapsedTime = new AtomicLong(0);
        var barrier = new CyclicBarrier(runners.length);
        var threads = new Thread[runners.length];

        for (var t = 0; t < threads.length; t++) {
            var tid = t;

            threads[t] = new Thread(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ie) {
                    throw new RuntimeException(ie);
                }

                var startTime = currentTimeMillis();
                runners[tid].run();
                var endTime = currentTimeMillis();

                elapsedTime.addAndGet(endTime - startTime);
            });
            threads[t].setPriority(MAX_PRIORITY);
            threads[t].start();
        }

        for (var thread : threads) {
            thread.join();
        }

        return elapsedTime.get();
    }

    private static TestResults mainKernel(int numThreads,
                                          ListSet list, String lbl,
                                          SequenceGenerator addSeq, SequenceGenerator containsSeq,
                                          SequenceGenerator removeSeq) throws InterruptedException {

        var addRunnables = new AddTestThread[numThreads];
        var containsRunnables = new ContainsTestThread[numThreads];
        var removeRunnables = new RemoveTestThread[numThreads];

        // Test add bandwidth
        for (var t = 0; t < numThreads; t++) {
            addRunnables[t] = new AddTestThread(addSeq, addSeq.sequenceLength() / numThreads, list);
        }
        var addTime = launchAndJoinAll(addRunnables);

        // request GC
        requestGarbageCollection();

        /*
         * Assert that the resulting list is sorted and save the length of the
         * list after the initial adds.
         */
        var listLengthAfterAdds = 1;
        var prev = list.getHead();
        var curr = prev.next;
        while (curr != null) {
            assertThat(curr.object)
                .withFailMessage("List was not sorted, index %d is %d and index %d is %d".formatted(
                    listLengthAfterAdds - 1, prev.object, listLengthAfterAdds, curr.object))
                .isGreaterThan(prev.object);

            prev = curr;
            curr = curr.next;
            listLengthAfterAdds++;
        }

        // Test contains bandwidth
        for (var t = 0; t < numThreads; t++) {
            containsRunnables[t] = new ContainsTestThread(containsSeq, containsSeq.sequenceLength() / numThreads, list);
        }
        var containsTime = launchAndJoinAll(containsRunnables);

        // request GC
        requestGarbageCollection();

        // Record the total number of successes and failures
        var totalContainsSuccesses = 0;
        var totalContainsFailures = 0;
        for (var t = 0; t < numThreads; t++) {
            totalContainsSuccesses += containsRunnables[t].getNSuccessful();
            totalContainsFailures += containsRunnables[t].getNFailed();
        }

        // Test remove bandwidth
        for (var t = 0; t < numThreads; t++) {
            removeRunnables[t] = new RemoveTestThread(removeSeq, removeSeq.sequenceLength() / numThreads, list);
        }
        var removeTime = launchAndJoinAll(removeRunnables);

        // request GC
        requestGarbageCollection();

        /*
         * Verify list is still sorted and record length, successes, and
         * failures.
         */
        var listLengthAfterRemoves = 1;
        prev = list.getHead();
        curr = prev.next;
        while (curr != null) {
            assertThat(curr.object)
                .withFailMessage("List was not sorted")
                .isGreaterThan(prev.object);

            prev = curr;
            curr = curr.next;
            listLengthAfterRemoves++;
        }

        var totalRemovesSuccesses = 0;
        var totalRemovesFailures = 0;
        for (var t = 0; t < numThreads; t++) {
            totalRemovesSuccesses += removeRunnables[t].getNSuccessful();
            totalRemovesFailures += removeRunnables[t].getNFailed();
        }

        // Ops per second
        var addRate = (double) (numThreads * addSeq.sequenceLength()) /
            (double) addTime;
        var containsRate = (double) (numThreads * containsSeq.sequenceLength()) /
            (double) containsTime;
        var removeRate = (double) (numThreads * removeSeq.sequenceLength()) /
            (double) removeTime;
        return new TestResults(lbl, addRate, containsRate, removeRate,
            listLengthAfterAdds, totalContainsSuccesses,
            totalContainsFailures, listLengthAfterRemoves,
            totalRemovesSuccesses, totalRemovesFailures);
    }

    private static void requestGarbageCollection() {
        tryGarbageCollection();
        tryGarbageCollection();
    }

    private static void tryGarbageCollection() {
        System.gc();
        for (var t = 0; t < 10_000; t++) {
            random();  // some action
        }
    }

    @Test
    void testCoarseGrainedLockingRandomLarge() throws InterruptedException {
        SequenceGenerator addSeq = new RandomSequenceGenerator(0,
            getNCores() * randNumsLength, randNumRange);
        SequenceGenerator containsSeq = new RandomSequenceGenerator(1,
            getNCores() * randNumsLength, randNumRange);
        SequenceGenerator removeSeq = new ReversedSequenceGenerator(
            new RandomSequenceGenerator(2, getNCores() * randNumsLength, randNumRange));

        final var expectedAdd = 0.5;
        final var expectedContains = 0.7;
        final var expectedRemove = 0.7;

        testCoarseGrainedLockingHelper(addSeq, containsSeq, removeSeq,
            expectedAdd, expectedContains, expectedRemove, "Large");
    }

    @Test
    void testCoarseGrainedLockingRepeatingLarge() throws InterruptedException {
        SequenceGenerator addSeq = new RepeatingSequenceGenerator(
            getNCores() * 6 * randNumsLength, randNumsLength);
        SequenceGenerator containsSeq = new RepeatingSequenceGenerator(
            getNCores() * 6 * randNumsLength, randNumsLength);
        SequenceGenerator removeSeq = new ReversedSequenceGenerator(
            new RepeatingSequenceGenerator(
                getNCores() * 6 * randNumsLength,
                randNumsLength));

        final var expectedAdd = 0.5;
        final var expectedContains = 0.6;
        final var expectedRemove = 0.6;

        testCoarseGrainedLockingHelper(addSeq, containsSeq, removeSeq,
            expectedAdd, expectedContains, expectedRemove, "Large");
    }

    @Test
    void testReadWriteLocksRandomLarge() throws InterruptedException {
        SequenceGenerator addSeq = new RandomSequenceGenerator(0,
            getNCores() * randNumsLength, randNumRange);
        SequenceGenerator containsSeq = new RandomSequenceGenerator(1,
            getNCores() * randNumsLength, randNumRange);
        SequenceGenerator removeSeq = new ReversedSequenceGenerator(
            new RandomSequenceGenerator(2, getNCores() * randNumsLength, randNumRange));

        final var expectedAdd = 0.5;
        final var expectedRemove = 0.5;
        final var expectedContains = 1.8;

        testReadWriteLocksHelper(addSeq, containsSeq, removeSeq, expectedAdd,
            expectedContains, expectedRemove, "Large");
    }

    @Test
    void testReadWriteLocksRandomSmall() throws InterruptedException {
        SequenceGenerator addSeq = new RandomSequenceGenerator(0,
            getNCores() * randNumsLength / 2, randNumRange);
        SequenceGenerator containsSeq = new RandomSequenceGenerator(1,
            getNCores() * randNumsLength / 2, randNumRange);
        SequenceGenerator removeSeq = new ReversedSequenceGenerator(
            new RandomSequenceGenerator(2, getNCores() * randNumsLength / 2, randNumRange));

        final var expectedAdd = 0.5;
        final var expectedRemove = 0.5;
        final var expectedContains = 1.8;

        testReadWriteLocksHelper(addSeq, containsSeq, removeSeq, expectedAdd,
            expectedContains, expectedRemove, "Small");
    }

    @Test
    void testReadWriteLocksRepeatingLarge() throws InterruptedException {
        SequenceGenerator addSeq = new RepeatingSequenceGenerator(
            getNCores() * 6 * randNumsLength, randNumsLength);
        SequenceGenerator containsSeq = new RepeatingSequenceGenerator(
            getNCores() * 6 * randNumsLength, randNumsLength);
        SequenceGenerator removeSeq = new ReversedSequenceGenerator(
            new RepeatingSequenceGenerator(getNCores() * 6 * randNumsLength, randNumsLength));

        final var expectedAdd = 0.5;
        final var expectedRemove = 0.5;
        final var expectedContains = 1.8;

        testReadWriteLocksHelper(addSeq, containsSeq, removeSeq, expectedAdd,
            expectedContains, expectedRemove, "Large");
    }

    @Test
    void testReadWriteLocksRepeatingSmall() throws InterruptedException {
        SequenceGenerator addSeq = new RepeatingSequenceGenerator(
            getNCores() * 3 * randNumsLength, randNumsLength);
        SequenceGenerator containsSeq = new RepeatingSequenceGenerator(
            getNCores() * 3 * randNumsLength, randNumsLength);
        SequenceGenerator removeSeq = new ReversedSequenceGenerator(
            new RepeatingSequenceGenerator(getNCores() * 3 * randNumsLength, randNumsLength));

        final var expectedAdd = 0.4;
        final var expectedRemove = 0.4;
        final var expectedContains = 1.8;

        testReadWriteLocksHelper(addSeq, containsSeq, removeSeq, expectedAdd,
            expectedContains, expectedRemove, "Small");
    }

    private void testCoarseGrainedLockingHelper(SequenceGenerator addSeq,
                                                SequenceGenerator containsSeq,
                                                SequenceGenerator removeSeq,
                                                double expectedAdd,
                                                double expectedContains,
                                                double expectedRemove,
                                                String datasetName) throws InterruptedException {

        var results = runKernel(
            CoarseList::new,
            "CoarseList", SyncList::new, "SyncList", addSeq,
            containsSeq, removeSeq);
        var lockResults = results.A;
        var syncResults = results.B;
        printStats(syncResults, lockResults, addSeq, datasetName);

        assertThat(lockResults.listLengthAfterAdds).isEqualTo(syncResults.listLengthAfterAdds);
        assertThat(lockResults.totalContainsSuccesses).isEqualTo(syncResults.totalContainsSuccesses);
        assertThat(lockResults.totalContainsFailures).isEqualTo(syncResults.totalContainsFailures);
        assertThat(lockResults.listLengthAfterRemoves).isEqualTo(syncResults.listLengthAfterRemoves);
        assertThat(lockResults.totalRemovesSuccesses).isEqualTo(syncResults.totalRemovesSuccesses);
        assertThat(lockResults.totalRemovesFailures).isEqualTo(syncResults.totalRemovesFailures);

        var addImprovement = lockResults.addRate / syncResults.addRate;
        var containsImprovement = lockResults.containsRate /
            syncResults.containsRate;
        var removeImprovement = lockResults.removeRate /
            syncResults.removeRate;

        var addmsg = format("Expected add throughput to remain " +
                "similar (at least %fx) with locks, but found %fx", expectedAdd,
            addImprovement);
        softAssertTrue(addImprovement >= expectedAdd, addmsg);

        var containsmsg = format("Expected contains throughput to " +
                "remain similar (at least %fx) with locks, but found %fx",
            expectedContains, containsImprovement);
        softAssertTrue(containsImprovement >= expectedContains, containsmsg);

        var removemsg = format("Expected remove throughput to " +
                "remain similar (at least %fx) with locks, but found %fx",
            expectedRemove, removeImprovement);
        softAssertTrue(removeImprovement >= expectedRemove, removemsg);
    }

    public void testReadWriteLocksHelper(SequenceGenerator addSeq,
                                         SequenceGenerator containsSeq,
                                         SequenceGenerator removeSeq, double expectedAdd,
                                         double expectedContains, double expectedRemove,
                                         String datasetName) throws InterruptedException {
        var results = runKernel(RWCoarseList::new,
            "RWCoarseList", SyncList::new, "SyncList", addSeq,
            containsSeq, removeSeq);
        var rwResults = results.A;
        var syncResults = results.B;
        printStats(syncResults, rwResults, addSeq, datasetName);

        assertThat(rwResults.listLengthAfterAdds).isEqualTo(syncResults.listLengthAfterAdds);
        assertThat(rwResults.totalContainsSuccesses).isEqualTo(syncResults.totalContainsSuccesses);
        assertThat(rwResults.totalContainsFailures).isEqualTo(syncResults.totalContainsFailures);
        assertThat(rwResults.listLengthAfterRemoves).isEqualTo(syncResults.listLengthAfterRemoves);
        assertThat(rwResults.totalRemovesSuccesses).isEqualTo(syncResults.totalRemovesSuccesses);
        assertThat(rwResults.totalRemovesFailures).isEqualTo(syncResults.totalRemovesFailures);

        var addImprovement = rwResults.addRate / syncResults.addRate;
        var containsImprovement = rwResults.containsRate /
            syncResults.containsRate;
        var removeImprovement = rwResults.removeRate /
            syncResults.removeRate;

        var addmsg = format("Expected add throughput to remain " +
                "similar (at least %fx) with locks, but found %fx", expectedAdd,
            addImprovement);
        softAssertTrue(addImprovement >= expectedAdd, addmsg);

        var containsmsg = format("Expected contains throughput to " +
                "remain similar (at least %fx) with locks, but found %fx",
            expectedContains, containsImprovement);
        softAssertTrue(containsImprovement >= expectedContains, containsmsg);

        var removemsg = format("Expected remove throughput to " +
                "remain similar (at least %fx) with locks, but found %fx",
            expectedRemove, removeImprovement);
        softAssertTrue(removeImprovement >= expectedRemove, removemsg);
    }

    private interface ListFactory {

        ListSet construct();
    }

    private record TestResults(
        String lbl,
        double addRate,
        double containsRate,
        double removeRate,
        int listLengthAfterAdds,
        int totalContainsSuccesses,
        int totalContainsFailures,
        int listLengthAfterRemoves,
        int totalRemovesSuccesses,
        int totalRemovesFailures
    ) {

    }

    private static class TestResultsPair {

        public final TestResults A;
        public final TestResults B;

        public TestResultsPair(TestResults setA, TestResults setB) {
            A = setA;
            B = setB;
        }
    }
}
