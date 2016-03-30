package io.github.furti.jmxhealth.testwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class JmxHealthTestWebappApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(JmxHealthTestWebappConfig.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(JmxHealthTestWebappConfig.class, args);
	}

}
