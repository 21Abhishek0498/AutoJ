package com.auto.gen.junit.autoj.validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.auto.gen.junit.autoj.exceptions.PathDoesNotExistsException;
import com.auto.gen.junit.autoj.validator.intf.SourceCodePathValidator;

@Service
public class SourcePathValidator implements SourceCodePathValidator {
    /**
     * @param sourceCodePath
     */
    @Override
    public void validate(String sourceCodePath) {
        Path codePath = Paths.get(sourceCodePath);
        if(!Files.exists(codePath))
            throw new PathDoesNotExistsException(sourceCodePath);
    }
}
