package io.github.furti.jmxhealth.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.validation.ValidationResult;

public class ValidationTypeTest {

	@Test(dataProvider = "validateEqualsData")
	public void validateEquals(Object attributeValue, Map<String, Object> validationConfig, ValidationResult expected)
			throws Exception {
		validate(attributeValue, validationConfig, expected, ValidationType.EQUALS);
	}

	@DataProvider
	public Object[][] validateEqualsData() {
		Map<String, Object> validationConfig = new HashMap<>();
		validationConfig.put("value", "expected");

		return new Object[][] { //
				{ "expected", validationConfig, new ValidationResult(HealthState.OK) }, //
				{ "invalid", validationConfig, new ValidationResult(HealthState.ALERT,
						"Actual value \"invalid\" and expected value \"expected\" do not match") } //
		};
	}

	@Test(dataProvider = "validateLowerThanData")
	public void validateLowerThan(Object attributeValue, Map<String, Object> validationConfig,
			ValidationResult expected) throws Exception {
		validate(attributeValue, validationConfig, expected, ValidationType.LOWER);
	}

	@DataProvider
	public Object[][] validateLowerThanData() {
		Map<String, Object> validationConfig = new HashMap<>();
		validationConfig.put("warnOn", 20);
		validationConfig.put("alertOn", 10);

		return new Object[][] { //
				{ 21, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 30, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 20, validationConfig,
						new ValidationResult(HealthState.WARN, "Actual value \"20\" is less than or equal to \"20\"") }, //
				{ 11, validationConfig,
						new ValidationResult(HealthState.WARN, "Actual value \"11\" is less than or equal to \"20\"") }, //
				{ 10, validationConfig,
						new ValidationResult(HealthState.ALERT,
								"Actual value \"10\" is less than or equal to \"10\"") }, //
				{ 5, validationConfig,
						new ValidationResult(HealthState.ALERT, "Actual value \"5\" is less than or equal to \"10\"") } //
		};
	}

	@Test(dataProvider = "validateGreaterThanData")
	public void validateGreaterThan(Object attributeValue, Map<String, Object> validationConfig,
			ValidationResult expected) throws Exception {
		validate(attributeValue, validationConfig, expected, ValidationType.GREATER);
	}

	@DataProvider
	public Object[][] validateGreaterThanData() {
		Map<String, Object> validationConfig = new HashMap<>();
		validationConfig.put("warnOn", 10);
		validationConfig.put("alertOn", 20);

		return new Object[][] { //
				{ 5, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 9, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 10, validationConfig,
						new ValidationResult(HealthState.WARN,
								"Actual value \"10\" is bigger than or equal to \"10\"") }, //
				{ 19, validationConfig,
						new ValidationResult(HealthState.WARN,
								"Actual value \"19\" is bigger than or equal to \"10\"") }, //
				{ 20, validationConfig,
						new ValidationResult(HealthState.ALERT,
								"Actual value \"20\" is bigger than or equal to \"20\"") }, //
				{ 30, validationConfig, new ValidationResult(HealthState.ALERT,
						"Actual value \"30\" is bigger than or equal to \"20\"") }, //
		};
	}

	private void validate(Object attributeValue, Map<String, Object> validationConfig, ValidationResult expected,
			ValidationType type) throws Exception {
		ValidationResult actual = type.validate(attributeValue, validationConfig);

		assertThat(actual, notNullValue());
		assertThat(actual.getState(), equalTo(expected.getState()));
		assertThat(actual.getMessage(), equalTo(expected.getMessage()));
	}
}
