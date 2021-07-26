package week1.miniproject_1;

import java.util.Arrays;

/**
 * A Runnable class used to test the performance of each concurrent ListSet
 * implementation. This thread simply hits the list with a large number of adds.
 */
public class AddTestThread extends TestThread implements Runnable {
    public AddTestThread(final SequenceGenerator seq, final int seqToUse, final ListSet setL) {
        super(seq, seqToUse, setL);
    }

    @Override
    public void run() {
        Arrays.stream(nums).forEach(num -> l.add(num));
    }
}
