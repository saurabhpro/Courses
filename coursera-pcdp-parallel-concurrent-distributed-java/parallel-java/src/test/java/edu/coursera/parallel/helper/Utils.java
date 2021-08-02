package edu.coursera.parallel.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    public static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static void softAssertTrue(boolean condition, String failMsg) {
        if (!condition) {
            LOG.error(failMsg);
        }
    }
}
