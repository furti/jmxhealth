package io.github.furti.jmxhealth.server.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.github.furti.jmxhealth.HealthState;

public abstract class ValueValidatorBase<T> implements AttributeValidator {

	@SuppressWarnings("unchecked")
	@Override
	public ValidationResult validate(Object attributeValue, Map<String, Object> validationConfig) {
		Object warnValue = validationConfig.get(WARN_KEY);
		Object alertValue = validationConfig.get(ALERT_KEY);

		String typeMessage = validateType(attributeValue);

		if (typeMessage != null) {
			return new ValidationResult(HealthState.WARN, typeMessage);
		}

		HealthState state = doValidate((T) attributeValue, (T) warnValue, (T) alertValue);

		if (state == HealthState.WARN) {
			return new ValidationResult(state, buildMessage((T) attributeValue, (T) warnValue));
		} else if (state == HealthState.ALERT) {
			return new ValidationResult(state, buildMessage((T) attributeValue, (T) alertValue));
		}

		return new ValidationResult(state);
	}

	protected abstract String validateType(Object attributeValue);

	protected abstract HealthState doValidate(T attributeValue, T warnValue, T alertValue);

	protected abstract String buildMessage(T attributeValue, T expectedValue);

	@Override
	public Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig) {
		return new ArrayList<>();
	}
}
