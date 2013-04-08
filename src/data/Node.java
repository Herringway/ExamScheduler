package data;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Course;
import SharedData.Section;

//~--- JDK imports ------------------------------------------------------------

import java.util.Arrays;

/*
* Author(s): Zack Delaney
*
* Used by optimizer, completely empty at the moment, but this will need filling and testing later.
* Maybe the next milestone.
*
* Nodes need to be comparable to one another, so implement compareTo. They'll need the relevant course they're filling in for,
* a concurrency value (?), a colour value, and some way of knowing what colours they can be. It *might* be helpful for them
* to know about their neighbours, but I'm unsure as of yet.
* Date: 14/02/2013
*
* Date: 05/03/2013
* Changes made: I've added an i value, which they need to know. I missed this upon my first read through. I have also added the relevent course.
* Further additions will be needed later as above.
*
 */
public class Node implements Comparable<Node> {
    private int i;
    private Course course;
    private int degree;
    private int concurrencyLevel;
    private int studentLevel;
    private boolean isColoured;
    private Node[] adjNodes;
    private ScheduledExamSlot thisSlot;

    public Node(int iVal, Course c) {
        i = iVal;
        course = c;
        degree = -1;
        isColoured = false;
        studentLevel = 0;
        thisSlot = null;

        for (int j = 0; j < c.getSectionKeys().length; j++) {
            Section temp = c.getSection(c.getSectionKeys()[j]);

            studentLevel += temp.getStudents().length;
        }

        concurrencyLevel = course.getSectionKeys().length;
    }

    public ScheduledExamSlot getSlot() {
        return thisSlot;
    }

    public void setSlot(ScheduledExamSlot slot) {
        thisSlot = slot;
    }

    public void setAdjNodes(Node[] n) {
        adjNodes = n;
    }

    public void sortAdjNodes() {
        Arrays.sort(adjNodes);
    }

    public Node[] getAdjNodes() {
        return adjNodes;
    }

    public boolean isThisColoured() {
        return isColoured;
    }

    public void thisIsColoured(ScheduledExamSlot a) {
        thisSlot = a;
        isColoured = true;
    }

    public void thisIsNotColoured() {
        thisSlot = null;
        isColoured = false;
    }

    public int getI() {
        return i;
    }

    public Course getCourse() {
        return course;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int d) {
        degree = d;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public int getStudentLevel() {
        return studentLevel;
    }

    @Override
    public int compareTo(Node otherNode) {
        if (this.degree < otherNode.getDegree()) {
            return 1;
        } else if (this.degree > otherNode.getDegree()) {
            return -1;
        }

        return 0;
    }
}
