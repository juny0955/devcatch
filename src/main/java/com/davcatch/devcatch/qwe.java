package com.davcatch.devcatch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcatch.devcatch.schedule.SchedulerRunner;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class qwe {

	private final SchedulerRunner runner;

	@GetMapping("/qwe")
	public String qwe() {
		runner.runCreateNewArticlesScheduler();
		return "";
	}
}
