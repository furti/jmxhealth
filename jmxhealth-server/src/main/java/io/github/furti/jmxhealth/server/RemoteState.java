package io.github.furti.jmxhealth.server;

import java.util.List;

import io.github.furti.jmxhealth.AttributeState;

public class RemoteState {
	private String application;
	private String environment;
	private List<AttributeState> attributes;

	public RemoteState(String application, String environment, List<AttributeState> attributes) {
		super();
		this.application = application;
		this.environment = environment;
		this.attributes = attributes;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public List<AttributeState> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeState> attributes) {
		this.attributes = attributes;
	}

}
