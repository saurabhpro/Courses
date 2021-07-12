package week3;

import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;
import java.util.stream.IntStream;

import static week3.MergeSort.merge;

public class MergeBottomUp {

    public static void sort(Comparable<Integer>[] a) {
        int n = a.length;
        Comparable<Integer>[] aux = new Comparable[n + 1];

        int width;

        for (width = 1; width < n; width = 2 * width) {
            // Combine pairs of array a of width "width"
            int leftStart;

            for (leftStart = 0; leftStart < n; leftStart = leftStart + (2 * width)) {

                // Find ending point of left sub-array, with boundary of n-1
                // mid+1 is starting point of right
                int middle = Math.min((leftStart + width) - 1, n - 1);

                int right = Math.min((leftStart + (2 * width)) - 1, n - 1);

                merge(a, aux, leftStart, middle, right);
            }
        }
    }

    public static void main(String[] args) {
        int n = 12;
        Integer[] a = IntStream.range(0, n)
                .mapToObj(i -> StdRandom.uniform(n))
                .toArray(Integer[]::new);

        sort(a);

        System.out.print(Arrays.toString(a));
    }
}