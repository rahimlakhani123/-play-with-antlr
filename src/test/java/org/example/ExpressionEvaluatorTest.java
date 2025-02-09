package org.example;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ExpressionEvaluatorTest {

    @DisplayName("In clause test")
    @Test
    void inClauseTest() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR", "Apple");
        variables.put("price", 100);
        variables.put("x", 10);
        variables.put("quantity", 5);

        String input = "if(VAR in (\"apple\",\"banana\")) price * 1.2 else price * 0.9";
        var result = createExpressionEvaluator(input, variables);
        Assertions.assertEquals(result, 90.0);
    }

    @Test
    void equalsTest() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR", "apple");
        variables.put("price", 100);
        variables.put("x", 10);
        variables.put("quantity", 5);

        String input = "if (VAR == \"apple\") price * 1.2 else price * 0.9";
        var result = createExpressionEvaluator(input, variables);
        Assertions.assertEquals(result, 120.0);
    }

    @Test
    void notEqualsTest() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR", "apple");
        variables.put("price", 100);
        variables.put("x", 10);
        variables.put("quantity", 5);

        String input = "if (VAR != \"apple\") price * 1.2 else price * 0.9";
        var result = createExpressionEvaluator(input, variables);
        Assertions.assertEquals(result, 90.0);

    }

    @Test
    void testNestedIfs() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR", "apple");
        variables.put("price", 100);
        variables.put("x", 10);
        variables.put("quantity", 5);

        String input = "if (VAR == \"apple\") if(x==10) price * 1.2 else price * 0.9 else 0";
        var result = createExpressionEvaluator(input, variables);
        Assertions.assertEquals(result, 120.0);

    }

    @Test
    void numeralEquals() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR", 150.0);
        variables.put("price", 100);

        String input = "if (VAR == 150.0) price * 1.2 else price * 0.9";
        var result = createExpressionEvaluator(input, variables);
        Assertions.assertEquals(result, 120.0);
    }

    @Test
    void numeralOrEquals() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR", 149.0);
        variables.put("price", 100.00);

        String input = "if (VAR == 150.0 || price == 100.00) price * 1.2 else price * 0.9";
        var result = createExpressionEvaluator(input, variables);
        Assertions.assertTrue(Math.abs((Double)result- 120.0) <= 0.01);
    }


    @Test
    void mixStringAndNumberCheckWithAnd() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR1", 150.0);
        variables.put("InstrumentType", "OTR");
        variables.put("price", 100.00);

        String input = "if(InstrumentType == \"OTR\" && VAR1==150.0 ) price * 1.2 else price * 0.9 )";
        var result = createExpressionEvaluator(input, variables);
        Assertions.assertEquals(result, 120.0);

    }

    private Object createExpressionEvaluator(String input,
            Map<String,Object> variables) {
        org.example.formula.ExpressionLexer lexer = new org.example.formula.ExpressionLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        org.example.formula.ExpressionParser parser = new org.example.formula.ExpressionParser(tokens);
        ParseTree tree = parser.expr();

        // Evaluate expression
        ExpressionEvaluator evaluator = new ExpressionEvaluator(variables);
        return evaluator.visit(tree);
    }
}