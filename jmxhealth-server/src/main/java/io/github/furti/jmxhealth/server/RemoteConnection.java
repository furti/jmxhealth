package io.github.furti.jmxhealth.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import io.github.furti.jmxhealth.server.template.Template;
import io.github.furti.jmxhealth.server.template.TemplateParser;
import io.github.furti.jmxhealth.server.validation.ValidationResult;

public class RemoteConnection {
	private JMXConnector connector;
	private RemoteServer serverConfig;
	private Map<Check, Template> checkTemplates;
	private TemplateParser templatParser;

	public RemoteConnection(JMXConnector connector, RemoteServer serverConfig,
			TemplateParser templateParser) {
		super();
		this.connector = connector;
		this.serverConfig = serverConfig;
		this.checkTemplates = new HashMap<>();
		this.templatParser = templateParser;
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
			Template messagePrefixTemplate = null;

			if (watcher.getBeanName() != null) {
				result.addAll(this
						.queryAndValidate(group,
								new ObjectName(watcher.getBeanName()),
								messagePrefixTemplate)
						.values());
			} else if (watcher.getBeanQuery() != null) {
				Collection<ObjectName> beanNames = getConnection()
						.queryNames(new ObjectName(watcher.getBeanQuery()), null);

				if (beanNames.isEmpty()) {
					throw new RuntimeException(
							"No Beans where found for query " + watcher.getBeanQuery());
				}

				Map<Check, AttributeState> statesByCheck = new HashMap<>();

				for (ObjectName beanName : beanNames) {
					Map<Check, AttributeState> validateResult = this.queryAndValidate(group,
							beanName,
							messagePrefixTemplate);

					// Merge the result of all beans into a single entry per
					// attribute
					for (Entry<Check, AttributeState> entry : validateResult.entrySet()) {
						if (!statesByCheck.containsKey(entry.getKey())) {
							statesByCheck.put(entry.getKey(), entry.getValue());
						} else {
							HealthUtils.mergeAttributeState(entry.getValue(),
									statesByCheck.get(entry.getKey()));
						}
					}
				}

				result.addAll(statesByCheck.values());
			} else {
				throw new IllegalArgumentException(
						"One of beanName or beanQuery must be set. " + this.serverConfig);
			}
		}

		return result;
	}

	private Template getTemplate(Check check, String messageTemplate) {
		if (!checkTemplates.containsKey(check)) {
			checkTemplates.put(check, this.templatParser.parseTemplate(messageTemplate));
		}

		return checkTemplates.get(check);
	}

	private Map<Check, AttributeState> queryAndValidate(CheckGroup group,
			ObjectName beanName,
			Template messagePrefixTemplate) throws Exception {
		Map<String, Object> mBeanAttributes = null;

		if (!group.getAllAttributes().isEmpty()) {
			mBeanAttributes = this.getMBeanAttributes(beanName,
					this.getAttributeNames(group.getAllAttributes()));
		}

		List<PreparedCheck> checks = this.prepareChecks(group, mBeanAttributes);

		return this.validateAttributes(checks, mBeanAttributes);
	}

	private List<PreparedCheck> prepareChecks(CheckGroup group,
			Map<String, Object> mBeanAttributes) {
		List<PreparedCheck> checks = new ArrayList<>();

		if (!group.getSelfValidations().isEmpty()) {
			for (Check check : group.getSelfValidations()) {
				checks.add(new PreparedCheck(
						HealthUtils.filterAttributes(mBeanAttributes,
								check.getType()
										.getRequiredAttributeNames(check.getValidationConfig())),
						check));
			}
		}

		if (!group.getAttributeValidations().isEmpty()) {
			for (Entry<String, Check> entry : group.getAttributeValidations().entrySet()) {
				checks.add(
						new PreparedCheck(mBeanAttributes.get(entry.getKey()), entry.getValue()));
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
				group.getAllAttributes().addAll(
						check.getType().getRequiredAttributeNames(check.getValidationConfig()));
			}

			if (check.getMessage() != null) {
				Template template = this.getTemplate(check, check.getMessage());

				group.getAllAttributes().addAll(template.getBeanAttributes());
			}
		}

		return group;
	}

	private Map<Check, AttributeState> validateAttributes(List<PreparedCheck> checks,
			Map<String, Object> mBeanAttributes) throws Exception {
		Map<Check, AttributeState> result = new HashMap<>();
		Map<String, Object> templateContext = new HashMap<>();
		templateContext.put("bean", mBeanAttributes);

		for (PreparedCheck preparedCheck : checks) {
			Check check = preparedCheck.getCheck();
			Template messageTemplate = null;

			if (check.getMessage() != null) {
				messageTemplate = this.getTemplate(check, check.getMessage());
			}

			try {
				ValidationResult validationresult = check.getType().validate(
						preparedCheck.getBeanToValidate(),
						check.getValidationConfig(),
						messageTemplate,
						templateContext);

				result.put(check,
						new AttributeState(check.getDisplayName(), validationresult.getState(),
								validationresult.getMessage()));
			} catch (Exception ex) {
				result.put(check,
						new AttributeState(check.getDisplayName(), HealthState.ALERT, HealthUtils
								.createMessageWithStacktrace("Error validating attribute", ex)));
			}
		}

		return result;
	}

	private Map<String, Object> getMBeanAttributes(ObjectName objectName, String[] attributeNames)
			throws InstanceNotFoundException, ReflectionException, IOException,
			MalformedObjectNameException {
		AttributeList attributes = getConnection().getAttributes(objectName, attributeNames);
		Map<String, Object> attributeMap = new HashMap<>();

		// Validate the attributes and add them to the map
		List<String> missing = new ArrayList<String>(Arrays.asList(attributeNames));
		for (Attribute a : attributes.asList()) {
			missing.remove(a.getName());
			attributeMap.put(a.getName(), a.getValue());
		}

		if (!missing.isEmpty()) {
			throw new RuntimeException("Not all attributes where recieved for bean " + objectName
					+ ". Missing attributes " + missing);
		}

		return attributeMap;
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
