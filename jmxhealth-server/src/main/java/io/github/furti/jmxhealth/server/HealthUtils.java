package io.github.furti.jmxhealth.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.openmbean.CompositeDataSupport;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.StateResponse;

public final class HealthUtils {

	private HealthUtils() {

	}

	@SuppressWarnings("unchecked")
	public static <T> T getPropertyFromAttributeValue(Object attributeValue, String propertyName, Class<T> type) {
		if (attributeValue instanceof CompositeDataSupport) {
			return (T) ((CompositeDataSupport) attributeValue).get(propertyName);
		} else {
			throw new IllegalArgumentException("Error getting property " + propertyName + " from attribute value "
					+ attributeValue + ". Class not supported.");
		}
	}

	public static String createMessageWithStacktrace(String message, Exception ex) {
		StringBuilder builder = new StringBuilder(message);

		builder.append(" - ").append(ex.getMessage()).append("\n");

		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));

		builder.append(writer.toString());

		return builder.toString();
	}

	public static StateResponse toStateResponse(List<AttributeState> states) {
		Optional<HealthState> overallState = states.stream().map((attributeState) -> attributeState.getState())
				.max((state1, state2) -> {
					return state1.getWeight() - state2.getWeight();
				});

		if (overallState.get() == HealthState.OK) {
			return new StateResponse(overallState.get());
		}

		List<AttributeState> unsuccessfulStates = states.stream()
				.filter((attributeState) -> attributeState.getState() != HealthState.OK).collect(Collectors.toList());

		return new StateResponse(overallState.get(), unsuccessfulStates);
	}
}
