package com.auto.gen.junit.autoj.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    public String getMethodToBeTested() {
        return methodToBeTested;
    }

    public void setMethodToBeTested(String methodToBeTested) {
        this.methodToBeTested = methodToBeTested;
    }

    public List<String> getMethodToBeTestedParameters() {
        return methodToBeTestedParameters;
    }

    public void setMethodToBeTestedParameters(List<String> methodToBeTestedParameters) {
        this.methodToBeTestedParameters = methodToBeTestedParameters;
    }

    public MockObjects getMockObjects() {
        return mockObjects;
    }

    public void setMockObjects(MockObjects mockObjects) {
        this.mockObjects = mockObjects;
    }

    private String methodToBeTested;
    private List<String> methodToBeTestedParameters;
    private MockObjects mockObjects;

    @Builder
    @Getter
    @Setter
    @Data
    public static class MockObjects{

        private HashMap<String,List<String>>  mockObjectList ;

        @JsonIgnore
        public void addObjectsToMock(HashMap<String,List<String>> statementsToMock){
            if(mockObjectList==null)
                mockObjectList = new LinkedHashMap<>();
            mockObjectList.putAll(statementsToMock);
        }

    }

    @JsonIgnore
    public void addMethodToBeTestedParameters(List<String> methodToBeTestedParameters){
        this.methodToBeTestedParameters = new LinkedList<>();
        this.methodToBeTestedParameters.addAll(methodToBeTestedParameters);
    }

}
