package com.davcatch.devcatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.davcatch.devcatch.rss.DaangnRssReader;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
public class DevcatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevcatchApplication.class, args);
    }

}
