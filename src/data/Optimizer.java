package data;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Course;

import SharedData.Databases.CourseDB;
import SharedData.Databases.RoomDB;
import SharedData.Databases.StudentDB;

import SharedData.Resource;
import SharedData.Room;
import SharedData.Section;

import io.FileProcessing.FileProcessor;

import ui.panels.ScheduleRunningPanel;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

/*Author(s): Zack Delaney, Sarah Van Der Laan
*
* Purpose: This class will do all the manipulating and working of the schedules.
*
* Aside from the constructor and the activator, everything is private. This is to avoid other groups accidentally trying to use *any* of
* our functions in contexts they should not be used in.
* Date: 14/02/2013
*
* Date: 05/03/2013
* Changes made:
*      - Made an constructor, initializing all relevant pieces of information aside from the weight matrix and schedule.
*      - Sarah added some code to the getSmallestAvailableColour, is not functional yet.
*      - Just discovered I've been misspelling "initialise" (it's initialize) so I've been trying to go through and correct that where I see it.
*
* Date: 14/03/2013
* Changes made:
*  - Finished the constructor
*  - Commented out obsolete functions
*  - Started getFirstColour
*  - Wrote a function to calculate the weight of a colour.
*  - Added variables to state the number of exams per day and maximum numner of days.
*
*  Date: 31/03/2013
*  Changes made:
*
*  - Progress has been made on optimizeSchedule
*
*  Date: 4/4/2013
*
*  This changelog has been very poorly maintained, so I'm going to give up keeping a bullet-form list of things that have happened.
*  Optimizer is now being bug-tested.
 */
public class Optimizer extends Thread {

    // The panel that created this thread (report back upon completion)
    ScheduleRunningPanel threadCreator;

    // Where to save the completed schedule
    private String saveFilePath;

    // The first day of exams
    private Calendar startDate;

    // These are constants
    private final int NUM_EXAMS_PER_DAY = 3;
    private final int MAX_NUM_OF_DAYS = 12;

    // THESE SHOULD BE INITIALISED IN THE CONSTRUCTOR
    private CourseDB courses;
    private RoomDB rooms;
    private StudentDB students;
    private int numNodesProcessed;    // equivalent to No-Of-Coloured-Courses in the paper.
    private int numOfCourses;    // needed, and never changes.
    private Node[] nodes;
    private int[][] weightMatrix;

    // THESE SHOULD BE INITIALISED IN THE CONSTRUCTOR
    // -----------------------------------------------
    // THESE ARE WORKED OUT AS PART OF THE ALGORITHM
    private Schedule newSchedule;

