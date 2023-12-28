package com.auto.gen.junit.autoj.exclusions;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class MethodCallExprExclusions {

    private List<String> list;

    public MethodCallExprExclusions(){
        list = new ArrayList<>();
        list.add("log.");
        list.add(".println");
        list.add(".print");
    }

    public List<String> getMethodCallExprExclusions() {
        return list;
    }
}
