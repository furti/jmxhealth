package io.github.furti.jmxhealth.server.config;

import java.util.Map;

import io.github.furti.jmxhealth.server.ValidationType;

public class Check {
	private String displayName;

	/**
	 * Optional name of an attribute to validate. If not set the bean itself
	 * will be validated.
	 */
	private String attributeName;

	/**
	 * Optional template for messages to emit.
	 */
	private String message;

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

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public String toString() {
		return "Check [displayName=" + displayName + "]";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		// Do not override. Checks have to be unique.
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// Do not override. Checks have to be unique.
		return super.equals(obj);
	}
}
