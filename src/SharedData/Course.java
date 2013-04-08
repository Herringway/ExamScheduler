package SharedData;

//~--- JDK imports ------------------------------------------------------------

import java.util.Hashtable;

public class Course {
    private String title;
    private Hashtable<Character, Section> sectionInfo;

    public Course(String courseTitle) {
        title = courseTitle;
        sectionInfo = new Hashtable<Character, Section>();
    }

    public void addSection(Character sectionLetter, Section newSection) {
        sectionInfo.put(sectionLetter, newSection);
    }

    public Character[] getSectionKeys() {
        return sectionInfo.keySet().toArray(new Character[] {});
    }

    public Section[] getSectionValues() {
        return sectionInfo.values().toArray(new Section[] {});
    }

    public Section getSection(char sectLetter) {
        return (sectionInfo.get(sectLetter));
    }

    @Override
    public String toString() {
        return title;
    }
}
