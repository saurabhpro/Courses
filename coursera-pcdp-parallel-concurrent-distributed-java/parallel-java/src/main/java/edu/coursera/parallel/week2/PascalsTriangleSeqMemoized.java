
package edu.coursera.parallel.week2;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.runtime.config.HjSystemProperty;
import edu.rice.hj.runtime.metrics.AbstractMetricsManager;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static edu.rice.hj.Module1.abstractMetrics;
import static edu.rice.hj.Module1.doWork;
import static edu.rice.hj.Module1.finish;
import static edu.rice.hj.Module1.launchHabaneroApp;

/**
 * Pascal's Triangle --- Computes (n C k) using futures
 * <p>
 * The purpose of this example is to illustrate effects on memoization with abstract metrics while using futures. C(n,
 * k) = C(n - 1, k - 1) + C(n - 1, k)
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class PascalsTriangleSeqMemoized {

    private static final Map<Map.Entry<Integer, Integer>, Integer> chooseMemoizedSeqCache = new ConcurrentHashMap<>();

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) {

        final int n = args.length > 0 ? Integer.parseInt(args[0]) : 8;
        final int k = args.length > 1 ? Integer.parseInt(args[1]) : (n - 3);

        System.out.println(" N = " + n);
        System.out.println(" K = " + k);

        kernel("Recursive Version (Sequential)", n, k, () -> chooseRecursiveSeq(n, k));
        kernel("Memoized Version (Sequential)", n, k, () -> chooseMemoizedSeq(n, k));

    }

    private static void kernel(final String mode, final int N, final int K, final Callable<Integer> hjProcedure) {

        System.out.println("===============================================");
        System.out.println("\n Running: " + mode);

        HjSystemProperty.abstractMetrics.setProperty(true);
        launchHabaneroApp(() -> {

            finish(() -> {
                int res = 0;
                try {
                    res = hjProcedure.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(N + " choose " + K + " = " + res);
            });

            final HjMetrics actualMetrics = abstractMetrics();
            AbstractMetricsManager.dumpStatistics(actualMetrics);
        });

        System.out.println("===============================================");
    }

    private static int computeSum(final int left, final int right) {
        doWork(1);
        return left + right;
    }

    private static int computeBaseCaseResult() {
        doWork(1);
        return 1;
    }

    private static int chooseRecursiveSeq(final int N, final int K) {
        if (N == 0 || K == 0 || N == K) {
            return computeBaseCaseResult();
        }

        final int left = chooseRecursiveSeq(N - 1, K - 1);
        final int right = chooseRecursiveSeq(N - 1, K);

        return computeSum(left, right);
    }


    private static int chooseMemoizedSeq(final int N, final int K) {
        final Map.Entry<Integer, Integer> key = Map.entry(N, K);

        if (chooseMemoizedSeqCache.containsKey(key)) {
            return chooseMemoizedSeqCache.get(key);
        }

        if (N == 0 || K == 0 || N == K) {
            final int result = computeBaseCaseResult();
            chooseMemoizedSeqCache.put(key, result);
            return result;
        }

        final int left = chooseMemoizedSeq(N - 1, K - 1);
        final int right = chooseMemoizedSeq(N - 1, K);

        final int result = computeSum(left, right);
        chooseMemoizedSeqCache.put(key, result);
        return result;
    }
}