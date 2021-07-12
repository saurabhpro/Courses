package week2.dparanthesis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DijkstraAlgorithmForExpressionEvaluationTest {

    @Test
    void evaluateParenthesis() {
        final var v = DijkstraAlgorithmForExpressionEvaluation.evaluateExpression("( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) )");
        Assertions.assertEquals(101, v);
    }
}