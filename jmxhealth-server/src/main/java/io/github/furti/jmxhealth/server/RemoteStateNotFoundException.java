package io.github.furti.jmxhealth.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RemoteStateNotFoundException extends Exception {

	private static final long serialVersionUID = -131416149441240056L;

	public RemoteStateNotFoundException(String application, String environment) {
		super("No State for RemoteServer[ application=" + application + ", environment=" + environment + "] found.");
	}

}
