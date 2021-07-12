package week3;

import edu.princeton.cs.algs4.StdRandom;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;

public class InsertionSortUsingComparator {

    public static void Sort(Integer[] a, Comparator<Integer> c) {
        for (int i = 0; i < a.length; i++) {
            for (int j = i; j > 0; j--) {
                if (less(c, a[j], a[j - 1])) {
                    swap(a, j, j - 1);
                } else {
                    break;
                }
            }
        }
    }

    public static boolean less(Comparator<Integer> c, Integer a, Integer b) {
        return c.compare(a, b) < 0;
    }

    public static void swap(Integer[] a, int i, int j) {
        Integer swapval = a[i];
        a[i] = a[j];
        a[j] = swapval;
    }

    public static void main(String[] args) {
        int n = 12;
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = StdRandom.uniform(n * 7);
        }

        Instant instant = Instant.now();
        Sort(a, Comparator.naturalOrder());
        Instant instant1 = Instant.now();
        System.out.println("time taken= " + Duration.between(instant, instant1).toMillis());
        System.out.println(Arrays.toString(a));
    }
}