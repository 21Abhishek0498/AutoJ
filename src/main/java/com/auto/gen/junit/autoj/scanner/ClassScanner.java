package com.auto.gen.junit.autoj.scanner;

import java.io.FileNotFoundException;
import java.util.List;

public interface ClassScanner {
    List<String> dtoIdentifier(String packageName) throws FileNotFoundException, ClassNotFoundException;
}
