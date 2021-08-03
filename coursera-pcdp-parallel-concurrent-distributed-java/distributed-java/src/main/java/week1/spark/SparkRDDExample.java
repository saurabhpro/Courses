package week1.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * https://spark.apache.org/docs/latest/quick-start.html
 */
public class SparkRDDExample {
    public static final Logger LOG = LoggerFactory.getLogger(SparkRDDExample.class);

    public static void main(String[] args) {

        // necessary to define the master url
        SparkConf configuration = new SparkConf()
                .setAppName("Simple Application")
                .setMaster("local[4]");


        List<Integer> data = IntStream.range(1, 1_000_000).boxed().collect(Collectors.toList());

        try (JavaSparkContext sc = new JavaSparkContext(configuration)) {
            Instant start = Instant.now();
            int acc = 0;
            for (Integer datum : data) {
                acc += datum;
            }
            LOG.info("Sum of sequential {} numbers = {} in {} ms", data.size(), acc,
                    Duration.between(start, Instant.now()).toMillis());

            start = Instant.now();
            JavaRDD<Integer> distData = sc.parallelize(data);
            acc = distData.reduce(Integer::sum);
            LOG.info("Sum of parallelized {} numbers  = {} in {} ms", data.size(), acc,
                    Duration.between(start, Instant.now()).toMillis());


            start = Instant.now();
            JavaRDD<String> lines = sc.textFile("distributed-java/src/main/resources/data1.txt");
            var pairs = lines.mapToPair(s -> new Tuple2<>(s, 1));
            JavaPairRDD<String, Integer> counts = pairs.reduceByKey(Integer::sum);

            LOG.info("Reduced parallelized lines = {} in {} ms", counts.sortByKey().collect(),
                    Duration.between(start, Instant.now()).toMillis());
        }
    }
}