package com.applicake.beanstalkclient.test;

import java.io.IOException;
import java.util.ArrayList;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.YamlEntry;
import com.applicake.beanstalkclient.utils.CustomYamlParser;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.test.InstrumentationTestCase;

public class YamlTesting extends InstrumentationTestCase {

  final String example1 = "- - branches/\n" + "  - :add\n" + "- - tags/\n" + "  - :add\n"
      + "- - trunk/\n" + "  - :add\n";
  private static final String CHANGESET_XML_ADDRESS = "mockxmls/changesets.xml";

  @Override
  protected void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
  }

  public void testCustomYamlParser() {
    CustomYamlParser yamlParser = new CustomYamlParser();
    ArrayList<YamlEntry> yamlList = yamlParser.parseEntriesList(example1);
    assertEquals("branches/", yamlList.get(0).getValue());
    assertEquals("add", yamlList.get(0).getProperty());
    assertEquals("tags/", yamlList.get(1).getValue());
    assertEquals("add", yamlList.get(1).getProperty());
    assertEquals("trunk/", yamlList.get(2).getValue());
    assertEquals("add", yamlList.get(2).getProperty());

  }

  public void testCustomYamlParserWithDataFromChangeset() throws IOException,
      XMLParserException {
    String testXml = XmlParserTests.convertIStoString(getInstrumentation().getContext()
        .getAssets().open(CHANGESET_XML_ADDRESS));
    ArrayList<Changeset> changesetList = XmlParser.parseChangesetList(testXml);
    Changeset changeset1 = changesetList.get(0);

    ArrayList<YamlEntry> yamlListChangeset1Dirs = changeset1.getChangedDirs();
    ArrayList<YamlEntry> yamlListChangeset1Files = changeset1.getChangedFiles();
    ArrayList<YamlEntry> yamlListChangeset1Properties = changeset1.getChangedProperties();

    assertTrue(yamlListChangeset1Dirs.isEmpty());
    assertEquals("README", yamlListChangeset1Files.get(0).getValue());
    assertEquals("add", yamlListChangeset1Files.get(0).getProperty());
    assertTrue(yamlListChangeset1Properties.isEmpty());

    Changeset changeset2 = changesetList.get(1);
    ArrayList<YamlEntry> yamlListChangeset2Dirs = changeset2.getChangedDirs();
    ArrayList<YamlEntry> yamlListChangeset2Files = changeset2.getChangedFiles();
    ArrayList<YamlEntry> yamlListChangeset2Properties = changeset2.getChangedProperties();

    assertEquals(3, yamlListChangeset2Dirs.size());
    assertEquals("branches/", yamlListChangeset2Dirs.get(0).getValue());
    assertEquals("add", yamlListChangeset2Dirs.get(0).getProperty());
    assertEquals("tags/", yamlListChangeset2Dirs.get(1).getValue());
    assertEquals("add", yamlListChangeset2Dirs.get(1).getProperty());
    assertEquals("trunk/", yamlListChangeset2Dirs.get(2).getValue());
    assertEquals("add", yamlListChangeset2Dirs.get(2).getProperty());
    assertTrue(yamlListChangeset2Files.isEmpty());
    assertTrue(yamlListChangeset2Properties.isEmpty());

  }

}
