package week2.dparanthesis;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Parenthesis {
    private static final char LEFT_PAREN = '(';
    private static final char RIGHT_PAREN = ')';
    private static final char LEFT_BRACE = '{';
    private static final char RIGHT_BRACE = '}';
    private static final char LEFT_BRACKET = '[';
    private static final char RIGHT_BRACKET = ']';

    public static boolean isBalanced(String s) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case LEFT_PAREN:
                    stack.push(LEFT_PAREN);
                    break;
                case LEFT_BRACE:
                    stack.push(LEFT_BRACE);
                    break;
                case LEFT_BRACKET:
                    stack.push(LEFT_BRACKET);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + s.charAt(i));
            }

            switch (s.charAt(i)) {
                case RIGHT_PAREN:
                    if (stack.isEmpty()) return false;
                    if (stack.pop() != LEFT_PAREN) return false;
                    break;
                case RIGHT_BRACE:
                    if (stack.isEmpty()) return false;
                    if (stack.pop() != LEFT_BRACE) return false;
                    break;
                case RIGHT_BRACKET:
                    if (stack.isEmpty()) return false;
                    if (stack.pop() != LEFT_BRACKET) return false;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + s.charAt(i));
            }
        }
        return stack.isEmpty();
    }


    public static void main(String[] args) {
        In in = new In();
        String s = in.readAll().trim();
        StdOut.println(isBalanced(s));
    }
}
