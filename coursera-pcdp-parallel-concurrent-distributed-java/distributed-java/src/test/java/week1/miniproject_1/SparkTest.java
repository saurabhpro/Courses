package week1.miniproject_1;

import helper.Utils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparkTest {

    private static JavaSparkContext getSparkContext(int nCores) {
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        var conf = new SparkConf()
                .setAppName("edu.coursera.distributed.PageRank")
                .setMaster("local[" + nCores + "]")
                .set("spark.ui.showConsoleProgress", "false");
        var ctx = new JavaSparkContext(conf);
        ctx.setLogLevel("OFF");
        return ctx;
    }

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            ncoresStr = System.getProperty("COURSERA_GRADER_NCORES");
        }

        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private static Website generateWebsite(int i, int nNodes,
                                           int minEdgesPerNode, int maxEdgesPerNode,
                                           EdgeDistribution edgeConfig) {
        var r = new Random(i);

        var site = new Website(i);

        int nEdges;
        switch (edgeConfig) {
            case INCREASING:
                var frac = (double) i / (double) nNodes;
                var offset = (double) (maxEdgesPerNode - minEdgesPerNode)
                        * frac;
                nEdges = minEdgesPerNode + (int) offset;
                break;
            case RANDOM:
                nEdges = minEdgesPerNode +
                        r.nextInt(maxEdgesPerNode - minEdgesPerNode);
                break;
            case UNIFORM:
                nEdges = maxEdgesPerNode;
                break;
            default:
                throw new RuntimeException();
        }

        for (var j = 0; j < nEdges; j++) {
            site.addEdge(r.nextInt(nNodes));
        }

        return site;
    }

    private static JavaPairRDD<Integer, Website> generateGraphRDD(
            int nNodes, int minEdgesPerNode,
            int maxEdgesPerNode, EdgeDistribution edgeConfig,
            JavaSparkContext context) {
        List<Integer> nodes = IntStream.range(0, nNodes)
                .boxed()
                .collect(Collectors.toCollection(() -> new ArrayList<>(nNodes)));

        return context.parallelize(nodes).mapToPair(i ->
                new Tuple2<>(i, generateWebsite(i, nNodes, minEdgesPerNode, maxEdgesPerNode, edgeConfig)));
    }

    private static JavaPairRDD<Integer, Double> generateRankRDD(
            int nNodes, JavaSparkContext context) {
        List<Integer> nodes = IntStream.range(0, nNodes)
                .boxed()
                .collect(Collectors.toCollection(() -> new ArrayList<>(nNodes)));

        return context.parallelize(nodes)
                .mapToPair(i -> new Tuple2<>(i, 100.0 * new Random(i).nextDouble()));
    }

    private static Website[] generateGraphArr(int nNodes,
                                              int minEdgesPerNode, int maxEdgesPerNode,
                                              EdgeDistribution edgeConfig) {
        var sites = new Website[nNodes];
        Arrays.setAll(sites, i -> generateWebsite(i, nNodes, minEdgesPerNode, maxEdgesPerNode, edgeConfig));
        return sites;
    }

    private static double[] generateRankArr(int nNodes) {
        var ranks = new double[nNodes];
        IntStream.range(0, ranks.length).forEach(i -> {
            var r = new Random(i);
            ranks[i] = 100.0 * r.nextDouble();
        });
        return ranks;
    }

    private static double[] seqPageRank(Website[] sites, double[] ranks) {
        var newRanks = new double[ranks.length];

        IntStream.range(0, sites.length).forEach(j -> {
            var iter = sites[j].edgeIterator();
            while (iter.hasNext()) {
                int target = iter.next();
                newRanks[target] += ranks[j] / (double) sites[j].getNEdges();
            }
        });

        IntStream.range(0, newRanks.length)
                .forEach(j -> newRanks[j] = 0.15 + 0.85 * newRanks[j]);

        return newRanks;
    }

    private static void testDriver(int nNodes, int minEdgesPerNode,
                                   int maxEdgesPerNode, int niterations,
                                   EdgeDistribution edgeConfig) {
        System.err.println("Running the PageRank algorithm for " + niterations +
                " iterations on a website graph of " + nNodes + " websites");
        System.err.println();

        final var repeats = 2;
        var nodesArr = generateGraphArr(nNodes, minEdgesPerNode,
                maxEdgesPerNode, edgeConfig);
        var ranksArr = generateRankArr(nNodes);
        for (var i = 0; i < niterations; i++) {
            ranksArr = seqPageRank(nodesArr, ranksArr);
        }

        var context = getSparkContext(1);

        JavaPairRDD<Integer, Website> nodes = null;
        JavaPairRDD<Integer, Double> ranks = null;
        var singleStart = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            nodes = generateGraphRDD(nNodes, minEdgesPerNode,
                    maxEdgesPerNode, edgeConfig, context);
            ranks = generateRankRDD(nNodes, context);
            for (var i = 0; i < niterations; i++) {
                ranks = PageRank.sparkPageRank(nodes, ranks);
            }
            var parResult = ranks.collect();
        }
        var singleElapsed = System.currentTimeMillis() - singleStart;
        context.stop();

        context = getSparkContext(getNCores());

        List<Tuple2<Integer, Double>> parResult = null;
        var parStart = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            nodes = generateGraphRDD(nNodes, minEdgesPerNode,
                    maxEdgesPerNode, edgeConfig, context);
            ranks = generateRankRDD(nNodes, context);
            for (var i = 0; i < niterations; i++) {
                ranks = PageRank.sparkPageRank(nodes, ranks);
            }
            parResult = ranks.collect();
        }
        var parElapsed = System.currentTimeMillis() - parStart;
        var speedup = (double) singleElapsed / (double) parElapsed;
        context.stop();

        Map<Integer, Double> keyed = new HashMap<Integer, Double>();
        for (var site : parResult) {
            assert (!keyed.containsKey(site._1()));
            keyed.put(site._1(), site._2());
        }

        assertEquals(nodesArr.length, parResult.size());
        for (var i = 0; i < parResult.size(); i++) {
            assertTrue(keyed.containsKey(nodesArr[i].getId()));
            var delta = Math.abs(ranksArr[i] -
                    keyed.get(nodesArr[i].getId()));
            assertTrue(delta < 1E-9);
        }

        System.err.println();
        System.err.println("Single-core execution ran in " + singleElapsed +
                " ms");
        System.err.println(getNCores() + "-core execution ran in " +
                parElapsed + " ms, yielding a speedup of " + speedup + "x");
        System.err.println();

        final var expectedSpeedup = 1.35;
        var msg = "Expected at least " + expectedSpeedup +
                "x speedup, but only saw " + speedup + "x. Sequential time = " +
                singleElapsed + " ms, parallel time = " + parElapsed + " ms";
        Utils.softAssertTrue(speedup >= expectedSpeedup, msg);
    }

    @Test
    public void testUniformTwentyThousand() {
        final var nNodes = 20000;
        final var minEdgesPerNode = 20;
        final var maxEdgesPerNode = 40;
        final var niterations = 5;
        final var edgeConfig = EdgeDistribution.UNIFORM;

        testDriver(nNodes, minEdgesPerNode, maxEdgesPerNode, niterations,
                edgeConfig);
    }

    @Test
    public void testUniformFiftyThousand() {
        final var nNodes = 50000;
        final var minEdgesPerNode = 20;
        final var maxEdgesPerNode = 40;
        final var niterations = 5;
        final var edgeConfig = EdgeDistribution.UNIFORM;

        testDriver(nNodes, minEdgesPerNode, maxEdgesPerNode, niterations,
                edgeConfig);
    }

    @Test
    public void testIncreasingTwentyThousand() {
        final var nNodes = 20000;
        final var minEdgesPerNode = 20;
        final var maxEdgesPerNode = 40;
        final var niterations = 5;
        final var edgeConfig = EdgeDistribution.INCREASING;

        testDriver(nNodes, minEdgesPerNode, maxEdgesPerNode, niterations,
                edgeConfig);
    }

    @Test
    public void testIncreasingFiftyThousand() {
        final var nNodes = 50000;
        final var minEdgesPerNode = 20;
        final var maxEdgesPerNode = 40;
        final var niterations = 5;
        final var edgeConfig = EdgeDistribution.INCREASING;

        testDriver(nNodes, minEdgesPerNode, maxEdgesPerNode, niterations,
                edgeConfig);
    }

    @Test
    public void testRandomTwentyThousand() {
        final var nNodes = 20000;
        final var minEdgesPerNode = 20;
        final var maxEdgesPerNode = 40;
        final var niterations = 5;
        final var edgeConfig = EdgeDistribution.RANDOM;

        testDriver(nNodes, minEdgesPerNode, maxEdgesPerNode, niterations,
                edgeConfig);
    }

    @Test
    public void testRandomFiftyThousand() {
        final var nNodes = 50000;
        final var minEdgesPerNode = 20;
        final var maxEdgesPerNode = 40;
        final var niterations = 5;
        final var edgeConfig = EdgeDistribution.RANDOM;

        testDriver(nNodes, minEdgesPerNode, maxEdgesPerNode, niterations,
                edgeConfig);
    }

    private enum EdgeDistribution {
        INCREASING,
        RANDOM,
        UNIFORM
    }
}