    // THESE ARE WORKED OUT AS PART OF THE ALGORITHM
    // ---------------------------------------------
    // FUNCTIONS USED BELOW. NOTE THAT PRIVATE FUNCTIONS ARE MEANT TO BE HELPER FUNCTIONS TO THE MAIN FUNCTION, AND THAT SOME OF THEM ARE NOT STRICTLY NECESSARY
    // Initializing the Optimizer object. Requires the course database, room database, and student database.
    // NOTE THIS INITIALISES THE NODE ARRAY, INITALISE NODE ARRAY IS NOW DEPRECATED, BUT IN THIS CASE, ACTUALLY DON'T USE IT.
    public void run() {
       // System.out.println("Now initializing private or protected variables. ");

    	System.out.println("Scheduler has been started.");
        String[] courseIdentifierList = courses.getCourses();

        numOfCourses = courseIdentifierList.length;
        numNodesProcessed = 0;

        //System.out.println("numOfCourses initialised: numOfCourses = " + numOfCourses);
        //System.out.println("numNodesProcessed initialised: numNodesProcessed = " + numNodesProcessed);

        nodes = new Node[numOfCourses];

        //System.out.println("The array of nodes has been initialised");

        for (int i = 0; i < numOfCourses; i++) {
            nodes[i] = new Node(i, courses.getCourse(courseIdentifierList[i]));
        }

        //System.out.println("Elements of that array have been initialised");
        //System.out.println("Initialising weight matrix");

        weightMatrix = new int[numOfCourses][numOfCourses];

        /*
         *  This mess of for loops makes the weight matrix. The first two loops iterate through the nodes.
         * For each node (both i and j), which is a course, we iterate through the list of sections.
         * For each section (both k and m), we iterate through the list of students.
         * If studentListI[n] and studentListJ[o] are the same, it means there is a student that is
         * taking both course I and course J, so increment weightMatrix[i][j]. Note that
         * because both i and j start at zero, this matrix should be mirrored along the diagonal.
         * We need the continue if i == j because otherwise we would be counting all the
         * students in a course as being in a course, which is highly redundant and could throw
         * off the algorithm.
         *
         */
        for (int i = 0; i < numOfCourses; i++) {    // For each node along the row, get it's course
            Course courseI = nodes[i].getCourse();

            for (int j = 0; j < numOfCourses; j++) {    // For each node along the column get it's course
                Course courseJ = nodes[j].getCourse();

                // Making sure we aren't looking at the same node from i and j.
                if (i == j) {
                    continue;
                }

                Character[] sectionListI = courseI.getSectionKeys();

                for (int k = 0; k < sectionListI.length; k++) {    // for each course in row, get it's sections
                    Section sectionI = courseI.getSection(sectionListI[k]);
                    Character[] sectionListJ = courseJ.getSectionKeys();

                    for (int m = 0; m < sectionListJ.length; m++) {    // for each course in the columsn, get it's sections
                        Section sectionJ = courseJ.getSection(sectionListJ[m]);
                        Integer[] studentListI = sectionI.getStudents();
                        Integer[] studentListJ = sectionJ.getStudents();

                        for (int n = 0; n < studentListI.length; n++) {
                            for (int o = 0; o < studentListJ.length; o++) {
                                if (studentListI[n].equals(studentListJ[o])) {
                                    weightMatrix[i][j]++;
                                }
                            }    // for o
                        }    // for n
                    }    // for m
                }    // for k
            }    // for j
        }    // for i

        /*
         *  Oh ye gods that was ugly. If anyone else has to look at that, I apologize in advance, I commented as best I could.
         *
         */
        /*
        System.out.println("The weightMatrix has been constructed. ");
        System.out.println("Now assigning node degrees and adjacency lists");
        System.out.println("The number of courses: " + numOfCourses);
        System.out.println("The number of nodes: " + nodes.length);
		*/

        for (int i = 0; i < numOfCourses; i++) {
            int Count = 0;
            ArrayList<Node> thisNodeList = new ArrayList<Node>();
            int j = 0;

            for (; j < numOfCourses; j++) {
                if (weightMatrix[i][j] > 0) {
                    Count++;

                    thisNodeList.add(nodes[j]);
                }
            }

            /*
             * if(Count > 0){
             *       thisNodeList.add(nodes[Count]);
             * }
             */
            nodes[i].setDegree(Count);

            Node[] arrNode = new Node[thisNodeList.size()];

            arrNode = thisNodeList.toArray(arrNode);

            nodes[i].setAdjNodes(arrNode);
        }

        
        
       // System.out.println("Node degrees and adjacency assigned");

        Schedule sched = this.optimizeSchedule();


        try {
            FileProcessor.writeExamFile(saveFilePath, sched, startDate);
        } catch (Exception e) {

            // TODO deal with it
            e.printStackTrace();
        }
        threadCreator.schedulerFinished();
    }

    public Optimizer(CourseDB c, RoomDB r, StudentDB s, ScheduleRunningPanel threadCreator, String saveFilePath, Calendar startDate) {
        System.out.println("Beginning the initializing of optimization.");

        courses = c;
        rooms = r;
        students = s;
        this.threadCreator = threadCreator;
        this.saveFilePath = saveFilePath;
        this.startDate = startDate;

        if ((courses == null) || (rooms == null) || (students == null) || (threadCreator == null)) {
            System.out.println("You have passed us a null pointer.");
            System.exit(0);
        }

        System.out.println("The Course, Room, and Student Databases are initialized in the optimizer.");
    }

