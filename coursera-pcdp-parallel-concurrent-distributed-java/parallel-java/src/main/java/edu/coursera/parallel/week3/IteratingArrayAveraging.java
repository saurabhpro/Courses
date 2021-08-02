package edu.coursera.parallel.week3;

import edu.rice.pcdp.PCDP;

public class IteratingArrayAveraging {

    private final int n;
    private double[] myNew;
    private double[] myVal;

    public IteratingArrayAveraging(int n) {
        this.n = n;
        this.myNew = myNew;
        this.myVal = myVal;
    }

    public void runSeq(int iterations) {
        for (int i = 0; i < iterations; i++) {
            for (int j = 1; j <= n; j++) {
                myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0;
            }

            double[] temp = myNew;
            myNew = myVal;
            myVal = temp;
        }
    }

    /**
     * n tasks for every iteration
     *
     * @param iterations
     */
    public void runForAll(int iterations) {
        for (int i = 0; i < iterations; i++) {
            PCDP.forall(1, n, j ->
                    myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0
            );

            double[] temp = myNew;
            myNew = myVal;
            myVal = temp;
        }
    }

    /**
     * grouped tasks for every iteration
     *
     * @param iterations
     */
    public void runForAllGrouped(int iterations, int tasks) {
        for (int iter = 0; iter < iterations; iter++) {
            PCDP.forall(1, tasks - 1, i -> {
                        final var chunkSize = n / tasks;
                        for (int j = i * chunkSize + 1; j <= (i + 1) * chunkSize; j++) {
                            myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0;
                        }
                    }
            );

            double[] temp = myNew;
            myNew = myVal;
            myVal = temp;
        }
    }

    /**
     * grouped tasks for every iteration
     *
     * @param iterations
     */
    public void runForAllBarrier(int iterations, int tasks) {
        PCDP.forall(1, tasks - 1, i -> {
            double[] myVal2 = this.myVal;
            double[] myNew2 = this.myNew;
            final var chunkSize = n / tasks;

            for (int iter = 0; iter < iterations; iter++) {

                for (int j = i * chunkSize + 1; j <= (i + 1) * chunkSize; j++) {
                    myNew2[j] = (myVal2[j - 1] + myVal2[j + 1]) / 2.0;
                }

                // todo add barrier

                double[] temp = myNew;
                myNew = myVal;
                myVal = temp;
            }
        });
    }
}
