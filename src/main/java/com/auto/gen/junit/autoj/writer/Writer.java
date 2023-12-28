package com.auto.gen.junit.autoj.writer;

import com.auto.gen.junit.autoj.dto.*;
import io.jbock.javapoet.MethodSpec;
import io.jbock.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Writer {
//    void writeClass(List<TestClassBuilder> testClasses) throws IOException;

    void writeTestMethod(TypeSpec.Builder testClassSpec, List<JunitMethod> methodList, String testClassName);

//    void writeDependencies(List<ClazzDependencies> fields);

    void writeDependencies(TypeSpec.Builder testClassSpec, Map<String,String> fields);

    void writeImports(List<ClazImportStatement> imports);

    void writeJavaClass(MyJunitClass testClasses) throws Exception;
}