package edu.coursera.parallel.week1.miniproject_0;

import static edu.rice.pcdp.PCDP.async;
import static edu.rice.pcdp.PCDP.finish;

/**
 * A simple class for testing compilation of an PCDP project.
 * https://github.com/habanero-rice/pcdp
 */
public final class Setup {

    /**
     * Default constructor.
     */
    private Setup() {
    }

    /**
     * A simple method for testing compilation of an PCDP project.
     *
     * @param val Input value
     *
     * @return Dummy value
     */
    public static int setup(final int val) {
        final var result = new int[1];
        finish(() -> async(() -> result[0] = val));
        return result[0];
    }
}
