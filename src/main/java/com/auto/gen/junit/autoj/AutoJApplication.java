package com.auto.gen.junit.autoj;

import com.auto.gen.junit.autoj.dto.MyJunitClass;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.generator.Generator;
import com.auto.gen.junit.autoj.mapper.CommonObjectMapper;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.scanner.ClassScanner;
import com.auto.gen.junit.autoj.transformer.TransformerProcessor;
import com.auto.gen.junit.autoj.translator.TranslationManager;
import com.auto.gen.junit.autoj.type.resolver.StringToClassResolver;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import com.auto.gen.junit.autoj.writer.Writer;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.readAllLines;

@SpringBootApplication
public class AutoJApplication implements CommandLineRunner {

    @Autowired
    private SourceCodePathValidator sourceCodePathValidator;

    @Autowired
    private ParseFile parseFile;

    @Autowired
    private TransformerProcessor transformerProcessor;

    @Autowired
    Generator generator;

    @Autowired
    StringToClassResolver stringToClassResolver;

    @Autowired
    CommonObjectMapper commonObjectMapper;

    @Autowired
    TranslationManager translationManager;

    @Autowired
    Writer classWriter;

    @Autowired
    ClassScanner classScanner;

    public static void main(String[] args) {

        Class clazz = AutoJApplication.class;
        SpringApplication.run(clazz, args);
    }

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        boolean isDtoFlag = false;
        //validatePath(args);
//        Map<String, Object> map = generator.generate("src/main/java/com/auto/gen/junit/autoj/dto");
		Map<String,Object> map = generator.generate("src/main/java/com/auto/gen/junit/autoj/dto/MyJunitClass.java", "test");
        //TestClassBuilder testClassBuilder = parseFile.startParsing(new File("src/main/java/com/auto/gen/junit/autoj/AutoJApplication.java"));


//		List<String> resultList = classScanner.dtoIdentifier("src/main/java/com/auto/gen/junit/autoj");
//		if(!resultList.isEmpty()){
//			return;
//		}
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            TestClassBuilder testClassBuilder = (TestClassBuilder) entry.getValue();
            String packageStr = testClassBuilder.getPackageName().replaceAll("\\.", "/");
//            List<String> classList = classScanner.dtoIdentifier(packageStr);
//            classList.forEach(System.out::println);

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
                classWriter.writeJavaClass(translatedClass, isDtoFlag);
            }
        }
        System.out.println("AutoJ flow completed!");
    }

    private void validatePath(String[] args) {
        if (args != null && args.length > 0)
            sourceCodePathValidator.validate(args[0]);
        else
            System.out.println("Please provide a valid path");
    }
}
