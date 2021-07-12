package week2.dparanthesis;

import edu.princeton.cs.algs4.Stack;

import java.util.Arrays;

/**
 * Two stack operation for braces and evaluating parenthesis
 */
public final class DijkstraAlgorithmForExpressionEvaluation {
    private DijkstraAlgorithmForExpressionEvaluation() {
    }

    static double evaluateExpression(String str) {
        Stack<String> operatorStack = new Stack<>();
        Stack<Double> valueStack = new Stack<>();

        final var arr = str.split(" ");

        Arrays.stream(arr).forEach(s -> {
            switch (s) {
                case "(": // ignore
                    break;
                case "+":
                case "*":
                    operatorStack.push(s);
                    break;
                case ")": // pop operator and two values and then push the sum in value stack again
                    String p = operatorStack.pop();
                    if (p.equals("+")) {
                        valueStack.push(valueStack.pop() + valueStack.pop());
                    } else if (p.equals("*")) {
                        valueStack.push(valueStack.pop() * valueStack.pop());
                    }
                    break;
                default:
                    valueStack.push(Double.parseDouble(s));
                    break;
            }
        });

        return valueStack.pop();
    }
}