    // The method to be called to perform the optimization. This is part B in our notes.
    public Schedule optimizeSchedule() {
        if (numNodesProcessed == numOfCourses) {    // A simple check to see if we've made our schedule.
            System.out.println("We have. Returning our schedule.");

            return newSchedule;
        }

        // Create a new schedule
    //    System.out.println("Making the Schedule Object.");

        newSchedule = new Schedule(MAX_NUM_OF_DAYS, NUM_EXAMS_PER_DAY, rooms);

      //  System.out.println("Now starting step 1 part 1, sorting the array of nodes.");
        Arrays.sort(nodes);    // Step 1 part 1. Will implement further sorting if it proves necessary.
       // System.out.println("Step 1 of 1: Completed");
       // System.out.println("Part B Starting now:");

        for (int i = 0; i < nodes.length; i++) {
            System.out.println("Checking to see if we've finished our schedule. We have scheduled " + numNodesProcessed + " nodes");

            if (numNodesProcessed == numOfCourses) {    // A simple check to see if we've made our schedule.
                System.out.println("We have. Returning our schedule.");

                return newSchedule;
            }

            int zeroVal = 0;

            System.out.println("We have not. Checking to see if the course we're looking at has been scheduled.");

            if (!nodes[i].isThisColoured()) {    // If the node we're looking at is not coloured, we'll do our best to colour it.
                System.out.println("It has not been, trying to colour it now.");

                ScheduledExamSlot Rab = null;

                if (i == zeroVal) {    // If this is the first node we see, it gets special treatment.
                    System.out.println("This is the first node to look at. Entering getFirstColour...");

                    Rab = getFirstColour(nodes[i]);

                    if (Rab == null) {
                        System.out.println("There is no schedule possible, there is no room that can fit the largest class.");

                        zeroVal++;

                        continue;
                    }
                } else {
                    Rab = getSmallestAvailableColour(nodes[i]);
                }

                if (Rab != null) {
                    System.out.println("We've managed to get a \"colour\" that works. Now processing the node.");
                    System.out.println("Getting space requirements for the course... ");

                    int spaceReq = nodes[i].getStudentLevel();

                    System.out.println("This course requires " + spaceReq + " seats.");
                    System.out.println("Looking for a room in our slot that fits.");

                    Room goodRoom = Rab.getRoomWithBestCapacity(spaceReq);
                    if(goodRoom == null){
                    	System.out.println("\n\n THIS SHOULD NEVER, EVER HAPPEN LOGICALLY. GO CHECK getRoomWIthBestCapacity.");
                    	System.out.println("This is the original node.");
                    	throw new NullPointerException("Unable to find room for exam");
                    }
                    System.out.println("In standard nodes, printing out days and time");
                    System.out.println(Rab.getDay());
                    System.out.println(Rab.getTimeSlot());
                    Exam temp = new Exam(nodes[i].getCourse(), goodRoom);

                    Rab.addExam(temp);
                    nodes[i].thisIsColoured(Rab);

                    numNodesProcessed++;
                }
            }

            System.out.println("It has been scheduled. Checking neighbours and sorting them...");
            nodes[i].sortAdjNodes();

            Node[] adjToI = nodes[i].getAdjNodes();

            System.out.println("Nodes sorted and are in their own list. Now scanning...");

            for (int j = 0; j < adjToI.length; j++) {
             //   System.out.println("If an adjacent node is not coloured, attempt to colour it.");

                if (!adjToI[j].isThisColoured()) {
                    System.out.println("Attempting to colour an adjacent node.");
                    System.out.println("Entering getSmallestAvailableColour");

                    ScheduledExamSlot Rcd = getSmallestAvailableColour(adjToI[j]);

                    System.out.println("If we found an available colour, process that node.");

                    if (Rcd != null) {
                        numNodesProcessed++;

                        int spaceReq = adjToI[j].getStudentLevel();
                        Room goodRoom = Rcd.getRoomWithBestCapacity(spaceReq);
                        
                        if(goodRoom == null){
                        	System.out.println("\n\n THIS SHOLD NEVER, EVER HAPPEN LOGICALLY. GO CHECK getRoomWIthBestCapacity.");
                        	System.out.println("We're currently just dealing with the adjacent nodes.");
                        	throw new NullPointerException("Unable to find room for exam");
                        }
                        System.out.println("In adjacency nodes, printing out days and time");
                        System.out.println(Rcd.getDay());
                        System.out.println(Rcd.getTimeSlot());
                        Exam temp = new Exam(adjToI[j].getCourse(), goodRoom);

                        Rcd.addExam(temp);
                        adjToI[j].thisIsColoured(Rcd);
                    }
                }
            }    // for int j

            //System.out.println("List scanned, go look at more courses.");
        }    // for int i

        System.out.println("All nodes should be handled at this point.");

        if (numNodesProcessed != numOfCourses) {
            System.out.println("SOMETHING HAS GONE HORRIBLY WRONG! WE THINK WE'RE FINISHED BUT WE ARE NOT");
            System.out.println("\n");
            System.out.println("The number of nodes processed : " + numNodesProcessed);
            System.out.println("The number of courses, ie the number of nodes in total : " + numOfCourses);
            System.out.println("\n");

            return null;
        }

        System.out.println("--------------- We're done with Optimizer -----------------");

        return newSchedule;
    }

