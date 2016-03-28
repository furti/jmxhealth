package io.github.furti.jmxhealth.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
import io.github.furti.jmxhealth.StateResponse;
import io.github.furti.jmxhealth.server.config.RemoteServer;

@Service
public class StateManager {
	private static final Logger LOG = LoggerFactory.getLogger(StateManager.class);

	private List<AttributeState> selfState;
	private Queue<StateResponse> remoteStates;
	private ObjectMapper objectMapper;

	@Value("${" + HealthUtils.CONFIG_KEY + "}")
	private String dataLocation = null;
	private TypeReference<List<AttributeState>> attributeStateListType = new TypeReference<List<AttributeState>>() {
	};

	@Autowired
	public StateManager(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
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

		StateResponse stateResponse = HealthUtils.toStateResponse(remoteServer.getApplication(),
				remoteServer.getEnvironment(), attributeStates);

		if (stateResponse.getOverallState() != HealthState.OK) {
			try {
				this.writeToDisk(stateResponse);
				this.removeFailedState(
						"DISK_STORAGE-" + stateResponse.getApplication() + "-" + stateResponse.getEnvironment());
			} catch (IOException e) {
				LOG.error("Error writing to disk", e);
				this.failed("DISK_STORAGE-" + stateResponse.getApplication() + "-" + stateResponse.getEnvironment(),
						"Error writing to disk", e);
			}
		}

		remoteStates.add(stateResponse);
	}

	public StateResponse getRemoteState(String application, String environment) throws RemoteStateNotFoundException {
		for (StateResponse remoteState : this.remoteStates) {
			if (remoteState.getApplication().equals(application) && remoteState.getEnvironment().equals(environment)) {
				return remoteState;
			}
		}

		throw new RemoteStateNotFoundException(application, environment);
	}

	private void removeRemoteState(RemoteServer toDelete) {
		Iterator<StateResponse> it = this.remoteStates.iterator();

		while (it.hasNext()) {
			StateResponse state = it.next();

			if (state.getApplication().equals(toDelete.getApplication())
					&& state.getEnvironment().equals(toDelete.getEnvironment())) {
				it.remove();
			}
		}
	}

	private void writeToDisk(StateResponse stateResponse) throws IOException {
		Path path = Paths.get(dataLocation,
				stateResponse.getApplication() + "-" + stateResponse.getEnvironment() + ".json");

		List<AttributeState> currentStates = null;

		if (!Files.exists(path)) {
			Files.createFile(path);
			currentStates = new ArrayList<>();
		} else {
			currentStates = objectMapper.readValue(path.toFile(), attributeStateListType);
		}

		currentStates.addAll(stateResponse.getUnsuccessfulAttributes());
		objectMapper.writeValue(path.toFile(), currentStates);
	}
}
