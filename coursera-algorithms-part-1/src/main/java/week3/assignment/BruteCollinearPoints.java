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
public class BruteCollinearPoints {
    // Line segments
    private final LineSegment[] lineSegments;

    // Finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (isNullPoints(points)) {
            throw new IllegalArgumentException("Points array can't be null or contain null values");
        }

        lineSegments = getLineSegments(points);
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    private LineSegment[] getLineSegments(Point[] points) {
        // to avoid mutation
        final Point[] pointsCopy = Arrays.copyOf(points, points.length);
        // first sort object to be able to compare with next value
        Arrays.sort(pointsCopy);

        // we can only check for duplicates after sorting as one next will be same
        if (isDuplicatedPoints(pointsCopy)) {
            throw new IllegalArgumentException("Points array can't contain duplicated points");
        }

        final int pointsLength = pointsCopy.length;
        final List<LineSegment> lineSegmentList = new ArrayList<>();

        // add (-3, -2 ...) indent to avoid checking with same points (as we need to check adjacent points)
        for (int p = 0; p < (pointsLength - 3); p++) {
            for (int q = (p + 1); q < (pointsLength - 2); q++) {
                for (int r = (q + 1); r < (pointsLength - 1); r++) {
                    // to avoid one loop if not need
                    if (pointsCopy[p].slopeTo(pointsCopy[q]) != pointsCopy[p].slopeTo(pointsCopy[r])) {
                        continue;
                    }

                    for (int s = (r + 1); s < pointsLength; s++) {
                        if (pointsCopy[p].slopeTo(pointsCopy[q]) == pointsCopy[p].slopeTo(pointsCopy[s])) {
                            lineSegmentList.add(new LineSegment(pointsCopy[p], pointsCopy[s]));
                        }
                    }
                }
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