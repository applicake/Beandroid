package com.applicake.beanstalkclient;

public class YamlEntry {
	String value;
	String property;
	//TODO high risk handle bad yaml parsing
	public YamlEntry(String value, String key) {
		this.value = value;
		this.property = key;
	}
	public String getValue() {
		return value;
	}
	public String getProperty() {
		return property;
	}
	
}
