package com.applicake.beanstalkclient.utils;

import java.util.ArrayList;

import com.applicake.beanstalkclient.YamlEntry;

public class CustomYamlParser {

	public CustomYamlParser() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<YamlEntry> parseEntriesList(String yamlString) {

		if (yamlString == "--- []") {
			return new ArrayList<YamlEntry>();
		}
		ArrayList<YamlEntry> entryList = new ArrayList<YamlEntry>();

		String prepared = yamlString.trim().length() > 3 ? yamlString.trim().substring(3)
				: yamlString.trim();

		String[] level1 = prepared.split("- - ");
		for (int i = 0; i < level1.length; i++) {
			String s = level1[i];
			if (!s.trim().equals("") && !s.trim().equals("---")) {
				String[] level2 = s.split("  - :");
				if (level2.length == 2) {
					String value = level2[0].trim();
					String key = level2[1].trim();
					entryList.add(new YamlEntry(value, key));
				}
			}
		}

		return entryList;
	}

}
