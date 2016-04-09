package io.github.furti.jmxhealth.server.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Template {

	private List<TemplatePart> parts;
	private List<String> beanAttributes;

	public Template(List<TemplatePart> parts) {
		super();
		this.parts = parts;
		this.beanAttributes = parseBeanAttributes(parts);
	}

	private List<String> parseBeanAttributes(List<TemplatePart> templateParts) {
		List<String> tmp = new ArrayList<>();

		for (TemplatePart part : templateParts) {
			String beanAttribute = part.getBeanAttribute();

			if (beanAttribute != null) {
				tmp.add(beanAttribute);
			}
		}

		return tmp;
	}

	public String render(Map<String, Object> context) {
		StringBuilder rendered = new StringBuilder();

		for (TemplatePart part : parts) {
			rendered.append(part.render(context));
		}

		return rendered.toString();
	}

	public List<String> getBeanAttributes() {
		return beanAttributes;
	}
}
