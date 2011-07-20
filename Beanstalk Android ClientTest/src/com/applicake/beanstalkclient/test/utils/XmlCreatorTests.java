package com.applicake.beanstalkclient.test.utils;

import java.io.IOException;

import android.test.AndroidTestCase;

import com.applicake.beanstalkclient.enums.ColorLabels;
import com.applicake.beanstalkclient.utils.XmlCreator;

public class XmlCreatorTests extends AndroidTestCase {

  private XmlCreator xmlCreator;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

  }

  public void testCreateCommentXml() throws IllegalArgumentException,
      IllegalStateException, IOException {
    xmlCreator = new XmlCreator();

    String revision = "1";
    String body = "comment 1 body";
    // String filePath = "file/path";
    // int lineNumber = 0;

    String comment1xml = xmlCreator.createCommentXML(revision, body);

    assertEquals("<comment>" + "<revision>1</revision>" + "<body>comment 1 body</body>"
        + "<file-path></file-path>" + "<line-number></line-number>" + "</comment>",
        comment1xml);

  }

  public void testCreateGitRepositoryXML() throws IllegalArgumentException,
      IllegalStateException, IOException {
    xmlCreator = new XmlCreator();

    String name = "test-repository";
    String title = "test repository title";
    String colorLabel = ColorLabels.PINK.getLabel();

    String createGitRepoXML = xmlCreator.createGitRepositoryCreationXML(name, title,
        colorLabel);

    assertEquals("<?xml version='1.0' encoding='UTF-8' ?>" + "<repository>"
        + "<name>test-repository</name>" + "<type_id>git</type_id>"
        + "<title>test repository title</title>"
        + "<color_label>label-pink</color_label>" + "</repository>", createGitRepoXML);

  }

  public void testCreateSVNRepositoryXML() throws IllegalArgumentException,
      IllegalStateException, IOException {
    xmlCreator = new XmlCreator();

    String name = "test-svn-repository";
    String title = "test repository title";
    String colorLabel = ColorLabels.BLUE.getLabel();

    String createGitRepoXML = xmlCreator.createSVNRepositoryCreationXML(name, title,
        colorLabel, false);

    assertEquals("<?xml version='1.0' encoding='UTF-8' ?>" + "<repository>"
        + "<name>test-svn-repository</name>" + "<type_id>subversion</type_id>"
        + "<title>test repository title</title>"
        + "<create_structure type=\"boolean\">false</create_structure>"
        + "<color_label>label-blue</color_label>" + "</repository>", createGitRepoXML);

  }

  public void testModifyReposiotoryXML() throws IllegalArgumentException,
      IllegalStateException, IOException {
    xmlCreator = new XmlCreator();

    String title = "test repository new title";
    String colorLabel = ColorLabels.GREY.getLabel();

    String createGitRepoXML = xmlCreator.createRepositoryModifyXML(title, colorLabel);

    assertEquals("<?xml version='1.0' encoding='UTF-8' ?>" + "<repository>"
        + "<title>test repository new title</title>"
        + "<color_label>label-grey</color_label>" + "</repository>", createGitRepoXML);

  }

}
