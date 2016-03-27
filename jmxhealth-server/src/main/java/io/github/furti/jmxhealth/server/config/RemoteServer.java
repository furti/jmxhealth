package io.github.furti.jmxhealth.server.config;

import java.util.List;

public class RemoteServer {

	private String application;
	private String environment;
	private String host;
	private Integer port;
	private List<Watcher> watchers;

	public List<Watcher> getWatchers() {
		return watchers;
	}

	public void setWatchers(List<Watcher> watchers) {
		this.watchers = watchers;
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "RemoteServer [application=" + application + ", environment=" + environment + "]";
	}
}
