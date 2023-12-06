package com.auto.gen.junit.autoj.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "testclass_builder")
public class TestClassBuilder {
    private final String testClassName;
    private List<Method> methodList;
    private List<ClazzDependencies> dependencies;
    private List<ClazImportStatement> importStatementList;
    private String packageName;
    public TestClassBuilder(String className, String packageName){
        this.packageName = packageName;
        methodList = new ArrayList<>();
        testClassName = className;
        dependencies = new ArrayList<>();
        importStatementList = new ArrayList<>();
    }
    public void addMethods(List<Method> allMethods){
        this.methodList.addAll(allMethods);
    }

    public void addClassDependencies(List<ClazzDependencies> clazzDependencies){
        this.dependencies.addAll(clazzDependencies);
    }

    public void addImportStatements(List<ClazImportStatement> importStatementList){
        this.importStatementList.addAll(importStatementList);
    }
}
