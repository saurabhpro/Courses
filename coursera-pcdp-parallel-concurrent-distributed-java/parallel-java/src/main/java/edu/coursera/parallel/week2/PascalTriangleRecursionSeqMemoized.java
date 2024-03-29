package edu.coursera.parallel.week2;// Pascal's triangle in java using recursion

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * a and b here is n C k, so every pascal triangle is essentially row C col
 */
public class PascalTriangleRecursionSeqMemoized {
    public static final Logger LOG = LoggerFactory.getLogger(RecursivePascalTriangle.class);

    public static void main(String[] args) {
        final var row = 9;
        IntStream.range(0, 5).forEach(i -> RecursivePascalTriangle.display(row));
    }

    record RecursivePascalTriangle(int a, int b) {
        private static int pascalTriangle(int a, int b, int[][] store) {
            if (b == 0 || b == a) {
                return 1;
            } else if (store[a][b] != Integer.MIN_VALUE) {
                return store[a][b];
            }

            store[a][b] = pascalTriangle(a - 1, b - 1, store) + pascalTriangle(a - 1, b, store);

            return store[a][b];
        }

        public static void display(int num) {
            final var store = new int[num][num];
            Arrays.stream(store).forEach(a -> Arrays.fill(a, Integer.MIN_VALUE));

            final var start = Instant.now();
            final var sb = new StringBuilder("\n");
            // CODE HERE
            for (var a = 0; a < num; a++) {
                for (var b = 0; b <= a; b++) {
                    sb.append(String.format("%d ", RecursivePascalTriangle.pascalTriangle(a, b, store)));
                }
                sb.append('\n');
            }
            LOG.info(sb.toString());

            final var finish = Instant.now();
            final var timeElapsed = Duration.between(start, finish).toMillis();
            LOG.info("Seq Time: {} ms", timeElapsed);
        }
    }
}