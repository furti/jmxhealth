package io.github.furti.jmxhealth.testwebapp;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JmxHealthTestWebappConfig {

	@PostConstruct
	public void init() {
		System.out.println("Initial GC");
		Runtime.getRuntime().gc();
	}
}
