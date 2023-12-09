package com.auto.gen.junit.autoj.type.resolver;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class StringToClassResolver {

    private HashMap<String,String> converter ;
    public StringToClassResolver(){
        converter =  new HashMap<>();
        converter.put("java.lang.String[]","[Ljava.lang.String;");
        converter.put("java.lang.String...","[Ljava.lang.String;");
    }

    public String convert(String key){
        if(converter.containsKey(key)){
            return converter.get(key);
        }
        return "";
    }

}
