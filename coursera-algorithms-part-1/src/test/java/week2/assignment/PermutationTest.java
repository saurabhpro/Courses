package week2.assignment;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermutationTest {

    @Test
    void main_1() throws IOException {
        final var items = Files.readString(Path.of("src/test/resources/distinct.txt"));
        final var strings = Arrays.stream(items.split(" ")).collect(Collectors.toList());

        Deque<String> queue = new Deque<>();
        strings.forEach(queue::addLast);

        assertEquals(9, strings.size());
        assertEquals("A", queue.removeFirst());
        assertEquals("B", queue.removeFirst());
    }

    @Test
    void main_2() throws IOException {
        final var items = Files.readString(Path.of("src/test/resources/duplicates.txt"));
        final var strings = Arrays.stream(items.split(" ")).collect(Collectors.toList());

        Deque<String> queue = new Deque<>();
        strings.forEach(queue::addLast);

        assertEquals(8, strings.size());
        assertEquals("AA", queue.removeFirst());
        assertEquals("BB", queue.removeFirst());
    }

    @Test
    void main_3() throws IOException {
        final var items = Files.readString(Path.of("src/test/resources/distinct.txt"));
        final var strings = Arrays.stream(items.split(" ")).collect(Collectors.toList());

        RandomizedQueue<String> queue = new RandomizedQueue<>();
        strings.forEach(queue::enqueue);

        assertEquals(9, strings.size());
        assertTrue(IntStream.range(0, strings.size())
                .mapToObj(i -> queue.dequeue())
                .allMatch(strings::contains));
    }

    @Test
    void main_4() throws IOException {
        final var items = Files.readString(Path.of("src/test/resources/duplicates.txt"));
        final var strings = Arrays.stream(items.split(" ")).collect(Collectors.toList());

        RandomizedQueue<String> queue = new RandomizedQueue<>();
        strings.forEach(queue::enqueue);

        assertEquals(8, strings.size());
        assertTrue(IntStream.range(0, strings.size())
                .mapToObj(i -> queue.dequeue())
                .allMatch(strings::contains));
    }
}