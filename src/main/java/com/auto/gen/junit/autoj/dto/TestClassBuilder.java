package com.auto.gen.junit.autoj.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
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
        methodList.addAll(allMethods);
    }

    public void addClassDependencies(List<ClazzDependencies> clazzDependencies){
        dependencies.addAll(clazzDependencies);
    }

    public void addImportStatements(ClazImportStatement clazImportStatement){
        importStatementList.add(clazImportStatement);
    }

    public void addImportStatements(List<ClazImportStatement> importStatementList){
        importStatementList.addAll(importStatementList);
    }
}
