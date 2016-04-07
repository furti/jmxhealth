package io.github.furti.jmxhealth.crypt;

public class CryptConsoleApplication {

	public static void main(String[] args) throws Exception {
		String command = args.length > 0 ? args[0] : null;

		if (command == null) {
			System.out.println("A command is required");
			System.out.println("Usage java -jar jmxhealth-crypt.jar <command>");
			printAvailableCommands();
		}

		if ("genkey".equals(command)) {
			System.out.println("-D" + CryptUtils.CRYPT_KEY + "=" + CryptUtils.generateKey());
		} else if ("encrypt".equals(command)) {
			String value = args.length > 1 ? args[1] : null;

			if (value == null) {
				System.out.println("Specify the value to encrypt as second parameter!");
			} else {
				System.out.println(CryptUtils.encrypt(value));
			}
		} else {
			System.out.println("Unknown command " + command);
			printAvailableCommands();
		}
	}

	private static void printAvailableCommands() {
		System.out.println("Available commands:");
		System.out.println("genkey --> Generate a secret key.");
		System.out.println("encrypt <value> --> Encrypts the given value with the key set by the system property "
				+ CryptUtils.CRYPT_KEY);
	}
}
