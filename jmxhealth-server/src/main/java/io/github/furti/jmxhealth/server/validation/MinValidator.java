package io.github.furti.jmxhealth.server.validation;

import io.github.furti.jmxhealth.HealthState;

public class MinValidator extends ValueValidatorBase<Number> {

	@Override
	protected String validateType(Object attributeValue) {
		if (!(attributeValue instanceof Number)) {
			return "Cant't validate " + attributeValue + ". Not an instance of Number";
		}

		return null;
	}

	@Override
	protected ValidationResult doValidate(Number attributeValue, Number expectedValue) {
		if (attributeValue.floatValue() < expectedValue.floatValue()) {
			return new ValidationResult(HealthState.ALERT, this.buildMessage(attributeValue, expectedValue));
		}

		return new ValidationResult(HealthState.OK);
	}

	private String buildMessage(Object attributeValue, Object expectedValue) {
		return "Actual value \"" + attributeValue + "\" is smaller than \"" + expectedValue + "\"";
	}
}
