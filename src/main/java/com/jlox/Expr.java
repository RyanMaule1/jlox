package com.jlox;

import java.util.List;


//this class represents generic expressions
abstract class Expr{

  //this is the method that gets overridden by each sub class
  abstract <R> R accept(Visitor<R> visitor);


  //this interface allows us to add new methods to each new type of expression without going to each 
  //expression class and adding the new mthods explicitly
  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
  }


  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    final Expr left;
    final Token operator;
    final Expr right;


    //this method takes in the visitor interface and 
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }
  }


  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
      }
    }
  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    final Object value;

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }
  }
  
  //sub class for unary expr
  static class Unary extends Expr {
    
    //unary constructor
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    final Token operator;
    final Expr right;



    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }
  }

  
}
