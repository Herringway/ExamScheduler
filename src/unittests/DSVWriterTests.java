package unittests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.PrintStream;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

import io.FileProcessing.DSVWriter;

public class DSVWriterTests {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}
	/**
	 * Test for DSVWriter's basic functionality: writing columns and values to a file.
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void basicTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestValue");
		testVals.put("Test2", "TestValue2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter);
		writer.writeEntry(testVals);
		assertArrayEquals(charWriter.toCharArray(), "Test,Test2\nTestValue,TestValue2".toCharArray());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test to ensure delimiters don't appear in entries without enclosure
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void delimiterEnclosureTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		String delimiter = ",";
		testVals.put("DelimTest", "Test" + delimiter + "Value");
		testVals.put("DelimTest2", "Test" + delimiter + "Value2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter, delimiter);
		writer.writeEntry(testVals);
		assertArrayEquals(charWriter.toCharArray(), ("DelimTest"+delimiter+"DelimTest2\n\"Test" + delimiter + "Value\""+delimiter+"\"Test" + delimiter + "Value2\"").toCharArray());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test to ensure enclosures are enclosed as well
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void enclosureEnclosureTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		String delimiter = ",";
		String enclosure = "\"";
		testVals.put("EnclosureTest", "Test" + enclosure + "Value");
		testVals.put("EnclosureTest2", "Test" + enclosure + "Value2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter, delimiter, enclosure);
		writer.writeEntry(testVals);
		assertArrayEquals(charWriter.toCharArray(), ("EnclosureTest"+delimiter+"EnclosureTest2\n\"Test" + enclosure + "Value\""+delimiter+"\"Test" + enclosure + "Value2\"").toCharArray());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test for behaviour on entry delimiters in values
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void entryDelimiterValueTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		testVals.put("EDelimTest", "Test\nValue");
		testVals.put("EDelimTest2", "TestValue2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter);
		try {
			writer.writeEntry(testVals);
			fail("Exception not thrown");
		} catch (Exception e) { }
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test to ensure multiple entries get properly written
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void multiEntryTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestEntry1Value");
		testVals.put("Test2", "TestEntry1Value2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter);
		writer.writeEntry(testVals);
		testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestEntry2Value");
		testVals.put("Test2", "TestEntry2Value2");
		writer.writeEntry(testVals);
		assertArrayEquals(charWriter.toCharArray(), "Test,Test2\nTestEntry1Value,TestEntry1Value2\nTestEntry2Value,TestEntry2Value2".toCharArray());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test to ensure multiple entries get properly written, even with differing value orders
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void multiEntrySwappedTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestEntry1Value");
		testVals.put("Test2", "TestEntry1Value2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter);
		writer.writeEntry(testVals);
		testVals = new LinkedHashMap<String,String>();
		testVals.put("Test2", "TestEntry2Value2");
		testVals.put("Test", "TestEntry2Value");
		writer.writeEntry(testVals);
		assertArrayEquals(charWriter.toCharArray(), "Test,Test2\nTestEntry1Value,TestEntry1Value2\nTestEntry2Value,TestEntry2Value2".toCharArray());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test to ensure multiple entries get properly written, even if a value is missing
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void multiEntryMissingValueTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestEntry1Value");
		testVals.put("Test2", "TestEntry1Value2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter);
		writer.writeEntry(testVals);
		testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestEntry2Value");
		writer.writeEntry(testVals);
		assertArrayEquals(charWriter.toCharArray(), "Test,Test2\nTestEntry1Value,TestEntry1Value2\nTestEntry2Value,".toCharArray());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test to ensure multiple entries get properly written, even with extraneous values
	 * @throws Exception
	 * @author Cameron Ross
	 */
	@Test
	public void multiEntryExtraneousValueTest() throws Exception {
		LinkedHashMap<String,String> testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestEntry1Value");
		testVals.put("Test2", "TestEntry1Value2");
		CharArrayWriter charWriter = new CharArrayWriter();
		DSVWriter writer = new DSVWriter(charWriter);
		writer.writeEntry(testVals);
		testVals = new LinkedHashMap<String,String>();
		testVals.put("Test", "TestEntry2Value");
		testVals.put("Test2", "TestEntry2Value2");
		testVals.put("Test3", "TestEntry2Value3");
		writer.writeEntry(testVals);
		assertArrayEquals(charWriter.toCharArray(), "Test,Test2\nTestEntry1Value,TestEntry1Value2\nTestEntry2Value,TestEntry2Value2".toCharArray());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
}
