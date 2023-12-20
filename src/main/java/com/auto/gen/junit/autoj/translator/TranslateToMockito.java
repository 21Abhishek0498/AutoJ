package com.auto.gen.junit.autoj.translator;

import org.springframework.stereotype.Component;

@Component
public class TranslateToMockito {
    @Translate(value = "com.java.lang.String")
    public String getStringMock(){
        return "Mockito.anyString()";
    }

}
