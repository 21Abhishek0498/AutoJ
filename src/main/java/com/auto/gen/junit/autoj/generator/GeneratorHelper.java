package com.auto.gen.junit.autoj.generator;

import com.auto.gen.junit.autoj.dto.MyJunitClass;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.mapper.CommonObjectMapper;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.scanner.ClassScanner;
import com.auto.gen.junit.autoj.transformer.TransformerProcessor;
import com.auto.gen.junit.autoj.translator.TranslationManager;
import com.auto.gen.junit.autoj.type.resolver.Resolver;
import com.auto.gen.junit.autoj.type.resolver.StringToClassResolver;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import com.auto.gen.junit.autoj.writer.Writer;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
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

    @Autowired
    private TransformerProcessor transformerProcessor;


    @Autowired
    CommonObjectMapper commonObjectMapper;

    @Autowired
    TranslationManager translationManager;

    @Autowired
    Writer classWriter;

    @Autowired
    ClassScanner classScanner;


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

    @Override
    public void actualTestClass(List<String> classPath) throws Exception {
        boolean isDtoFlag = false;
        for (String line : classPath) {
            Map<String, Object> map = generate(line, "test");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                TestClassBuilder testClassBuilder = (TestClassBuilder) entry.getValue();
                String packageStr = testClassBuilder.getPackageName().replaceAll("\\.", "/");


                File file = new File(testClassBuilder.getClassDirectoryPath());
                CompilationUnit cu = StaticJavaParser.parse(file);

                if (file.getName().endsWith(".java")) {
                    if (classScanner.isDtoOrEntityClass(cu) || classScanner.isDto(cu, file)) {
                        isDtoFlag = true;
                    }
                }

                MyJunitClass junitsClassToBeBuild = transformerProcessor.transform((TestClassBuilder) entry.getValue());
                commonObjectMapper.toJsonString(junitsClassToBeBuild);
                System.out.println("TRANSFORMED JSON ==== " + commonObjectMapper.toJsonString(junitsClassToBeBuild));
                MyJunitClass translatedClass = translationManager.startTranslation(junitsClassToBeBuild, isDtoFlag);
                if (ObjectUtils.isNotEmpty(translatedClass)) {
                    System.out.println("TRANSLATED JSON ==== " + commonObjectMapper.toJsonString(translatedClass));
                    classWriter.writeJavaClass(translatedClass, isDtoFlag, line);
                }
            }
            System.out.println("AutoJ flow completed!");
        }
    }

}

