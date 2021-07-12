package week2.bqueues;

public class LinkedQueueOfStrings {

    private Node first = null;
    private Node last = null;

    public boolean isEmpty() {
        return first == null;
    }

    // add to end
    public void enqueue(String item) {
        addLast(item);
    }

    private void addLast(String item) {
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty()) {
            first = last;
        } else {
            oldLast.next = last;
        }
    }

    // remove from beginning
    public String dequeue() {
        return removeFirst();
    }

    private String removeFirst() {
        String item = first.item;
        first = first.next;
        if (isEmpty()) {
            last = null;
        }
        return item;
    }

    public static class Node {
        Node next;
        private String item;
    }
}
