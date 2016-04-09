package io.github.furti.jmxhealth.server.template;

import java.util.ArrayList;
import java.util.List;

public class TemplateParser {

	private String expressionStart;
	private String expressionEnd;

	public TemplateParser(String expressionStart, String expressionEnd) {
		super();
		this.expressionStart = expressionStart;
		this.expressionEnd = expressionEnd;
	}

	public Template parseTemplate(String template) {
		int lastIndex = 0, actualIndex = -1;
		List<TemplatePart> parts = new ArrayList<>();

		while ((actualIndex = template.indexOf(expressionStart, lastIndex)) != -1) {
			int expressionEndIndex = template.indexOf(expressionEnd, actualIndex);

			if (expressionEndIndex == -1) {
				throw new IllegalArgumentException(
						"Expression at index " + actualIndex + " not closed in themplate \"" + template + "\"");
			}

			// Add everything between the last and acutal position
			if (actualIndex > lastIndex) {
				parts.add(new LiteralTemplatePart(template.substring(lastIndex, actualIndex)));
			}

			parts.add(new DynamicTemplatePart(
					template.substring(actualIndex + expressionStart.length(), expressionEndIndex)));

			lastIndex = expressionEndIndex + 1;
		}

		// Add everything that is left as literal template
		if (lastIndex <= template.length() - 1) {
			parts.add(new LiteralTemplatePart(template.substring(lastIndex)));
		}

		return new Template(parts);
	}
}
