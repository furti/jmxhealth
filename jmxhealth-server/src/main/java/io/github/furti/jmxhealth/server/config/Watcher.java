package io.github.furti.jmxhealth.server.config;

import java.util.List;

public class Watcher {
	private String beanName;
	private List<Check> checks;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public List<Check> getChecks() {
		return checks;
	}

	public void setChecks(List<Check> checks) {
		this.checks = checks;
	}

	@Override
	public String toString() {
		return "Watcher [beanName=" + beanName + "]";
	}

}
