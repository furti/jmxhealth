package io.github.furti.jmxhealth.server.validation;

import java.util.Map;

import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.HealthUtils;

public class PercentageValidator implements AttributeValidator {
	private static final String MAX_KEY = "max";
	private static final String ACTUAL_KEY = "actual";

	@Override
	public ValidationResult validate(Object attributeValue, Map<String, Object> validationConfig) {
		Integer warnOn = (Integer) validationConfig.get(WARN_KEY);
		Integer alertOn = (Integer) validationConfig.get(ALERT_KEY);

		Number max = HealthUtils.getPropertyFromAttributeValue(attributeValue, (String) validationConfig.get(MAX_KEY),
				Number.class);
		Number actual = HealthUtils.getPropertyFromAttributeValue(attributeValue,
				(String) validationConfig.get(ACTUAL_KEY), Number.class);
		float percentage = actual.floatValue() / max.floatValue() * 100;

		if (percentage >= alertOn) {
			return new ValidationResult(HealthState.ALERT, this.buildMessage(max, actual, percentage));
		} else if (percentage >= warnOn) {
			return new ValidationResult(HealthState.WARN, this.buildMessage(max, actual, percentage));
		}

		return new ValidationResult(HealthState.OK);
	}

	private String buildMessage(Number max, Number actual, float percentage) {
		return "Percentage: " + percentage + ", Actual: " + (actual.floatValue() / 1024 / 1024) + "MB, Max: "
				+ (max.floatValue() / 1024 / 1024) + "MB";
	}
}
