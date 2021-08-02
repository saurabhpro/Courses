package edu.coursera.parallel.week2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static edu.rice.pcdp.PCDP.finish;
import static edu.rice.pcdp.PCDP.future;

/**
 * Pascal's Triangle --- Computes (n C k) using futures
 * <p>
 * The purpose of this example is to illustrate effects on memoization with abstract metrics while using futures. C(n,
 * k) = C(n - 1, k - 1) + C(n - 1, k)
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class PascalsTriangleMemoized {
    public static final Logger LOG = LoggerFactory.getLogger(PascalsTriangleMemoized.class);

    private static final Map<Map.Entry<Integer, Integer>, Integer> chooseMemoizedSeqCache = new ConcurrentHashMap<>();
    private static final Map<Map.Entry<Integer, Integer>, Future<Integer>> chooseMemoizedParCache = new ConcurrentHashMap<>();

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) {

        final int n = args.length > 0 ? Integer.parseInt(args[0]) : 30;
        final int k = args.length > 1 ? Integer.parseInt(args[1]) : (n - 3);

        LOG.info(" N = {}", n);
        LOG.info(" K = {}", k);

        kernel("Recursive Version (Sequential)", n, k, () -> chooseRecursiveSeq(n, k));
        kernel("Recursive Version (Parallel)", n, k, () -> chooseRecursivePar(n, k));
        kernel("Memoized Version (Sequential)", n, k, () -> chooseMemoizedSeq(n, k));
        kernel("Memoized Version (Parallel)", n, k, () -> chooseMemoizedPar(n, k));
    }

    private static void kernel(final String mode, final int N, final int K, final Callable<Integer> hjProcedure) {
        LOG.info("===============================================");
        LOG.info("Running: {}", mode);
        Instant start = Instant.now();
        finish(() -> {
            int res = 0;
            try {
                res = hjProcedure.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOG.info("{} choose(C) {} = {}", N, K, res);
        });
        LOG.info("Finished in {} ms", Duration.between(start, Instant.now()).toMillis());
        LOG.info("===============================================");
    }

    private static int computeSum(final int left, final int right) {
        return left + right;
    }

    private static int computeBaseCaseResult() {
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

    private static int chooseRecursivePar(final int N, final int K) throws ExecutionException, InterruptedException {

        if (N == 0 || K == 0 || N == K) {
            return computeBaseCaseResult();
        }

        final Future<Integer> left = future(() -> chooseRecursivePar(N - 1, K - 1));
        final Future<Integer> right = future(() -> chooseRecursivePar(N - 1, K));

        final Future<Integer> resultFuture = future(() -> {
            final int leftValue = left.get();
            final int rightValue = right.get();
            return computeSum(leftValue, rightValue);
        });
        return resultFuture.get();
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

    static int chooseMemoizedPar(final int N, final int K) throws ExecutionException, InterruptedException {
        final Map.Entry<Integer, Integer> key = Map.entry(N, K);
        if (chooseMemoizedParCache.containsKey(key)) {
            final Future<Integer> result = chooseMemoizedParCache.get(key);
            return result.get();
        }

        final Future<Integer> resultFuture = future(() -> {
            if (N == 0 || K == 0 || N == K) {
                return computeBaseCaseResult();
            }

            final Future<Integer> left = future(() -> chooseMemoizedPar(N - 1, K - 1));
            final Future<Integer> right = future(() -> chooseMemoizedPar(N - 1, K));

            final Integer leftValue = left.get();
            final Integer rightValue = right.get();
            return computeSum(leftValue, rightValue);
        });
        chooseMemoizedParCache.put(key, resultFuture);
        return resultFuture.get();
    }

}