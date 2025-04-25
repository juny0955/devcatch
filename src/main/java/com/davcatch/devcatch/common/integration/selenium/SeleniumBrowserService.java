package com.davcatch.devcatch.common.integration.selenium;

import java.io.StringReader;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.domain.source.Source;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SeleniumBrowserService {

	private WebDriver createWebDriver() throws CustomException {
		try {
			ChromeOptions options = new ChromeOptions();
			options.setBinary("/usr/bin/headless-shell");

			options.addArguments("--headless=new");
			options.addArguments("--disable-gpu");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");

			options.addArguments("--user-data-dir=/tmp/chrome-user-data-" + System.currentTimeMillis());

			options.addArguments("--disable-extensions");
			options.addArguments("--disable-popup-blocking");
			options.addArguments("--disable-blink-features=AutomationControlled");
			options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
			options.setExperimentalOption("useAutomationExtension", false);

			WebDriver webDriver = new ChromeDriver(options);

			// 자동화 감지 방지 스크립트
			((JavascriptExecutor) webDriver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

			log.info("셀레니움 헤드리스 브라우저 초기화 성공");
			return webDriver;
		} catch (Exception e) {
			log.error("셀레니움 헤드리스 브라우저 초기화 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.SERVER_ERROR);
		}
	}

	public void destroyWebDriver(WebDriver webDriver) {
		if (webDriver != null) {
			try {
				webDriver.quit();
				log.info("셀레니움 헤드리스 브라우저 정상 종료");
			} catch (Exception e) {
				log.error("셀레니움 헤드리스 브라우저 종료 중 오류: {}", e.getMessage());
			}
		}
	}

	public Optional<SyndFeed> reader(Source source) {

		log.debug("[{}] 셀레니움 헤드리스 RSS FEED 수집 시작", source.getName());

		WebDriver webDriver = null;
		SyndFeed feed = null;
		try {
			webDriver = createWebDriver();

			// 페이지 접근
			webDriver.get(source.getFeedUrl());

			// 페이지 로딩 대기
			WebDriverWait wait = new WebDriverWait(webDriver, java.time.Duration.ofSeconds(10));
			wait.until(d -> ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete"));

			// Cloudflare 우회 대기
			Thread.sleep(3000);

			// 페이지 소스 가져오기
			String pageSource = webDriver.getPageSource();

			if (pageSource == null || pageSource.isBlank()) {
				log.warn("[{}] 페이지 소스를 가져올 수 없습니다", source.getName());
				return Optional.empty();
			}

			// XML 파싱
			SyndFeedInput syndFeedInput = new SyndFeedInput();
			syndFeedInput.setAllowDoctypes(true);
			syndFeedInput.setPreserveWireFeed(true);
			syndFeedInput.setXmlHealerOn(true); // XML 문법 오류 자동 복구 활성화

			String parse = Jsoup.parse(pageSource).body().text();

			feed = syndFeedInput.build(new StringReader(parse));
		} catch (Exception e) {
			log.error("[{}] RSS FEED 수집중 오류 발생 : {}", source.getName(), e.getMessage());
		} finally {
			destroyWebDriver(webDriver);
		}

		return Optional.ofNullable(feed);
	}
}
