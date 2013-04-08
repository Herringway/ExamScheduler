package SharedData.Databases;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Course;

//~--- JDK imports ------------------------------------------------------------

import java.util.Hashtable;
import java.util.Set;

public class CourseDB {
    private static CourseDB singleton;
    private static Hashtable<String, Course> database;

    /**
     *
     * Purposed changes to keep track of courses without exams. Issues existing:
     * naming convention conflicts, talking to other groups.
     *
     * private Hashtable<String,Course> noExamCourses=new Hashtable<String,
     * Course>(); public boolean hasNoExam(String courseIdentifier){ return
     * null==noExamCourses.get(courseIdentifier); }
     *
     * public String[] getNoExamCourses() { Set<String> courseIdentifiers =
     * noExamCourses.keySet(); String[] courseList =
     * courseIdentifiers.toArray(new String[]{}); return courseList; }
     *
     *
     * public void addNoExamCourse(String courseIdentifier, Course course) {
     * noExamCourses.put(courseIdentifier, course); }
     */
    private CourseDB() {
        database = new Hashtable<String, Course>();
    }

    public static void clear() {
        database = new Hashtable<String, Course>();
    }

    public static CourseDB getCourseDB() {
        if (singleton == null) {
            singleton = new CourseDB();
        }

        return singleton;
    }

    public Course getCourse(String courseIdentifier) {
        return database.get(courseIdentifier);
    }

    public void addCourse(String courseIdentifier, Course course) {
        database.put(courseIdentifier, course);
    }

    public String[] getCourses() {
        Set<String> courseIdentifiers = database.keySet();
        String[] courseList = courseIdentifiers.toArray(new String[] {});

        return courseList;
    }

    public boolean contains(String courseIdentifier) {
        return database.containsKey(courseIdentifier);
    }
}
