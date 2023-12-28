package com.auto.gen.junit.autoj.generator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;

public interface Generator {
    Map<String, Object> generate(String sourceCodePath) throws IOException;
    Map<String, Object> generate(String sourceCodePath,String test) throws IOException;
}
