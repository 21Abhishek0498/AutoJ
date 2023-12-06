package com.auto.gen.junit.autoj.generator;

import java.io.IOException;
import java.util.List;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;

public interface Generator {
    List<TestClassBuilder> generate(String sourceCodePath) throws IOException;
}
