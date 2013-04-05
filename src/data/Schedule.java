package data;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Course;

import SharedData.Databases.RoomDB;

import SharedData.Room;

import SharedInterface.ExamScheduleInterface;
import SharedInterface.ScheduledExamInterface;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

/*Author(s): Zack Delaney
*
* Purpose: This is an object representing the exam schedule.
* Date: 14/02/2013
*
* Update: 13/02/2013
* - Updated to implement ExamScheduleInterface for I/O's use.
*
* Update: 31/03/2013
* - Schedule now requires a room database. This is so that when creating it's slots it can create local copies of the rooms at those times.
* - Went from a 1D to a 2D array for simplicity and ease of use.
 */
public class Schedule implements ExamScheduleInterface {
    protected ScheduledExamSlot[][] schedule;
    protected static int examsPerDay;

    /*
     * The constructor requires the number of days in the exam schedule and the number of exams per day.
     *
     * scheduledExamSlots need to be optimized as well, and they require their own, deep copy of the
     * list of available rooms.
     */
    public Schedule(int numDays, int numExamsPerDay, RoomDB r) {
        examsPerDay = numExamsPerDay;
        schedule = new ScheduledExamSlot[numDays][numExamsPerDay];

        String[] roomKeys = r.getRooms();
        Room[] roomList = new Room[roomKeys.length];

        // Create our duplicate of rooms.
        for (int i = 0; i < numDays; i++) {
            for (int j = 0; j < numExamsPerDay; j++) {
                for (int k = 0; k < roomKeys.length; k++) {
                    roomList[k] = new Room(r.getRoom(roomKeys[k]));
                }

                schedule[i][j] = new ScheduledExamSlot(roomList);
            }
        }
    }

    /*
     * Takes the slot required, and returns the scheduled exam slot.
     *
     *
     */
    public ScheduledExamSlot getExamSlot(int i, int j) {
        return schedule[i][j];
    }

    public int getNumExamsPerDay() {
        return examsPerDay;
    }

    public int getNumSlotsInSchedule() {
        return schedule.length * schedule[0].length;
    }

    // THIS IS DEPRECATED. PROPER WAY OF ADDING AN EXAM IS TO GET THE SCHEDULED EXAM SLOT AND ADD IT TO THAT DIRECTLY.
    private void setExam(int day, int period, Course course) {}

    @Override
    public List<ScheduledExamInterface> getExams(int day, int period) {
        ScheduledExamSlot E = getExamSlot(day, period);
        ArrayList<Exam> X = E.getExams();
        ArrayList<ScheduledExamInterface> returnVal = new ArrayList<ScheduledExamInterface>();

        for (int i = 0; i < X.size(); i++) {
            returnVal.add(X.get(i));
        }

        return returnVal;
    }

    @Override
    public int getNumDays() {
        return schedule.length;
    }
}
