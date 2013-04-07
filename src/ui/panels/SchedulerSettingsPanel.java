package ui.panels;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Databases.CourseDB;
import SharedData.Databases.RoomDB;
import SharedData.Databases.StudentDB;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import com.michaelbaranov.microba.calendar.DatePicker;

import org.apache.log4j.Logger;

import ui.ExamSchedulerMain;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A panel that is shown when initializing scheduler.
 * @author Christian
 */
public class SchedulerSettingsPanel extends ApplicationPanel {
    private static final long serialVersionUID = -9160953845530006827L;
    private JTextField roomFileText;
    private JTextField courseFileText;
    private static Logger log = Logger.getLogger(SchedulerSettingsPanel.class.getName());
    private JTextField outputFileText;
    private DatePicker datePicker;

    /**
     * Constructs SchedulerSettingsPanel.
     * @param name SchedulerSettingsPanel name
     */
    public SchedulerSettingsPanel(String name) {
        super(name);

        setLayout(new BorderLayout(0, 0));

        JPanel northPanel = new JPanel();

        add(northPanel, BorderLayout.NORTH);
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Scheduler Settings");

        northPanel.add(titleLabel);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        JSeparator separator = new JSeparator();

        northPanel.add(separator);

        Component verticalStrut = Box.createVerticalStrut(20);

        northPanel.add(verticalStrut);

        status = "awaiting file locations";

        active();    // this is the first panel, so it is active by default -- BEWARE if this changes

        JPanel centerPanel = new JPanel();

        add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BorderLayout(0, 0));

        JPanel internalNorthPanel = new JPanel();

        centerPanel.add(internalNorthPanel, BorderLayout.CENTER);
        internalNorthPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        }));

        JLabel instructionLabelInput = new JLabel("Input file locations");

        instructionLabelInput.setFont(new Font("Dialog", Font.ITALIC, 12));
        internalNorthPanel.add(instructionLabelInput, "2, 2");

        JButton courseFileButton = new JButton("Course File");

        courseFileButton.setToolTipText("Choose the location of the course file");
        courseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Open Course File", "txt");

                chooser.setDialogTitle("Open Course File");
                chooser.setFileFilter(filter);

                int returnVal = chooser.showOpenDialog(ExamSchedulerMain.getInstance().getApplicationFrame());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    courseFileText.setText(chooser.getSelectedFile().getAbsolutePath());
                }

                checkContinue();
            }
        });
        internalNorthPanel.add(courseFileButton, "2, 4");

        courseFileText = new JTextField();

        courseFileText.setToolTipText("Use the adjacent button to populate this text box");
        courseFileText.setEditable(false);
        courseFileText.setColumns(10);
        internalNorthPanel.add(courseFileText, "4, 4, fill, default");

        JButton roomFileButton = new JButton("Room File");

        roomFileButton.setToolTipText("Choose the location of the room file");
        roomFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Open Room File", "txt");

                chooser.setDialogTitle("Open Room File");
                chooser.setFileFilter(filter);

                int returnVal = chooser.showOpenDialog(ExamSchedulerMain.getInstance().getApplicationFrame());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    roomFileText.setText(chooser.getSelectedFile().getAbsolutePath());
                }

                checkContinue();
            }
        });
        internalNorthPanel.add(roomFileButton, "2, 6");

        roomFileText = new JTextField();

        roomFileText.setToolTipText("Use the adjacent button to populate this text box");
        roomFileText.setEditable(false);
        roomFileText.setColumns(10);
        internalNorthPanel.add(roomFileText, "4, 6, fill, default");

        JLabel instructionLabelOutput = new JLabel("Output file location");

        instructionLabelOutput.setFont(new Font("Dialog", Font.ITALIC, 12));
        internalNorthPanel.add(instructionLabelOutput, "2, 8");

        JButton outputFileButton = new JButton("Output File");

        outputFileButton.setToolTipText("Choose the output schedule save location");
        outputFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser();

                fileChooser.setDialogTitle("Choose Schedule Output File Location");

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Plain text files", "txt");

                fileChooser.setFileFilter(filter);

                int userSelection = fileChooser.showSaveDialog(ExamSchedulerMain.getInstance().getApplicationFrame());

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String fileToSave = fileChooser.getSelectedFile().getAbsolutePath();

                    if (!fileToSave.toLowerCase().endsWith(".csv")) {
                        fileToSave += ".csv";
                    }

                    outputFileText.setText(fileToSave);
                }

                checkContinue();
            }
        });
        internalNorthPanel.add(outputFileButton, "2, 10");

        outputFileText = new JTextField();

        outputFileText.setToolTipText("Use the adjacent button to populate this text box");
        outputFileText.setEditable(false);
        outputFileText.setColumns(10);
        internalNorthPanel.add(outputFileText, "4, 10, fill, default");

        JPanel internalCenterPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) internalCenterPanel.getLayout();

        flowLayout.setAlignment(FlowLayout.LEFT);
        centerPanel.add(internalCenterPanel, BorderLayout.NORTH);

        JLabel lblChooseStartDate = new JLabel("Exam start date");

        lblChooseStartDate.setFont(new Font("Dialog", Font.ITALIC, 12));
        internalCenterPanel.add(lblChooseStartDate);

        Component horizontalStrut = Box.createHorizontalStrut(11);

        internalCenterPanel.add(horizontalStrut);

        datePicker = new DatePicker();

        datePicker.setToolTipText("Select a date for the first exam");
        internalCenterPanel.add(datePicker);
    }

    /**
     * Check to see if the user is allowed to proceed (i.e. all input has been given).
     */
    private void checkContinue() {
        if (allFilesSpecified()) {
            ExamSchedulerMain.getInstance().getApplicationFrame().enableContinue();

            updateStatus("all file locations given, click Continue to load input files");
        } else {
            ExamSchedulerMain.getInstance().getApplicationFrame().disableContinue();

            updateStatus("awaiting file locations");
        }
    }

    /**
     * @return True if the user has specified all the required file paths, false otherwise
     */
    private boolean allFilesSpecified() {
        if ((roomFileText.getText() == null) || (roomFileText.getText().trim().length() < 1)) {
            return false;
        }

        if ((courseFileText.getText() == null) || (courseFileText.getText().trim().length() < 1)) {
            return false;
        }

        if ((outputFileText.getText() == null) || (outputFileText.getText().trim().length() < 1)) {
            return false;
        }

        return true;
    }

    @Override
    public void active() {
        log.info("Specifying scheduler settings");
        CourseDB.clear();
        RoomDB.clear();
        StudentDB.clear();
    }

    @Override
    public void navigateNext() {}

    @Override
    public void userRequestedClose() {
        ExamSchedulerMain.getInstance().exit(0);    // don't need to confirm with user
    }

    /**
     * @return Room file path specified by user
     */
    public String getRoomFilePath() {
        return roomFileText.getText();
    }

    /**
     * @return Course file path specified by user
     */
    public String getCourseFilePath() {
        return courseFileText.getText();
    }

    /**
     * @return Save file path specified by user
     */
    public String getSaveFilePath() {
        return outputFileText.getText();
    }

    /**
     * @return Start date specified by user
     */
    public Calendar getStartDate() {
        Calendar cal = Calendar.getInstance();

        cal.setTime(datePicker.getDate());

        return cal;
    }
}
