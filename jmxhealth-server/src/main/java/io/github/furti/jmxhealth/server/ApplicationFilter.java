package io.github.furti.jmxhealth.server;

/**
 * Request sent by the client to the /states url to select the applications the
 * client is interested in.
 * 
 * @author Daniel
 *
 */
public class ApplicationFilter {

	private String application;
	private String environment;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
}
