package com.auto.gen.junit.autoj.type.resolver;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StringToClassResolver {

    private HashMap<String,String> converter ;
    public StringToClassResolver(){
        converter =  new HashMap<>();
        converter.put("java.lang.String[]","[Ljava.lang.String;");
        converter.put("java.lang.String...","[Ljava.lang.String;");
    }

    public void convert(String key, Map<String,Class> methodParams) {
        if(converter.containsKey(key)){
            try {
                methodParams.put(key,Class.forName(converter.get(key)));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        methodParams.put(key,null);
    }

}
