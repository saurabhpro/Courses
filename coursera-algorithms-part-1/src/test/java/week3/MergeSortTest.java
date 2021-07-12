package week3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MergeSortTest {

    @Test
    void sort() {
        Integer[] arr = {6, 0, 7, 9, 4};
        MergeSort.sort(arr);

        assertArrayEquals(new Integer[]{0, 4, 6, 7, 9}, arr);
    }
}