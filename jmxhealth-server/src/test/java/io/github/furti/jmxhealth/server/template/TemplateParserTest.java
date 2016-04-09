package io.github.furti.jmxhealth.server.template;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TemplateParserTest {
	private TemplateParser parser = new TemplateParser("${", "}");

	@Test(dataProvider = "renderData")
	public void render(String template, Map<String, Object> context, String expected) {
		String actual = parser.parseTemplate(template).render(context);

		assertThat(actual, equalTo(expected));
	}

	@DataProvider
	public Object[][] renderData() {
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> bean = new HashMap<>();
		bean.put("test", "A test string");
		bean.put("other", Integer.valueOf(42));

		context.put("bean", bean);

		return new Object[][] { //
				{ "Simple String", null, "Simple String" }, //
				{ "${bean.test}", null, "null" }, //
				{ "Value is ${bean.test}", null, "Value is null" }, //
				{ "${bean.test} is the value", null, "null is the value" }, //
				{ "Got ${bean.test} as value", null, "Got null as value" }, //
				{ "First value is ${bean.test} and second value is ${bean.other} ", null,
						"First value is null and second value is null " }, //
				{ "${bean.test}", context, "A test string" }, //
				{ "Value is ${bean.test}", context, "Value is A test string" }, //
				{ "${bean.test} is the value", context, "A test string is the value" }, //
				{ "Got ${bean.test} as value", context, "Got A test string as value" }, //
				{ "First value is ${bean.test} and second value is ${bean.other} ", context,
						"First value is A test string and second value is 42 " }, //
				{ "${bean.undefined} shoud be null", context, "null shoud be null" } //
		};
	}
}
