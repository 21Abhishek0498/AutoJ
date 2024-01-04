package com.auto.gen.junit.autoj.scanner;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public interface ClassScanner {
    Map<String, String> classIdentifier(String packageName) throws FileNotFoundException, ClassNotFoundException;

    boolean isDtoOrEntityClass(CompilationUnit cu);

    boolean isDto(CompilationUnit cu, File file);
}
