package io.github.furti.jmxhealth;

import java.util.List;

public class StateResponse {

	private String application;
	private String environment;
	private String server;
	private HealthState overallState;
	private List<AttributeState> unsuccessfulAttributes;

	public StateResponse(String application, String environment, String server, HealthState overallState) {
		this(application, environment, server, overallState, null);
	}

	public StateResponse(String application, String environment, String server, HealthState overallState,
			List<AttributeState> unsuccessfulAttributes) {
		super();
		this.overallState = overallState;
		this.unsuccessfulAttributes = unsuccessfulAttributes;
		this.application = application;
		this.environment = environment;
		this.server = server;
	}

	public String getServer() {
		return server;
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
