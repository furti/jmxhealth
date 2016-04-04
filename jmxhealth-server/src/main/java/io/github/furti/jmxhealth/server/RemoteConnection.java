package io.github.furti.jmxhealth.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.config.RemoteServer;
import io.github.furti.jmxhealth.server.config.WatchedAttribute;
import io.github.furti.jmxhealth.server.config.Watcher;
import io.github.furti.jmxhealth.server.validation.ValidationResult;

public class RemoteConnection {
	private JMXConnector connector;
	private RemoteServer serverConfig;

	public RemoteConnection(JMXConnector connector, RemoteServer serverConfig) {
		super();
		this.connector = connector;
		this.serverConfig = serverConfig;
	}

	public MBeanServerConnection getConnection() throws IOException {
		return connector.getMBeanServerConnection();
	}

	public JMXConnector getConnector() {
		return connector;
	}

	public RemoteServer getServerConfig() {
		return serverConfig;
	}

	public List<AttributeState> poll() throws Exception {
		List<AttributeState> result = new ArrayList<>();

		for (Watcher watcher : this.serverConfig.getWatchers()) {
			AttributeList mBeanAttributes = this.getMBeanAttributes(watcher.getBeanName(),
					this.getAttributeNames(watcher.getAttributes()));

			result.addAll(this.validateAttributes(mBeanAttributes, watcher.getAttributes()));
		}

		return result;
	}

	private List<AttributeState> validateAttributes(AttributeList mBeanAttributes,
			Map<String, WatchedAttribute> watchedAttributes) throws Exception {
		List<AttributeState> result = new ArrayList<>();

		for (Attribute mBeanAttribute : mBeanAttributes.asList()) {
			WatchedAttribute watchedAttribute = watchedAttributes.get(mBeanAttribute.getName());

			try {

				ValidationResult validationresult = watchedAttribute.getType().validate(mBeanAttribute.getValue(),
						watchedAttribute.getValidationConfig());

				result.add(new AttributeState(watchedAttribute.getDisplayName(), validationresult.getState(),
						validationresult.getMessage()));
			} catch (Exception ex) {
				result.add(new AttributeState(watchedAttribute.getDisplayName(), HealthState.ALERT,
						HealthUtils.createMessageWithStacktrace("Error validating attribute", ex)));
			}
		}

		return result;
	}

	private AttributeList getMBeanAttributes(String beanName, String[] attributeNames)
			throws InstanceNotFoundException, ReflectionException, IOException, MalformedObjectNameException {
		ObjectName objectName = new ObjectName(beanName);

		AttributeList attributes = getConnection().getAttributes(objectName, attributeNames);

		// Validate the attributes
		List<String> missing = new ArrayList<String>(Arrays.asList(attributeNames));
		for (Attribute a : attributes.asList()) {
			missing.remove(a.getName());
		}

		if (!missing.isEmpty()) {
			throw new RuntimeException(
					"Not all attributes where recieved for bean " + beanName + ". Missing attributes " + missing);
		}

		return attributes;
	}

	private String[] getAttributeNames(Map<String, WatchedAttribute> attributes) {
		return attributes.keySet().toArray(new String[attributes.keySet().size()]);
	}

	public void close() {
		try {
			connector.close();
		} catch (Exception ex) {
			// Nothing to do here for now
		}
	}
}
