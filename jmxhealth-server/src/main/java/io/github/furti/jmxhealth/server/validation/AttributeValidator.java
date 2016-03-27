package io.github.furti.jmxhealth.server.validation;

import java.util.Map;

public interface AttributeValidator {
	public static final String WARN_KEY = "warnOn";
	public static final String ALERT_KEY = "alertOn";

	ValidationResult validate(Object attributeValue, Map<String, Object> validationConfig) throws Exception;
}
