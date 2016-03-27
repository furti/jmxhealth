package io.github.furti.jmxhealth.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;

@Service
public class StateManager {

	private List<AttributeState> selfState;

	public StateManager() {
		super();
		this.selfState = new ArrayList<>();

		this.selfState.add(new AttributeState("ServerState", HealthState.OK));
	}

	/**
	 * This method is called when a exception occurs that prevents the health
	 * checks to be executed.
	 * 
	 * @param message
	 *            - Message
	 * @param ex
	 *            - Exception that occured
	 */
	public void failed(String attribute, String message, Exception ex) {
		this.removeFailedState(attribute);
		this.selfState.add(
				new AttributeState(attribute, HealthState.ALERT, HealthUtils.createMessageWithStacktrace(message, ex)));
	}

	public void removeFailedState(String attribute) {
		Iterator<AttributeState> it = selfState.iterator();

		while (it.hasNext()) {
			AttributeState state = it.next();

			if (state.getAttributeName().equals(attribute)) {
				it.remove();
			}
		}
	}

	public List<AttributeState> getSelfState() {
		return selfState;
	}
}
