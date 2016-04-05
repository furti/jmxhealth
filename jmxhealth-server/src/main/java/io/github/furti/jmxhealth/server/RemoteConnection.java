package io.github.furti.jmxhealth.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import io.github.furti.jmxhealth.server.config.Check;
import io.github.furti.jmxhealth.server.config.RemoteServer;
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
			CheckGroup group = this.groupChecks(watcher);

			AttributeList mBeanAttributes = null;

			if (!group.getAllAttributes().isEmpty()) {
				mBeanAttributes = this.getMBeanAttributes(watcher.getBeanName(),
						this.getAttributeNames(group.getAllAttributes()));
			}

			List<PreparedCheck> checks = this.prepareChecks(group, mBeanAttributes);

			result.addAll(this.validateAttributes(checks));
		}

		return result;
	}

	private List<PreparedCheck> prepareChecks(CheckGroup group, AttributeList mBeanAttributes) {
		List<PreparedCheck> checks = new ArrayList<>();

		if (!group.getSelfValidations().isEmpty()) {
			for (Check check : group.getSelfValidations()) {
				checks.add(
						new PreparedCheck(
								HealthUtils.attributesToMap(mBeanAttributes,
										check.getType().getRequiredAttributeNames(check.getValidationConfig())),
						check));
			}
		}

		if (!group.getAttributeValidations().isEmpty()) {
			for (Attribute mBeanAttribute : mBeanAttributes.asList()) {
				checks.add(new PreparedCheck(mBeanAttribute.getValue(),
						group.getAttributeValidations().get(mBeanAttribute.getName())));
			}
		}

		return checks;
	}

	private CheckGroup groupChecks(Watcher watcher) {
		if (watcher.getChecks() == null) {
			return new CheckGroup();
		}

		CheckGroup group = new CheckGroup();

		for (Check check : watcher.getChecks()) {
			if (check.getAttributeName() != null) {
				group.getAllAttributes().add(check.getAttributeName());
				group.getAttributeValidations().put(check.getAttributeName(), check);
			} else {
				group.getSelfValidations().add(check);
				group.getAllAttributes().addAll(check.getType().getRequiredAttributeNames(check.getValidationConfig()));
			}
		}

		return group;
	}

	private List<AttributeState> validateAttributes(List<PreparedCheck> checks) throws Exception {
		List<AttributeState> result = new ArrayList<>();

		for (PreparedCheck preparedCheck : checks) {
			Check check = preparedCheck.getCheck();

			try {
				ValidationResult validationresult = check.getType().validate(preparedCheck.getBeanToValidate(),
						check.getValidationConfig());

				result.add(new AttributeState(check.getDisplayName(), validationresult.getState(),
						validationresult.getMessage()));
			} catch (Exception ex) {
				result.add(new AttributeState(check.getDisplayName(), HealthState.ALERT,
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

	private String[] getAttributeNames(Collection<String> attributes) {
		return attributes.toArray(new String[attributes.size()]);
	}

	public void close() {
		try {
			connector.close();
		} catch (Exception ex) {
			// Nothing to do here for now
		}
	}

	private static class PreparedCheck {
		private Object beanToValidate;
		private Check check;

		public PreparedCheck(Object beanToValidate, Check check) {
			super();
			this.beanToValidate = beanToValidate;
			this.check = check;
		}

		public Object getBeanToValidate() {
			return beanToValidate;
		}

		public Check getCheck() {
			return check;
		}
	}

	private static class CheckGroup {
		private List<String> allAttributes;
		private Map<String, Check> attributeValidations;
		private List<Check> selfValidations;

		public CheckGroup() {
			this.attributeValidations = new HashMap<>();
			this.selfValidations = new ArrayList<>();
			this.allAttributes = new ArrayList<>();
		}

		public List<String> getAllAttributes() {
			return allAttributes;
		}

		public Map<String, Check> getAttributeValidations() {
			return attributeValidations;
		}

		public List<Check> getSelfValidations() {
			return selfValidations;
		}
	}
}
