package io.github.furti.jmxhealth.server.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.HealthUtils;
import io.github.furti.jmxhealth.server.template.Template;

public class PercentageValidator implements AttributeValidator {
	private static final String MAX_KEY = "max";
	private static final String ACTUAL_KEY = "actual";

	@Override
	public ValidationResult validate(Object attributeValue,
			Map<String, Object> validationConfig,
			Template messageTemplate,
			Map<String, Object> renderContext) {
		Integer warnOn = (Integer) validationConfig.get(WARN_KEY);
		Integer alertOn = (Integer) validationConfig.get(ALERT_KEY);

		Number max = HealthUtils.getPropertyFromAttributeValue(attributeValue,
				(String) validationConfig.get(MAX_KEY),
				Number.class);
		Number actual = HealthUtils.getPropertyFromAttributeValue(attributeValue,
				(String) validationConfig.get(ACTUAL_KEY),
				Number.class);
		float percentage = actual.floatValue() / max.floatValue() * 100;

		if (percentage >= alertOn) {
			return new ValidationResult(HealthState.ALERT,
					this.buildMessage(max, actual, percentage, messageTemplate, renderContext));
		} else if (percentage >= warnOn) {
			return new ValidationResult(HealthState.WARN,
					this.buildMessage(max, actual, percentage, messageTemplate, renderContext));
		}

		return new ValidationResult(HealthState.OK);
	}

	private String buildMessage(Number max,
			Number actual,
			float percentage,
			Template messageTemplate,
			Map<String, Object> renderContext) {
		if (messageTemplate != null) {
			renderContext.put("percentage", percentage);
			renderContext.put("actualValue", actual);
			renderContext.put("maxValue", max);

			return messageTemplate.render(renderContext);
		}
		return "Percentage: " + percentage + ", Actual: " + actual.intValue() + ", Max: "
				+ max.intValue();
	}

	@Override
	public Collection<String> getRequiredAttributeNames(Map<String, Object> validationConfig) {
		List<String> names = new ArrayList<>();

		names.add((String) validationConfig.get(MAX_KEY));
		names.add((String) validationConfig.get(ACTUAL_KEY));

		return names;
	}
}
