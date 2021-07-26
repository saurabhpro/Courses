package week5.assignment;

import edu.princeton.cs.algs4.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class PointSET {

    // Set of points in BST
    private final Set<Point2D> points;

    // Construct an empty set of points
    public PointSET() {
        points = new TreeSet<>();
    }

    // If the set empty
    public boolean isEmpty() {
        return points.isEmpty();
    }


    // Number of points in the set
    public int size() {
        return points.size();
    }


    // Add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't add empty point");
        }

        points.add(p);
    }

    // If the set contain point p
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't check empty point");
        }

        return points.contains(p);
    }

    // Draw all points to standard draw
    public void draw() {
        points.forEach(Point2D::draw);
    }

    // All points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Can't use empty rectangle");
        }

        Queue<Point2D> range = new Queue<>();

        for (Point2D point : points) {
            if (rect.contains(point)) {
                range.enqueue(point);
            }
        }

        return range;
    }

    // A nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't check empty point");
        }

        if (isEmpty()) {
            return null;
        }

        return points.stream()
                .min(Comparator.comparingDouble(p::distanceTo))
                .orElse(null);
    }

    // Unit testing of the methods (optional)
    public static void main(String[] args) {

        //time test
        String filename = StdIn.readLine();
        In in = new In(filename);
        PointSET tree = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            tree.insert(p);
        }
        System.out.println("size of input = " + tree.size());
        double x = StdIn.readDouble();
        double y = StdIn.readDouble();
        Point2D p = new Point2D(x, y);

        Instant instant = Instant.now();
        System.out.println(tree.nearest(p));
        Instant instant1 = Instant.now();
        System.out.println("time taken for nearest neighbour in nanosecond = " + Duration.between(instant, instant1).toNanos());

        Instant instant2 = Instant.now();
        System.out.println(tree.range(new RectHV(.23, .32, .78, .89)));
        Instant instant3 = Instant.now();
        System.out.println("time taken for range search in nanosecond = " + Duration.between(instant2, instant3).toNanos());
    }
}
