package com.applicake.beanstalkclient.test;

import java.util.ArrayList;

import com.applicake.beanstalkclient.YamlEntry;
import com.applicake.beanstalkclient.utils.CustomYamlParser;

import android.test.AndroidTestCase;

public class YamlTesting extends AndroidTestCase {

	String example1 = "- - branches/\n" + "  - :add\n" + "- - tags/\n" + "  - :add\n"
			+ "- - trunk/\n" + "  - :add\n";
	
	public void testCustomYamlParser(){
		CustomYamlParser yamlParser = new CustomYamlParser();
		ArrayList<YamlEntry> yamlList = yamlParser.parseEntriesList(example1);
		
		
	}

}
