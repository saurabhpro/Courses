package week2.astacks;

public class LinkedStackOfStrings {

    private Node first = null;

    public boolean isempty() {
        return first == null;
    }

    public void push(String item) {
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
    }

    public String pop() {
        String item = first.item;
        first = first.next;
        return item;
    }

    public static class Node {
        private String item;
        private Node next;
    }
}
