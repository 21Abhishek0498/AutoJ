package com.auto.gen.junit.autoj.generator;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.type.resolver.Resolver;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
        resolver.setResolver(sourceCodePath, null);
        Map<String, String> files = getAllSourceDirJavaFiles(sourceCodePath);
        if(!Objects.isNull(files)){
            for(Map.Entry<String, String> entry : files.entrySet()) {
                TestClassBuilder testClass = parseFile.startParsing(new File(entry.getValue()));
               // parsedData.put("className", testClass.getTestClassName());
                parsedData.put(testClass.getTestClassName(), testClass);
            }
        }
        return parsedData;
    }


    public Map<String,String> getAllSourceDirJavaFiles(String sourceCodePath) throws IOException {
        Map<String, String> files = new HashMap<>();
        if(!StringUtils.isBlank(sourceCodePath)){
            Files.walk(Path.of(sourceCodePath))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String fileName = String.valueOf(path.getFileName());
                    String FilePath = path.toString();
                    files.put(fileName, FilePath);
                });
        }
        return files;
    }

    //Test single class
    @Override
    public Map<String, Object> generate(String sourceCodePath, String test) throws IOException {
        resolver.setResolver(sourceCodePath, test);
        Map<String, Object> parsedData = new HashMap<>();
        TestClassBuilder testClass = parseFile.startParsing(new File(sourceCodePath));
        parsedData.put(testClass.getTestClassName(), testClass);
        return parsedData;
    }
}

