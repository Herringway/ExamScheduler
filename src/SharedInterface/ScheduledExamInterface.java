package SharedInterface;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Course;
import SharedData.Room;

public interface ScheduledExamInterface {
    public Course getCourse();

    public Room getRoom();
}
