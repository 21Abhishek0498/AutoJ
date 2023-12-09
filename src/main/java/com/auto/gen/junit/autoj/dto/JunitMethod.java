package com.auto.gen.junit.autoj.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Builder
@Data
@Setter
@Getter
public class JunitMethod {
    private String methodToBeTested;
    private List<Class> methodToBeTestedParameters;

    private MockObjects mockObjects;

    @Builder
    @Getter
    @Setter
    @Data
    public static class MockObjects{
        private HashMap<String,List<String>>  mockObjectList ;

        public void addObjectsToMock(HashMap<String,List<String>> statementsToMock){
            if(mockObjectList==null)
                mockObjectList = new LinkedHashMap<>();
            mockObjectList.putAll(statementsToMock);
        }

    }
    public void addMethodToBeTestedParameters(List<Class> methodToBeTestedParameters){
        this.methodToBeTestedParameters = new ArrayList<>();
        this.methodToBeTestedParameters.addAll(methodToBeTestedParameters);
    }

}
