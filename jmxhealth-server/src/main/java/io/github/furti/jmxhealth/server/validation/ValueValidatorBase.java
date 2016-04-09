package io.github.furti.jmxhealth.server.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.github.furti.jmxhealth.HealthState;

public abstract class ValueValidatorBase<T> implements AttributeValidator {
	private static final String VALUE_KEY = "value";

	@SuppressWarnings("unchecked")
	@Override
	public ValidationResult validate(Object attributeValue, Map<String, Object> validationConfig) {
		Object value = validationConfig.get(VALUE_KEY);

		String typeMessage = validateType(attributeValue);

		if (typeMessage != null) {
			return new ValidationResult(HealthState.WARN, typeMessage);
		}

		return doValidate((T) attributeValue, (T) value);
	}

	protected abstract String validateType(Object attributeValue);

	protected abstract ValidationResult doValidate(T attributeValue, T expectedValue);

	@Override
	public Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig) {
		return new ArrayList<>();
	}
}
