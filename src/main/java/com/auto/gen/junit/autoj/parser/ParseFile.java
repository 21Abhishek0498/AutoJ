package com.auto.gen.junit.autoj.parser;

import java.io.IOException;
import java.util.List;

import com.auto.gen.junit.autoj.dto.Method;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.github.javaparser.ast.CompilationUnit;

public interface ParseFile {
    TestClassBuilder startParsing(String fileName) throws IOException;

    List<Method> getAllMethodOfSourceClass(CompilationUnit cu);

    CompilationUnit getCompilationUnit(String fileName) throws IOException;
}
