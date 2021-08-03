package week1.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
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
        String logFile = YOUR_SPARK_HOME + "/README.md"; // Should be some file on your system

        // necessary to define the master url
        SparkConf configuration = new SparkConf()
                .setAppName("Simple Application")
                .setMaster("local");

        SparkSession spark = SparkSession.builder()
                .config(configuration)
                .getOrCreate();

        Dataset<String> logData = spark.read()
                .textFile(logFile)
                .cache();

        long numAs = logData.filter((FilterFunction<String>) s -> s.contains("a")).count();
        long numBs = logData.filter((FilterFunction<String>) s -> s.contains("b")).count();

        LOG.info(String.format("Lines with a: %d, lines with b: %d", numAs, numBs));
        // SAMPLE OUTPUT: 21/08/03 07:23:36 INFO SimpleApp: Lines with a: 64, lines with b: 32

        spark.stop();
    }
}