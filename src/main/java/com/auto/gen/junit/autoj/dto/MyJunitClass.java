package com.auto.gen.junit.autoj.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Builder
@Data
@Setter
@Getter
public class MyJunitClass {
    private String className;

    public String getPreTestConfiguration() {
        return preTestConfiguration;
    }

    public void setPreTestConfiguration(String preTestConfiguration) {
        this.preTestConfiguration = preTestConfiguration;
    }

    private String preTestConfiguration;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private List<JunitMethod> methodList;

    private Map<String,String> dependencies;
    private List<String> importStatementList;

    @JsonIgnore
    public void addMethod(JunitMethod method){
        if(methodList==null)
            methodList  = new LinkedList<>();
        this.methodList.add(method);
    }

    @JsonIgnore
    public void addClassDependencies(List<ClazzDependencies> clazzDependencies){
        dependencies = new LinkedHashMap<>();
        dependencies.putAll(clazzDependencies.stream().collect(Collectors.toMap(ClazzDependencies::getType,ClazzDependencies::getName)));
    }

    @JsonIgnore
    public void addImportStatements(List<ClazImportStatement> importStmtList){
        importStatementList = new LinkedList<>();
        importStatementList.addAll(importStmtList.stream().map(imprtStmt -> imprtStmt.getImportStatement()).toList());
    }




}
