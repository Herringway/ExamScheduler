package unittests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import io.FileProcessing.DSVScanner;


public class DSVScannerTests {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}
	/**
	 * Ensure we don't have any extended entries in our output.
	 * 
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test
	public void NoEmptyTest() throws IOException {
		DSVScanner courseScanner;
		DSVScanner examDetailsScanner;
		courseScanner = new DSVScanner("tests/course.txt");
		examDetailsScanner = new DSVScanner("tests/exam.txt");
		while (courseScanner.hasNextEntry()) {
			assertFalse(courseScanner.getNextEntry().get("Student").equals(""));
		}
		while (examDetailsScanner.hasNextEntry()) {
			assertFalse(examDetailsScanner.getNextEntry().get("Term")
					.equals(""));
		}
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Undefined behaviour, but shouldn't fail.
	 * 
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test
	public void dupeColumnTest() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/exam-dupecolumn.txt");
		Map<String, String> output = scanner.getNextEntry();
		assertNotNull(output);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test to ensure swapped columns have identical results
	 * 
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test
	public void ColumnSwapTest() throws IOException {
		DSVScanner scanner1 = new DSVScanner("tests/course-short.txt");
		DSVScanner scanner2 = new DSVScanner("tests/course-shortswapped.txt");
		Map<String, String> output1 = scanner1.getNextEntry();
		Map<String, String> output2 = scanner2.getNextEntry();
		assertEquals(output1, output2);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test to ensure extraneous columns don't cause interference with normal
	 * operation. The extra column should be ignored if no data is available for
	 * it.
	 * 
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test
	public void ExtraColumnTest() throws IOException {
		DSVScanner scanner1 = new DSVScanner("tests/course-short.txt");
		DSVScanner scanner2 = new DSVScanner("tests/course-shortbonus.txt");
		Map<String, String> output1 = scanner1.getNextEntry();
		Map<String, String> output2 = scanner2.getNextEntry();
		assertNotNull(output2);
		assertEquals(output1, output2);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test to ensure header line is not also interpreted as an entry.
	 * 
	 * @author Cameron Ross
	 */
	@Test
	public void headerEntryTest() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-short.txt");
		Map<String, String> output = scanner.getNextEntry();
		assertFalse(output.values().toArray()[0].equals(output.keySet()
				.toArray()[0]));
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test to ensure all entries can be retrieved from the file for a multiline file that may have an empty line between lines (apparent in some example input files).
	 * 
	 * @author John Daniel Maguire
	 */
	@Test
	public void EmptyLineBetween() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-emptyLineBetween.txt");
		Map<String,String> output = scanner.getNextEntry();
		assertEquals(output.get("Name"), "Academic, Crystal\t");
		assertTrue(scanner.hasNextEntry());
		output = scanner.getNextEntry();
		assertEquals(output.get("Name"), "Academic, Crystal\t");
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	
	/**
	 * Test to ensure all entries can be retrieved from the file for a multiline file.
	 * 
	 * @author John Daniel Maguire, Cameron Ross
	 */
	@Test
	public void MultiLineTest() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-multiLine.txt");
		Map<String,String> output = scanner.getNextEntry();
		assertEquals(output.get("Name"), "Academic, Crystal\t");
		assertTrue(scanner.hasNextEntry());
		output = scanner.getNextEntry();
		assertEquals(output.get("Name"), "Scholar, Joseph");
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	
	/**
	 * Test to ensure all entries can be retrieved from the file.
	 * 
	 * @author Cameron Ross
	 */
	@Test
	public void LastEntryTest() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-lessshort.txt");
		Map<String, String> output = null;
		while (scanner.hasNextEntry()){
			output = scanner.getNextEntry();	
		}
		assertEquals(output.get("Student"), "6544990");
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test to ensure failure when reading beyond the end of the file.
	 * 
	 * @author Cameron Ross
	 */
	@Test
	public void BeyondLastEntryTest() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-lessshort.txt");
		Map<String, String> output = null;
		while (scanner.hasNextEntry())
			output = scanner.getNextEntry();
		output = scanner.getNextEntry();
		assertNull(output);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test for empty files. Should throw an IOException of some kind.
	 * 
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test
	public void EmptyFileTest() throws IOException {
		try {
			new DSVScanner("tests/empty.txt");
			fail("reading empty file succeeded unexpectedly");
		} catch (IOException e) {

		} catch (Exception e) {
			fail("Bad exception thrown");
		}
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test for file with no data. Should simply return no entries.
	 * 
	 * @author Cameron Ross
	 */
	@Test
	public void noDataTest() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-nodata.txt");
		assertEquals(scanner.hasNextEntry(), false);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

	/**
	 * Test case for a file with insufficient columns.
	 * 
	 * @author Cameron Ross
	 */
	@Test
	public void InsufficientColumnsTest() throws IOException {
		try {
			DSVScanner scanner = new DSVScanner(
					"tests/course-insufficientcolumns.txt");
			while (scanner.hasNextEntry())
				scanner.getNextEntry();
		} catch (Exception e) {
			fail("Exception caught");
			assertEquals("", errContent.toString());
			assertEquals("", outContent.toString());
		}
	}
	/**
	 * Tests entry counting for a simple file.
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test 
	public void entriesCountTestA() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-short.txt");
		int entries = 0;
		assertEquals(scanner.getEntriesRead(), entries);
		while (scanner.hasNextEntry()) {
			assertEquals(scanner.getEntriesRead(), entries);
			scanner.getNextEntry();
			assertEquals(scanner.getEntriesRead(), ++entries);
		}
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Tests entry counting for a file with multiline entries.
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test 
	public void entriesCountTestB() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-multiLine.txt");
		int entries = 0;
		assertEquals(scanner.getEntriesRead(), entries);
		while (scanner.hasNextEntry()) {
			assertEquals(scanner.getEntriesRead(), entries);
			scanner.getNextEntry();
			assertEquals(scanner.getEntriesRead(), ++entries);
		}
	}
	/**
	 * Tests line counting for a simple file.
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test 
	public void lineCountTestA() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-short.txt");
		assertEquals(scanner.getLinesRead(), 1);
		scanner.getNextEntry();
		assertEquals(scanner.getLinesRead(), 2);
	}
	/**
	 * Tests line counting for a file with multiline entries.
	 * @throws IOException
	 * @author Cameron Ross
	 */
	@Test 
	public void lineCountTestB() throws IOException {
		DSVScanner scanner = new DSVScanner("tests/course-multiLine.txt");
		assertEquals(scanner.getLinesRead(), 1);
		scanner.getNextEntry();
		assertEquals(scanner.getLinesRead(), 3);
		if (scanner.hasNextEntry()) {
			scanner.getNextEntry();
			assertEquals(scanner.getLinesRead(), 4);
		}
		
	}

}
