package io.github.furti.jmxhealth.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.server.config.RemoteServer;

@Service
public class StateManager {

	private List<AttributeState> selfState;
	private Queue<RemoteState> remoteStates;

	public StateManager() {
		super();
		this.selfState = new ArrayList<>();
		this.remoteStates = new ConcurrentLinkedQueue<>();

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

	public void remoteState(RemoteServer remoteServer, List<AttributeState> attributeStates) {
		this.removeRemoteState(remoteServer);
		this.remoteStates
				.offer(new RemoteState(remoteServer.getApplication(), remoteServer.getEnvironment(), attributeStates));
	}

	private void removeRemoteState(RemoteServer toDelete) {
		Iterator<RemoteState> it = this.remoteStates.iterator();

		while (it.hasNext()) {
			RemoteState state = it.next();

			if (state.getApplication().equals(toDelete.getApplication())
					&& state.getEnvironment().equals(toDelete.getEnvironment())) {
				it.remove();
			}
		}
	}

	public RemoteState getRemoteState(String application, String environment) throws RemoteStateNotFoundException {
		for (RemoteState remoteState : this.remoteStates) {
			if (remoteState.getApplication().equals(application) && remoteState.getEnvironment().equals(environment)) {
				return remoteState;
			}
		}

		throw new RemoteStateNotFoundException(application, environment);
	}
}
