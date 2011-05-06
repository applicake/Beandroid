package com.applicake.beanstalkclient.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.XmlParser;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

/* Testing XmlParser class
 * parsing repositories list, users list, changesets
 * mock xml files are stored in project assets/mockxmls
 */

public class XmlParserTests extends InstrumentationTestCase {
	
	private static final String CHANGESET_XML_ADDRESS = "mockxmls/changesets.xml";
	private static final String REPOSITORIES_XML_ADDRESS = "mockxmls/repositories.xml";
	private static final String USERS_XML_ADDRESS = "mockxmls/users.xml";
	
	private XmlParser xmlParser;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		xmlParser = new XmlParser();
		
	}
	
	public void testParseChangesets() throws IOException {
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets().open(CHANGESET_XML_ADDRESS));
		ArrayList<Changeset> changesetList = xmlParser.parseChangesetList(testXml);
		
		Changeset changeset1 = changesetList.get(0);
		assertNotNull("changeset is null", changeset1);
		assertEquals(88998, changeset1.getAccountId());
		assertEquals("Han Solo", changeset1.getAuthor());
		assertNotNull(changeset1.getChangedDirs());
		assertNotNull(changeset1.getChangedFiles());
		assertNotNull(changeset1.getChangedProperties());
		assertEquals("bartek.f+HanSolo@applicake.com", changeset1.getEmail());
		assertEquals("aea1c74c112667bb458957778d016a4a66233110", changeset1.getHashId());
		assertEquals(207784, changeset1.getRepositoryId());
		assertEquals("aea1c74c", changeset1.getRevision());
		assertEquals("2011-05-06T15:00:29+02:00", changeset1.getTime());
		assertEquals(false, changeset1.isTooLarge());
		assertEquals(184072, changeset1.getUserId());
		
		Changeset changeset2 = changesetList.get(1);
		assertNotNull("changeset is null", changeset2);
		assertEquals(88998, changeset2.getAccountId());
		assertEquals("admin", changeset2.getAuthor());
		assertNotNull(changeset2.getChangedDirs());
		assertNotNull(changeset2.getChangedFiles());
		assertNotNull(changeset2.getChangedProperties());
		assertEquals("", changeset2.getEmail());
		assertEquals("", changeset2.getHashId());
		assertEquals(205628, changeset2.getRepositoryId());
		assertEquals("1", changeset2.getRevision());
		assertEquals("2011-04-28T11:32:50+02:00", changeset2.getTime());
		assertEquals(false, changeset2.isTooLarge());
		assertEquals(0, changeset2.getUserId());
		
	}
	
	public void testParseUsers() throws IOException{
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets().open(USERS_XML_ADDRESS));
		ArrayList<User> userList = xmlParser.parseUserList(testXml);
		
	}
	
	public void  testParseRepositories() throws IOException{
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets().open(REPOSITORIES_XML_ADDRESS));
		ArrayList<Repository> repositoryList = xmlParser.parseRepositoryList(testXml);
	}
	
	
	
	// Santa's little helper
	
	// converting InputStream to string
	public static String convertIStoString(InputStream is) {
		BufferedReader BR = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while ((line = BR.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		return sb.toString();
	}
	

}
