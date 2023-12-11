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
        Map<String,Class> methodToTestParameters = new LinkedHashMap<>();
        method.getMethodParameters().stream().map(Parameter::resolve)
                .map(ResolvedParameterDeclaration::describeType)
                .map(String::toString)
                .forEach(type -> stringToClassResolver.convert(type,methodToTestParameters));
        JunitMethod junitMethod = JunitMethod.builder().methodToBeTested(method.getMethodName()+"_Test()").build();
        junitMethod.addMethodToBeTestedParameters(methodToTestParameters);
        classBuilder.addMethod(junitMethod);
        JunitMethod.MockObjects mockObjectsInst = JunitMethod.MockObjects.builder().build();
        if(methodBlock.isPresent()) {
            statementList = methodBlock.get().getStatements();
            for (Statement stmt : statementList) {
                HashMap<String,List<String>> methodMockExpr = extractExpressionsToBeMocked(stmt);
                buildMockExpr(mockObjectsInst, junitMethod, methodMockExpr);
            }
        }

      }
        return classBuilder;
    }

    private static MyJunitClass buildJunitClass(TestClassBuilder testClassBuilder) {
        return MyJunitClass.builder().className(testClassBuilder.getTestClassName() + "_Test")
                .importStatementList(testClassBuilder.getImportStatementList())
                .dependencies(testClassBuilder.getDependencies()).build();
    }

    private void buildMockExpr(JunitMethod.MockObjects mockObjectsInst, JunitMethod junitMethod, HashMap<String,List<String>> methodMockExpr) {
        mockObjectsInst.addObjectsToMock(methodMockExpr);
        junitMethod.setMockObjects(mockObjectsInst);
    }

    private HashMap<String,List<String>> extractExpressionsToBeMocked(Statement stmt) {
        if (stmt.isExpressionStmt()) {
            Expression expression = stmt.asExpressionStmt().getExpression();
            return extractMethodCallExpr(expression);
        }
        return new HashMap<>();
    }

    private HashMap<String,List<String>> extractMethodCallExpr(Expression expression) {
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
