package SharedData;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Databases.CourseDB;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashSet;

public class Student {
    private String name;
    private HashSet<String> courseSet;

    public Student(String studentName) {
        name = studentName;
        courseSet = new HashSet<String>();
    }

    public String getName() {
        return name;
    }

    public boolean addCourse(String courseIdentifier) {
        if (CourseDB.getCourseDB().contains(courseIdentifier)) {
            courseSet.add(courseIdentifier);

            return true;
        }

        return false;
    }

    public String[] getCourses() {    // HAVE NOT TESTED YET,

        // Set<String> courseSet=courseList.keySet();
        // String[] courseIdentifierArray=(String[]) courseSet.toArray();
        return (String[]) courseSet.toArray();
    }
}
