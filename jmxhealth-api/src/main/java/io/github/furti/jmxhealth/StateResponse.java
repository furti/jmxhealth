package io.github.furti.jmxhealth;

import java.util.List;

public class StateResponse {

	private String application;
	private String environment;
	private String server;
	private HealthState overallState;
	private List<AttributeState> unsuccessfulAttributes;

	public StateResponse(String application, String environment, String server,
			HealthState overallState) {
		this(application, environment, server, overallState, null);
	}

	public StateResponse(String application, String environment, String server,
			HealthState overallState, List<AttributeState> unsuccessfulAttributes) {
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

	public String description() {
		return this.application + "@" + this.server + "-" + this.environment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((server == null) ? 0 : server.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateResponse other = (StateResponse) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
			return false;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		if (server == null) {
			if (other.server != null)
				return false;
		} else if (!server.equals(other.server))
			return false;
		return true;
	}
}
