package io.github.furti.jmxhealth.server.config;

import java.util.List;

public class RemoteConfig {
	private List<RemoteServer> servers;

	public List<RemoteServer> getServers() {
		return servers;
	}

	public void setServers(List<RemoteServer> servers) {
		this.servers = servers;
	}
}
