package io.github.furti.jmxhealth.server.config;

import java.util.Map;

public class Watcher {
	private String beanName;
	private Map<String, WatchedAttribute> attributes;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Map<String, WatchedAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, WatchedAttribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "Watcher [beanName=" + beanName + "]";
	}
}
