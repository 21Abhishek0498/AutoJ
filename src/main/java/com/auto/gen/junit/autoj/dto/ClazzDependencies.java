package com.auto.gen.junit.autoj.dto;

import com.github.javaparser.ast.type.Type;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
public class ClazzDependencies {
    private String name;
    private Type type;
    private List<ClazzDependencies> clazzDependenciesList;

    private List<Class> excludeList;

   /* public ClazzDependencies(String name, String type){
        this.name = name;
        this.type = type;
        clazzDependenciesList = new ArrayList<>();
    }*/
    public List<Class> getExcludeList(){
        excludeList = new ArrayList<>();
        excludeList.add(String.class);
        excludeList.add(Long.class);
        excludeList.add(Integer.class);
        excludeList.add(Boolean.class);
        excludeList.add(Double.class);
        excludeList.add(Number.class);
        excludeList.add(BigDecimal.class);
        excludeList.add(Float.class);
        return excludeList;
    }
}
