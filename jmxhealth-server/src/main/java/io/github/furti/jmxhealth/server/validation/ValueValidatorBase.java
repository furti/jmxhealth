package io.github.furti.jmxhealth.server.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.template.Template;

public abstract class ValueValidatorBase<T> implements AttributeValidator {

	@SuppressWarnings("unchecked")
	@Override
	public ValidationResult validate(Object attributeValue,
			Map<String, Object> validationConfig,
			Template messageTemplate,
			Map<String, Object> renderContext) {
		Object warnValue = validationConfig.get(WARN_KEY);
		Object alertValue = validationConfig.get(ALERT_KEY);

		String typeMessage = validateType(attributeValue);

		if (typeMessage != null) {
			return new ValidationResult(HealthState.WARN, typeMessage);
		}

		HealthState state = doValidate((T) attributeValue, (T) warnValue, (T) alertValue);

		if (state == HealthState.WARN) {
			return new ValidationResult(state, buildMessage((T) attributeValue,
					(T) warnValue,
					messageTemplate,
					renderContext));
		} else if (state == HealthState.ALERT) {
			return new ValidationResult(state, buildMessage((T) attributeValue,
					(T) alertValue,
					messageTemplate,
					renderContext));
		}

		return new ValidationResult(state);
	}

	private String buildMessage(T attributeValue,
			T expectedValue,
			Template messageTemplate,
			Map<String, Object> renderContext) {
		if (messageTemplate != null) {
			renderContext.put("expectedValue", expectedValue);
			renderContext.put("actualValue", attributeValue);

			return messageTemplate.render(renderContext);
		}

		return this.buildDefaultMessage(attributeValue, expectedValue);
	}

	protected abstract String validateType(Object attributeValue);

	protected abstract HealthState doValidate(T attributeValue, T warnValue, T alertValue);

	protected abstract String buildDefaultMessage(T attributeValue, T expectedValue);

	@Override
	public Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig) {
		return new ArrayList<>();
	}
}
