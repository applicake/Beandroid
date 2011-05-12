package com.applicake.beanstalkclient.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.applicake.beanstalkclient.Account;
import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.Comment;
import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.XmlParser;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.DateInTimezoneConverter;

import android.test.InstrumentationTestCase;
import android.widget.ToggleButton;

/* Testing XmlParser class
 * parsing repositories list, users list, changesets
 * mock xml files are stored in project assets/mockxmls
 */

public class XmlParserTests extends InstrumentationTestCase {

	// valid xmls
	private static final String CHANGESET_XML_ADDRESS = "mockxmls/changesets.xml";
	private static final String REPOSITORIES_XML_ADDRESS = "mockxmls/repositories.xml";
	private static final String USERS_XML_ADDRESS = "mockxmls/users.xml";
	private static final String PERMISSIONS_XML_ADDRESS_1 = "mockxmls/permissions/185254.xml";
	private static final String PERMISSIONS_XML_ADDRESS_2 = "mockxmls/permissions/185174.xml";
	private static final String ACCOUNT_XML_ADDRESS = "mockxmls/account.xml";
	private static final String COMMENTS_XML_ADDRESS = "mockxmls/comments.xml";
	private static final String COMMENTS_XML_ADDRESS_PARTIAL_CHECK = "mockxmls/comments2.xml";

	// invalid xmls
	private static final String INVALID_REPOSITORIES_XML_ADDRESS_NFE = "mockxmls/corrupted_nfe_repositories.xml";
	private static final String INVALID_REPOSITORIES_XML_ADDRESS_DATEFORMAT = "mockxmls/corrupted_dateformat_repositories.xml";
	private static final String INVALID_REPOSITORIES_XML_ADDRESS_XMLSTRUCUTRE = "mockxmls/corrupted_xmlstructure_repositories.xml";

	private XmlParser xmlParser;
	private TimeZone defaultTimeZone;
	private Calendar calendar;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		xmlParser = new XmlParser();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		calendar = Calendar.getInstance();

	}

	public void testParseChangesets() throws IOException, ParserConfigurationException,
			SAXException {
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(CHANGESET_XML_ADDRESS));
		ArrayList<Changeset> changesetList = xmlParser.parseChangesetList(testXml);

		Changeset changeset1 = changesetList.get(0);
		assertNotNull("changeset is null", changeset1);
		assertEquals(88998, changeset1.getAccountId());
		assertEquals("Han Solo", changeset1.getAuthor());
		//testing moved to YamlTesting
//		assertEquals("--- []", changeset1.getChangedDirs());
//		assertEquals("--- - - README  - :add", changeset1.getChangedFiles());
//		assertEquals("", changeset1.getChangedProperties());
		assertEquals("bartek.f+HanSolo@applicake.com", changeset1.getEmail());
		assertEquals("aea1c74c112667bb458957778d016a4a66233110", changeset1.getHashId());
		assertEquals(207784, changeset1.getRepositoryId());
		assertEquals("aea1c74c", changeset1.getRevision());
		calendar.set(2011, 4, 06, 13, 00, 29);
		assertEquals(new Date(111, 4, 06, 13, 00, 29), changeset1.getTime());
		// assertEquals("2011-05-06T15:00:29+02:00", changeset1.getTime());
		assertEquals(false, changeset1.isTooLarge());
		assertEquals(184072, changeset1.getUserId());

		Changeset changeset2 = changesetList.get(1);
		assertNotNull("changeset is null", changeset2);
		assertEquals(88998, changeset2.getAccountId());
		assertEquals("admin", changeset2.getAuthor());
		//testing moved to YamlTesting
