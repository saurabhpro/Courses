package week2.astacks;

public class FixedCapacityStackOfStrings {

    private final String[] stack;
    int size = 0;

    public FixedCapacityStackOfStrings(int capacity) {
        stack = new String[capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void push(String item) {
        stack[size++] = item;
    }

    public String pop() {
        // loitering - keeping reference of an object after it is no longer needed - should be fixed
        String p = stack[--size];
        stack[size] = null;
        return p;
    }
}