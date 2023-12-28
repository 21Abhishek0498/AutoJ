package com.auto.gen.junit.autoj.rest;

import com.auto.gen.junit.autoj.service.ServiceImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "/autoj")
public class HomeController {
	
	@Autowired
	ServiceImpl serviceImpl;
	
	@GetMapping(value = "/home")
	public String getHome(Model model) {
		return "home-page";
	}

	@GetMapping(value="/showCheckPomDependencyForm")
	public String showChesckPomDependencyForm(Model model) {
		return "check-dependency-form";
	}
	
	@PostMapping(value = "/checkPomDependency")
	public String checkPomDependency(@RequestParam String pathToPom, Model model) {
		try {
			pathToPom.replace('\\', '/');
			boolean isDependencyPresent = serviceImpl.isDependencyPresent("org.springframework.boot", "spring-boot-starter-test", pathToPom);
			if(isDependencyPresent) {
				model.addAttribute("isDependencyPresent", "Yes. \"spring-boot-starter-test\" dependency is Present");
			} else {
				model.addAttribute("isDependencyPresent", "No! \"spring-boot-starter-test\" dependency is not Present. Need to Add");
			}
			Map<String, String> dependencies = serviceImpl.getJavaVersionAndSpringVersion(pathToPom);
        	model.addAttribute("javaVersion", dependencies.get("Java Version"));
        	model.addAttribute("springBootVersion", dependencies.get("Spring Boot Version"));
        	
		} catch (Exception e) {
			model.addAttribute("message", e.getStackTrace().toString());
		}
		return "check-dependency";
	}
	
}
