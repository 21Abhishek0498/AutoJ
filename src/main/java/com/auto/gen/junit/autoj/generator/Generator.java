package com.auto.gen.junit.autoj.generator;

import com.auto.gen.junit.autoj.dto.TestClassBuilder;

import java.io.IOException;
import java.util.List;

public interface Generator {
    List<TestClassBuilder> generate(String sourceCodePath) throws IOException;
}
