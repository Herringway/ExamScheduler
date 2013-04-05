
//Whoever wrote this should have commented it. To save them time I'm adding in comments now. 
//- Zack Delaney
//Note that this class is a singleton, which is to say that there can only be one database of Student. 
//Hopefully they never want to run this program more than once with a different set of students for 
//some reason. If this does become a requirement a 'purge' functionality will be necessary. 
package SharedData.Databases;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Student;

//~--- JDK imports ------------------------------------------------------------

import java.util.Hashtable;
import java.util.Set;

public class StudentDB {
    private static StudentDB singleton;
    private static Hashtable<Integer, Student> database;    // Contains a

    // Private constructor, only called the first time someone makes a student database object.
    private StudentDB() {
        database = new Hashtable<Integer, Student>();
    }

    // Public constructor, and how one gains access to the Student database.
    public static StudentDB getStudentDB() {
        if (singleton == null) {
            singleton = new StudentDB();
        }

        return singleton;
    }

    public static void clear() {
        database = new Hashtable<Integer, Student>();
    }

    // Requires a student number (?) and returns a student object.
    public Student getStudent(int studentIdentifier) {
        return database.get(studentIdentifier);
    }

    // Not sure why 'courseList' is the variable name, but I think this code returns an array
    // of student IDs, meaning it might be more effective to call it 'getStudentIDs'. It also
    // means accessing an array of Students requires this method, and then multiple calls to
    // to getStudent. Is this intentional?
    public Integer[] getStudents() {
        Set<Integer> courseIdentifiers = database.keySet();
        Integer[] courseList = new Integer[0];

        courseList = courseIdentifiers.toArray(courseList);

        return courseList;
    }

    // Adds a student, pretty basic.
    public void addStudent(int studentIdentifier, Student student) {
        database.put(studentIdentifier, student);
    }

    // Checks to see if a student has that ID, also pretty basic.
    public boolean contains(int studentIdentifier) {
        return database.containsKey(studentIdentifier);
    }
}
