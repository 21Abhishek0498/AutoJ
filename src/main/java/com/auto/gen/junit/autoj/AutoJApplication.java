package com.auto.gen.junit.autoj;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.parser.ParseFile;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutoJApplication {

	@Autowired
	private SourceCodePathValidator sourceCodePathValidator;

	@Autowired
	private ParseFile parseFile;

	public static void main(String[] args) {
		SpringApplication.run(AutoJApplication.class, args);
	}


}
