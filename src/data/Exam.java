package data;

//~--- non-JDK imports --------------------------------------------------------

/*Author(s): Zack Delaney
*
* Purpose: This is an object representing an exam.
* Date: 14/02/2013
*
* Update: 14/03/2013
*
* NOTE: This currently just contains information about the course, but will eventually contain
*               Information about the room as well, in theory.
* - As of the most recent update, it has room information. Not sure about proctors yet.
*
* Update: 31/3/2013
* - Implements ScheduledExamInterface for John.
*
 */
import SharedData.Course;
import SharedData.Room;

import SharedInterface.ScheduledExamInterface;

public class Exam implements ScheduledExamInterface {
    Course course;
    Room location;

    public Exam(Course c, Room r) {
        course = c;
        location = r;
    }

    @Override
    public Course getCourse() {
        return course;
    }

    @Override
    public Room getRoom() {
        return location;
    }
}
