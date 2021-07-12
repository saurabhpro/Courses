package week2.astacks;

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class TestStacks {
    public static void main(String[] args) {
        int n = 50;

        Stack<Integer> stack = new Stack<>();
        while (n > 0) {
            stack.push(n % 2);
            n = n / 2;
        }

        for (int digit : stack) {
            StdOut.print(digit);
        } // output : 110010
    }
}
