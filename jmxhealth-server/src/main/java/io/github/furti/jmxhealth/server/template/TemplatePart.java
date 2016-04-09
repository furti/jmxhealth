package io.github.furti.jmxhealth.server.template;

import java.util.Map;

public interface TemplatePart {

	String render(Map<String, Object> context);

	String getBeanAttribute();
}
