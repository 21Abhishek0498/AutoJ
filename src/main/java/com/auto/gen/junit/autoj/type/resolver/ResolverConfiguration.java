package com.auto.gen.junit.autoj.type.resolver;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ResolverConfiguration implements Resolver{

    /**
     *
     */
    @Override
    public void setResolver(String sourcePath) throws IOException {
        ClassLoaderTypeSolver classLoaderTypeSolver = new ClassLoaderTypeSolver(ClassLoader.getSystemClassLoader());
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(sourcePath));
       // JarTypeSolver jarTypeSolver = new JarTypeSolver("./");
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        combinedSolver.add(javaParserTypeSolver);
        combinedSolver.add(classLoaderTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        StaticJavaParser
            .getParserConfiguration()
            .setSymbolResolver(symbolSolver);
    }
}
