package io.github.furti.jmxhealth.server.validation;

import java.util.Map;

import org.springframework.util.ObjectUtils;

import io.github.furti.jmxhealth.HealthState;

public class EqualsValidator implements AttributeValidator {
	private static final String VALUE_KEY = "expectedValue";

	@Override
	public ValidationResult validate(Object attributeValue, Map<String, Object> validationConfig) {
		String expectedValue = (String) validationConfig.get(VALUE_KEY);

		if (!ObjectUtils.nullSafeEquals(attributeValue, expectedValue)) {
			return new ValidationResult(HealthState.ALERT, this.buildMessage(attributeValue, expectedValue));
		}

		return new ValidationResult(HealthState.OK);
	}

	private String buildMessage(Object attributeValue, String expectedValue) {
		return "Actual value " + attributeValue + " and expected value " + expectedValue + " do not match";
	}
}
