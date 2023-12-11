package com.auto.gen.junit.autoj.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Builder
@Data
@Setter
@Getter
public class MyJunitClass {
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private List<JunitMethod> methodList;
    private List<ClazzDependencies> dependencies;
    private List<ClazImportStatement> importStatementList;

    public void addMethod(JunitMethod method){
        if(methodList==null)
            methodList  = new LinkedList<>();
        this.methodList.add(method);
    }

    public void addClassDependencies(List<ClazzDependencies> clazzDependencies){
        this.dependencies.addAll(clazzDependencies);
    }

    public void addImportStatements(List<ClazImportStatement> importStatementList){
        this.importStatementList.addAll(importStatementList);
    }




}
