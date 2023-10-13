package edu.coursera.parallel.week2.miniproject_2;

import edu.coursera.parallel.helper.Utils;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudentAnalyticsTest {
    final static int REPEATS = 10;
    private final static String[] firstNames = {"Sanjay", "Yunming", "John", "Vivek", "Shams", "Max"};
    private final static String[] lastNames = {"Chatterjee", "Zhang", "Smith", "Sarkar", "Imam", "Grossman"};

    private static int getNCores() {
        var ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
        if (ncoresStr == null) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Integer.parseInt(ncoresStr);
        }
    }

    private Student[] generateStudentData() {
        final var N_STUDENTS = 2000000;
        final var N_CURRENT_STUDENTS = 600000;

        var students = new Student[N_STUDENTS];
        var r = new Random(123);

        for (var s = 0; s < N_STUDENTS; s++) {
            var firstName = firstNames[r.nextInt(firstNames.length)];
            var lastName = lastNames[r.nextInt(lastNames.length)];
            var age = r.nextDouble() * 100.0;
            var grade = 1 + r.nextInt(100);
            var current = (s < N_CURRENT_STUDENTS);

            students[s] = new Student(firstName, lastName, age, grade, current);
        }

        return students;
    }

    private double averageAgeOfEnrolledStudentsHelper(int repeats) {
        var students = generateStudentData();
        var analytics = new StudentAnalytics();

        var ref = analytics.averageAgeOfEnrolledStudentsImperative(students);

        var startSequential = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            analytics.averageAgeOfEnrolledStudentsImperative(students);
        }
        var endSequential = System.currentTimeMillis();

        var calc = analytics.averageAgeOfEnrolledStudentsParallelStream(students);
        var err = Math.abs(calc - ref);
        var msg = "Expected " + ref + " but found " + calc + ", err = " + err;
        assertTrue(err < 1E-5, msg);

        var startParallel = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            analytics.averageAgeOfEnrolledStudentsParallelStream(students);
        }
        var endParallel = System.currentTimeMillis();

        return (double) (endSequential - startSequential) / (double) (endParallel - startParallel);
    }

    /*
     * Test correctness of averageAgeOfEnrolledStudentsParallelStream.
     */
    @Test
    public void testAverageAgeOfEnrolledStudents() {
        averageAgeOfEnrolledStudentsHelper(1);
    }

    /*
     * Test performance of averageAgeOfEnrolledStudentsParallelStream.
     */
    @Test
    public void testAverageAgeOfEnrolledStudentsPerf() {
        var ncores = getNCores();
        var speedup = averageAgeOfEnrolledStudentsHelper(REPEATS);
        var msg = "Expected parallel version to run at least 1.2x faster but speedup was " + speedup;
        Utils.softAssertTrue(speedup > 1.2, msg);
    }

    private double mostCommonFirstNameOfInactiveStudentsHelper(int repeats) {
        var students = generateStudentData();
        var analytics = new StudentAnalytics();

        var ref = analytics.mostCommonFirstNameOfInactiveStudentsImperative(students);

        var startSequential = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            analytics.mostCommonFirstNameOfInactiveStudentsImperative(students);
        }
        var endSequential = System.currentTimeMillis();

        var calc = analytics.mostCommonFirstNameOfInactiveStudentsParallelStream(students);
        assertEquals(ref, calc, "Mismatch in calculated values");

        var startParallel = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            analytics.mostCommonFirstNameOfInactiveStudentsParallelStream(students);
        }
        var endParallel = System.currentTimeMillis();

        return (double) (endSequential - startSequential) / (double) (endParallel - startParallel);
    }

    /*
     * Test correctness of mostCommonFirstNameOfInactiveStudentsParallelStream.
     */
    @Test
    public void testMostCommonFirstNameOfInactiveStudents() {
        mostCommonFirstNameOfInactiveStudentsHelper(1);
    }

    /*
     * Test performance of mostCommonFirstNameOfInactiveStudentsParallelStream.
     */
    @Test
    public void testMostCommonFirstNameOfInactiveStudentsPerf() {
        var ncores = getNCores();
        var speedup = mostCommonFirstNameOfInactiveStudentsHelper(REPEATS);
        var expectedSpeedup = (double) ncores * 0.5;
        System.out.println("Speedup: " + speedup);
        var msg = "Expected speedup to be at least " + expectedSpeedup + " but was " + speedup;
        Utils.softAssertTrue(speedup >= expectedSpeedup, msg);
    }

    private double countNumberOfFailedStudentsOlderThan20Helper(int repeats) {
        var students = generateStudentData();
        var analytics = new StudentAnalytics();

        var ref = analytics.countNumberOfFailedStudentsOlderThan20Imperative(students);

        var startSequential = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            analytics.countNumberOfFailedStudentsOlderThan20Imperative(students);
        }
        var endSequential = System.currentTimeMillis();

        var calc = analytics.countNumberOfFailedStudentsOlderThan20ParallelStream(students);
        assertEquals(ref, calc, "Mismatch in calculated values");

        var startParallel = System.currentTimeMillis();
        for (var r = 0; r < repeats; r++) {
            analytics.countNumberOfFailedStudentsOlderThan20ParallelStream(students);
        }
        var endParallel = System.currentTimeMillis();

        return (double) (endSequential - startSequential) / (double) (endParallel - startParallel);
    }

    /*
     * Test correctness of countNumberOfFailedStudentsOlderThan20ParallelStream.
     */
    @Test
    public void testCountNumberOfFailedStudentsOlderThan20() {
        countNumberOfFailedStudentsOlderThan20Helper(1);
    }

    /*
     * Test performance of countNumberOfFailedStudentsOlderThan20ParallelStream.
     */
    @Test
    public void testCountNumberOfFailedStudentsOlderThan20Perf() {
        var ncores = getNCores();
        var speedup = countNumberOfFailedStudentsOlderThan20Helper(REPEATS);
        var msg = "Expected parallel version to run at least 1.2x faster but speedup was " + speedup;
        Utils.softAssertTrue(speedup > 1.2, msg);
    }
}
