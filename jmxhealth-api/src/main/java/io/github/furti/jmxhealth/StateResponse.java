package io.github.furti.jmxhealth;

import java.util.List;

public class StateResponse {

	private HealthState overallState;
	private List<AttributeState> unsuccessfulAttributes;

	public StateResponse(HealthState overallState) {
		super();
		this.overallState = overallState;
	}

	public StateResponse(HealthState overallState, List<AttributeState> unsuccessfulAttributes) {
		super();
		this.overallState = overallState;
		this.unsuccessfulAttributes = unsuccessfulAttributes;
	}

	public HealthState getOverallState() {
		return overallState;
	}

	public List<AttributeState> getUnsuccessfulAttributes() {
		return unsuccessfulAttributes;
	}
}
