package io.github.furti.jmxhealth.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.furti.jmxhealth.AttributeState;
import io.github.furti.jmxhealth.HealthState;
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

	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Collection<StateResponse> filterStates(@RequestBody Collection<ApplicationFilter> filters) {
		Collection<StateResponse> response = new ArrayList<>();

		filters.forEach((filter) -> {
			if (filter.getApplication().equals(HealthUtils.SELF_KEYWOARD)) {
				response.add(HealthUtils.toStateResponse(HealthUtils.SELF_KEYWOARD, "Monitoring", null,
						stateManager.getSelfState()));
			} else {
				try {
					response.addAll(this.stateManager.getRemoteState(filter.getApplication(), filter.getEnvironment()));
				} catch (RemoteStateNotFoundException e) {
					response.add(new StateResponse(filter.getApplication(), filter.getEnvironment(), null,
							HealthState.WARN, Arrays.asList(new AttributeState("State", HealthState.WARN,
									"The application could not be found. Maybe the application is not monitored or you provided the wrong filter data."))));
				}
			}
		});

		return response;
	}

	@RequestMapping(value = "/self", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public StateResponse selfState() {

		return HealthUtils.toStateResponse(HealthUtils.SELF_KEYWOARD, "Monitoring", null, stateManager.getSelfState());
	}

	@RequestMapping(value = "/remotes/environment/{environment}/application/{application}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Collection<StateResponse> environmentAndApplicationState(@PathVariable("environment") String environment,
			@PathVariable("application") String application) throws RemoteStateNotFoundException {

		return this.stateManager.getRemoteState(application, environment);
	}
}
