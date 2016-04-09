package io.github.furti.jmxhealth.server;

import java.util.Collection;
import java.util.Map;

import io.github.furti.jmxhealth.server.validation.AttributeValidator;
import io.github.furti.jmxhealth.server.validation.EqualsValidator;
import io.github.furti.jmxhealth.server.validation.GreaterThanValidator;
import io.github.furti.jmxhealth.server.validation.LowerThanValidator;
import io.github.furti.jmxhealth.server.validation.PercentageValidator;
import io.github.furti.jmxhealth.server.validation.ValidationResult;

public enum ValidationType {
	PERCENTAGE(new PercentageValidator()), EQUALS(new EqualsValidator()), GREATER(new GreaterThanValidator()), LOWER(
			new LowerThanValidator());

	private AttributeValidator validator;

	private ValidationType(AttributeValidator validator) {
		this.validator = validator;
	}

	public ValidationResult validate(Object attributeValue, Map<String, Object> validationConfig) throws Exception {
		return validator.validate(attributeValue, validationConfig);
	}

	public Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig) {
		return validator.getRequiredAttributeNames(validationConfig);
	}
}