    public double getPercentCompleted() {
        return (double) numNodesProcessed / (double) numOfCourses;
    }

    // As described in part C, apparently we'll need this?
    private ScheduledExamSlot getFirstColour(Node Ci) {
        System.out.println(" ------ INSIDE GET FIRSTCOLOUR --------");

        for (int i = 0; i < newSchedule.getNumDays(); i++) {
            for (int j = 0; j < newSchedule.getNumExamsPerDay(); j++) {

                // At this point there needs to be a comparison between the concurrency limit of the color ij,
                // and the concurrency level of Ci. Because their idea of concurrency is oversimplified for
                // our use, we need to sort out how we plan to use this ourselves.
                ScheduledExamSlot Rij = newSchedule.getExamSlot(i, j);
                Room[] rooms = Rij.getRooms();
                int available = 0;

                for (int k = 0; k < rooms.length; k++) {
                    if (rooms[k].getResource(Resource.SEAT) >= Ci.getStudentLevel()) {
                        available++;
                    }
                }

                if (available > 0) {
                    System.out.println(" ------- LEAVING WITH A VALID VALUE ------");

                    return Rij;
                }
            }
        }

        System.out.println("------------LEAVING UNABLE TO ENTER A VALUE ------------");

        return null;
    }

    // Again, we will need this and it looks like a doozy to write.
    private ScheduledExamSlot getSmallestAvailableColour(Node Ci) {
        System.out.println("------------ INSIDE getSmallestAvailableColour --------------");

        Node[] adjNodes = Ci.getAdjNodes();

        System.out.println("Ci's adjacency matrix is length : " + adjNodes.length);

        boolean isValid;
        ScheduledExamSlot Rjk = null;
        ScheduledExamSlot Ref = null;

        System.out.println("\nEntering first for loop; will run " + MAX_NUM_OF_DAYS + " time.");

        for (int j = 0; j < MAX_NUM_OF_DAYS; j++) {
            System.out.println("Entering second for loop; will run " + newSchedule.getNumExamsPerDay() + " times, multiplied by " + MAX_NUM_OF_DAYS);

            for (int k = 0; k < newSchedule.getNumExamsPerDay(); k++) {
                Rjk = newSchedule.getExamSlot(j, k);
                
                System.out.println("Evaluating a slot. Check to see what's j and k values are.");
                System.out.println("The day (J) is = " + Rjk.getDay());
                System.out.println("The timeslot (K) is = " + Rjk.getTimeSlot());
                
                
                isValid = true;

                System.out.println("\nEntering third for loop; will run " + adjNodes.length + " times, multiplied by "
                                   + MAX_NUM_OF_DAYS * newSchedule.getNumExamsPerDay());

                for (int r = 0; r < adjNodes.length; r++) {
                    System.out.println("We're now in a case where we are checking every possibility in terms of nodes adjacent to a node.");

                    Ref = adjNodes[r].getSlot();

                    System.out.println("Checking to see if this adjacent node is in a slot already.");

                    if (Ref != null) {
                        System.out.println("This adjacent node has been slotted; getting it's info now...");

                        int day = Ref.getDay();
                        int timeSlot = Ref.getTimeSlot();

                        System.out.println("\nThis exam takes place on day " + day);
                        System.out.println("This exam is during time slot " + timeSlot);
                        System.out.println(
                            "Checking to see if we're not looking at the same day/slot combo as the potential slot for the node we're looking for.");

                        if ((day != j) || (timeSlot != k)) {
                        	System.out.println("It is not. Now running a battery of other tests.");
                            if (Math.abs(day - j) == 0) {
                                if (Math.abs(timeSlot - k) <= 0) {
                                	System.out.println("It failed the D1 D2 stuff.");
                                	isValid = false;

                                    break;
                                }    // end D1
                            }    // end D2
                            System.out.println("It passed the D1 and D2 stuff.");
                            
                            // CONCURRENCY LIMIT????
                            Room[] rooms = Rjk.getRooms();
                            int available = 0;

                            for (int s = 0; s < rooms.length; s++) {
                                if (rooms[s].getResource(Resource.SEAT) >= Ci.getStudentLevel()) {
                                    available++;
                                }
                            }

                            if (available == 0) {
                                isValid = false;
                            	System.out.println("It failed the concurrency limit stuff.");

                                break;
                            }

                            System.out.println("It passed the concurrency limit stuff.");
                            
                            /*
                            if (checkThreeExamsConstraint(Ci, Rjk, j) == false) {
                                isValid = false;
                            	System.out.println("It failed the ThreeExamConstraint stuff.");

                                break;
                            }
                            */

                            System.out.println("Three exam constraint has been omitted.");
                        }    // end if
                        else {
                            System.out.println("It was.");
                            isValid = false;
                            continue;
                            
                        }
                    }    // end if Ref != null
                    //        else {
                       // break;
                    //}
                }    // end for r

                if (isValid == true) {
                    System.out.println("Found smallest available colour for node " + Ci.getI());
                    System.out.println("--------------------------- LEAVING getSmallestAvailableColour ---------------------");
                    return Rjk;
                    
                }
                
            }    // end for k
        }    // end for j

        System.out.println("Failed to find smallest available colour for node " + Ci.getI());
        System.out.println("--------------------------- LEAVING getSmallestAvailableColour ---------------------");
        return null;
    }

