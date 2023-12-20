package com.auto.gen.junit.autoj.transformer;

import com.auto.gen.junit.autoj.dto.*;
import com.auto.gen.junit.autoj.exclusions.MethodCallExprExclusions;
import com.auto.gen.junit.autoj.exclusions.PackageLevelExclusions;
import com.auto.gen.junit.autoj.type.resolver.StringToClassResolver;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Tansformer implements TransformerProcessor {

    @Autowired
    private StringToClassResolver stringToClassResolver;

    @Autowired
    private PackageLevelExclusions packageLevelExclusions;

    private int ifCounter =0 ;

    /**
     * @param testClassBuilder
     * @return
     */
    @Override
    public MyJunitClass transform(TestClassBuilder testClassBuilder) {
      List<Method> methods = testClassBuilder.getMethodList();
      NodeList<Statement> statementList;
      MyJunitClass classBuilder = buildJunitClass(testClassBuilder);
      for(Method method : methods){
        Optional<BlockStmt> methodBlock = method.getMethodBody().getMethodBody();
        List<String> methodToTestParameters = new LinkedList<>();
        methodToTestParameters = method.getMethodParameters().stream().map(Parameter::resolve)
                .map(ResolvedParameterDeclaration::describeType)
                .map(String::toString)
                .collect(Collectors.toList());
        JunitMethod junitMethod = JunitMethod.builder().methodToBeTested(method.getMethodName()).build();
        junitMethod.addMethodToBeTestedParameters(methodToTestParameters);
        classBuilder.addMethod(junitMethod);
        JunitMethod.MockObjects mockObjectsInst = JunitMethod.MockObjects.builder().build();
        HashMap<String,List<String>> methodMockExpr = whenBlockStatement(methodBlock);
        buildMockExpr(mockObjectsInst, junitMethod, methodMockExpr);
      }
        return classBuilder;
    }

    private HashMap<String,List<String>> whenBlockStatement(Optional<BlockStmt> methodBlock) {
        NodeList<Statement> statementList;
        HashMap<String,List<String>> methodMockExpr = new LinkedHashMap<>();
        if(methodBlock.isPresent()) {
            statementList = methodBlock.get().getStatements();
            for (Statement stmt : statementList) {
               methodMockExpr.putAll(extractExpressionsToBeMocked(stmt));
            }
        }
        return methodMockExpr;
    }

    private static MyJunitClass buildJunitClass(TestClassBuilder testClassBuilder) {
        MyJunitClass junitTransformer = MyJunitClass.builder().className(testClassBuilder.getTestClassName()).build();
        junitTransformer.addImportStatements(testClassBuilder.getImportStatementList());
        junitTransformer.addClassDependencies(testClassBuilder.getDependencies());
        return junitTransformer;
    }

    private void buildMockExpr(JunitMethod.MockObjects mockObjectsInst, JunitMethod junitMethod, HashMap<String,List<String>> methodMockExpr) {
        mockObjectsInst.addObjectsToMock(methodMockExpr);
        junitMethod.setMockObjects(mockObjectsInst);
    }

    private Map<String,List<String>> extractExpressionsToBeMocked(Statement stmt) {
        if (stmt.isExpressionStmt()) {
            Expression expression = stmt.asExpressionStmt().getExpression();
            return extractMethodCallExpr(expression);
        }
        if (stmt.isIfStmt()) {
           List<String> ifStatementParams = processIfExprStatement(stmt.asIfStmt().getCondition());
           System.out.println(ifStatementParams.toString());
            Map<String,List<String>> map = new HashMap<>();
            map.put("if_"+ifCounter,ifStatementParams);
            if(stmt.asIfStmt().hasThenBlock()){
               map.putAll(whenBlockStatement(Optional.of(stmt.asIfStmt().getThenStmt().asBlockStmt())));
            }
            return map;
        }
        if(stmt.isForEachStmt()){
            return whenBlockStatement(Optional.of(stmt.asForEachStmt().getBody().asBlockStmt()));
        }
        if(stmt.isForStmt()){
            return whenBlockStatement(Optional.of(stmt.asForStmt().getBody().asBlockStmt()));
        }
        return new HashMap<>();
    }

    private List<String> processIfExprStatement(Expression expr) {
        if(expr!=null && expr.isUnaryExpr()){
            MethodCallExpr methodCallExpr = expr.asUnaryExpr().getExpression().asMethodCallExpr();
            if(!packageLevelExclusions.isPackageExcluded(methodCallExpr.resolve().getQualifiedSignature()))
                return List.of(methodCallExpr.toString());
            return List.of();
        }
        else if(expr!=null && expr.isBinaryExpr()){
          List left =  processIfExprStatement(expr.asBinaryExpr().getLeft());
          List right =  processIfExprStatement(expr.asBinaryExpr().getRight());
          List<String> listOfIfStatementsToMock = new ArrayList<>();
          listOfIfStatementsToMock.addAll(left);
          listOfIfStatementsToMock.addAll(right);
          return listOfIfStatementsToMock;
        }
        else if(expr!=null && expr.isMethodCallExpr() && !expr.asMethodCallExpr().getArguments().isEmpty()){
            MethodCallExpr methodCallExpr = expr.asMethodCallExpr();
            if(!packageLevelExclusions.isPackageExcluded(methodCallExpr.resolve().getQualifiedSignature()))
                return List.of(methodCallExpr.toString());
            return List.of();
        }
        return List.of();
    }

    private HashMap<String,List<String>> extractMethodCallExpr(Expression expression) {
       if(expression.isMethodCallExpr() && !packageLevelExclusions.isPackageExcluded(expression.asMethodCallExpr().resolve().getQualifiedSignature())){
           return processMethodCallExpr(expression.asMethodCallExpr());
       }

       if(expression.isVariableDeclarationExpr() &&
               expression.asVariableDeclarationExpr()
                .getVariables().get(0).getInitializer().isPresent() &&
                    expression.asVariableDeclarationExpr().getVariables().get(0).getInitializer().get().isMethodCallExpr()){
           MethodCallExpr methodCallExpr = expression.asVariableDeclarationExpr().getVariables().get(0).getInitializer().get().asMethodCallExpr();
           return processMethodCallExpr(methodCallExpr);
        }
        /*expression.asVariableDeclarationExpr().getVariables().stream()
                .filter(variableDeclarator -> variableDeclarator.resolve().);*/
       /* if(expression.isVariableDeclarationExpr()){
            processMethodCallExpr(expression.asAssignExpr().getTarget(), mapping, orderedListOfMockMethod);
        }
*/
      //  processMethodCallExpr(expression, mapping, orderedListOfMockMethod);
       return new LinkedHashMap<>();
    }

    private HashMap<String, List<String>> processMethodCallExpr(Expression expression) {
        HashMap<String, List<String>> mapping = new LinkedHashMap<>();
        List<String> orderedListOfMockMethod = new LinkedList<>();
        if (!expression.asMethodCallExpr().toString().contains(".info(") && !packageLevelExclusions.isPackageExcluded(expression
                .asMethodCallExpr().resolve().getQualifiedSignature())) {
            System.out.println("expression ::: " + expression.toString());
            System.out.println("Signature ::: " + expression.asMethodCallExpr().resolve().getQualifiedSignature());
            MethodCallExpr expr = expression.asMethodCallExpr();
            orderedListOfMockMethod.add(expression.asMethodCallExpr().resolve().getSignature());
            //SimpleName className = expression.asMethodCallExpr().getScope().get().asNameExpr().getName();
            //SimpleName calledMethod = expression.asMethodCallExpr().getName();
            String mockMethodReturnType = expression.asMethodCallExpr().resolve().getReturnType().describe();
            orderedListOfMockMethod.add(mockMethodReturnType);
            mapping.put(expression.toString(), orderedListOfMockMethod);
       /* List<ClazzDependencies> dependenciesMockList = testClassBuilder.getDependencies().stream()
                .filter( fields -> fields.getType().equals(className.getClass()))
                .collect(Collectors.toList());*/
            // JunitMethod.MockObjects.builder().mockObjectList();
        }
        return mapping;
    }
}
