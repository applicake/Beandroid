package com.applicake.beanstalkclient.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ColorLabels {
	WHITE(0, "label-white"), RED(1, "label-red"), ORANGE(2, "label-orange"), YELLOW(3,
			"label-yellow"), GREEN(4, "label-green"), BLUE(5, "label-blue"), PINK(6,
			"label-pink"), GREY(7, "label-grey");

	private static final Map<Integer, String> intToString = new HashMap<Integer, String>();
	private static final Map<String, Integer> stringToInt = new HashMap<String, Integer>();

	static {
		for (ColorLabels cl : EnumSet.allOf(ColorLabels.class)) {
			intToString.put(cl.code, cl.label);
			stringToInt.put(cl.label, cl.code);
		}
	}

	private int code;
	private String label;

	ColorLabels(int number, String label) {
		this.label = label;
		this.code = number;
	}

	public String getLabel() {
		return label;
	}

	public int getNumber() {
		return code;
	}

	public static String getLabelFromNumber(int i) {
		return intToString.get(i);
	}

	public static int getNumberFromLabel(String label) {
		return stringToInt.get(label);
	}

}
