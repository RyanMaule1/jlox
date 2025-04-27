package com.jlox;

import com.jlox.Expr;
import com.jlox.Expr.Literal;

class ASTprinter implements Expr.Visitor<String> {


    String print(Expr expr) {
        return expr.accept(this);
    }


    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexum ,expr.left, expr.right);
    }


    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexum, expr.right);
    }


    @Override 
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }


    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        //literals are simple and dont need to be in the parenthensize mehtod
        
        if (expr.value == null) return "nil";
        return expr.value.toString();

    }


    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        //begin by with a open parenth
        builder.append('(').append(name);

        //for each expr in the exprs, append a space then recursively call this funciton
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }

        builder.append(")");


        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(
                new Expr.Literal(45.67)));
    
        System.out.println(new ASTprinter().print(expression));
    }
}
