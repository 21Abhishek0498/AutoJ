package com.auto.gen.junit.autoj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;

@SpringBootApplication
public class AutoJApplication implements CommandLineRunner {

	@Autowired
	private SourceCodePathValidator sourceCodePathValidator;

	@Autowired
	private ParseFile parseFile;

	public static void main(String[] args) {
		SpringApplication.run(AutoJApplication.class, args);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	@Override
	public void run(String... args) throws Exception {
		//validatePath(args);

		TestClassBuilder testClassBuilder = parseFile.startParsing("src/main/java/com/auto/gen/junit/autoj/AutoJApplication.java");
		System.out.println(testClassBuilder.toString());
	}

	private void validatePath(String[] args) {
		if (args !=null && args.length  > 0)
			sourceCodePathValidator.validate(args[0]);
		else{
			System.out.println("Please provide a valid path");
		}
	}
}
