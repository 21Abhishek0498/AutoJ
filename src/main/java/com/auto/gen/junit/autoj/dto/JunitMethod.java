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
    public String getMethodToBeTested() {
        return methodToBeTested;
    }

    public void setMethodToBeTested(String methodToBeTested) {
        this.methodToBeTested = methodToBeTested;
    }

    public Map<String, Class> getMethodToBeTestedParameters() {
        return methodToBeTestedParameters;
    }

    public void setMethodToBeTestedParameters(Map<String, Class> methodToBeTestedParameters) {
        this.methodToBeTestedParameters = methodToBeTestedParameters;
    }

    public MockObjects getMockObjects() {
        return mockObjects;
    }

    public void setMockObjects(MockObjects mockObjects) {
        this.mockObjects = mockObjects;
    }

    private String methodToBeTested;
    private Map<String,Class> methodToBeTestedParameters;
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
    public void addMethodToBeTestedParameters(Map<String,Class> methodToBeTestedParameters){
        this.methodToBeTestedParameters = new LinkedHashMap<>();
        this.methodToBeTestedParameters.putAll(methodToBeTestedParameters);
    }

}
