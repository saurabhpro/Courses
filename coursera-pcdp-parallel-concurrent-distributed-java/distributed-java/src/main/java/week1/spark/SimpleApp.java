package week1.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In Java 11 we need extra jvm option
 * https://spark.apache.org/docs/latest/quick-start.html
 */
public class SimpleApp {
    public static final Logger LOG = LoggerFactory.getLogger(SimpleApp.class);
    private static final String YOUR_SPARK_HOME = "/usr/local/Cellar/apache-spark/3.1.2";

    public static void main(String[] args) {
        final var logFile = YOUR_SPARK_HOME + "/README.md"; // Should be some file on your system

        // necessary to define the master url
        final var configuration = new SparkConf()
                .setAppName("Simple Application")
                .setMaster("local");

        final var spark = SparkSession.builder()
                .config(configuration)
                .getOrCreate();

        final var logData = spark.read()
                .textFile(logFile)
                .cache();

        final var numAs = logData.filter((FilterFunction<String>) s -> s.contains("a")).count();
        final var numBs = logData.filter((FilterFunction<String>) s -> s.contains("b")).count();

        LOG.info(String.format("Lines with a: %d, lines with b: %d", numAs, numBs));
        // SAMPLE OUTPUT: 21/08/03 07:23:36 INFO SimpleApp: Lines with a: 64, lines with b: 32

        spark.stop();
    }
}