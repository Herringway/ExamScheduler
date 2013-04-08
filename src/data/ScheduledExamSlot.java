package data;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Course;
import SharedData.Resource;
import SharedData.Room;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

/*Author(s): Zack Delaney
*
* Purpose: This is an object representing an exam slot in a schedule.
* Date: 14/02/2013
*
* Date: 3/31/2013
* Updates: I've added the addExam functionality. This is our equivalent of colours now as well.
*
 */
public class ScheduledExamSlot {
    ArrayList<Exam> exams;
    int numExams;
    int ConcurrencyLimit;
    Room[] roomsAvailable;
    int day;
    int timeSlot;

    public ScheduledExamSlot(Room[] rooms) {
        numExams = 0;
        exams = new ArrayList<Exam>();
        roomsAvailable = rooms;
        ConcurrencyLimit = rooms.length;
    }

    public Room[] getRooms() {
        return roomsAvailable;
    }

    public void setDay(int i) {
        day = i;
    }

    public void setTimeSlot(int j) {
        timeSlot = j;
    }

    public ArrayList<Exam> getExams() {
        return exams;
    }

    public int getDay() {
        return day;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public Course[] getCourses() {
        Course[] courseList = new Course[numExams];

        for (int i = 0; i < numExams; i++) {
            courseList[i] = exams.get(i).getCourse();
        }

        return courseList;
    }

    public Room getRoomWithBestCapacity(int StudentNum) {
        int bestFit = -1;
        int roomIndex = -1;

        for (int i = 0; i < roomsAvailable.length; i++) {
            int seatsAvailable = roomsAvailable[i].getResource(Resource.SEAT);
            int numRemaining = seatsAvailable - StudentNum;

            if (((bestFit == -1) && (numRemaining > 0)) || ((bestFit > numRemaining + 10) && (bestFit > 0))) {
                bestFit = numRemaining;
                roomIndex = i;
            }
        }

        if (bestFit == -1) {
            System.out.println(
                "Something has gone wrong upstream, apparently there is no room that will fit this class remaining. This should have been checked prior to this happening.");
            System.out.println("The number of seats that this class needed was " + StudentNum);

            return null;
        }

        roomsAvailable[roomIndex].addResource(Resource.SEAT, bestFit);

        return roomsAvailable[roomIndex];
    }

    public void addExam(Exam e) {
        exams.add(e);

        numExams++;
    }
}
