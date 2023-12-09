package com.auto.gen.junit.autoj.transformer;

import com.auto.gen.junit.autoj.dto.MyJunitClass;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;

public interface TransformerProcessor {
    MyJunitClass transform(TestClassBuilder testClassBuilder);

}
