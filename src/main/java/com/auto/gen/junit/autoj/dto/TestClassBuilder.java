package com.auto.gen.junit.autoj.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestClassBuilder {
    public String getTestClassName() {
        return testClassName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private final String testClassName;
    private List<Method> methodList;
    private List<ClazzDependencies> dependencies;
    private List<ClazImportStatement> importStatementList;
    private String packageName;

    public String getClassDirectoryPath() {
        return classDirectoryPath;
    }

    public void setClassDirectoryPath(String classDirectoryPath) {
        this.classDirectoryPath = classDirectoryPath;
    }

    private String classDirectoryPath;
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

    public void addImportStatements(ClazImportStatement clazImportStatement){
        this.importStatementList.add(clazImportStatement);
    }

    public void addImportStatements(List<ClazImportStatement> importStatementList){
        this.importStatementList.addAll(importStatementList);
    }
}
