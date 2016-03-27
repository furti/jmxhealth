package io.github.furti.jmxhealth;

import java.time.LocalDateTime;

public class AttributeState {

	private String attributeName;
	private HealthState state;
	private String message;
	private LocalDateTime timestamp;

	public AttributeState(String attributeName, HealthState state) {
		this(attributeName, state, null);
	}

	public AttributeState(String attributeName, HealthState state, String message) {
		super();
		this.state = state;
		this.message = message;
		this.attributeName = attributeName;
		this.timestamp = LocalDateTime.now();
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public HealthState getState() {
		return state;
	}

	public void setState(HealthState state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ValidationResult [state=" + state + ", message=" + message + "]";
	}
}
