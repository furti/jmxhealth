package io.github.furti.jmxhealth.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.crypt.CryptUtils;
import io.github.furti.jmxhealth.server.config.RemoteConfig;
import io.github.furti.jmxhealth.server.config.RemoteServer;
import io.github.furti.jmxhealth.server.template.TemplateParser;

@Service
public class JmxManager {
	private static final Logger LOG = LoggerFactory.getLogger(JmxManager.class);

	@Value("${" + HealthUtils.CONFIG_KEY + "}")
	private String dataLocation = null;

	private ObjectMapper objectMapper;
	private List<RemoteConnection> connections;
	private StateManager stateManager;
	private List<RemoteServer> failedConnections;
	private boolean initialized = false;
	private TemplateParser templateParser;

	@Autowired
	public JmxManager(ObjectMapper objectMapper, StateManager stateManager,
			TemplateParser templateParser) {
		this.objectMapper = objectMapper;
		this.stateManager = stateManager;
		this.templateParser = templateParser;
	}

	@Scheduled(fixedDelay = 30000, initialDelay = 1000)
	public void pollServers() {
		if (!initialized) {
			return;
		}

		this.reconnectFailedConnections();

		if (this.connections == null) {
			return;
		}

		Iterator<RemoteConnection> it = this.connections.iterator();

		while (it.hasNext()) {
			RemoteConnection connection = it.next();

			try {
				this.stateManager.remoteState(connection.getServerConfig(), connection.poll());
			} catch (Exception ex) {
				/*
				 * Something went wrong while communicating with the server
				 * Maybe the connection is closed. So close it and reconnect
				 * later.
				 */
				if (ex instanceof IOException) {
					connection.close();
					it.remove();

					if (failedConnections == null) {
						failedConnections = new ArrayList<>();
					}

					this.failedConnections.add(connection.getServerConfig());
				}

				LOG.error("Error polling " + connection.getServerConfig(), ex);
				this.stateManager.remoteState(connection.getServerConfig(),
						Arrays.asList(new AttributeState("Remote Check", HealthState.ALERT, HealthUtils
								.createMessageWithStacktrace("Error polling Remote Server", ex))));
			}
		}
	}

	@PostConstruct
	public void configureRemotes() {
		try {
			Assert.notNull(dataLocation,
					"Data location must not be null. Set the path to the data file via "
							+ HealthUtils.CONFIG_KEY
							+ " System Property or Servlet Config Attribute.");

			RemoteConfig config = objectMapper.readValue(new File(dataLocation + "/jmxhealth.json"),
					RemoteConfig.class);

			this.setupConnections(config);
		} catch (Exception ex) {
			LOG.error("Error setting up JMX Connections", ex);
			this.stateManager.failed("ServerState", "Error setting up JMX Connections", ex);
		}
	}

	@PreDestroy
	public void close() {
		if (this.connections != null) {
			for (RemoteConnection connection : this.connections) {
				connection.close();
			}
		}
	}

	private void setupConnections(RemoteConfig config) {
		if (config.getServers() == null || config.getServers().isEmpty()) {
			return;
		}

		connections = new ArrayList<>(config.getServers().size());

		config.getServers().forEach((server) -> {
			try {
				this.connectToServer(server);
			} catch (Exception e) {
				if (failedConnections == null) {
					failedConnections = new ArrayList<>();
				}

				failedConnections.add(server);

				LOG.error("Error connection to " + server, e);

				this.stateManager.failed(server.toString(), "Error connecting to remote server", e);
			}
		});

		this.initialized = true;
	}

	private void reconnectFailedConnections() {
		if (this.failedConnections == null || this.failedConnections.isEmpty()) {
			return;
		}

		Iterator<RemoteServer> it = this.failedConnections.iterator();

		while (it.hasNext()) {
			RemoteServer server = it.next();
			try {
				this.connectToServer(server);
				it.remove();

				this.stateManager.removeFailedState(server.toString());
			} catch (Exception e) {
				LOG.error("Error connection to " + server, e);

				this.stateManager.failed(server.toString(), "Error connecting to remote server", e);
			}
		}
	}

	private void connectToServer(RemoteServer server) throws Exception {
		this.validateServerConfig(server);

		RemoteConnection connection = this.createConnection(server);

		connections.add(connection);
	}

	private void validateServerConfig(RemoteServer server) {
		if (server.getWatchers() == null || server.getWatchers().isEmpty()) {
			throw new IllegalArgumentException("No Watchers are defined for " + server);
		}
	}

	private RemoteConnection createConnection(RemoteServer server) throws Exception {
		LOG.info("Connecting to " + server);

		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + server.getHost()
				+ ":" + server.getPort() + "/jmxrmi");

		JMXConnector connector = JMXConnectorFactory.connect(url, buildEnv(server));

		return new RemoteConnection(connector, server, templateParser);
	}

	private Map<String, ?> buildEnv(RemoteServer server) throws Exception {
		Map<String, Object> env = null;

		if (server.getPassword() != null && server.getUsername() != null) {
			env = new HashMap<>();

			String[] creds = { server.getUsername(), CryptUtils.decrypt(server.getPassword()) };
			env.put(JMXConnector.CREDENTIALS, creds);
		}

		return env;
	}
}
