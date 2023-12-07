package com.auto.gen.junit.autoj.rest;


import com.auto.gen.junit.autoj.constants.Constants;
import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.generator.GeneratorHelper;
import com.auto.gen.junit.autoj.service.CodeParser;
import com.auto.gen.junit.autoj.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/dependency")
public class DependenciesConfigController {

    @Autowired
    private ServiceImpl serviceImpl;

    @Autowired
    private GeneratorHelper codeParser;

    @GetMapping(value = "/pom-path")
    public ResponseEntity<Map<String, String>> getDependencies(@RequestParam(name = "pathToPom", required = true) String pathToPom) {
        try {
            return new ResponseEntity<>(serviceImpl.getJavaVersionAndSpringVersion(pathToPom), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("Error processing POM file: ", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/check-and-add/pom-path")
    public ResponseEntity<String> checkAndAddDependency(@RequestParam(name = "pathToPom", required = true) String pathToPom) {
        try {

            boolean isDependencyPresent = serviceImpl.isDependencyPresent(Constants.GROUP_ID, Constants.ARTIFACT_ID, pathToPom);

            if (isDependencyPresent) {
                return new ResponseEntity<>("Dependency already present in the POM file. No changes made.", HttpStatus.OK);
            } else {
                serviceImpl.addDependencyToPom(Constants.GROUP_ID, Constants.ARTIFACT_ID, Constants.SCOPE_ID, pathToPom);
                serviceImpl.mavenBuild(pathToPom);
                return new ResponseEntity<>("Dependency added to POM file. Maven build triggered.", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/GenerateTestcase/project-path")
    public  ResponseEntity<List<TestClassBuilder>> genrateTestcase(@RequestParam(name = "project-location", required = true) String path) throws IOException {

            var parsedData = codeParser.generate(path);

            for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                String fileName = entry.getKey();
                Object parsedObject = entry.getValue();

                // Now you can do something with the key and value
                System.out.println("File Name: " + fileName);

                if (parsedObject instanceof Map) {
                    // If the value is another map, it means it's a subdirectory
                    System.out.println("Subdirectory Contents:");
                    Map<String, Object> subdirectoryData = (Map<String, Object>) parsedObject;
                    for (Map.Entry<String, Object> subEntry : subdirectoryData.entrySet()) {
                        System.out.println("  " + subEntry.getKey() + ": " + subEntry.getValue());
                    }
                } else {
                    // If the value is not a map, it means it's the parsed data for a file
                    System.out.println("Parsed Data:");
                    System.out.println(parsedObject);
                }

                System.out.println("--------------------------");
            }
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

//        return new ResponseEntity<>(codeParser.generate(path), HttpStatus.OK);
//    }
}




