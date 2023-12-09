package com.auto.gen.junit.autoj.generator;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.mapper.CommonObjectMapper;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.type.resolver.Resolver;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneratorHelper implements Generator {

    @Autowired
    private SourceCodePathValidator sourceCodePathValidator;

    @Autowired
    private ParseFile parseFile;

    @Autowired
    private Resolver resolver;


    /**
     * @param sourceCodePath
     * @return
     */
    @Override
    public Map<String, Object> generate(String sourceCodePath) throws IOException {
        Map<String, Object> parsedData = new HashMap<>();
        File directory = new File(sourceCodePath);
        resolver.setResolver(sourceCodePath);
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".java")) {

                TestClassBuilder testClass = parseFile.startParsing(file);
                parsedData.put("className", testClass.getTestClassName());
                parsedData.put("testClass", testClass);

            } else if (file.isDirectory()) {

                Map<String, Object> subdirectoryData = generate(file.getPath());
                parsedData.put(file.getName(), subdirectoryData);
            }
        }

        return parsedData;
    }
}


