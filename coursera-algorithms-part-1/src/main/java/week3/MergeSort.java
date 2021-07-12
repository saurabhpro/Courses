package week3;

import edu.princeton.cs.algs4.Insertion;

import java.util.stream.IntStream;

public class MergeSort {
    private static final int CUTOFF = 7;

    static <Integer> void merge(Comparable<Integer>[] arr,
                                Comparable<Integer>[] aux,
                                int lo,
                                int mid,
                                int hi) {

        // validate arr halves are sorted
        assert isSorted(arr, lo, mid);
        assert isSorted(arr, mid + 1, hi);

        // copy everything
        for (int k = lo; k <= hi; k++) {
            aux[k] = arr[k];
        }

        // lo pointer of first part
        int i = lo;
        // lo pointer of second part
        int j = mid + 1;

        // lo from the very beginning to all the way hi
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                // if we have already started after mid - just copy everything from right subpart
                arr[k] = aux[j++];
            } else if (j > hi) {
                // if we have already reached the hi - just copy everything from left subpart if not exhausted
                arr[k] = aux[i++];
            } else if (less(aux[j], aux[i])) {
                // if value at j is less than i, place jth value in aux array and move j
                arr[k] = aux[j++];
            } else {
                // if value at j is more than i, place ith value in aux array and move i
                arr[k] = aux[i++];
            }
        }
    }

    private static <Integer> void sort(Comparable<Integer>[] arr,
                                       Comparable<Integer>[] aux,
                                       int lo,
                                       int hi) {

        //improvement 1(for small subarrays.CUTOFF maximum is 7)
        if (hi <= lo + CUTOFF - 1) {
            Insertion.sort(arr, lo, hi);
            return;
        }

//        // base case
//        if (hi <= lo) {
//            return;
//        }

        // Find the middle point
        int mid = lo + (hi - lo) / 2;

        // Sort first and second halves
        sort(arr, aux, lo, mid);
        sort(arr, aux, mid + 1, hi);

        //improvement 2(if already sorted,then return)
        if (less(arr[mid], arr[mid + 1])) {
            return;
        }

        // Merge the sorted halves
        merge(arr, aux, lo, mid, hi);
    }

    public static <Integer> void sort(Comparable<Integer>[] arr) {
        final Comparable<Integer>[] aux = new Comparable[arr.length];
        sort(arr, aux, 0, arr.length - 1);
    }

    public static <Integer> boolean less(Comparable<Integer> a, Comparable<Integer> b) {
        return a.compareTo((Integer) b) < 0;
    }

    public static <Integer> boolean isSorted(Comparable<Integer>[] a, int start, int end) {
        return IntStream.rangeClosed(start + 1, end)
                .noneMatch(i -> less(a[i], a[i - 1]));
    }
}
