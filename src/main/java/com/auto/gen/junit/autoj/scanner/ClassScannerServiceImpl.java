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
import java.util.*;

/**
 * Service class for scanning Java classes in a specified package to identify DTOs and entities.
 */
@Service
@Slf4j
public class ClassScannerServiceImpl implements ClassScanner{


    @Override
    public Map<String, String> classIdentifier(String packageName) throws FileNotFoundException {
        Map<String, String> classMap = processDirectory(packageName);
        log.info("Classes:");
        for (Map.Entry<String, String> entry : classMap.entrySet()) {
            log.info("Class name: {}, Path: {}", entry.getKey(), entry.getValue());
        }
        return classMap;
    }

    private Map<String, String> processDirectory(String packageName) throws FileNotFoundException {
        Map<String, String> classMap = new HashMap<>();
        File directory = new File(packageName);

        if (directory.exists() && directory.isDirectory()) {
            processFiles(directory, classMap);
        } else {
            throw new IllegalArgumentException("No directory found with name: " + packageName);
        }

        return classMap;
    }

    private void processFiles(File directory, Map<String, String> classMap) throws FileNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processFiles(file, classMap);
                } else if (file.getName().endsWith(".java")) {
                    CompilationUnit cu = StaticJavaParser.parse(file);
                    if (containsDtoOrEntityOrControllerOrServiceClassAnnotations(cu) || isDto(cu, file)) {
                        classMap.put(file.getName(), file.getAbsolutePath());
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
    @Override
    public boolean isDtoOrEntityClass(CompilationUnit cu) {
        return cu.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .anyMatch(c -> c.isAnnotationPresent("Entity") || c.isAnnotationPresent("Getter") || c.isAnnotationPresent("Data"));
    }

    /**
     * Checks whether a given CompilationUnit contains a class annotated as a Data Transfer Object (DTO) or an Entity.
     *
     * @param cu The CompilationUnit to be checked for DTO or Entity classes.
     * @return {@code true} if the CompilationUnit contains at least one class annotated as an Entity or with specific annotations
     *         indicating DTO, Controller, RestController, or Service; {@code false} otherwise.
     */
    public boolean containsDtoOrEntityOrControllerOrServiceClassAnnotations(CompilationUnit cu) {
        return cu.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .anyMatch(c -> c.isAnnotationPresent("Entity") || c.isAnnotationPresent("Getter")
                        || c.isAnnotationPresent("RestController") || c.isAnnotationPresent("Controller") || c.isAnnotationPresent("Data")
                        || c.isAnnotationPresent("Service"));
    }

    /**
     * Checks if a class is a DTO by examining its fields and getter methods.
     *
     * @param cu   The CompilationUnit representing a Java class.
     * @param file The File object representing the Java source file.
     * @return {@code true} if the class is identified as a DTO, {@code false} otherwise.
     */
    @Override
    public boolean isDto(CompilationUnit cu, File file) {
        Optional<ClassOrInterfaceDeclaration> classOptional = cu.getClassByName(file.getName().replace(".java", ""));
        if (classOptional.isPresent()) {
            ClassOrInterfaceDeclaration clazz = classOptional.get();

            // Get all fields in the class
            for (FieldDeclaration field : clazz.getFields()) {
                String fieldName = field.getVariable(0).getName().asString();

                // Check if there is a getter method for the field or if the class is annotated with @Getter
                if (hasGetterMethod(clazz.getMethods(), fieldName) || cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                        .anyMatch(c -> c.isAnnotationPresent("Getter") || c.isAnnotationPresent("Data"))) {
                    return true;
                }

//                log.info("Field name: " + fieldName);
                System.out.println("Field name: " + fieldName);
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