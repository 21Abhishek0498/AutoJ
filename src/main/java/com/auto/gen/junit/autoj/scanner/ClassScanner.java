package com.auto.gen.junit.autoj.scanner;

import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface ClassScanner {
    List<String> dtoIdentifier(String packageName) throws FileNotFoundException, ClassNotFoundException;

    boolean isDtoOrEntityClass(CompilationUnit cu);

    boolean isDto(CompilationUnit cu, File file);
}
