package com.auto.gen.junit.autoj.translator;

import org.springframework.stereotype.Component;

@Component
public class TranslateToMockito {
    @Translate(value = "java.lang.String")
    public String getStringMock() {
        return "Mockito.anyString()";
    }

    public String getAnyMock() {
        return "Mockito.any()";
    }

    @Translate(value = "java.lang.Boolean")
    public String getBooleanMock() {
        return "Mockito.anyBoolean()";
    }
}