//		assertEquals("--- - - branches/  - :add- - tags/  - :add- - trunk/  - :add",
//				changeset2.getChangedDirs());
//		assertEquals("--- []", changeset2.getChangedFiles());
//		assertEquals("", changeset2.getChangedProperties());
		assertEquals("", changeset2.getEmail());
		assertEquals("", changeset2.getHashId());
		assertEquals(205628, changeset2.getRepositoryId());
		assertEquals("1", changeset2.getRevision());
		assertEquals(new Date(111, 3, 28, 9, 32, 50), changeset2.getTime());
		// assertEquals("2011-04-28T11:32:50+02:00", changeset2.getTime());
		assertEquals(false, changeset2.isTooLarge());
		assertEquals(0, changeset2.getUserId());

	}

	public void testParseUsers() throws IOException, ParserConfigurationException,
			SAXException {
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(USERS_XML_ADDRESS));
		ArrayList<User> userList = xmlParser.parseUserList(testXml);

		User user1 = userList.get(0);
		assertNotNull("user1 is null", user1);
		assertEquals(88998, user1.getAccountId());
		assertEquals(UserType.OWNER, user1.getAdmin());
		// assertEquals("2011-04-26T09:29:27+02:00", user1.getCreatedAt());
		assertEquals(new Date(111, 3, 26, 7, 29, 27), user1.getCreatedAt());
		assertEquals("bartek.f@applicake.com", user1.getEmail());
		assertEquals("Bartosz", user1.getFirstName());
		assertEquals(181892, user1.getId());
		assertEquals("Filipowicz", user1.getLastName());
		assertEquals("bartoszfilipowicz", user1.getLogin());
		assertEquals("Warsaw", user1.getTimezone());
		// assertEquals("2011-05-09T09:10:19+02:00", user1.getUpdatedAt());
		assertEquals(new Date(111, 4, 9, 7, 10, 19), user1.getUpdatedAt());

		User user2 = userList.get(1);
		assertNotNull("user2 is null", user2);
		assertEquals(88998, user2.getAccountId());
		assertEquals(UserType.ADMIN, user2.getAdmin());
		// assertEquals("2011-05-09T09:11:21+02:00", user2.getCreatedAt());
		assertEquals(new Date(111, 4, 9, 7, 11, 21), user2.getCreatedAt());
		assertEquals("bartek.f+DarthVader@applicake.com", user2.getEmail());
		assertEquals("Darth", user2.getFirstName());
		assertEquals(185174, user2.getId());
		assertEquals("Vader", user2.getLastName());
		assertEquals("", user2.getLogin());
		assertEquals("", user2.getTimezone());
		// assertEquals("2011-05-09T09:11:24+02:00", user2.getUpdatedAt());
		assertEquals(new Date(111, 4, 9, 7, 11, 24), user2.getUpdatedAt());

		User user3 = userList.get(2);
		assertNotNull("user3 is null", user3);
		assertEquals(88998, user3.getAccountId());
		assertEquals(UserType.USER, user3.getAdmin());
		// assertEquals("2011-05-04T12:34:31+02:00", user3.getCreatedAt());
		assertEquals(new Date(111, 4, 4, 10, 34, 31), user3.getCreatedAt());
		assertEquals("bartek.f+HanSolo@applicake.com", user3.getEmail());
		assertEquals("Han", user3.getFirstName());
		assertEquals(184072, user3.getId());
		assertEquals("Solo", user3.getLastName());
		assertEquals("testHanSolo", user3.getLogin());
		assertEquals("", user3.getTimezone());
		// assertEquals("2011-05-09T09:10:19+02:00<", user3.getUpdatedAt());
		assertEquals(new Date(111, 4, 9, 7, 10, 19), user3.getUpdatedAt());

		User user4 = userList.get(3);
		assertNotNull("user4 is null", user4);
		assertEquals(88998, user4.getAccountId());
		assertEquals(UserType.USER, user4.getAdmin());
		// assertEquals("2011-05-09T09:10:46+02:00", user4.getCreatedAt());
		assertEquals(new Date(111, 4, 9, 7, 10, 46), user4.getCreatedAt());
		assertEquals("bartek.f+LukeSkywalker@applicake.com", user4.getEmail());
		assertEquals("Luke", user4.getFirstName());
		assertEquals(185172, user4.getId());
		assertEquals("Skywalker", user4.getLastName());
		assertEquals("", user4.getLogin());
		assertEquals("", user4.getTimezone());
		assertEquals(new Date(111, 4, 9, 7, 10, 53), user4.getUpdatedAt());
		// assertEquals("2011-05-09T09:10:53+02:00", user4.getUpdatedAt());

	}

	public void testParseRepositories() throws IOException, ParserConfigurationException,
			SAXException {
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(REPOSITORIES_XML_ADDRESS));
		ArrayList<Repository> repositoryList = xmlParser.parseRepositoryList(testXml);

		Repository repo1 = repositoryList.get(0);
		assertNotNull("repo1 is null", repo1);
		assertEquals(88998, repo1.getAccountId());
		assertEquals(true, repo1.isAnonymous());
		assertEquals("label-orange", repo1.getColorLabel());
		// assertEquals("2011-04-28T11:32:49+02:00", repo1.getCreatedAt());
		assertEquals(new Date(111, 3, 28, 9, 32, 49), repo1.getCreatedAt());
		assertEquals(205628, repo1.getId());
		assertEquals(new Date(111, 3, 28, 9, 32, 50), repo1.getLastCommitAt());
		// assertEquals("2011-04-28T09:32:50Z", repo1.getLastCommitAt());
		assertEquals("beanstalk", repo1.getName());
		assertEquals(1, repo1.getRevision());
		assertEquals(59392, repo1.getStorageUsedBytes());
		assertEquals("beanstalk", repo1.getTitle());
		assertEquals("SubversionRepository", repo1.getType());
		// assertEquals("2011-05-04T09:59:35+02:00" , repo1.getUpdatedAt());
		assertEquals(new Date(111, 4, 4, 7, 59, 35), repo1.getUpdatedAt());
		assertEquals("subversion", repo1.getVcs());
		assertEquals(null, repo1.getDefaultBranch());

		Repository repo2 = repositoryList.get(1);
		assertNotNull("repo2 is null", repo2);
		assertEquals(88998, repo2.getAccountId());
		assertEquals(false, repo2.isAnonymous());
		assertEquals("label-red", repo2.getColorLabel());
		// assertEquals("2011-05-04T12:30:51+02:00", repo2.getCreatedAt());
		assertEquals(new Date(111, 4, 4, 10, 30, 51), repo2.getCreatedAt());
		assertEquals("master", repo2.getDefaultBranch());
		assertEquals(207784, repo2.getId());
		assertEquals(null, repo2.getLastCommitAt());
		assertEquals("test-git-repository", repo2.getName());
		assertEquals(0, repo2.getRevision());
		assertEquals(0, repo2.getStorageUsedBytes());
		assertEquals("Test Git repository 2", repo2.getTitle());
		assertEquals("GitRepository", repo2.getType());
		assertEquals(new Date(111, 4, 4, 10, 31, 59), repo2.getUpdatedAt());
		// assertEquals("2011-05-04T12:31:59+02:00" , repo2.getUpdatedAt());
		assertEquals("git", repo2.getVcs());

	}

	public void testParseRepositoriesHashMap() throws IOException,
			ParserConfigurationException, SAXException {
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(REPOSITORIES_XML_ADDRESS));
		HashMap<Integer, Repository> repositoryHashMap = xmlParser
				.parseRepositoryHashMap(testXml);

		Repository repo1 = repositoryHashMap.get(205628);
		assertNotNull("repo1 is null", repo1);
		assertEquals(88998, repo1.getAccountId());
		assertEquals(true, repo1.isAnonymous());
		assertEquals("label-orange", repo1.getColorLabel());
		// assertEquals("2011-04-28T11:32:49+02:00", repo1.getCreatedAt());
		assertEquals(new Date(111, 3, 28, 9, 32, 49), repo1.getCreatedAt());
		assertEquals(205628, repo1.getId());
		assertEquals(new Date(111, 3, 28, 9, 32, 50), repo1.getLastCommitAt());
		// assertEquals("2011-04-28T09:32:50Z", repo1.getLastCommitAt());
		assertEquals("beanstalk", repo1.getName());
		assertEquals(1, repo1.getRevision());
		assertEquals(59392, repo1.getStorageUsedBytes());
		assertEquals("beanstalk", repo1.getTitle());
		assertEquals("SubversionRepository", repo1.getType());
		// assertEquals("2011-05-04T09:59:35+02:00" , repo1.getUpdatedAt());
		assertEquals(new Date(111, 4, 4, 7, 59, 35), repo1.getUpdatedAt());
		assertEquals("subversion", repo1.getVcs());
		assertEquals(null, repo1.getDefaultBranch());

		Repository repo2 = repositoryHashMap.get(207784);
		assertNotNull("repo2 is null", repo2);
		assertEquals(88998, repo2.getAccountId());
		assertEquals(false, repo2.isAnonymous());
		assertEquals("label-red", repo2.getColorLabel());
		// assertEquals("2011-05-04T12:30:51+02:00", repo2.getCreatedAt());
		assertEquals(new Date(111, 4, 4, 10, 30, 51), repo2.getCreatedAt());
		assertEquals("master", repo2.getDefaultBranch());
		assertEquals(207784, repo2.getId());
		assertEquals(null, repo2.getLastCommitAt());
		assertEquals("test-git-repository", repo2.getName());
		assertEquals(0, repo2.getRevision());
		assertEquals(0, repo2.getStorageUsedBytes());
		assertEquals("Test Git repository 2", repo2.getTitle());
		assertEquals("GitRepository", repo2.getType());
		assertEquals(new Date(111, 4, 4, 10, 31, 59), repo2.getUpdatedAt());
		// assertEquals("2011-05-04T12:31:59+02:00" , repo2.getUpdatedAt());
		assertEquals("git", repo2.getVcs());

	}

	// testing Xml parser capability of parsing permissions
	public void testParsePermissions() throws IOException, ParserConfigurationException,
			SAXException {
		String testXml1 = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(PERMISSIONS_XML_ADDRESS_1));
		ArrayList<Permission> permissionList1 = xmlParser.parsePermissionList(testXml1);

		Permission permission1 = permissionList1.get(0);

		assertNotNull("premission 1 is null", permission1);
		assertEquals(false, permission1.isFullDeploymentAccess());
		assertEquals(309214, permission1.getId());
		assertEquals(207784, permission1.getRepositoryId());
		assertEquals(0, permission1.getServerEnvironmentId());
		assertEquals(185254, permission1.getUserId());
		assertEquals(true, permission1.isReadAccess());
		assertEquals(true, permission1.isWriteAccess());

		Permission permission2 = permissionList1.get(1);

		assertNotNull("premission 2 is null", permission2);
		assertEquals(false, permission2.isFullDeploymentAccess());
		assertEquals(309216, permission2.getId());
		assertEquals(205628, permission2.getRepositoryId());
		assertEquals(0, permission2.getServerEnvironmentId());
		assertEquals(185254, permission2.getUserId());
		assertEquals(true, permission2.isReadAccess());
		assertEquals(true, permission2.isWriteAccess());

		// testing user with no permissions or admin/owner user
		String testXml2 = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(PERMISSIONS_XML_ADDRESS_2));
		ArrayList<Permission> permissionList2 = xmlParser.parsePermissionList(testXml2);

		assertTrue("This permission arraylist should be empty", permissionList2.isEmpty());
	}

	// test parsing own account info

	public void testParseAccount() throws IOException, ParserConfigurationException,
			SAXException {
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(ACCOUNT_XML_ADDRESS));
		Account account = xmlParser.parseAccountInfo(testXml);

		assertNotNull("account is null", account);
		assertEquals(new Date(111, 3, 26, 7, 29, 27), account.getCreatedAt());
		assertEquals(88998, account.getId());
		assertEquals("Bartosz Filipowicz", account.getName());
		assertEquals(181892, account.getOwnerId());
		assertEquals(16, account.getPlanId());
		assertEquals(false, account.isSuspended());
		assertEquals("bartosz-filipowicz", account.getThirdLevelDomain());
		assertEquals("International Date Line West", account.getTimeZone());
		assertEquals(new Date(111, 4, 6, 11, 31, 57), account.getUpdatedAt());

	}

	// test comment parsing

	public void testParseComments() throws IOException, ParserConfigurationException,
			SAXException {
		String testXml = convertIStoString(getInstrumentation().getContext().getAssets()
				.open(COMMENTS_XML_ADDRESS));
		ArrayList<Comment> commentList = xmlParser.parseCommentList(testXml);

		Comment comment = commentList.get(0);

		assertNotNull("comment is null", comment);
		assertEquals(88998, comment.getAccountId());
		assertEquals("bartek.f@applicake.com", comment.getAuthorEmail());
		assertEquals(181892, comment.getAuthorId());
		assertEquals("bartoszfilipowicz", comment.getAuthorLogin());
		assertEquals("Bartosz Filipowicz", comment.getAuthorName());
		assertEquals("123 nowy komentarz", comment.getBody());
		assertEquals(new Date(111, 3, 29, 12, 27, 34), comment.getCreatedAt());
		assertEquals("", comment.getFilePath());
		assertEquals(17626, comment.getId());
		assertEquals("", comment.getLineNumber());
		assertEquals("<p>123 nowy komentarz</p>", comment.getRenderedBody());
		assertEquals(205628, comment.getRepositoryId());
		assertEquals("1", comment.getRevision());
		assertEquals(new Date(111, 3, 29, 12, 27, 34), comment.getUpdatedAt());

		// partial testing of longer comment file

		String testXmlPartial = convertIStoString(getInstrumentation().getContext()
				.getAssets().open(COMMENTS_XML_ADDRESS_PARTIAL_CHECK));
		ArrayList<Comment> commentListPartial = xmlParser
				.parseCommentList(testXmlPartial);

		Comment comment1Partial = commentListPartial.get(0);
		assertNotNull("comment1Partial is null", comment1Partial);
		assertEquals(
				"Whoa, this is the greatest commit ever commited! Keep up the good job!",
				comment1Partial.getBody());
		assertEquals(18084, comment1Partial.getId());

		Comment comment2Partial = commentListPartial.get(1);
		assertNotNull("comment2Partial is null", comment2Partial);
		assertEquals(18086, comment2Partial.getId());
		assertEquals(new Date(111, 4, 10, 10, 2, 35), comment2Partial.getUpdatedAt());

		Comment comment3Partial = commentListPartial.get(2);
		assertNotNull("comment3Partial is null", comment3Partial);
		assertEquals("bartek.f+LukeSkywalker@applicake.com",
				comment3Partial.getAuthorEmail());
		assertEquals(new Date(111, 4, 10, 10, 6, 24), comment3Partial.getCreatedAt());

		Comment comment4Partial = commentListPartial.get(3);
		assertNotNull("comment2Partial is null", comment4Partial);
		assertEquals("<p>Luke, I am your father.</p>", comment4Partial.getRenderedBody());
		assertEquals("aea1c74c112667bb458957778d016a4a66233110",
				comment4Partial.getRevision());

	}

	// test exception throwing capabilities of XmlParser class

	// the mock xml for this test contains invalid number field
	// this test should catch an SEXException which encapsulates a
	// NumberFormatException which is a result of Integer.parseInt failed
	// parsing

	public void testParserForThrowingNumberFormatExceptions() {

		try {
			String testXml = convertIStoString(getInstrumentation().getContext()
					.getAssets().open(INVALID_REPOSITORIES_XML_ADDRESS_NFE));
			xmlParser.parseRepositoryList(testXml);
			fail("parseRepositoryList() was supposed to throw an exception");
		} catch (SAXException se) {
			assertTrue(se.getException() instanceof NumberFormatException);
		} catch (Exception e) {
			fail("SAXException should have beed caught");
		}

	}

	// the mock xml for this test contains invalid date format
	// this test should catch an SEXException which encapsulates a
	// ParseException which is a result of date parsing
	public void testParserForThrowingDateParsingExceptions() {
		try {
			String testXml = convertIStoString(getInstrumentation().getContext()
					.getAssets().open(INVALID_REPOSITORIES_XML_ADDRESS_DATEFORMAT));
			xmlParser.parseRepositoryList(testXml);
			fail("parseRepositoryList() was supposed to throw an exception");
		} catch (SAXException se) {
			assertTrue(se.getException() instanceof ParseException);
		} catch (Exception e) {
			fail("SAXException should have beed caught");
		}

	}

	public void testParserForThrowingXMLStructureException() {
		try {
			String testXml = convertIStoString(getInstrumentation().getContext()
					.getAssets().open(INVALID_REPOSITORIES_XML_ADDRESS_XMLSTRUCUTRE));
			xmlParser.parseRepositoryList(testXml);
			fail("parseRepositoryList() was supposed to throw an exception");
		} catch (SAXParseException se) {
			// valid exception -> test passes if no other exception was thrown
		} catch (Exception e) {

			fail("Apache parse exception should have been caught");
		}
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

//	public Date toDefaultTZ(Date date) {
//		return DateInTimezoneConverter.getDateInTimeZone(date, defaultTimeZone);
//	}

}
