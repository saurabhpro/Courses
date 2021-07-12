package week3.assignment;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author Saurabh Kumar
 */
public class FastCollinearPoints {
    // Line segments - array representation
    private final LineSegment[] lineSegments;

    // Line segments - array list representation
    private final List<LineSegment> lineSegmentList = new ArrayList<>();

    // Finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (isNullPoints(points)) {
            throw new IllegalArgumentException("Points array can't be null or contain null values");
        }

        this.lineSegments = getLineSegments(points);
    }

    // Test client
    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    private LineSegment[] getLineSegments(Point[] points) {
        // to avoid mutation + to be able to sort it separately from origin
        Point[] pointsCopy = Arrays.copyOf(points, points.length);
        // first sort object to be able to compare with next value
        Arrays.sort(pointsCopy);


        // we can only check for duplicates after sorting as one next will be same
        if (isDuplicatedPoints(pointsCopy)) {
            throw new IllegalArgumentException("Points array can't contain duplicated points");
        }

        int d;
        for (Point point : pointsCopy) {

            Point[] all = pointsCopy.clone();
            Arrays.sort(all, point.slopeOrder());

            d = 1;
            double previous = Double.NEGATIVE_INFINITY;
            int start = 0;

            for (int j = 0; j < all.length; j++) {
                double current = point.slopeTo(all[j]);

                if (Double.compare(current, previous) != 0) {
                    if (d >= 4 && point.compareTo(all[start]) <= 0) {
                        lineSegmentList.add(new LineSegment(point, all[j - 1]));
                    }
                    d = 1;
                    start = j;
                } else if (j == all.length - 1) {
                    if (d >= 3 && point.compareTo(all[start]) <= 0) {
                        lineSegmentList.add(new LineSegment(point, all[j]));
                    }
                    d = 1;
                }

                d++;
                previous = current;
            }
        }
        // transform found segments to array
        return lineSegmentList.toArray(new LineSegment[0]);
    }

    // The number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // The line segments
    public LineSegment[] segments() {
        return lineSegments.clone();
    }

    // Check if there no null values
    private boolean isNullPoints(Point[] points) {
        if (points == null) {
            return true;
        }

        return Arrays.stream(points).anyMatch(Objects::isNull);
    }

    // Check if there no duplicated points
    private boolean isDuplicatedPoints(Point[] points) {
        return IntStream.range(0, (points.length - 1))
                .anyMatch(i -> points[i].compareTo(points[i + 1]) == 0);
    }
}
