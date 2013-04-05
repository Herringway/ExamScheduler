package io.FileProcessing;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Course;

import SharedData.Databases.CourseDB;
import SharedData.Databases.RoomDB;
import SharedData.Databases.StudentDB;

import SharedData.Resource;
import SharedData.Room;
import SharedData.Section;
import SharedData.Student;

import SharedInterface.ExamScheduleInterface;
import SharedInterface.IPredicate;
import SharedInterface.ScheduledExamInterface;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class FileProcessor {

    /*
     * -
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * Save actions
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    private static int curFileNumber = 0;

    public static void writeTemporaryFile(ExamScheduleInterface exams, Calendar startDay) throws Exception {
        curFileNumber++;

        File writeTo;

        do {
            writeTo = new File(new File(".").getAbsolutePath() + File.separator + "temp" + curFileNumber + ".csv");

            curFileNumber++;
        } while (writeTo.exists());

        writeExamFile(writeTo.getAbsolutePath(), exams, startDay);
    }

    public static void writeExamFile(String path, ExamScheduleInterface exams, Calendar startDay) throws Exception {
        writeExamFile(path, exams, startDay, new IPredicate<Calendar>() {
            public boolean apply(Calendar day) {
                return day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
            }
        });
    }

    /**
     *
     * @param path
     *            where to write the file to.
     * @param exams
     * @param examDays
     *            a filter that says whether a certain day is available to have
     *            exams on. It is assumed that there are enough days after the
     *            startDay to put the exams.
     * @throws Exception
     */
    public static void writeExamFile(String path, ExamScheduleInterface exams, Calendar startDay, IPredicate<Calendar> validDay) throws Exception {
        int numExamDays = exams.getNumDays();
        Calendar curDay;

        (curDay = Calendar.getInstance()).setTime(startDay.getTime());

        int schDay = 0;
        TreeSet<Exam> examSet = new TreeSet<Exam>();

//      int[] startTimes = new int[] { 9, 12 + 2, 12 + 7 };
        // String[] amPM = new String[] { "AM", "PM" };
        while (schDay < numExamDays) {
            while (!validDay.apply(curDay)) {
                System.out.println("Not valid " + curDay.get(Calendar.DAY_OF_WEEK));
                curDay.add(Calendar.DAY_OF_YEAR, 1);
            }

            String curDayString = curDay.get(Calendar.DAY_OF_MONTH) + "-" + (curDay.get(Calendar.MONTH) + 1) + "-" + curDay.get(Calendar.YEAR);
			System.out.printf("Schedule day %d of %d, %s\n",schDay,numExamDays,curDayString);
            String dayOfWeek = curDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.CANADA);

            for (int timePer = 0; timePer < 3; timePer++) {

                // String startTime = startTimes[timePer] + ":00:00 " + amPM[startTimes[timePer] / 12];
                for (ScheduledExamInterface secExam : exams.getExams(schDay, timePer)) {
                    Course sec = secExam.getCourse();
                    Room roomB = secExam.getRoom();
                    if (sec == null)
                    	throw new NullPointerException("Scheduled exam contained null course");
                    if (roomB == null)
                    	throw new NullPointerException("Scheduled exam contained null room");
					System.out.printf("\t %s happens on %d, period %d\n",sec.toString(),schDay,timePer);

                    /*
                     * int intEndTime = (startTimes[timePer] + sec.getExamLength()) % 24;
                     * String endTime = intEndTime + ":00:00 " + amPM[intEndTime / 12];
                     */

                    // examSet.add(new Exam(sec.toString(), curDayString, dayOfWeek, startTime, endTime, roomB.getName()));
                    examSet.add(new Exam(sec.toString(), curDayString, dayOfWeek, ""+timePer, roomB.getName()));
                }
            }
			curDay.add(Calendar.DAY_OF_YEAR, 1);
			
            schDay++;
        }

        // String[] headers = new String[] { "Course", "Exam Date", "Exam Day", "Start Time", "End Time", "Location" };
        String[] headers = new String[] { "Course", "Exam Date", "Exam Day", "Exam Period", "Location" };

        writeExams(path, headers, examSet);
    }

    /**
     * Writes the given exams out in with the given headers to a csv file.
     *
     * Assumes the iterator of the collection will return in the desired output
     * order and the header lenght is the same as the field lengths for all
     * exams.
     *
     * @throws Exception
     */
    private static void writeExams(String path, String[] headers, Collection<Exam> exams) throws Exception {
        DSVWriter wr = new DSVWriter(path);

        for (Exam ex : exams) {
            TreeMap<String, String> vals = new TreeMap<String, String>();

            for (int head = 0; head < headers.length; head++) {
                vals.put(headers[head], ex.fields[head]);
            }

            wr.writeEntry(vals);
        }
    }

    private static class Exam implements Comparable<Exam> {
        public String[] fields;

        public Exam(String... columns) {
            fields = columns;
        }

        public int compareTo(Exam toComp) {

            // i=typical index variable, so iSelf is index for self (this).
            int iSelf = 0;
            int iOther = 0;

            while ((iOther < toComp.fields.length) && (iSelf < this.fields.length)) {
                int fieldDiff = this.fields[iSelf].compareTo(toComp.fields[iOther]);

                iSelf++;
                iOther++;

                if (fieldDiff != 0) {
                    return fieldDiff;
                }
            }

            /*
             * If we get here, one is longer than the other but equal in the
             * first iSelf/iOther fields. Alphabetically then, the shorter
             * should be smaller in length (ex. negative return for compareTo is
             * 'less than')
             */
            return this.fields.length - toComp.fields.length;
        }
    }


    /*
     * -
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * LOAD ACTIONS
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */

    /**
     * Opens a file containing Room information and populates the room database
     * for use elsewhere
     *
     * @param roomFile
     *            File that contains the room information #FIXME add more to
     *            this later
     * @throws IOException
     */
    public static void loadRooms(String roomFile) throws IOException {
        DSVScanner obs = new DSVScanner(roomFile);

        while (obs.hasNextEntry()) {
            Map<String, String> nextEntry = obs.getNextEntry();
            String build = parseName(nextEntry.get("Building"), "room's building");
            String roomNum = parseName(nextEntry.get("Room Number"), "room's number");
            Room toAdd = new Room(build + roomNum);
            String[] techs = parseName(nextEntry.get("Tech Available"), "room's tech").split(",");

            for (int i = 0; i < techs.length; i++) {
                techs[i] = techs[i].trim().toUpperCase().replaceAll("\\s+", "");
            }

            int seatCount = parseNum(nextEntry.get("Seating"), "Seating", "for a room");

            toAdd.addResource(Resource.SEAT, seatCount);

            ArrayList<String> issues = new ArrayList<String>();

AddTechs:   for (String tech : techs) {

                /*
                 * I'm changing tech the following IF if it gets in, copy for
                 * error message
                 */
                String oriTech = tech;
                int quantity = Room.getDefaultNumber();

                if ((tech.indexOf("(") > 0) && (tech.indexOf(")") > tech.indexOf("("))) {
                    String quantString = tech.substring(tech.indexOf("(") + 1, tech.indexOf(")"));

                    tech = tech.substring(0, tech.indexOf("("));

                    for (Character c : quantString.toCharArray()) {
                        if (!Character.isDigit(c)) {
                            issues.add(String.format("Malformed entry tech for rooom %s %s, got %s", build, roomNum, oriTech));

                            continue AddTechs;
                        }
                    }

                    quantity = Integer.parseInt(quantString);
                }

                Resource techType = Resource.valueOf(tech);

                if (techType == null) {
                    issues.add(String.format("Malformed entry tech for rooom %s %s, got %s", build, roomNum, oriTech));

                    continue AddTechs;
                }

                toAdd.addResource(techType, quantity);
            }

            if (issues.size() > 0) {
                System.out.println("Issues:");
                System.out.println("-" + issues.remove(0));
            }

            RoomDB.getRoomDB().addRoom(build + roomNum, toAdd);
        }
    }

    /**
     * This loads the student course file and fills up the course database and
     * student database
     *
     * #FIXME, There may be another file that talks about courses with exams, or
     * without, or says which one does and does not have exams. Leaving this
     * field open until further information
     *
     * @param courseFile
     *            The file student course file that for each student, lists the
     *            courses they are in (and attributes about that course)
     * @param examFile
     * @throws IOException
     */
    public static void loadCourses(String courseFile, String examFile) throws IOException {
ExamFile:{
            if (examFile != null) {
                DSVScanner obs = new DSVScanner(examFile);

                while (obs.hasNextEntry()) {
                    Map<String, String> nextEntry = obs.getNextEntry();
                }
            }
        }

        StudentDB stDB = StudentDB.getStudentDB();
        CourseDB cDB = CourseDB.getCourseDB();

StduentFile:{
            DSVScanner obs = new DSVScanner(courseFile);

            while (obs.hasNextEntry()) {
                Map<String, String> stuLine = obs.getNextEntry();
                int studentID = parseNum(stuLine.get("Student"), "ID", "for a student");
                String name = parseName(stuLine.get("Name"), "student").trim();
                String[] courseSec = parseCourseSection(stuLine.get("Course/Section"));

                if (courseSec == null) {
                    continue;
                }

                char secLet = courseSec[1].charAt(0);

                // Currently days of the week does not matter for use,
                // #FIXME, "inTime" for course?
                // For now I'll take the first time, if it meets.
                String startTime = stuLine.get("State Time");

                startTime = ((startTime == null)
                             ? ""
                             : startTime.trim());

                String teachName = parseName(stuLine.get("(Link to) Course Section First Name"), "teacher's first name", "N");

                teachName += parseName(stuLine.get("(Link to) Course Section Sec All Faculty Last Names"), "teacher's last name", "/A");

                Course inCourse;

                if (!cDB.contains(courseSec[0])) {
                    cDB.addCourse(courseSec[0], new Course(courseSec[0]));
                }

                inCourse = cDB.getCourse(courseSec[0]);

                Section inSection;

                if (inCourse.getSection(courseSec[1].charAt(0)) == null) {
                    inCourse.addSection(secLet, new Section(inCourse, secLet, teachName, startTime));
                }

                inSection = inCourse.getSection(secLet);

                if (stDB.getStudent(studentID) == null) {
                    stDB.addStudent(studentID, new Student(name));
                }

                inSection.addStudent(studentID);
            }
        }
    }

    private static String[] parseCourseSection(String courseSec) {
        if (courseSec == null) {
            throw new NullPointerException("Course and Section not specified for a student");
        }

        /*
         * After triming, singularizing, and splitting whitespace, assume we are
         * left with something in the form Subject COURSENUMBER SECTION For now,
         * we just return the subject/course number together and section as a
         * string.
         */
        String[] parts = courseSec.trim().replaceAll("\\s+", " ").split("\\s");

        if (parts[1].matches(".*L")) {
            return null;
        } else if ((parts.length != 3) || (parts[2].length() != 1)) {
            String errorMes = String.format("String mangled, got %s but expected something like Subject Course# SectionLetter, i.e. ECON 1312 A\"",
                                            courseSec);

            throw new IllegalArgumentException(errorMes);
        }

        return new String[] { parts[0] + parts[1], parts[2] };
    }

    private static String parseName(String aName, String forA, String def) {
        if ((aName == null) && (def == null)) {
            throw new NullPointerException("No name in file for a " + forA + ".");
        } else if (aName == null) {
            return def;
        }

        return aName;
    }

    private static String parseName(String aName, String forA) {
        return parseName(aName, forA, null);
    }

    /**
     * Parses a number, throwing the error if needed. An error message will be
     * displayed as: forWhat_strNum_concern. (_=space) {@link
     * FileProcessor.parseNum(String,String,String,Integer)} for giving a
     * default value if strNum is null
     */
    private static int parseNum(String strNum, String forWhat, String concern) {
        return parseNum(strNum, forWhat, concern, null);
    }

    /**
     * Parses a number, throwing the error if needed. An error message will be
     * displayed as: forWhat_strNum_concern. (_=space) {@link
     * FileProcessor.parseNum(String,String,String)}
     *
     * nullAs is what to take as the default value if strNum is null. nullAs
     * being null means to ignore this argument.
     */
    private static int parseNum(String strNum, String forWhat, String concern, Integer nullAs) {
        try {

            /* Pardon the string for leading and trailing whitespace */
            strNum = (strNum == null)
                     ? null
                     : strNum.trim();

            return Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            if (nullAs != null) {
                return nullAs;
            }

            throw new NumberFormatException(forWhat + " " + strNum + " " + concern + ".");
        }
    }
}
