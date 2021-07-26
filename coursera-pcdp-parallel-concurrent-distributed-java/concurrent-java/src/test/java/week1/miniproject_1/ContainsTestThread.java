package week1.miniproject_1;

import java.util.Arrays;

/**
 * A Runnable class used to test the performance of each concurrent ListSet
 * implementation. This thread simply hits the list with a large number of
 * contains.
 */
public class ContainsTestThread extends TestThread implements Runnable {
    private int nSuccessful = 0;
    private int nFailed = 0;

    public ContainsTestThread(final SequenceGenerator seq, final int seqToUse, final ListSet setL) {
        super(seq, seqToUse, setL);
    }

    @Override
    public void run() {
        Arrays.stream(nums).forEach(num -> {
            if (l.contains(num)) {
                nSuccessful++;
            } else {
                nFailed++;
            }
        });
    }

    public int getNSuccessful() {
        return nSuccessful;
    }

    public int getNFailed() {
        return nFailed;
    }
}
