package com.auto.gen.junit.autoj;

import com.auto.gen.junit.autoj.dto.MyJunitClass;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.transformer.TransformerProcessor;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutoJApplication implements CommandLineRunner {

	@Autowired
	private SourceCodePathValidator sourceCodePathValidator;

	@Autowired
	private ParseFile parseFile;

	@Autowired
	private TransformerProcessor transformerProcessor;

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
		//validatePath(args);

		TestClassBuilder testClassBuilder = parseFile.startParsing("src/main/java/com/auto/gen/junit/autoj/AutoJApplication.java");
		MyJunitClass junitsClassToBeBuild  = transformerProcessor.transform(testClassBuilder);
		System.out.println(junitsClassToBeBuild);
	}

	private void validatePath(String[] args) {
		if (args !=null && args.length  > 0)
			sourceCodePathValidator.validate(args[0]);
		else{
			System.out.println("Please provide a valid path");
		}
	}
}
