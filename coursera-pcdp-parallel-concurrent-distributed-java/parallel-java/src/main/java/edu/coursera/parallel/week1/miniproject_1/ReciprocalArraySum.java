package edu.coursera.parallel.week1.miniproject_1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * Class wrapping methods for implementing reciprocal array sum in parallel.
 */
public final class ReciprocalArraySum {

    /**
     * Default constructor.
     */
    private ReciprocalArraySum() {
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     *
     * @param input Input array
     *
     * @return The sum of the reciprocals of the array input
     */
    private static double seqArraySum(final double[] input, final int start, final int end) {
        double sum = 0;

        // Compute sum of reciprocals of array elements
        for (var i = start; i < end; i++) {
            sum += 1 / input[i];
        }

        return sum;
    }

    /**
     * Computes the size of each chunk, given the number of chunks to create
     * across a given number of elements.
     *
     * @param nChunks   The number of chunks to create
     * @param nElements The number of elements to chunk across
     *
     * @return The default chunk size
     */
    private static int getChunkSize(final int nChunks, final int nElements) {
        // Integer ceil
        return (nElements + nChunks - 1) / nChunks;
    }

    /**
     * Computes the inclusive element index that the provided chunk starts at,
     * given there are a certain number of chunks.
     *
     * @param chunk     The chunk to compute the start of
     * @param nChunks   The number of chunks created
     * @param nElements The number of elements to chunk across
     *
     * @return The inclusive index that this chunk starts at in the set of
     * nElements
     */
    private static int getChunkStartInclusive(final int chunk, final int nChunks, final int nElements) {
        final var chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    /**
     * Computes the exclusive element index that the provided chunk ends at,
     * given there are a certain number of chunks.
     *
     * @param chunk     The chunk to compute the end of
     * @param nChunks   The number of chunks created
     * @param nElements The number of elements to chunk across
     *
     * @return The exclusive end index for this chunk
     */
    private static int getChunkEndExclusive(final int chunk, final int nChunks, final int nElements) {
        final var chunkSize = getChunkSize(nChunks, nElements);
        final var end = (chunk + 1) * chunkSize;

        return Math.min(end, nElements);
    }

    /**
     * TODO: Modify this method to compute the same reciprocal sum as
     * seqArraySum, but use two tasks running in parallel under the Java Fork
     * Join framework. You may assume that the length of the input array is
     * evenly divisible by 2.
     *
     * @param input Input array
     *
     * @return The sum of the reciprocals of the array input
     */
    private static double parArraySum(final double[] input) {
        if (input.length % 2 != 0) {
            throw new IllegalArgumentException("expected even inputs");
        }
        return parManyTaskArraySum(input, 2);
    }

    /**
     * TODO: Extend the work you did to implement parArraySum to use a set
     * number of tasks to compute the reciprocal array sum. You may find the
     * above utilities getChunkStartInclusive and getChunkEndExclusive helpful
     * in computing the range of element indices that belong to each chunk.
     *
     * @param input    Input array
     * @param numTasks The number of tasks to create
     *
     * @return The sum of the reciprocals of the array input
     */
    private static double parManyTaskArraySum(final double[] input, final int numTasks) {
        if (input.length % 2 != 0) {
            throw new IllegalArgumentException("expected even inputs");
        }

        var sum = 0.0;
        final List<ReciprocalArraySumTask> taskList = new ArrayList<>();

        // Compute sum of reciprocals of array elements
        for (var i = 0; i < numTasks; i++) {
            taskList.add(new ReciprocalArraySumTask(
                    getChunkStartInclusive(i, numTasks, input.length),
                    getChunkEndExclusive(i, numTasks, input.length),
                    input
            ));
        }

        // forks all tasks in the specified collection
        ForkJoinTask.invokeAll(taskList);

        for (final var reciprocalArraySumTask : taskList) {
            sum += reciprocalArraySumTask.getValue();
        }

        return sum;
    }

    /**
     * This class stub can be filled in to implement the body of each task
     * created to perform reciprocal array sum in parallel.
     */
    private static class ReciprocalArraySumTask extends RecursiveAction {

        private static final int SEQUENTIAL_THRESHOLD = 58000;

        /**
         * Starting index for traversal done by this task.
         */
        private final int startIndexInclusive;
        /**
         * Ending index for traversal done by this task.
         */
        private final int endIndexExclusive;
        /**
         * Input array to reciprocal sum.
         */
        private final double[] input;
        /**
         * Intermediate value produced by this task.
         */
        private double value;

        /**
         * Constructor.
         *
         * @param setStartIndexInclusive Set the starting index to begin
         *                               parallel traversal at.
         * @param setEndIndexExclusive   Set ending index for parallel traversal.
         * @param setInput               Input values
         */
        ReciprocalArraySumTask(final int setStartIndexInclusive, final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }

        /**
         * Getter for the value produced by this task.
         *
         * @return Value produced by this task
         */
        public double getValue() {
            return value;
        }

        /**
         * TODO
         * This is the function we had to implement
         */
        @Override
        protected void compute() {
            final var range = this.endIndexExclusive - this.startIndexInclusive;
            if (range <= SEQUENTIAL_THRESHOLD) {
                this.value = ReciprocalArraySum.seqArraySum(input, startIndexInclusive, endIndexExclusive);
            } else {
                final var middle = (this.startIndexInclusive + this.endIndexExclusive) >>> 1;
                final var leftSum = new ReciprocalArraySumTask(this.startIndexInclusive, middle, this.input);
                final var rightSum = new ReciprocalArraySumTask(middle, this.endIndexExclusive, this.input);

                leftSum.fork();
                rightSum.compute();
                leftSum.join();

                this.value = leftSum.getValue() + rightSum.getValue();
            }
        }
    }
}
