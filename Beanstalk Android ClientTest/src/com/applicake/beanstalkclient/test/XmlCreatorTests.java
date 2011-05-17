package com.applicake.beanstalkclient.test;

import java.io.IOException;

import com.applicake.beanstalkclient.enums.ColorLabels;
import com.applicake.beanstalkclient.utils.XmlCreator;

import android.test.AndroidTestCase;

public class XmlCreatorTests extends AndroidTestCase {

	private XmlCreator xmlCreator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		xmlCreator = new XmlCreator();
	}

	public void testCreateCommentXml() throws IllegalArgumentException, IllegalStateException, IOException {
		String revision = "1";
		String body = "comment 1 body";
		// String filePath = "file/path";
		// int lineNumber = 0;

		String comment1xml = xmlCreator.createCommentXML(revision, body);

		assertEquals("<comment>" + "<revision type=\"integer\">1</revision>"
				+ "<body>comment 1 body</body>"
				+ "<file-path></file-path>"
				+ "<line-number></line-number>" + "</comment>", comment1xml);

	}
	
	public void testCreateGitRepositoryXML(){
		String name = "test-repository";
		String title = "test repository title";
		String colorLabel = ColorLabels.PINK.getLabel();
			
	}
	
	public void testModifyReposiotoryXML(){
		
	}
	
	
}

