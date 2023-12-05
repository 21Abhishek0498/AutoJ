package com.auto.gen.junit.autoj.generator;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeneratorHelper implements Generator {

    @Autowired
    private SourceCodePathValidator sourceCodePathValidator;

    @Autowired
    private ParseFile parseFile;


    /**
     * @param sourceCodePath
     * @return
     */
    @Override
    public List<TestClassBuilder> generate(String sourceCodePath) throws IOException {
        List<TestClassBuilder> testClassList = new ArrayList<>();
        TestClassBuilder testClass= parseFile.startParsing(sourceCodePath);
        testClassList.add(testClass);
        return testClassList;
    }
}
