package org.example;

import java.util.*;
import java.util.stream.Collectors;
import org.example.formula.ExpressionBaseVisitor;
import org.example.formula.ExpressionParser;

public class ExpressionEvaluator extends ExpressionBaseVisitor<Object> {

    private final Map<String, Object> variables;

    public ExpressionEvaluator(Map<String, Object> variables) {
        this.variables = variables;
    }

    @Override
    public Object visitIfExpr(ExpressionParser.IfExprContext ctx) {
        boolean condition = (boolean) visit(ctx.condition());
        return condition ? visit(ctx.expr(0)) : visit(ctx.expr(1));
    }

    public Object visitOrCondition(ExpressionParser.OrConditionContext ctx) {
        boolean left = (boolean) visit(ctx.condition(0));
        boolean right = (boolean) visit(ctx.condition(1));
        return left || right;
    }

    @Override
    public Object visitAndCondition(ExpressionParser.AndConditionContext ctx) {
        boolean left = (boolean) visit(ctx.condition(0));
        boolean right = (boolean) visit(ctx.condition(1));
        return left && right;
    }

    @Override
    public Object visitComparison(ExpressionParser.ComparisonContext ctx) {
        Object left = visit(ctx.value(0));
        Object right = visit(ctx.value(1));

        if(left.getClass()!= right.getClass()) {
            throw new RuntimeException("Comparison must be between same types");
        }

        String op = ctx.getChild(1).getText();

        if(left instanceof Double) {
            areEqual((Double) left, (Double)right, 0.0001);
        }

        return op.equals("==") ? left.equals(right) : !left.equals(right);
    }

    public static boolean areEqual(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    @Override
    public Object visitContainsExpr(ExpressionParser.ContainsExprContext ctx) {
        String left = visit(ctx.value(0)).toString();
        String right = visit(ctx.value(1)).toString();
        return left.contains(right);
    }

    @Override
    public Object visitInClause(ExpressionParser.InClauseContext ctx) {
        String left = visit(ctx.value(0)).toString();
        List<String> values = ctx.value().subList(1, ctx.value().size()).stream()
                .map(v -> visit(v).toString())
                .collect(Collectors.toList());
        return values.contains(left);
    }

    @Override
    public Object visitAddSub(ExpressionParser.AddSubContext ctx) {
        double left = Double.parseDouble(visit(ctx.arithmetic(0)).toString());
        double right = Double.parseDouble(visit(ctx.arithmetic(1)).toString());

        return ctx.getChild(1).getText().equals("+") ? left + right : left - right;
    }

    @Override
    public Object visitMulDiv(ExpressionParser.MulDivContext ctx) {
        double left = Double.parseDouble(visit(ctx.arithmetic(0)).toString());
        double right = Double.parseDouble(visit(ctx.arithmetic(1)).toString());

        switch (ctx.getChild(1).getText()) {
            case "*":
                return left * right;
            case "/":
                return left / right;
            case "%":
                return left % right;
            default:
                throw new RuntimeException("Unknown operator: " + ctx.getChild(1).getText());
        }
    }

    @Override
    public Object visitParenthesizedExpr(ExpressionParser.ParenthesizedExprContext ctx) {
        return visit(ctx.arithmetic());
    }

    @Override
    public Object visitValueExpr(ExpressionParser.ValueExprContext ctx) {
        return visit(ctx.value());
    }

    @Override
    public Object visitValue(ExpressionParser.ValueContext ctx) {
        if (ctx.STRING() != null) {
            return ctx.STRING().getText().replace("\"", "");
        } else if (ctx.NUMBER() != null) {
            var number = ctx.NUMBER().getText();
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {}
            return Double.parseDouble(ctx.NUMBER().getText());
        } else {
            String varName = ctx.IDENTIFIER().getText();
            if (!variables.containsKey(varName)) {
                throw new RuntimeException("Variable " + varName + " not found");
            }
            return variables.get(varName);
        }
    }
}
