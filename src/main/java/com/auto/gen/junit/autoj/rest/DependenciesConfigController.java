package com.auto.gen.junit.autoj.rest;

import com.auto.gen.junit.autoj.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Handles the check and addition of Maven dependencies for a project located in the specified directory.
     *
     * @param directoryPath The path to the directory containing the project's POM file.
     * @return A ResponseEntity containing a message indicating the status of the operation.
     * If the operation is successful, it returns a message indicating that all required dependencies are present.
     * If there are missing dependencies, it adds them to the POM file and returns a message indicating the addition.
     * @throws Exception If an error occurs during the operation, an internal server error response is returned.
     */
    @GetMapping(value = "/check-and-add")
    public ResponseEntity<String> checkAndAddDependency(@RequestParam(name = "directory-path", required = true) String directoryPath) {
        try {
            return new ResponseEntity<>(serviceImpl.checkAndAdd(directoryPath), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}




