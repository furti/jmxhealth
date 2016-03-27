package io.github.furti.jmxhealth;

public enum HealthState {

	OK(10), WARN(20), ALERT(30);

	private Integer weight;

	private HealthState(Integer weight) {
		this.weight = weight;
	}

	public Integer getWeight() {
		return weight;
	}

}
