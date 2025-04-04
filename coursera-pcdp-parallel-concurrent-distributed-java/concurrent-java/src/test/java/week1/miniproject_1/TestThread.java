package week1.miniproject_1;

import java.util.stream.IntStream;

/*
 * An abstract interface to be implemented by all testing threads.
 */
public abstract class TestThread {

    // The values to pass to the target list
    protected final Integer[] nums;
    // The ListSet to operate on
    protected final ListSet l;

    public TestThread(SequenceGenerator seq, int seqToUse,
                      ListSet setL) {
        this.nums = new Integer[seqToUse];

        IntStream.range(0, this.nums.length).forEach(i -> this.nums[i] = seq.next());

        this.l = setL;
    }
}
