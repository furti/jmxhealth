package io.github.furti.jmxhealth.server.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.template.Template;

public class EqualsValidator implements AttributeValidator {

	private static final String VALUE_KEY = "value";

	@Override
	public ValidationResult validate(Object attributeValue,
			Map<String, Object> validationConfig,
			Template messageTemplate,
			Map<String, Object> renderContext) {
		Object expectedValue = validationConfig.get(VALUE_KEY);
		if (!ObjectUtils.nullSafeEquals(attributeValue, expectedValue)) {
			return new ValidationResult(HealthState.ALERT, this.buildMessage(attributeValue,
					expectedValue,
					messageTemplate,
					renderContext));
		}

		return new ValidationResult(HealthState.OK);
	}

	private String buildMessage(Object attributeValue,
			Object expectedValue,
			Template messageTemplate,
			Map<String, Object> renderContext) {

		if (messageTemplate != null) {
			renderContext.put("actualValue", attributeValue);
			renderContext.put("expectedValue", attributeValue);

			return messageTemplate.render(renderContext);
		}

		return "Actual value \"" + attributeValue + "\" and expected value \"" + expectedValue
				+ "\" do not match";
	}

	@Override
	public Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig) {
		return new ArrayList<>();
	}
}
