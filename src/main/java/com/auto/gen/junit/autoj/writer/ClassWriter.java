package com.auto.gen.junit.autoj.writer;

import com.auto.gen.junit.autoj.dto.*;
import com.github.javaparser.ast.body.Parameter;
import io.jbock.javapoet.*;
import org.springframework.stereotype.Service;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
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

    @Override
    public void writeJavaClass(MyJunitClass testClasses) throws Exception {

        ClassName springBootTestClass = ClassName.get("org.springframework.boot.test.context", "SpringBootTest");
        TypeSpec.Builder testClassSpec = TypeSpec.classBuilder(testClasses.getClassName()+"Test");
        testClassSpec.addAnnotation(springBootTestClass);

        ClassName propertySourceClass = ClassName.get("org.springframework.context.annotation", "PropertySource");
        AnnotationSpec.Builder propertySourceAnnotationBuilder = AnnotationSpec.builder(propertySourceClass);
        propertySourceAnnotationBuilder.addMember("value", "$S", "classpath:/application.properties");
        AnnotationSpec propertySourceAnnotation = propertySourceAnnotationBuilder.build();
        testClassSpec.addAnnotation(propertySourceAnnotation);

        ClassName injectMocks = ClassName.get("org.mockito", "InjectMocks");

        ClassName classTypeName = ClassName.get("", testClasses.getClassName());
        TypeName typeName = classTypeName;
        FieldSpec tokenServiceField = FieldSpec.builder(classTypeName, testClasses.getClassName().toLowerCase(), Modifier.PRIVATE)
                .addAnnotation(injectMocks)
                .build();

        System.out.println("tokenServiceField == "+tokenServiceField.type);
        testClassSpec.addField(tokenServiceField);

        if (!testClasses.getMethodList().isEmpty()) {
            for (JunitMethod method : testClasses.getMethodList()) {
                testClassSpec.addMethod(MethodSpec.methodBuilder(method.getMethodToBeTested() + "Test")
                        .addAnnotation(Test.class)
                        .returns(void.class)
                        .addStatement(String.join(";\n",createMockStmts(method,testClasses.getClassName())))
                        .build());
            }
        }
        TypeSpec classType = testClassSpec.build();
        JavaFile.Builder javaFileBuilder = JavaFile.builder("com.auto.gen.junit.autoj.javapoet", classType);

        for(String imports: testClasses.getImportStatementList()){
//            javaFileBuilder.addStaticImport(imports.getClass());
            ClassName something = ClassName.get(imports.getClass());

        }

        JavaFile javaFile = javaFileBuilder.build();

        File outputDirectory = new File("src/test/java");
        javaFile.writeTo(outputDirectory);
        System.out.println("created classes");

    }

    private List<String> createMockStmts(JunitMethod method, String className) {
        Map<String, List<String>> mockStmtList = method.getMockObjects().getMockObjectList();
        List<String> mockStmts = new ArrayList<>();
        String temp = "";
        mockStmtList.entrySet().stream().filter(entry -> (!entry.getValue().isEmpty() && !entry.getKey().contains("_") && !entry.getValue().get(0).contains(className))).forEach(entry -> {
            System.out.println("key "+entry.getKey()+" value "+entry.getValue());
            if(entry.getValue().get(1).contains("doNothing()")){
                mockStmts.add(String.format("Mockito.%s.when(%s)",entry.getValue().get(1),entry.getValue().get(0)));
            } else{
                mockStmts.add(String.format("Mockito.when(%s).thenReturn(%s)",entry.getValue().get(0),entry.getValue().get(1)));
            }
        });
        mockStmts.add(String.format("Mockito.verify(%s())",method.getMethodToBeTested()));
        return mockStmts;
    }

}
