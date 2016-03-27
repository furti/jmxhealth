package io.github.furti.jmxhealth;

import java.util.List;

public class StateResponse {

	private String application;
	private String environment;
	private HealthState overallState;
	private List<AttributeState> unsuccessfulAttributes;

	public StateResponse(String application, String environment, HealthState overallState) {
		this(application, environment, overallState, null);
	}

	public StateResponse(String application, String environment, HealthState overallState,
			List<AttributeState> unsuccessfulAttributes) {
		super();
		this.overallState = overallState;
		this.unsuccessfulAttributes = unsuccessfulAttributes;
		this.application = application;
		this.environment = environment;
	}

	public HealthState getOverallState() {
		return overallState;
	}

	public List<AttributeState> getUnsuccessfulAttributes() {
		return unsuccessfulAttributes;
	}

	public String getApplication() {
		return application;
	}

	public String getEnvironment() {
		return environment;
	}
}
