package edu.coursera.parallel.week2;

import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.api.HjSuspendingCallable;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;
import edu.rice.hj.runtime.metrics.AbstractMetricsManager;
import edu.rice.hj.runtime.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static edu.rice.hj.Module1.abstractMetrics;
import static edu.rice.hj.Module1.doWork;
import static edu.rice.hj.Module1.finish;
import static edu.rice.hj.Module1.future;
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
public class PascalsTriangleParMemoized {

    private static final Map<Pair<Integer, Integer>, HjFuture<Integer>> chooseMemoizedParCache = new ConcurrentHashMap<>();

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


        kernel("Recursive Version (Parallel)", n, k, () -> chooseRecursivePar(n, k));
        kernel("Memoized Version (Parallel)", n, k, () -> chooseMemoizedPar(n, k));

    }

    private static void kernel(final String mode, final int N, final int K, final HjSuspendingCallable<Integer> hjProcedure) {

        System.out.println("===============================================");
        System.out.println("\n Running: " + mode);

        HjSystemProperty.abstractMetrics.setProperty(true);
        launchHabaneroApp(() -> {

            finish(() -> {
                final int res = hjProcedure.call();
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

    private static int chooseRecursivePar(final int N, final int K) throws SuspendableException {

        if (N == 0 || K == 0 || N == K) {
            return computeBaseCaseResult();
        }

        final HjFuture<Integer> left = future(() -> chooseRecursivePar(N - 1, K - 1));
        final HjFuture<Integer> right = future(() -> chooseRecursivePar(N - 1, K));

        final HjFuture<Integer> resultFuture = future(() -> {
            final Integer leftValue = left.get();
            final Integer rightValue = right.get();
            return computeSum(leftValue, rightValue);
        });
        return resultFuture.get();
    }

    private static int chooseMemoizedPar(final int N, final int K) throws SuspendableException {

        final Pair<Integer, Integer> key = Pair.factory(N, K);
        if (chooseMemoizedParCache.containsKey(key)) {
            final HjFuture<Integer> result = chooseMemoizedParCache.get(key);
            return result.get();
        }

        final HjFuture<Integer> resultFuture = future(() -> {
            if (N == 0 || K == 0 || N == K) {
                return computeBaseCaseResult();
            }

            final HjFuture<Integer> left = future(() -> chooseMemoizedPar(N - 1, K - 1));
            final HjFuture<Integer> right = future(() -> chooseMemoizedPar(N - 1, K));

            final Integer leftValue = left.get();
            final Integer rightValue = right.get();
            return computeSum(leftValue, rightValue);
        });
        chooseMemoizedParCache.put(key, resultFuture);
        return resultFuture.get();
    }

}