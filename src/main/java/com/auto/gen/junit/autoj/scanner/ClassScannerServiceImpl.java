package com.auto.gen.junit.autoj.scanner;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for scanning Java classes in a specified package to identify DTOs and entities.
 */
@Service
@Slf4j
public class ClassScannerServiceImpl implements ClassScanner{

    /**
     * Identifies and returns the names of DTOs within the specified package.
     *
     * here packageName is file path as directory.
     *
     * @param packageName The name of the package to scan.
     * @return A list of DTO class names.
     * @throws IllegalArgumentException If the specified directory is not found.
     */
    @Override
    public List<String> dtoIdentifier(String packageName) throws FileNotFoundException{
        List<String> classes = processDirectory(packageName);

        log.info("Classes:");
        for (String clazz : classes) {
            log.info(clazz);
        }
        return classes;
    }

    private List<String> processDirectory(String packageName) throws FileNotFoundException{
        List<String> classes = new ArrayList<>();
        File directory = new File(packageName);

        if (directory.exists() && directory.isDirectory()) {
            processFiles(directory, classes);
        } else {
            throw new IllegalArgumentException("No directory found with name: " + packageName);
        }

        return classes;
    }

    private void processFiles(File directory, List<String> classes) throws FileNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processFiles(file, classes);
                } else if (file.getName().endsWith(".java")) {
                    CompilationUnit cu = StaticJavaParser.parse(file);
                    if (isDtoOrEntityClass(cu) || isDto(cu, file)) {
                        classes.add(file.getName());
                    }
                }
            }
        }
    }

    /**
     * Checks if a class or interface is annotated with {@code @Entity} or {@code @Getter}.
     *
     * @param cu The CompilationUnit representing a Java class or interface.
     * @return {@code true} if the class or interface is annotated with {@code @Entity} or {@code @Getter},
     * {@code false} otherwise.
     */
    private boolean isDtoOrEntityClass(CompilationUnit cu) {
        return cu.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .anyMatch(c -> c.isAnnotationPresent("Entity") || c.isAnnotationPresent("Getter"));
    }

    /**
     * Checks if a class is a DTO by examining its fields and getter methods.
     *
     * @param cu   The CompilationUnit representing a Java class.
     * @param file The File object representing the Java source file.
     * @return {@code true} if the class is identified as a DTO, {@code false} otherwise.
     */
    public boolean isDto(CompilationUnit cu, File file) {
        Optional<ClassOrInterfaceDeclaration> classOptional = cu.getClassByName(file.getName().replace(".java", ""));
        if (classOptional.isPresent()) {
            ClassOrInterfaceDeclaration clazz = classOptional.get();

            // Get all fields in the class
            for (FieldDeclaration field : clazz.getFields()) {
                String fieldName = field.getVariable(0).getName().asString();

                // Check if there is a getter method for the field or if the class is annotated with @Getter
                if (hasGetterMethod(clazz.getMethods(), fieldName) || cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                        .anyMatch(c -> c.isAnnotationPresent("Getter"))) {
                    return true;
                }

                log.info("Field name: " + fieldName);
            }

            return false;
        }

        return false; // or throw an exception if the class is not found
    }

    /**
     * Checks if a getter method exists for a given field in a class.
     *
     * @param methods   The list of MethodDeclaration objects representing methods in a class.
     * @param fieldName The name of the field for which to check the getter method.
     * @return {@code true} if a getter method exists, {@code false} otherwise.
     */
    private boolean hasGetterMethod(List<MethodDeclaration> methods, String fieldName) {
        String getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        for (MethodDeclaration method : methods) {
            if (method.getName().asString().equals(getterMethodName) && method.getParameters().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}