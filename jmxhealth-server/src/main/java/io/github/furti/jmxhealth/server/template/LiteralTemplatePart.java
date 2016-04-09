package io.github.furti.jmxhealth.server.template;

import java.util.Map;

public class LiteralTemplatePart implements TemplatePart {

	private String literal;

	public LiteralTemplatePart(String literal) {
		super();
		this.literal = literal;
	}

	@Override
	public String render(Map<String, Object> context) {
		return literal;
	}

	@Override
	public String getBeanAttribute() {
		return null;
	}
}
