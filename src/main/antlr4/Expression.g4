grammar Expression;

@header {
package org.example.formula;
}
// Entry point
expr: ifExpr | condition | arithmetic | IDENTIFIER | STRING | NUMBER;

// If-Else Expression
ifExpr: 'if' '(' condition ')' expr 'else' expr;

// Boolean Conditions
condition: condition '||' condition    # OrCondition
         | condition '&&' condition    # AndCondition
         | comparison                  # ComparisonCondition
         | containsExpr                # ContainsCondition
         | inClause                    # InCondition
         ;
comparison: value ('==' | '!=') value;
containsExpr: value 'contains' value;
inClause: value 'in' '(' value (',' value)* ')';

// Arithmetic Expressions (with operator precedence)
arithmetic
    : arithmetic ('*' | '/' | '%') arithmetic  # MulDiv
    | arithmetic ('+' | '-') arithmetic        # AddSub
    | '(' arithmetic ')'                       # ParenthesizedExpr
    | value                                    # ValueExpr
    ;

value: STRING | NUMBER | IDENTIFIER;

// Tokens
STRING      : '"' (~["])* '"';
NUMBER      : [0-9]+ ('.' [0-9]+)?;
IDENTIFIER  : [a-zA-Z_][a-zA-Z_0-9]*; // Variable names
WS          : [ \t\r\n]+ -> skip;
