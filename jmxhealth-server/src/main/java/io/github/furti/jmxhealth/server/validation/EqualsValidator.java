package io.github.furti.jmxhealth.server.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import io.github.furti.jmxhealth.HealthState;

public class EqualsValidator implements AttributeValidator {

	private static final String VALUE_KEY = "value";

	public ValidationResult validate(Object attributeValue, Map<String, Object> validationConfig) {
		Object expectedValue = validationConfig.get(VALUE_KEY);
		if (!ObjectUtils.nullSafeEquals(attributeValue, expectedValue)) {
			return new ValidationResult(HealthState.ALERT, this.buildMessage(attributeValue, expectedValue));
		}

		return new ValidationResult(HealthState.OK);
	}

	private String buildMessage(Object attributeValue, Object expectedValue) {
		return "Actual value \"" + attributeValue + "\" and expected value \"" + expectedValue + "\" do not match";
	}

	@Override
	public Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig) {
		return new ArrayList<>();
	}
}
