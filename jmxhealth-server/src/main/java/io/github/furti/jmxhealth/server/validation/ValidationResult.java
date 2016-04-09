package io.github.furti.jmxhealth.server.validation;

import io.github.furti.jmxhealth.HealthState;

public class ValidationResult {
	private HealthState state;
	private String message;

	public ValidationResult(HealthState state, String message) {
		super();
		this.state = state;
		this.message = message;
	}

	public ValidationResult(HealthState state) {
		super();
		this.state = state;
	}

	public HealthState getState() {
		return state;
	}

	public String getMessage() {
		return message;
	}

	public void prefixMessage(String prefix) {
		if (this.message == null) {
			this.message = prefix;
		} else {
			this.message = prefix + ": " + this.message;
		}
	}
}
