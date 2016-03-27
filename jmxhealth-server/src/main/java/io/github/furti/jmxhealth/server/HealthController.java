package io.github.furti.jmxhealth.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.furti.jmxhealth.StateResponse;

@RestController
@RequestMapping("/states")
public class HealthController {

	private StateManager stateManager;

	@Autowired
	public HealthController(StateManager stateManager) {
		super();
		this.stateManager = stateManager;
	}

	@RequestMapping(value = "/self", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public StateResponse selfState() {

		return HealthUtils.toStateResponse("SELF", null, stateManager.getSelfState());
	}

	@RequestMapping(value = "/remotes/environment/{environment}/application/{application}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public StateResponse environmentAndApplicationState(@PathVariable("environment") String environment,
			@PathVariable("application") String application) throws RemoteStateNotFoundException {

		RemoteState state = this.stateManager.getRemoteState(application, environment);

		return HealthUtils.toStateResponse(state.getApplication(), state.getEnvironment(), state.getAttributes());
	}
}
