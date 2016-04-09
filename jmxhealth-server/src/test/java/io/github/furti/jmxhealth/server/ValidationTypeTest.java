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

	@Test(dataProvider = "validateMinData")
	public void validateMin(Object attributeValue, Map<String, Object> validationConfig, ValidationResult expected)
			throws Exception {
		validate(attributeValue, validationConfig, expected, ValidationType.MIN);
	}

	@DataProvider
	public Object[][] validateMinData() {
		Map<String, Object> validationConfig = new HashMap<>();
		validationConfig.put("value", 10);

		return new Object[][] { //
				{ 20, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 10, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 9, validationConfig,
						new ValidationResult(HealthState.ALERT, "Actual value \"9\" is smaller than \"10\"") } //
		};
	}

	@Test(dataProvider = "validateMaxData")
	public void validateMax(Object attributeValue, Map<String, Object> validationConfig, ValidationResult expected)
			throws Exception {
		validate(attributeValue, validationConfig, expected, ValidationType.MAX);
	}

	@DataProvider
	public Object[][] validateMaxData() {
		Map<String, Object> validationConfig = new HashMap<>();
		validationConfig.put("value", 10);

		return new Object[][] { //
				{ 8, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 10, validationConfig, new ValidationResult(HealthState.OK) }, //
				{ 20, validationConfig,
						new ValidationResult(HealthState.ALERT, "Actual value \"20\" is bigger than \"10\"") } //
		};
	}

	private void validate(Object attributeValue, Map<String, Object> validationConfig, ValidationResult expected,
			ValidationType type) throws Exception {
		ValidationResult actual = type.validate(attributeValue, validationConfig);

		assertThat(actual, notNullValue());
		assertThat(actual.getState(), equalTo(expected.getState()));
		assertThat(actual.getMessage(), equalTo(expected.getMessage()));
	}

	private Map<String, Object> toMap(Object... args) {
		Map<String, Object> map = new HashMap<>();

		for (int i = 0; i < args.length; i += 2) {
			map.put((String) args[i], args[i + 1]);
		}

		return map;
	}
}
