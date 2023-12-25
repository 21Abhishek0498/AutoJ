package com.auto.gen.junit.autoj.writer;

import com.auto.gen.junit.autoj.dto.*;
import com.github.javaparser.ast.body.Parameter;
import io.jbock.javapoet.*;
import org.springframework.stereotype.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ClassWriter implements Writer{

    /**
     * @param testClassSpec
     * @param methodList
     * @param testClassName
     * @return
     */
    @Override
    public void writeTestMethod(TypeSpec.Builder testClassSpec, List<JunitMethod> methodList, String testClassName) {
        for (JunitMethod method : methodList) {
            testClassSpec.addMethod(MethodSpec.methodBuilder(method.getMethodToBeTested() + "Test")
                    .addAnnotation(Test.class)
                    .returns(void.class)
                    .addStatement(String.join(";\n",createMockStmts(method,testClassName)))
                    .build());
        }
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
    public void writeDependencies(TypeSpec.Builder testClassSpec, Map<String,String> fields) {
        ClassName mockDependency = ClassName.get("org.mockito", "Mock");

        fields.entrySet().forEach(entry -> {
            System.out.println("key "+entry.getKey()+" value "+entry.getValue());
            ClassName classTypeName = ClassName.get("", entry.getKey());
            FieldSpec tokenServiceField = FieldSpec.builder(classTypeName, entry.getValue(), Modifier.PRIVATE)
                    .addAnnotation(mockDependency)
                    .build();
            testClassSpec.addField(tokenServiceField);
        });
    }

    private String createDependencyStatement(Map<String, String> fields) {
        System.out.println("fields value "+fields);
        return null;
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
        ClassName spyMock = ClassName.get("org.mockito", "Spy");
        ClassName classTypeName = ClassName.get("", testClasses.getClassName());
        FieldSpec tokenServiceField = FieldSpec.builder(classTypeName, testClasses.getClassName().toLowerCase(), Modifier.PRIVATE)
                .addAnnotation(injectMocks)
                .addAnnotation(spyMock)
                .build();

        System.out.println("tokenServiceField == "+tokenServiceField.type);
        testClassSpec.addField(tokenServiceField);

        //create dependency method call
        writeDependencies(testClassSpec, testClasses.getDependencies());

        writeSetupMethod(testClasses, testClassSpec);

        if (!testClasses.getMethodList().isEmpty()) {
            writeTestMethod(testClassSpec, testClasses.getMethodList(), testClasses.getClassName());
        }
        TypeSpec classType = testClassSpec.build();
        JavaFile.Builder javaFileBuilder = JavaFile.builder("com.auto.gen.junit.autoj.javapoet", classType);

        for(String imports: testClasses.getImportStatementList()){
//            javaFileBuilder.addStaticImport(imports.getClass());
            ClassName something = ClassName.get(imports.getClass());
//            testClassSpec.

        }

        JavaFile javaFile = javaFileBuilder.build();

        File outputDirectory = new File("src/test/java");
        javaFile.writeTo(outputDirectory);
        System.out.println("created classes");

    }

    private void writeSetupMethod(MyJunitClass testClasses, TypeSpec.Builder testClassSpec) {
        testClassSpec.addMethod(MethodSpec.methodBuilder("setup")
                .addAnnotation(BeforeEach.class)
                .returns(void.class)
                .addStatement(String.join(";\n",createSetupMethod(testClasses.getPreTestConfiguration())))
                .build());
    }

    private String createSetupMethod(String preTestConfiguration) {
        String modifiedString = preTestConfiguration.replaceAll("\n","");
        String[] testConfig = modifiedString.split(";");
        String setupStatement = String.join(";\n var ", testConfig);
        System.out.println("setupStatement = "+setupStatement);
        return setupStatement;
    }

    private List<String> createMockStmts(JunitMethod method, String className) {
        Map<String, List<String>> mockStmtList = method.getMockObjects().getMockObjectList();
        List<String> mockStmts = new ArrayList<>();
        mockStmtList.entrySet().stream().filter(entry -> (!entry.getValue().isEmpty() && !entry.getKey().contains("_") && !entry.getValue().get(0).contains(className))).forEach(entry -> {
            System.out.println("key "+entry.getKey()+" value "+entry.getValue());
            if(entry.getValue().get(1).contains("doNothing()")){
                mockStmts.add(String.format("Mockito.%s.when(%s)",entry.getValue().get(1),entry.getValue().get(0)));
            } else{
                mockStmts.add(String.format("Mockito.when(%s).thenReturn(%s)",entry.getValue().get(0),entry.getValue().get(1)));
            }
        });
        mockStmts.add(String.format("%s()",method.getMethodToBeTested()));
        mockStmts.add(String.format("Mockito.verify(%s())",method.getMethodToBeTested()));
        return mockStmts;
    }

}
