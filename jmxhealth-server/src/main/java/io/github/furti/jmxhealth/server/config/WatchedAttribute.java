package io.github.furti.jmxhealth.server.config;

import java.util.Map;

import io.github.furti.jmxhealth.server.ValidationType;

public class WatchedAttribute {
	private String displayName;
	private ValidationType type;
	private Map<String, Object> validationConfig;

	public Map<String, Object> getValidationConfig() {
		return validationConfig;
	}

	public void setValidationConfig(Map<String, Object> validationConfig) {
		this.validationConfig = validationConfig;
	}

	public ValidationType getType() {
		return type;
	}

	public void setType(ValidationType type) {
		this.type = type;
	}

	private Map<String, Object> values;

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "WatchedAttribute [displayName=" + displayName + "]";
	}
}
