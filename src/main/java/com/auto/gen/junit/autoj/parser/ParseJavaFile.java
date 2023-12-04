package com.auto.gen.junit.autoj.parser;

import com.auto.gen.junit.autoj.ParserUtil;
import com.auto.gen.junit.autoj.dto.ClazzDependencies;
import com.auto.gen.junit.autoj.dto.Method;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParseJavaFile implements ParseFile{
    /**
     * @param fileName
     * @return
     */
    @Override
    public TestClassBuilder startParsing(String fileName) throws IOException {
        CompilationUnit cu = getCompilationUnit(fileName);
        TestClassBuilder testClass = new TestClassBuilder(cu.getClass().getName(), cu.getPackageDeclaration().get().getName().asString());
        log.info("Source class : "+ testClass.getTestClassName());
        log.info("PackageName : "+ testClass.getPackageName());
        testClass.addImportStatements(ParserUtil.getImportStatementsFromSourceClass(cu));
        testClass.addMethods(getAllMethodOfSourceClass(cu));
        testClass.addClassDependencies(getAllClassDependencies(cu, fileName));
        return testClass;
    }

    public List<ClazzDependencies> getAllClassDependencies(CompilationUnit cu, String className){
        List<Class> excludeClassDependencies = ClazzDependencies.builder().build().getExcludeList();
        List<ClazzDependencies> clasDependencies = null;
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : cu.getLocalDeclarationFromClassname(className)) {
            clasDependencies = classOrInterfaceDeclaration.getFields().stream().filter(fieldDeclaration -> !excludeClassDependencies.contains(fieldDeclaration.getElementType().asClassOrInterfaceType().getClass()))
                    .map(FieldDeclaration::asFieldDeclaration)
                    .map( field ->
                        ClazzDependencies.builder().type(field.getElementType())
                                .name(field.getVariable(0).getNameAsString()).build()
                    ).collect(Collectors.toList());
        };
        return clasDependencies;
    }
    @Override
    public List<Method> getAllMethodOfSourceClass(CompilationUnit cu) {
        if(Objects.isNull(cu))
            return new ArrayList<>();
        List<Method> methodList = new ArrayList<>();
        cu.findAll(MethodDeclaration.class).forEach(
                methodDeclaration -> {
                   Method method = Method.builder().methodName(methodDeclaration.getType().asString())
                                    .methodParameters((methodDeclaration.getParameters().stream().collect(Collectors.toList())))
                                    .methodBody(Method.MethodBody.builder().methodBody(methodDeclaration.getBody()).build())
                                    .accessModifier("")
                                    .returnType(methodDeclaration.getType())
                                    .build();
                   log.info(method.toString());
                   methodList.add(method);
                     });
        return methodList;
    }

    @Override
    public CompilationUnit getCompilationUnit(String fileName) throws IOException {
        if(Objects.isNull(fileName))
            throw new FileNotFoundException("File name cannot be null");
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(Paths.get(fileName));
        return parseResult.getResult().get();
    }
}
