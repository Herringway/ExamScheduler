package unittests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import SharedData.Course;
import SharedData.Room;
import SharedData.Student;
import SharedData.Databases.CourseDB;
import SharedData.Databases.RoomDB;
import SharedData.Databases.StudentDB;

/**
 * 
 */

/**
 * @author Cameron
 *
 */
public class DBTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}
	private StudentDB studentdb;
	private CourseDB coursedb;
	private RoomDB roomdb;
	/**
	 * Creates the database instances used for the tests.
	 * @throws java.lang.Exception
	 * @author Cameron Ross
	 */
	@Before
	public void setUp() throws Exception {
		studentdb = StudentDB.getStudentDB();
		coursedb = CourseDB.getCourseDB();
		roomdb = RoomDB.getRoomDB();
	}
	/**
	 * A test to ensure that the database instances exist.
	 * @author Cameron Ross
	 */
	@Test
	public void CreationTest() {
		assertNotNull(studentdb);
		assertNotNull(coursedb);
		assertNotNull(roomdb);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Ensure that the databases are singletons.
	 */
	@Test
	public void SingletonTest() {
		assertSame(studentdb, StudentDB.getStudentDB());
		assertSame(coursedb, CourseDB.getCourseDB());
		assertSame(roomdb, RoomDB.getRoomDB());
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * A test to ensure that inserting entries into databases succeeds and returns the object put into it.
	 * @author Cameron Ross
	 */
	@Test
	public void insertionTest() {
		Student student = new Student("Salmon");
		studentdb.addStudent(9001, student);
		assertTrue(studentdb.contains(9001));
		assertSame(studentdb.getStudent(9001), student);
		
		Course course = new Course("Hat-wearing 1001");
		coursedb.addCourse("TEST-9001", course);
		assertTrue(coursedb.contains("TEST-9001"));
		assertSame(coursedb.getCourse("TEST-9001"), course);
		
		Room room = new Room("GYM");
		roomdb.addRoom("TEST-101", room);
		assertTrue(roomdb.contains("TEST-101"));
		assertSame(roomdb.getRoom("TEST-101"), room);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}

}
