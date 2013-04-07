package unittests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.FileProcessing.FileProcessor;
import SharedInterface.ExamScheduleInterface;
import SharedInterface.ScheduledExamInterface;
import SharedData.Course;
import SharedData.Room;
import SharedData.Databases.CourseDB;
import SharedData.Databases.RoomDB;
import SharedData.Databases.StudentDB;

public class FileProcessorTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	@Before
	public void setUpStreams() {
	    //System.setOut(new PrintStream(outContent));
	    //System.setErr(new PrintStream(errContent));
	}
	@After
	public void wipeDBS() {
		StudentDB.clear();
		RoomDB.clear();
		CourseDB.clear();
	}
	/**
	 * Test a simple single entry course file.
	 * @author Cameron Ross
	 * @throws IOException 
	 */
	@Test
	public void testSimpleFile() throws IOException {
		FileProcessor.loadCourses("tests/course-short.txt",null);
		assertEquals(StudentDB.getStudentDB().getStudent(6544222).getName(), "Scholar, Joseph");
		Character[] test = new Character[1];
		test[0] = 'A';
		assertArrayEquals(CourseDB.getCourseDB().getCourse("GENS2421").getSectionKeys(), test);
		assertEquals(CourseDB.getCourseDB().getCourse("GENS2421").getSection('A').getStudents().length, 1);
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	/**
	 * Test a simple two-entry course file, with two students sharing a class and
	 * one student having an entry spanning two lines.
	 * @author Cameron Ross
	 * @throws IOException 
	 */
	@Test
	public void testMultiEntry() throws IOException {
		FileProcessor.loadCourses("tests/course-multiLine.txt",null);
		assertEquals(StudentDB.getStudentDB().getStudent(6544222).getName(), "Scholar, Joseph");
		assertEquals(StudentDB.getStudentDB().getStudent(6544990).getName(), "Academic, Crystal");
		assertEquals(CourseDB.getCourseDB().getCourse("GENS2421").getSection('A').getExamLength(), 3);
		assertEquals(CourseDB.getCourseDB().getCourse("GENS2421").getSection('A').getStudents().length, 2);
		assertArrayEquals(CourseDB.getCourseDB().getCourse("GENS2421").getSection('A').getStudents(), new Integer[]{6544990,6544222});
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
		
	}
	/**
	 * Tests room database loading.
	 * @throws IOException 
	 */
	@Test
	public void testLoadRooms() throws IOException {
		FileProcessor.loadRooms("roomfile.txt");
		assertEquals("", errContent.toString());
		assertEquals("", outContent.toString());
	}
	
	@Test
	public void testWriteSchedule() throws Exception {
		String filename = "ntest.txt";
		File f = new File(filename);
		if (f.exists())
			f.delete();
		FileProcessor.loadCourses("tests/course-multiLine.txt",null);
		FileProcessor.loadRooms("roomfile.txt");
		testSchedule testschedule = new testSchedule(1, new String[]{"GENS2421"}, new String[]{"CTEEM14"});
		FileProcessor.writeExamFile(filename, testschedule, Calendar.getInstance());
		Scanner fileReader = new Scanner(f);
		assertEquals(fileReader.useDelimiter("\\A").next(), "Course,Exam Date,Exam Day,Location\nGENS2421,4-4-2013,Thursday,CTEEM14");
		fileReader.close();
		if (f.exists())
			f.delete();
	}
	private class testSchedule implements ExamScheduleInterface {
		private class scheduleExam implements ScheduledExamInterface {
			private String course;
			private String room;
			public scheduleExam(String c, String r) {
				course = c;
				room = r;
			}
			public Course getCourse() {
				return CourseDB.getCourseDB().getCourse(course);
			}
			public Room getRoom() {
				return RoomDB.getRoomDB().getRoom(room);
			}
		}
		private int days;
		private ArrayList<scheduleExam> exams;
		public testSchedule(int days, String[] exams, String[] rooms) {
			this.days = days;
			this.exams = new ArrayList<scheduleExam>();
			for (int i = 0; i < days; i++)
				this.exams.add(new scheduleExam(exams[i], rooms[i]));
		}
		public List<ScheduledExamInterface> getExams(int day, int period) {
			ArrayList<ScheduledExamInterface> output = new ArrayList<ScheduledExamInterface>(); 
			output.add(exams.get(day));
			return output;
		}

		public int getNumDays() {
			return days;
		}
	}

}
