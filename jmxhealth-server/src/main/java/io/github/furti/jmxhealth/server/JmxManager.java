package io.github.furti.jmxhealth.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.config.RemoteConfig;
import io.github.furti.jmxhealth.server.config.RemoteServer;

@Service
public class JmxManager implements ServletContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(JmxManager.class);

	private static final String CONFIG_KEY = "io.github.furti.jmxhealth.data-location";
	private String dataLocation = null;
	private ObjectMapper objectMapper;
	private List<RemoteConnection> connections;
	private StateManager stateManager;
	private List<RemoteServer> failedConnections;
	private boolean initialized = false;

	@Autowired
	public JmxManager(ObjectMapper objectMapper, StateManager stateManager) {
		this.objectMapper = objectMapper;
		this.stateManager = stateManager;
	}

	@Scheduled(fixedDelay = 10000, initialDelay = 1000)
	public void pollServers() {
		if (!initialized) {
			return;
		}

		this.reconnectFailedConnections();

		if (this.connections == null) {
			return;
		}

		for (RemoteConnection connection : this.connections) {
			try {
				this.stateManager.remoteState(connection.getServerConfig(), connection.poll());
			} catch (Exception ex) {
				LOG.error("Error polling " + connection.getServerConfig(), ex);
				this.stateManager.remoteState(connection.getServerConfig(),
						Arrays.asList(new AttributeState("POLL", HealthState.ALERT,
								HealthUtils.createMessageWithStacktrace("Error polling Remote Server", ex))));
			}
		}
	}

	@PostConstruct
	public void configureRemotes() {
		try {
			Assert.notNull(dataLocation, "Data location must not be null. Set the path to the data file via "
					+ CONFIG_KEY + " System Property or Servlet Config Attribute.");

			RemoteConfig config = objectMapper.readValue(new File(dataLocation + "/jmxhealth.json"),
					RemoteConfig.class);

			this.setupConnections(config);
		} catch (Exception ex) {
			LOG.error("Error setting up JMX Connections", ex);
			this.stateManager.failed("ServerState", "Error setting up JMX Connections", ex);
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

	private void connectToServer(RemoteServer server) throws IOException {
		this.validateServerConfig(server);

		RemoteConnection connection = this.createConnection(server);

		connections.add(connection);
	}

	private void validateServerConfig(RemoteServer server) {
		if (server.getWatchers() == null || server.getWatchers().isEmpty()) {
			throw new IllegalArgumentException("No Watchers are defined for " + server);
		}
	}

	private RemoteConnection createConnection(RemoteServer server) throws IOException {
		LOG.info("Connecting to " + server);

		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi://localhost/jndi/rmi://" + server.getHost() + ":" + server.getPort() + "/jmxrmi");

		JMXConnector connector = JMXConnectorFactory.connect(url, null);
		MBeanServerConnection connection = connector.getMBeanServerConnection();

		return new RemoteConnection(connection, connector, server);
	}

	public void setServletContext(ServletContext servletContext) {
		if (System.getProperty(CONFIG_KEY) != null) {
			dataLocation = System.getProperty(CONFIG_KEY);
		} else {
			dataLocation = servletContext.getInitParameter(CONFIG_KEY);
		}
	}
}
