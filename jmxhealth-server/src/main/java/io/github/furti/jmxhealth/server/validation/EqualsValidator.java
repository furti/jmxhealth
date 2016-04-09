package io.github.furti.jmxhealth.server.validation;

import org.springframework.util.ObjectUtils;

import io.github.furti.jmxhealth.HealthState;

public class EqualsValidator extends ValueValidatorBase<Object> {

	@Override
	protected String validateType(Object attributeValue) {
		return null;
	}

	@Override
	protected ValidationResult doValidate(Object attributeValue, Object expectedValue) {
		if (!ObjectUtils.nullSafeEquals(attributeValue, expectedValue)) {
			return new ValidationResult(HealthState.ALERT, this.buildMessage(attributeValue, expectedValue));
		}

		return new ValidationResult(HealthState.OK);
	}

	private String buildMessage(Object attributeValue, Object expectedValue) {
		return "Actual value \"" + attributeValue + "\" and expected value \"" + expectedValue + "\" do not match";
	}

}
