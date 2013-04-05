package SharedInterface;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * The interface for the exam schedule. It is responsible to tell clients how
 * many days their are in the exam schedule and on a given day and period what
 * exams are taking place.
 *
 * @author John Maguire
 *
 */
public interface ExamScheduleInterface {

    /**
     * Returns a list of exams on a given day and period of day (the exam
     * schedule begins on day 0)
     */
    public List<ScheduledExamInterface> getExams(int day, int period);

    public int getNumDays();
}
