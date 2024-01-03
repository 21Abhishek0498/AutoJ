package com.auto.gen.junit.autoj.writer;

import com.auto.gen.junit.autoj.dto.*;
import com.github.javaparser.ast.body.Parameter;
import io.jbock.javapoet.*;
import org.springframework.stereotype.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Service
public class ClassWriter implements Writer {

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
                    .addStatement(String.join(";\n", createMockStmts(method, testClassName)))
                    .build());
        }
    }

    private MethodSpec.Builder addMethodParameters(List<Parameter> parameters, MethodSpec.Builder methodBuilder) {
        for (Parameter params : parameters) {
            methodBuilder.addParameter(params.getClass(), params.getName().asString());
        }
        return methodBuilder;
    }

    /**
     * @param testClassSpec
     * @param fields
     */
    @Override
    public void writeDependencies(TypeSpec.Builder testClassSpec, Map<String, String> fields) {
        ClassName mockDependency = ClassName.get("org.mockito", "Mock");
        fields.entrySet().forEach(entry -> {
            System.out.println("key " + entry.getKey() + " value " + entry.getValue());
            ClassName classTypeName = ClassName.get("", entry.getKey());
            String[] fieldNames = entry.getValue().split(",");
            for (String field : fieldNames) {
                FieldSpec tokenServiceField = FieldSpec.builder(classTypeName, field, Modifier.PRIVATE)
                        .addAnnotation(mockDependency)
                        .build();
                testClassSpec.addField(tokenServiceField);
            }
        });
    }

    private String createDependencyStatement(Map<String, String> fields) {
        System.out.println("fields value " + fields);
        return null;
    }

    @Override
    public void writeJavaClass(MyJunitClass testClasses, boolean isDtoFlag) throws Exception {

        ClassName springBootTestClass = ClassName.get("org.springframework.boot.test.context", "SpringBootTest");
        TypeSpec.Builder testClassSpec = TypeSpec.classBuilder(testClasses.getClassName() + "Test");
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

        System.out.println("tokenServiceField == " + tokenServiceField.type);
        testClassSpec.addField(tokenServiceField);

        ClassName mockDependency = ClassName.get("org.mockito", "Mock");
        ClassName easyRandomTypeName = ClassName.get("org.jeasy.random", "EasyRandom");
        FieldSpec easyRandomServiceField = FieldSpec.builder(easyRandomTypeName, "easyRandom", Modifier.PRIVATE)
                .addAnnotation(mockDependency)
                .build();
        testClassSpec.addField(easyRandomServiceField);

        if (!testClasses.getDependencies().isEmpty()) {
            writeDependencies(testClassSpec, testClasses.getDependencies());
        }
        if (!testClasses.getPreTestConfiguration().isEmpty() && !isDtoFlag) {
            writeSetupMethod(testClassSpec, testClasses);
        }
        if (!testClasses.getMethodList().isEmpty()) {
            FieldSpec dtoClassInstance = FieldSpec.builder(classTypeName, "dtoClassInstance")
                    .initializer(String.format("easyRandom.nextObject(%s.class)", classTypeName))
                    .build();
            testClassSpec.addField(dtoClassInstance);
            if (isDtoFlag) {
                FieldSpec dtoClassInstance = FieldSpec.builder(classTypeName, "dtoClassInstance")
                        .initializer(String.format("easyRandom.nextObject(%s.class)", classTypeName))
                        .build();
                testClassSpec.addField(dtoClassInstance);
                writeSetterGetterMethod(testClassSpec, testClasses, classTypeName);
                writeBuilderMethod(testClassSpec, testClasses, classTypeName);
            } else {
                writeTestMethod(testClassSpec, testClasses.getMethodList(), testClasses.getClassName());
            }
        }
        TypeSpec classType = testClassSpec.build();
        JavaFile.Builder javaFileBuilder = JavaFile.builder("com.auto.gen.junit.autoj.javapoet", classType);

        List<String> importStmts = testClasses.getImportStatementList();
        StringBuilder importStr = new StringBuilder();
        if (!importStmts.isEmpty()) {
            for (String signature : importStmts) {
                importStr.append(String.format("import %s;\n", signature));
            }
        }
        JavaFile javaFile = javaFileBuilder.build();

        File outputDirectory = new File("src/test/java");

        javaFile.writeTo(outputDirectory);

        if (!testClasses.getImportStatementList().isEmpty()) {
            writeImports(testClasses, importStr);
        }

        System.out.println("created classes");

    }

    private void writeBuilderMethod(TypeSpec.Builder testClassSpec, MyJunitClass testClasses, ClassName classTypeName) {
        testClassSpec.addMethod(MethodSpec.methodBuilder("testBuilder")
                .addAnnotation(Test.class)
                .returns(void.class)
                .addStatement(createBuilderMethod(testClasses, classTypeName))
                .addStatement("Assert.assertNotNull(dtoBuilder)")
                .build());
    }

    private String createBuilderMethod(MyJunitClass testClasses, ClassName classTypeName) {
        StringBuilder stmt = new StringBuilder();
        stmt.append(String.format("%s dtoBuilder = %s.builder()", testClasses.getClassName(), testClasses.getClassName()));
        for (JunitMethod method : testClasses.getMethodList()) {
            if (method.getMethodToBeTested().startsWith("set")) {
                String dtoField = method.getMethodToBeTested().replaceFirst("^"+"set","");
                dtoField = Character.toLowerCase(dtoField.charAt(0)) + dtoField.substring(1);
                stmt.append(String.format(".%s(%s)",dtoField, String.join(",", method.getMethodToBeTestedParameters())));
            }
        }
        stmt.append(".build()");
        System.out.println("BUILDER STMT = " + stmt.toString());
        return stmt.toString();
    }

    private void writeSetterGetterMethod(TypeSpec.Builder testClassSpec, MyJunitClass testClasses, ClassName classTypeName) {
        testClassSpec.addMethod(MethodSpec.methodBuilder("testSetterGetter")
                .addAnnotation(Test.class)
                .returns(void.class)
                .addStatement(String.join(";\n",createSetterGetterMethod(testClasses, classTypeName)))
                .build());

//        for (JunitMethod method : testClasses.getMethodList()) {
//            System.out.println(method.getMethodToBeTested());
//        }
    }

    private List<String> createSetterGetterMethod(MyJunitClass testClasses, ClassName classTypeName) {
        System.out.println("CLASSNAME = " + classTypeName.toString());
        List<String> stmt = new ArrayList<>();
        for (JunitMethod method : testClasses.getMethodList()) {
            if (method.getMethodToBeTested().startsWith("set")) {
                String getterMethod = method.getMethodToBeTested().replaceFirst("set", "get");
                stmt.add(String.format("%s.%s(dtoClassInstance.%s())", testClasses.getClassName().toLowerCase(), method.getMethodToBeTested(), getterMethod));
                stmt.add(String.format("Assert.assertEquals(%s.%s(),dtoClassInstance.%s())",testClasses.getClassName().toLowerCase(), method.getMethodToBeTested(),method.getMethodToBeTested()));
            }
        }

        System.out.println("DTO STMT = " + stmt.toString());
        return stmt;
    }

    @Override
    public void writeImports(MyJunitClass testClasses, StringBuilder importStr) throws IOException {
        String pckg = "com.auto.gen.junit.autoj.javapoet";
        String pathStr = pckg.replaceAll("\\.", "/");
        String filePath = "src/test/java/" + pathStr + "/" + testClasses.getClassName() + "Test.java";

        // Read the existing content of the file
        List<String> existingLines = Files.readAllLines(Path.of(filePath));

        // Check if the file has at least two lines
        if (existingLines.size() >= 2) {
            // Modify the second line by appending the new string
            String secondLine = existingLines.get(1) + importStr.toString();
            existingLines.set(1, secondLine);

            // Write the updated content back to the file
            Files.write(Path.of(filePath), existingLines, StandardOpenOption.WRITE);

            System.out.println("String appended to the second line of the file.");
        } else {
            System.out.println("File does not have at least two lines.");
        }
    }

    private void writeSetupMethod(TypeSpec.Builder testClassSpec, MyJunitClass testClasses) {
        testClassSpec.addMethod(MethodSpec.methodBuilder("setup")
                .addAnnotation(BeforeEach.class)
                .returns(void.class)
                .addStatement(String.join(";\n", createSetupMethod(testClasses.getPreTestConfiguration())))
                .build());
    }

    private String createSetupMethod(String preTestConfiguration) {
        String modifiedString = preTestConfiguration.replaceAll("\n", "");
        String[] testConfig = modifiedString.split(";");
        String setupStatement = String.join(";\n var ", testConfig);
//        System.out.println("setupStatement = " + setupStatement);
        return setupStatement;
    }

    private List<String> createMockStmts(JunitMethod method, String className) {
        Map<String, List<String>> mockStmtList = method.getMockObjects().getMockObjectList();
        List<String> mockStmts = new ArrayList<>();
        mockStmtList.entrySet().stream().filter(entry -> (!entry.getValue().isEmpty() && !entry.getKey().contains("_") && !entry.getValue().get(0).contains(className))).forEach(entry -> {
            System.out.println("key " + entry.getKey() + " value " + entry.getValue());
            if (entry.getValue().get(1).contains("doNothing()")) {
                mockStmts.add(String.format("Mockito.%s.when(%s)", entry.getValue().get(1), entry.getValue().get(0)));
            } else {
                mockStmts.add(String.format("Mockito.when(%s).thenReturn(%s)", entry.getValue().get(0), entry.getValue().get(1)));
            }
        });
        mockStmts.add(String.format("%s.%s(%s)", className.toLowerCase(), method.getMethodToBeTested(), method.getMethodToBeTestedParameters()));
        mockStmts.add(String.format("Mockito.verify(%s.%s(%s))", className.toLowerCase(), method.getMethodToBeTested(), method.getMethodToBeTestedParameters()));
        return mockStmts;
    }

}
