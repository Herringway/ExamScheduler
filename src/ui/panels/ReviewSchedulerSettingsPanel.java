package ui.panels;

//~--- non-JDK imports --------------------------------------------------------

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import io.FileProcessing.FileProcessor;

import org.apache.log4j.Logger;

import ui.ExamSchedulerMain;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * A panel for reviewing scheduler settings.
 */
public class ReviewSchedulerSettingsPanel extends ApplicationPanel {
    private static Logger log = Logger.getLogger(FileProcessor.class.getName());
    private static final long serialVersionUID = -2079726290397496169L;
    private JLabel examFileStatusLabel, roomFileStatusLabel, courseFileStatusLabel, chosenStartDateLabel, chosenOutputFileLabel, examFileLocation,
                   roomFileLocation, courseFileLocation;
    private JPanel centerPanel;

    /**
     * Constructs ReviewSchedulerSettingsPanel.
     * @param name ReviewSchedulerSettingsPanel name
     */
    public ReviewSchedulerSettingsPanel(String name) {
        super(name);

        setBorder(null);
        setLayout(new BorderLayout(0, 0));

        centerPanel = new JPanel();

        centerPanel.setBorder(null);
        add(centerPanel, BorderLayout.WEST);
        centerPanel.setLayout(new FormLayout(new ColumnSpec[] {
            FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
            FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
            FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        }));

        JLabel startDateLabel = new JLabel("Start date");

        startDateLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        centerPanel.add(startDateLabel, "2, 2");

        chosenStartDateLabel = new JLabel();

        chosenStartDateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        centerPanel.add(chosenStartDateLabel, "4, 2");

        chosenOutputFileLabel = new JLabel();

        chosenOutputFileLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        centerPanel.add(chosenOutputFileLabel, "4, 4");

        JLabel lblOutputFileLocation = new JLabel("Output file location");

        lblOutputFileLocation.setFont(new Font("Dialog", Font.BOLD, 12));
        centerPanel.add(lblOutputFileLocation, "2, 4");

        JLabel examFileLabel = new JLabel("Exam file status");

        examFileLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        centerPanel.add(examFileLabel, "2, 6");

        examFileLocation = new JLabel();

        examFileLocation.setFont(new Font("Dialog", Font.PLAIN, 12));
        centerPanel.add(examFileLocation, "4, 6");

        JLabel courseFileLabel = new JLabel("Course file status");

        courseFileLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        centerPanel.add(courseFileLabel, "2, 8");

        courseFileLocation = new JLabel();

        courseFileLocation.setFont(new Font("Dialog", Font.PLAIN, 12));
        centerPanel.add(courseFileLocation, "4, 8");

        JLabel roomFileLabel = new JLabel("Room file status");

        roomFileLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        centerPanel.add(roomFileLabel, "2, 10");

        roomFileLocation = new JLabel();

        roomFileLocation.setFont(new Font("Dialog", Font.PLAIN, 12));
        centerPanel.add(roomFileLocation, "4, 10");

        JPanel northPanel = new JPanel();

        add(northPanel, BorderLayout.NORTH);
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Review Scheduler Settings");

        northPanel.add(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        JSeparator separator = new JSeparator();

        northPanel.add(separator);

        Component verticalStrut = Box.createVerticalStrut(20);

        northPanel.add(verticalStrut);

        status = "awaiting user confirmation";
    }

    private void loadFiles() {
        SchedulerSettingsPanel settings =
            (SchedulerSettingsPanel) ExamSchedulerMain.getInstance().getApplicationFrame().getPanel("Scheduler Settings");

        try {
            FileProcessor.loadRooms(settings.getRoomFilePath());

            roomFileStatusLabel = new JLabel("Okay");

            roomFileStatusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            roomFileStatusLabel.setForeground(new Color(0, 128, 0));
            centerPanel.add(roomFileStatusLabel, "6, 10");
        } catch (Exception e) {
            log.error(e.getMessage());

            roomFileStatusLabel = new JLabel("Problems encountered");

            roomFileStatusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            roomFileStatusLabel.setForeground(new Color(128, 0, 0));
            centerPanel.add(roomFileStatusLabel, "6, 10");
            inputFail(e.getMessage());

            return;
        }

        try {
            FileProcessor.loadCourses(settings.getCourseFilePath(), settings.getExamFilePath());

            courseFileStatusLabel = new JLabel("Okay");

            courseFileStatusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            courseFileStatusLabel.setForeground(new Color(0, 128, 0));
            centerPanel.add(courseFileStatusLabel, "6, 8");

            examFileStatusLabel = new JLabel("Okay");

            examFileStatusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            examFileStatusLabel.setForeground(new Color(0, 128, 0));
            centerPanel.add(examFileStatusLabel, "6, 6");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());

            courseFileStatusLabel = new JLabel("Problems encountered");

            courseFileStatusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            courseFileStatusLabel.setForeground(new Color(128, 0, 0));
            centerPanel.add(courseFileStatusLabel, "6, 8");

            examFileStatusLabel = new JLabel("Problems encountered");

            examFileStatusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            examFileStatusLabel.setForeground(new Color(128, 0, 0));
            centerPanel.add(examFileStatusLabel, "6, 6");
            inputFail(e.getMessage());

            return;
        }

        inputSuccess();
    }

    private void inputSuccess() {
        status = "input files okay, awaiting user confirmation";

        ExamSchedulerMain.getInstance().getApplicationFrame().updateProgress(this);
    }

    private void inputFail(String message) {
        status = "problems encountered, can not proceed";

        ExamSchedulerMain.getInstance().getApplicationFrame().disableContinue();
        ExamSchedulerMain.getInstance().getApplicationFrame().updateProgress(this);

        String m = "Error encountered when loading input files: " + message + "\n";

        m += "See the help file for correct input file format, and see the log file for more details about the error.\n";
        m += "Application will exit now...";

        ExamSchedulerMain.getInstance().error(m, true);
    }

    @Override
    public void active() {
        setLabels();
        loadFiles();
    }

    private void setLabels() {
        SchedulerSettingsPanel settings =
            (SchedulerSettingsPanel) ExamSchedulerMain.getInstance().getApplicationFrame().getPanel("Scheduler Settings");

        courseFileLocation.setText(settings.getCourseFilePath());
        examFileLocation.setText(settings.getExamFilePath());
        roomFileLocation.setText(settings.getRoomFilePath());
        chosenStartDateLabel.setText(ExamSchedulerMain.formatDate(settings.getStartDate().getTime(), "dd MMMM yyyy"));
        chosenOutputFileLabel.setText(settings.getSaveFilePath());
    }

    @Override
    public void navigateNext() {}
}
