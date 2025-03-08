package com.davcatch.devcatch;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcatch.devcatch.service.schedue.SchedulerRunner;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class Test {

	private final SchedulerRunner runner;

	@GetMapping("/")
	public String tet() throws IOException {
		Connection connect = Jsoup.connect("https://tech.kakaopay.com");
		Document document = connect.get();
		return document.text();
	}

	@GetMapping("/a")
	public String tqet() throws IOException {
		runner.runCreateNewArticlesScheduler();
		return "";
	}


}
