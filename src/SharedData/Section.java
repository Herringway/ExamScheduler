package SharedData;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Databases.StudentDB;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashSet;

public class Section {
    char sectionLetter;
    String prof;
    String time;
    Course courseOf;
    private HashSet<Integer> studentSet;
    private int examLength = 3;

    public Section(Course inCourse, char letter, String inProf, String inTime) {
        sectionLetter = letter;
        prof = inProf;
        courseOf = inCourse;
        time = inTime;
        studentSet = new HashSet<Integer>();
    }

    public Section setExamLength(int newLength) {
        examLength = newLength;

        return this;
    }

    public int getExamLength() {
        return examLength;
    }

    public Course getCourse() {
        return courseOf;
    }

    public boolean addStudent(int studentIdentifier) {
        if (StudentDB.getStudentDB().contains(studentIdentifier) &&!studentSet.contains(studentIdentifier)) {
            studentSet.add(studentIdentifier);

            return false;
        }

        return true;
    }

    public Integer[] getStudents() {
        return studentSet.toArray(new Integer[] {});
    }

    @Override
    public String toString() {
        return courseOf.toString() + "-" + sectionLetter;
    }
}
