package week3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MergeBottomUpTest {

    @Test
    void sort() {

        Integer[] arr = {6, 0, 7, 9, 4};
        MergeBottomUp.sort(arr);

        assertArrayEquals(new Integer[]{0, 4, 6, 7, 9}, arr);
    }

    @Test
    void sort_2() {

        Integer[] arr = {5, 1, 6, 2, 3, 4};
        MergeBottomUp.sort(arr);

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, arr);
    }
}