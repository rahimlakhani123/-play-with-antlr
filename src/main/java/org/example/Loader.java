package org.example;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Loader {

    @State(Scope.Benchmark)
    public static class TreeAndVAriables {
        private ParseTree tree;
        private Map<String, Object> variables;

        @Setup
        public void setup() {
            org.example.formula.ExpressionLexer lexer = new org.example.formula.ExpressionLexer(CharStreams.fromString("if (VAR == 150.0 || price == 100.00) price * 1.2 else price * 0.9"));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            org.example.formula.ExpressionParser parser = new org.example.formula.ExpressionParser(tokens);
            tree = parser.expr();

            variables = new HashMap<>();
            variables.put("VAR", ThreadLocalRandom.current().nextDouble(100.0, 150.0));
            variables.put("price", ThreadLocalRandom.current().nextDouble(100.0, 150.0));
        }
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void antlrParser(Blackhole blackhole, TreeAndVAriables treeAndVAriables) {

        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(treeAndVAriables.variables);
        var value=expressionEvaluator.visit(treeAndVAriables.tree);
        blackhole.consume(value);
    }
}
