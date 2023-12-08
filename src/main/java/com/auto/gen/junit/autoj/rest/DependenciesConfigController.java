package com.auto.gen.junit.autoj.rest;


import com.auto.gen.junit.autoj.dto.TestClassBuilder;
import com.auto.gen.junit.autoj.generator.GeneratorHelper;
import com.auto.gen.junit.autoj.service.CodeParser;
import com.auto.gen.junit.autoj.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    private Environment env;

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
            String groupId = env.getProperty("dependency.group.id");
            String artifactId = env.getProperty("dependency.artifact.id");
            String scopeId = env.getProperty("dependency.scope.id");
            boolean isDependencyPresent = serviceImpl.isDependencyPresent(groupId,artifactId, pathToPom);

            if (isDependencyPresent) {
                return new ResponseEntity<>("Dependency already present in the POM file. No changes made.", HttpStatus.OK);
            } else {
                serviceImpl.addDependencyToPom(groupId,artifactId, scopeId, pathToPom);
                serviceImpl.mavenBuild(pathToPom);
                return new ResponseEntity<>("Dependency added to POM file. Maven build triggered.", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/GenerateTestcase/project-path")
    public ResponseEntity<Map<String, Object>> genrateTestcase(@RequestParam(name = "project-location", required = true) String path) throws IOException {
        return new ResponseEntity<>(codeParser.generate(path), HttpStatus.OK);
    }
}




