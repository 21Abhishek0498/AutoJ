package com.auto.gen.junit.autoj.scanner;

import java.io.FileNotFoundException;
import java.util.Map;

public interface ClassScanner {
    Map<String, String> dtoIdentifier(String packageName) throws FileNotFoundException, ClassNotFoundException;
}
