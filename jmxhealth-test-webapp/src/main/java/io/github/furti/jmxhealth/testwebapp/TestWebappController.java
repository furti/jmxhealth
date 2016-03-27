package io.github.furti.jmxhealth.testwebapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestWebappController {
	private static final String TYPE_WARN = "warn";
	private static final String TYPE_ALERT = "alert";

	@RequestMapping(value = "memoryusage/{type}")
	public String increaseMemoryUsage(@PathVariable("type") String type) {
		int percentage = 55;

		if (type.equals(TYPE_ALERT)) {
			percentage = 80;
		}

		List<TestBean> list = new ArrayList<>();

		Runtime.getRuntime().gc();
		long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		while (used < (Runtime.getRuntime().maxMemory() / 100 * percentage)) {
			list.add(new TestBean().fillList());
			used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		}

		try

		{
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			// Left blank
		}

		list.clear();

		return "DONE";
	}
}
