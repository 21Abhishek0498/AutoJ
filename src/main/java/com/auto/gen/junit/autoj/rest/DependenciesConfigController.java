package com.auto.gen.junit.autoj.rest;


import com.auto.gen.junit.autoj.generator.Generator;
import com.auto.gen.junit.autoj.scanner.ClassScannerServiceImpl;
import com.auto.gen.junit.autoj.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DependenciesConfigController {

    @Autowired
    private ServiceImpl serviceImpl;

    @Autowired
    private ClassScannerServiceImpl classScannerService;

    @Autowired
    private Generator gen;


    @GetMapping("/home")
    public String getHome(Model model) {
        return "index";
    }

    @GetMapping(value = "/get-test-classes")
    public String getTestClasses(
            @RequestParam(name = "Directory-Path", required = true) String directoryPath,
            Model model) {
        try {
            Map<String, String> classMap = classScannerService.classIdentifier(directoryPath);
            model.addAttribute("mapValues", classMap);
            return "map-of-class-paths";
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Error processing request: " + e.getMessage());
            model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("mapValues", errorMap);
            return "map-values";
        }
    }

    @PostMapping("/generate-tests")
    public String generateTests(@RequestParam(name = "selectedKeys", required = false) String selectedKeys, Model model) {
        try {
            if (selectedKeys == null || selectedKeys.isEmpty()) {
                model.addAttribute("error", "Please select at least one key.");
                return "map-of-class-paths";
            }
            List<String> selectedKeyList = Arrays.asList(selectedKeys.split(","));
            model.addAttribute("message", gen.actualTestClass(selectedKeyList));
            return "common-message";
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Error processing request: " + e.getMessage());
            model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("mapValues", errorMap);
            return "map-values";
        }
    }


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
    public String checkAndAddDependency(@RequestParam(name = "directory-path", required = true) String directoryPath,Model model) {
        try {
            model.addAttribute("message", serviceImpl.checkAndAdd(directoryPath));
            return "common-message";
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Error processing request: " + e.getMessage());
            model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("mapValues", errorMap);
            return "map-values";
        }
    }

}






