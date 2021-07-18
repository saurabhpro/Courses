package edu.coursera.parallel.week3;

import java.util.Arrays;
import java.util.stream.IntStream;

import static edu.rice.pcdp.PCDP.forasync;

public class ParallelLoop {

    public static void main(String[] args) {
        int[] A = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] B = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] C;

        C = IntStream.range(0, 10)
                .parallel()
                .map(i -> A[i] + B[i])
                .toArray();

        System.out.println(Arrays.toString(C));
    }
}
