package com.davcatch.devcatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping(value = {"/", ""})
	public String index() {
		return "index";
	}
}
