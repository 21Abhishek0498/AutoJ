package com.auto.gen.junit.autoj.parser;

import com.auto.gen.junit.autoj.dto.Method;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ParseFile {
    TestClassBuilder startParsing(File file) throws IOException;

    List<Method> getAllMethodOfSourceClass(CompilationUnit cu);

    CompilationUnit getCompilationUnit(String fileName) throws IOException;
}
