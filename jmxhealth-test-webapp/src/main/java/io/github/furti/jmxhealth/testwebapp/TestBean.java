package io.github.furti.jmxhealth.testwebapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestBean {

	private List<String> list;

	public TestBean fillList() {
		Random r = new Random();
		list = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			list.add("Test " + r.nextInt());
		}

		return this;
	}
}
