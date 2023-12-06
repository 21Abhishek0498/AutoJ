package com.auto.gen.junit.autoj.transformer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.auto.gen.junit.autoj.dto.ClazzDependencies;
import com.auto.gen.junit.autoj.dto.Method;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

@Service
public class Tansformer implements TransformerProcessor {
    /**
     * @param testClassBuilder
     */
    @Override
    public void transform(TestClassBuilder testClassBuilder) {
      List<Method> methods = testClassBuilder.getMethodList();
      NodeList<Statement> statementList;
      for(Method method : methods){
        Optional<BlockStmt> statements = method.getMethodBody().getMethodBody();

        if(statements.isPresent());
          statementList = statements.get().getStatements();
          for(Statement stmt : statementList){
              Expression expression = stmt.asExpressionStmt().getExpression();
              if(expression.isMethodCallExpr()){
                  SimpleName className = expression.asMethodCallExpr().getName();
                  NameExpr calledMethod = expression.asMethodCallExpr().asFieldAccessExpr().asNameExpr();
                  List<ClazzDependencies> dependenciesMockList = testClassBuilder.getDependencies().stream()
                          .filter( fields -> fields.getType().equals(className.getClass()))
                          .collect(Collectors.toList());
              }
          }
      }
    }
}
