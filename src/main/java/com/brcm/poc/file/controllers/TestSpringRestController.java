package com.brcm.poc.file.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/bsl/test")
public class TestSpringRestController {
	static int i= 1;
	
	@GetMapping
    public String index() {
		System.out.println(i++);
        return "Hi All";
    }

}
