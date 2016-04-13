package io.github.furti.jmxhealth.server.validation;

import java.util.Collection;
import java.util.Map;

import io.github.furti.jmxhealth.server.template.Template;

public interface AttributeValidator {
	public static final String WARN_KEY = "warnOn";
	public static final String ALERT_KEY = "alertOn";

	ValidationResult validate(Object attributeValue,
			Map<String, Object> validationConfig,
			Template messageTemplate,
			Map<String, Object> renderContext) throws Exception;

	Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig);
}
