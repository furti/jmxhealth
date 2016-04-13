package io.github.furti.jmxhealth.server.validation;

import io.github.furti.jmxhealth.HealthState;

public class GreaterThanValidator extends ValueValidatorBase<Number> {

	@Override
	protected String validateType(Object attributeValue) {
		if (!(attributeValue instanceof Number)) {
			return "Cant't validate " + attributeValue + ". Not an instance of Number";
		}

		return null;
	}

	@Override
	protected HealthState doValidate(Number attributeValue, Number warn, Number alert) {
		if (attributeValue.floatValue() >= alert.floatValue()) {
			return HealthState.ALERT;
		}

		if (attributeValue.floatValue() >= warn.floatValue()) {
			return HealthState.WARN;
		}

		return HealthState.OK;
	}

	@Override
	protected String buildDefaultMessage(Number attributeValue, Number expectedValue) {
		return "Actual value \"" + attributeValue + "\" is bigger than or equal to \"" + expectedValue + "\"";
	}
}
