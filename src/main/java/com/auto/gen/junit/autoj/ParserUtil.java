package com.auto.gen.junit.autoj;

import com.auto.gen.junit.autoj.dto.ClazImportStatement;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParserUtil {

    public static List<ClazImportStatement> getImportStatementsFromSourceClass(CompilationUnit cu){
        List<ImportDeclaration> importDeclarations = cu.getImports().stream().collect(Collectors.toList());
        return importDeclarations.stream().map(importDeclaration -> importDeclaration.getName().asString())
                .map(e -> ClazImportStatement.builder().importStatement(e).build())
                .collect(Collectors.toList());
    }
}
