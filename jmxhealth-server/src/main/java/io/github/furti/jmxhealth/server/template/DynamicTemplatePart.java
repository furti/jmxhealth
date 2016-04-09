package io.github.furti.jmxhealth.server.template;

import java.util.Map;

import io.github.furti.jmxhealth.server.HealthUtils;

public class DynamicTemplatePart implements TemplatePart {
	private String contextElementName;
	private String propertyPath;

	public DynamicTemplatePart(String expression) {
		parseExpression(expression);
	}

	@Override
	public String render(Map<String, Object> context) {
		Object element = context != null ? context.get(contextElementName) : null;

		if (element == null) {
			return null;
		}

		Object property = HealthUtils.getPropertyFromAttributeValue(element,
				propertyPath,
				Object.class);

		return property != null ? property.toString() : null;
	}

	private void parseExpression(String expression) {
		int firstPoint = expression.indexOf(".");

		if (firstPoint > -1) {
			this.contextElementName = expression.substring(0, firstPoint);
			this.propertyPath = expression.substring(firstPoint + 1);
		}

		if (!"bean".equals(this.contextElementName)) {
			throw new IllegalArgumentException("Maleformed expression \"" + expression
					+ "\". Expressions must start with \"bean.\"");
		}
	}

	@Override
	public String getBeanAttribute() {
		return propertyPath;
	}
}
