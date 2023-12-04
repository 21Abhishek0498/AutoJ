package com.auto.gen.junit.autoj;

import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

@SpringBootApplication
public class AutoJApplication implements CommandLineRunner {

	@Autowired
	private SourceCodePathValidator sourceCodePathValidator;

	public static void main(String[] args) {
		SpringApplication.run(AutoJApplication.class, args);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	@Override
	public void run(String... args) throws Exception {
		validatePath(args);

	}

	private void validatePath(String[] args) {
		if (args !=null && args.length  > 0)
			sourceCodePathValidator.validate(args[0]);
		else{
			System.out.println("Please provide a valid path");
		}
	}
}
