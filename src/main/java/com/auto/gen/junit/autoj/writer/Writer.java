package com.auto.gen.junit.autoj.writer;

import java.io.IOException;
import java.util.List;

import com.auto.gen.junit.autoj.dto.ClazImportStatement;
import com.auto.gen.junit.autoj.dto.ClazzDependencies;
import com.auto.gen.junit.autoj.dto.Method;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;

import io.jbock.javapoet.MethodSpec;

public interface Writer {
    void writeClass(List<TestClassBuilder> testClasses) throws IOException;

    MethodSpec writeMethod(List<Method> methods);

    void writeDependencies(List<ClazzDependencies> fields);

    void writeImports(List<ClazImportStatement> imports);
}
