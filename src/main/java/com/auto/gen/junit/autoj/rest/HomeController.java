package com.auto.gen.junit.autoj.rest;

import org.springframework.web.bind.annotation.RestController;

import com.auto.gen.junit.autoj.service.ServiceImpl;

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
		return "home";	
	}

	@PostMapping(value="/showCheckPomDependencyForm")
	public String showCheckPomDependencyForm(Model model) {
		return "check-dependency-form";
	}
	
	@PostMapping(value = "/checkPomDependency")
	public String checkPomDependency(@RequestParam String pathToPom, Model model) {
		try {
			pathToPom.replace('\\', '/');
			boolean isDependencyPresent = serviceImpl.isDependencyPresent("org.springframework.boot", "spring-boot-starter-test", pathToPom);
			if(isDependencyPresent) {
				model.addAttribute("isDependencyPresent", "Yes. It is Present");
			} else {
				model.addAttribute("isDependencyPresent", "No! It is not Present. Need to Add");
			}
		} catch (Exception e) {
			model.addAttribute("message", e.getStackTrace().toString());
		}
		return "check-dependency";
	}
	
}
