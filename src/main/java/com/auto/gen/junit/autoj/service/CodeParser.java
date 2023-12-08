package com.auto.gen.junit.autoj.service;

import com.auto.gen.junit.autoj.parser.ParseJavaFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodeParser {

    @Autowired
    private ParseJavaFile parseJavaFile;

    public Map<String, Object> parseCode(String sourcecodePath) throws FileNotFoundException {
        Map<String, Object> result = null;
        try {
            result = parseJavaFiles(sourcecodePath);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonOutput = objectMapper.writeValueAsString(result);
            System.out.println(jsonOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Parses Java files in the specified directory and its subdirectories, extracting relevant information using a custom visitor.
     *
     * @param directoryPath The path of the directory containing Java files to be parsed.
     * @return A {@code Map<String, Object>} containing parsed data for each Java file. The keys are file names, and the values are the parsed data.
     * @throws FileNotFoundException If the specified directory is not found.
     */
    private static Map<String, Object> parseJavaFiles(String directoryPath) throws IOException {
        Map<String, Object> parsedData = new HashMap<>();

        ParseJavaFile parseJavaFile = new ParseJavaFile();
        File directory = new File(directoryPath);
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".java")) {

                parsedData.put(file.getName(), parseJavaFile.startParsing(file));
            } else if (file.isDirectory()) {
                // Recursively process subdirectories and include the result in the parsedData map
                Map<String, Object> subdirectoryData = parseJavaFiles(file.getPath());
                parsedData.put(file.getName(), subdirectoryData);
            }
        }
        // In this method we need to save in MongoDB
        return parsedData;
    }
}







