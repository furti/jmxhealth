package io.github.furti.jmxhealth.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.StateResponse;

public final class HealthUtils {
	public static final String SELF_KEYWOARD = "jmxhealth.self";
	public static final String CONFIG_KEY = "io.github.furti.jmxhealth.data-location";

	private HealthUtils() {

	}

	@SuppressWarnings("unchecked")
	public static <T> T getPropertyFromAttributeValue(Object attributeValue,
			String propertyPath,
			Class<T> type) {
		Collection<Object> propertyNames = parsePropertyPath(propertyPath);

		for (Object propertyName : propertyNames) {
			if (attributeValue == null) {
				throw new IllegalArgumentException("Error getting property " + propertyName
						+ " from atrribute. Path was " + propertyPath + ". Maybe the wrong path?");
			}

			// Extract the actual Value from the table

			if (attributeValue instanceof CompositeDataSupport) {
				attributeValue = ((CompositeDataSupport) attributeValue).get((String) propertyName);
			} else if (attributeValue instanceof TabularDataSupport) {
				TabularDataSupport tab = (TabularDataSupport) attributeValue;

				if (propertyName instanceof Object[]) {
					attributeValue = tab.get(propertyName);
				} else {
					attributeValue = tab.get(new Object[] { propertyName });
				}

				if (attributeValue instanceof CompositeDataSupport) {
					attributeValue = ((CompositeDataSupport) attributeValue).get("value");
				}
			} else if (attributeValue instanceof Map) {
				attributeValue = ((Map<Object, Object>) attributeValue).get(propertyName);
			} else {
				throw new IllegalArgumentException("Error getting property " + propertyName
						+ " from attribute value " + attributeValue + ". Class not supported.");
			}
		}

		return (T) attributeValue;

	}

	public static String createMessageWithStacktrace(String message, Exception ex) {
		StringBuilder builder = new StringBuilder(message);

		builder.append(" - ").append(ex.getMessage()).append("\n");

		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));

		builder.append(writer.toString());

		return builder.toString();
	}

	public static StateResponse toStateResponse(String application,
			String environment,
			String server,
			List<AttributeState> states) {
		Optional<HealthState> overallState = states.stream()
				.map((attributeState) -> attributeState.getState()).max((state1, state2) -> {
					return state1.getWeight() - state2.getWeight();
				});

		if (overallState.get() == HealthState.OK) {
			return new StateResponse(application, environment, server, overallState.get());
		}

		List<AttributeState> unsuccessfulStates = states.stream()
				.filter((attributeState) -> attributeState.getState() != HealthState.OK)
				.collect(Collectors.toList());

		return new StateResponse(application, environment, server, overallState.get(),
				unsuccessfulStates);
	}

	private static Collection<Object> parsePropertyPath(String propertyPath) {
		String[] parts = propertyPath.split("\\.");
		List<Object> path = new ArrayList<>();

		for (String part : parts) {
			// Create a array
			if (part.startsWith("[") && part.endsWith("]")) {
				path.add(part.substring(1, part.length() - 1).split(","));
			} else {
				path.add(part);
			}
		}

		return path;
	}

	public static Map<String, Object> filterAttributes(Map<String, Object> mBeanAttributes,
			Collection<String> requiredAttributeNames) {
		if (mBeanAttributes == null) {
			return new HashMap<>();
		}

		Map<String, Object> filtered = new HashMap<>();

		for (String attributeName : requiredAttributeNames) {
			filtered.put(attributeName, mBeanAttributes.get(attributeName));
		}

		return filtered;
	}

	public static void mergeAttributeState(AttributeState source, AttributeState target) {
		if (source.getState().getWeight() > target.getState().getWeight()) {
			target.setState(source.getState());
		}

		if (source.getMessage() != null) {
			if (target.getMessage() == null) {
				target.setMessage(source.getMessage());
			} else {
				target.setMessage(target.getMessage() + "\n" + source.getMessage());
			}
		}
	}
}
