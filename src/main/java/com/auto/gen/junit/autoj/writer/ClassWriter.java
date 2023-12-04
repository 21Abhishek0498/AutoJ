package com.auto.gen.junit.autoj.writer;

import com.auto.gen.junit.autoj.dto.ClazImportStatement;
import com.auto.gen.junit.autoj.dto.ClazzDependencies;
import com.auto.gen.junit.autoj.dto.Method;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.github.javaparser.ast.body.Parameter;
import io.jbock.javapoet.JavaFile;
import io.jbock.javapoet.MethodSpec;
import io.jbock.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;

public class ClassWriter implements Writer{

    /**
     * @param testClasses
     */
    @Override
    public void writeClass(List<TestClassBuilder> testClasses) throws IOException {
        for(TestClassBuilder testClass : testClasses){
            TypeSpec testClassSpec = TypeSpec.classBuilder(testClass.getTestClassName())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(writeMethod(testClass.getMethodList()))
                    .build();
            JavaFile javaFile = JavaFile.builder("com", testClassSpec)
                    .build();

            javaFile.writeTo(System.out);
        }

    }

    /**
     * @param methods
     * @return
     */
    @Override
    public MethodSpec writeMethod(List<Method> methods) {
        for(Method method : methods){

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getMethodName())
                    .addModifiers(Modifier.DEFAULT)
                    .returns(method.getReturnType().getClass());
            return addMethodParameters(method.getMethodParameters(), methodBuilder).addCode("").build();
       }
        return null;
    }

    private MethodSpec.Builder addMethodParameters(List<Parameter> parameters, MethodSpec.Builder methodBuilder){
        for(Parameter params : parameters){
            methodBuilder.addParameter(params.getClass(), params.getName().asString());
        }
        return methodBuilder;
    }

    /**
     * @param fields
     */
    @Override
    public void writeDependencies(List<ClazzDependencies> fields) {

    }

    /**
     * @param imports
     */
    @Override
    public void writeImports(List<ClazImportStatement> imports) {

    }
}
