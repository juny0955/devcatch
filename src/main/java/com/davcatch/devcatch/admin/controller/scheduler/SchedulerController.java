package com.davcatch.devcatch.admin.controller.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davcatch.devcatch.admin.service.schduler.SchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/scheduler")
@Slf4j
public class SchedulerController {

	private final SchedulerService schedulerService;


	@GetMapping(value = {"", "/"})
	public String schedulerPage() {
		return "admin/scheduler";
	}

	/**
	 * 스케줄러 수동 실행
	 * @param type 스케줄러 타입 (article-create, article-send)
	 */
	@GetMapping("/run/{type}")
	public String runScheduler(@PathVariable String type, RedirectAttributes redirectAttributes) {
		log.info("스케줄러 '{}' 수동 실행 요청", type);
		String resultMessage;

		try {
			switch (type) {
				case "article-create":
					resultMessage = schedulerService.runArticleCreationScheduler();
					break;
				case "article-send":
					resultMessage = schedulerService.runArticleNotificationScheduler();
					break;
				default:
					resultMessage = "알 수 없는 스케줄러 타입입니다: " + type;
					log.warn(resultMessage);
			}
		} catch (Exception e) {
			resultMessage = "스케줄러 실행 중 오류가 발생했습니다: " + e.getMessage();
			log.error("스케줄러 '{}' 실행 중 오류 발생: {}", type, e.getMessage(), e);
		}

		redirectAttributes.addFlashAttribute("resultMessage", resultMessage);
		redirectAttributes.addFlashAttribute("executionTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		return "redirect:/admin/scheduler";
	}
}