    // I really get the feeling that they didn't think this through, but we apparently need this too?
    private boolean checkThreeExamsConstraint(Node Ci, ScheduledExamSlot Rjk, int j) {
        Course courseI = Ci.getCourse();
        Section[] sectionsI = courseI.getSectionValues();
        HashSet<Integer> Si = new HashSet<Integer>();

        // Builds a list of students in courses on Node Ci.
        for (int b = 0; b < sectionsI.length; b++) {
            Integer[] students = sectionsI[b].getStudents();

            for (int c = 0; c < students.length; b++) {
                Si.add(students[c]);
            }
        }

        // Iterate across every student
        for (int r = 0; r < Si.size(); r++) {
            int counter = 0;

            // Across every exam timeslot of a day
            for (int q = 0; q < newSchedule.getNumExamsPerDay(); q++) {
                ScheduledExamSlot ses = newSchedule.getExamSlot(j, q);
                ArrayList<Exam> exams = ses.getExams();
                ArrayList<Course> CRS = new ArrayList<Course>();

                // List of courses with exams at a certain timeslot
                for (Exam e : exams) {
                    CRS.add(e.getCourse());
                }

                // Across every course
                for (int u = 0; u < CRS.size(); u++) {
                    Section[] sectionsU = CRS.get(u).getSectionValues();
                    HashSet<Integer> Su = new HashSet<Integer>();

                    // A list of students in a particular course
                    for (int v = 0; v < sectionsU.length; v++) {
                        Integer[] students = sectionsU[v].getStudents();

                        for (int w = 0; w < students.length; w++) {
                            Su.add(students[w]);
                        }
                    }

                    // Checks if the original student from Si (r) is in the list of students Su
                    // (i.e. a student has two exams on the same day, in different timeslots)
                    if (Su.contains(r)) {
                        counter++;

                        if (counter >= 2) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
