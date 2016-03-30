package io.github.furti.jmxhealth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class JmxHealthServerApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(JmxHealthServerConfig.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(JmxHealthServerConfig.class, args);
	}

}
