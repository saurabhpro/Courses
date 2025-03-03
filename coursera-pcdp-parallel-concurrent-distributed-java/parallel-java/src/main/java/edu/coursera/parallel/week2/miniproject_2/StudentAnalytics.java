package edu.coursera.parallel.week2.miniproject_2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

/**
 * A simple wrapper class for various analytics methods.
 */
public final class StudentAnalytics {
    /**
     * Sequentially computes the average age of all actively enrolled students
     * using loops.
     *
     * @param studentArray Student data for the class.
     *
     * @return Average age of enrolled students
     */
    public double averageAgeOfEnrolledStudentsImperative(final Student[] studentArray) {
        final var activeStudents = Arrays.stream(studentArray)
                .filter(Student::checkIsCurrent)
                .collect(Collectors.toList());

        final var ageSum = activeStudents.stream()
                .mapToDouble(Student::getAge)
                .sum();

        return ageSum / (double) activeStudents.size();
    }

    /**
     * TODO compute the average age of all actively enrolled students using
     * parallel streams. This should mirror the functionality of
     * averageAgeOfEnrolledStudentsImperative. This method should not use any
     * loops.
     *
     * @param studentArray Student data for the class.
     *
     * @return Average age of enrolled students
     */
    public double averageAgeOfEnrolledStudentsParallelStream(final Student[] studentArray) {
        return Arrays.stream(studentArray)
                .parallel()
                .filter(Student::checkIsCurrent)
                .mapToDouble(Student::getAge)
                .average()
                .orElse(0.0);
    }

    /**
     * Sequentially computes the most common first name out of all students that
     * are no longer active in the class using loops.
     *
     * @param studentArray Student data for the class.
     *
     * @return Most common first name of inactive students
     */
    public String mostCommonFirstNameOfInactiveStudentsImperative(final Student[] studentArray) {
        final var inactiveStudents = Arrays.stream(studentArray)
                .filter(s -> !s.checkIsCurrent())
                .collect(Collectors.toList());

        final Map<String, Integer> nameCounts = new HashMap<>();

        for (final var s : inactiveStudents) {
            if (nameCounts.containsKey(s.getFirstName())) {
                nameCounts.put(s.getFirstName(), nameCounts.get(s.getFirstName()) + 1);
            } else {
                nameCounts.put(s.getFirstName(), 1);
            }
        }

        String mostCommon = null;
        var mostCommonCount = -1;
        for (final var entry : nameCounts.entrySet()) {
            if (mostCommon == null || entry.getValue() > mostCommonCount) {
                mostCommon = entry.getKey();
                mostCommonCount = entry.getValue();
            }
        }

        return mostCommon;
    }

    /**
     * TODO compute the most common first name out of all students that are no
     * longer active in the class using parallel streams. This should mirror the
     * functionality of mostCommonFirstNameOfInactiveStudentsImperative. This
     * method should not use any loops.
     *
     * @param studentArray Student data for the class.
     *
     * @return Most common first name of inactive students
     */
    public String mostCommonFirstNameOfInactiveStudentsParallelStream(final Student[] studentArray) {
        final var namesCountMap = Arrays.stream(studentArray)
                .parallel()
                .filter(s -> !s.checkIsCurrent())
                .map(Student::getFirstName)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return namesCountMap.entrySet()
                .stream()
                .parallel()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Sequentially computes the number of students who have failed the course
     * who are also older than 20 years old. A failing grade is anything below a
     * 65. A student has only failed the course if they have a failing grade and
     * they are not currently active.
     *
     * @param studentArray Student data for the class.
     *
     * @return Number of failed grades from students older than 20 years old.
     */
    public int countNumberOfFailedStudentsOlderThan20Imperative(final Student[] studentArray) {
        return (int) Arrays.stream(studentArray)
                .filter(getStudentPredicate())
                .count();
    }

    /**
     * TODO compute the number of students who have failed the course who are
     * also older than 20 years old. A failing grade is anything below a 65. A
     * student has only failed the course if they have a failing grade and they
     * are not currently active. This should mirror the functionality of
     * countNumberOfFailedStudentsOlderThan20Imperative. This method should not
     * use any loops.
     *
     * @param studentArray Student data for the class.
     *
     * @return Number of failed grades from students older than 20 years old.
     */
    public int countNumberOfFailedStudentsOlderThan20ParallelStream(final Student[] studentArray) {
        final var count = Arrays.stream(studentArray)
                .parallel()
                .filter(getStudentPredicate())
                .count();

        return toIntExact(count);
    }

    private Predicate<Student> getStudentPredicate() {
        return s -> !s.checkIsCurrent() && s.getAge() > 20 && s.getGrade() < 65;
    }
}
