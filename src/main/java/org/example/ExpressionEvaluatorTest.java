package org.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

public class ExpressionEvaluatorTest {
    public static void main(String[] args) throws Exception {
        // Define variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("VAR", "Apple");
        variables.put("price", 100);
        variables.put("x", 10);
        variables.put("quantity", 5);
        // String input = "if (VAR in (\"apple\",\"banana\")) then price * 1.2 else price * 0.9";
        // String input = "if (VAR == \"apple\") then price * 1.2 else price * 0.9";
        //   String input = "price+x*quantity";

        String input = "if (VAR !=\"Apple\") then price*0.9 else price*1.5";
    }
}

