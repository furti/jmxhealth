package io.github.furti.jmxhealth.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.furti.jmxhealth.server.template.TemplateParser;

@SpringBootApplication
@EnableScheduling
public class JmxHealthServerConfig {

	@Bean
	public TemplateParser templateParser() {
		return new TemplateParser("${", "}");
	}
}
