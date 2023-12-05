package com.auto.gen.junit.autoj.controller;


import com.auto.gen.junit.autoj.constants.Constants;
import com.auto.gen.junit.autoj.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/dependency")
public class DependenciesConfigController {

    @Autowired
    private ServiceImpl serviceImpl;

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

    @PostMapping(value = "/GenerateTestcase/project-path")
    public ResponseEntity<String> genrateTestcase(@RequestParam(name = "project-location", required = true) String path)
    {

        return new ResponseEntity<>("Testcase generated", HttpStatus.OK);
    }
}




