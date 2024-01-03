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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
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
    public String getHome(Model model){
        return "index";
    }

    @GetMapping(value = "/check-and-add")
    public ResponseEntity<String> checkAndAddDependency(@RequestParam(name = "pathToPom", required = true) String pathToPom) {
        try {
            return new ResponseEntity<>(serviceImpl.checkAndAdd(pathToPom), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/get-test-classes")
    public String getTestClasses(
            @RequestParam(name = "Directory-Path", required = true) String directoryPath,
            Model model) {

        try {
            Map<String, String> classMap = classScannerService.dtoIdentifier(directoryPath);
            model.addAttribute("mapValues", classMap);
            return "map-of-class-paths";
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Error processing request: " + e.getMessage());
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
//            gen.generateTest(selectedKeyList);
            return "map-of-class-paths";
        }  catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred.");
            return "map-of-class-paths";
        }
    }

}




