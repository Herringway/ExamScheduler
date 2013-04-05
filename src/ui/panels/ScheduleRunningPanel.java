package ui.panels;

//~--- non-JDK imports --------------------------------------------------------

import SharedData.Databases.CourseDB;
import SharedData.Databases.RoomDB;
import SharedData.Databases.StudentDB;

import data.Optimizer;

import org.apache.log4j.Logger;

import ui.ExamSchedulerMain;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * A panel to be shown when scheduler stage is active.
 */
public class ScheduleRunningPanel extends ApplicationPanel {
    private static final long serialVersionUID = -3712954268378404263L;
    private JButton pauseButton;
    private boolean paused = true;
    private JLabel startTimeLabel, percentCompleteLabel, runTimeLabel, infoLabel;
    private boolean firstStart = true;
    private static Logger log = Logger.getLogger(ScheduleRunningPanel.class.getName());
    private Optimizer optimizer_threaded = null;

    /**
     * Constructs ScheduleRunningPanel.
     * @param name ScheduleRunningPanel name
     */
    public ScheduleRunningPanel(String name) {
        super(name);

        setLayout(new BorderLayout(0, 0));

        JPanel northPanel = new JPanel();

        add(northPanel, BorderLayout.NORTH);
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Building Schedule");

        northPanel.add(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        JSeparator separator = new JSeparator();

        northPanel.add(separator);

        Component verticalStrut = Box.createVerticalStrut(20);

        northPanel.add(verticalStrut);

        JPanel southPanel = new JPanel();
        FlowLayout fl_southPanel = (FlowLayout) southPanel.getLayout();

        fl_southPanel.setAlignment(FlowLayout.RIGHT);
        add(southPanel, BorderLayout.SOUTH);

        pauseButton = new JButton();

        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseToggleClicked();
            }
        });
        southPanel.add(pauseButton);

        GridBagConstraints gbc_startTimeLabel;
        JPanel containerCenterPanel = new JPanel();

        add(containerCenterPanel, BorderLayout.CENTER);
        containerCenterPanel.setLayout(new BorderLayout(0, 0));

        JPanel internalCenterPanel = new JPanel();

        containerCenterPanel.add(internalCenterPanel, BorderLayout.NORTH);

        GridBagLayout gbl_internalCenterPanel = new GridBagLayout();

        gbl_internalCenterPanel.columnWidths = new int[] {
            12, 0, 158, 0, 39, 80, 8, 142, 0
        };
        gbl_internalCenterPanel.rowHeights = new int[] {
            16, 0, 0, 14, 0, 0, 0, 0
        };
        gbl_internalCenterPanel.columnWeights = new double[] {
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
        };
        gbl_internalCenterPanel.rowWeights = new double[] {
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
        };

        internalCenterPanel.setLayout(gbl_internalCenterPanel);

        JLabel timeStartedLabel = new JLabel("Time started");

        timeStartedLabel.setFont(new Font("Dialog", Font.PLAIN, 12));

        GridBagConstraints gbc_timeStartedLabel = new GridBagConstraints();

        gbc_timeStartedLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_timeStartedLabel.insets = new Insets(0, 0, 5, 5);
        gbc_timeStartedLabel.gridx = 1;
        gbc_timeStartedLabel.gridy = 0;

        internalCenterPanel.add(timeStartedLabel, gbc_timeStartedLabel);

        startTimeLabel = new JLabel();
        gbc_startTimeLabel = new GridBagConstraints();
        gbc_startTimeLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_startTimeLabel.insets = new Insets(0, 0, 5, 5);
        gbc_startTimeLabel.gridx = 2;
        gbc_startTimeLabel.gridy = 0;

        internalCenterPanel.add(startTimeLabel, gbc_startTimeLabel);

        JLabel percentCompleteTitleLabel = new JLabel("Percentage complete");

        percentCompleteTitleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));

        GridBagConstraints gbc_estimatedTimeLeftLabel = new GridBagConstraints();

        gbc_estimatedTimeLeftLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_estimatedTimeLeftLabel.insets = new Insets(0, 0, 5, 5);
        gbc_estimatedTimeLeftLabel.gridx = 1;
        gbc_estimatedTimeLeftLabel.gridy = 1;

        internalCenterPanel.add(percentCompleteTitleLabel, gbc_estimatedTimeLeftLabel);

        percentCompleteLabel = new JLabel();

        GridBagConstraints gbc_lblDays = new GridBagConstraints();

        gbc_lblDays.anchor = GridBagConstraints.WEST;
        gbc_lblDays.insets = new Insets(0, 0, 5, 5);
        gbc_lblDays.gridx = 2;
        gbc_lblDays.gridy = 1;

        internalCenterPanel.add(percentCompleteLabel, gbc_lblDays);

        JLabel elapsedRunningTimeLabel = new JLabel("Elapsed running time");

        elapsedRunningTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));

        GridBagConstraints gbc_elapsedRunningTimeLabel = new GridBagConstraints();

        gbc_elapsedRunningTimeLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_elapsedRunningTimeLabel.insets = new Insets(0, 0, 5, 5);
        gbc_elapsedRunningTimeLabel.gridx = 1;
        gbc_elapsedRunningTimeLabel.gridy = 2;

        internalCenterPanel.add(elapsedRunningTimeLabel, gbc_elapsedRunningTimeLabel);

        runTimeLabel = new JLabel();

        GridBagConstraints gbc_runTimeLabel = new GridBagConstraints();

        gbc_runTimeLabel.anchor = GridBagConstraints.WEST;
        gbc_runTimeLabel.insets = new Insets(0, 0, 5, 5);
        gbc_runTimeLabel.gridx = 2;
        gbc_runTimeLabel.gridy = 2;

        internalCenterPanel.add(runTimeLabel, gbc_runTimeLabel);

        JPanel internalSouthPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) internalSouthPanel.getLayout();

        flowLayout.setAlignment(FlowLayout.LEFT);
        containerCenterPanel.add(internalSouthPanel, BorderLayout.CENTER);

        infoLabel = new JLabel();

        infoLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        internalSouthPanel.add(infoLabel);

        int delay = 1000;    // milliseconds
        ActionListener taskPerformer = new ActionListener() {
            private long secs = 1;
            public void actionPerformed(ActionEvent evt) {
                if (!paused) {
                    runTimeLabel.setText(format(secs));

                    secs++;

                    setRunTimeEstimate();
                }
            }
            private String format(long seconds) {
                if (seconds < 60) {
                    return seconds + ((seconds == 1)
                                      ? " second"
                                      : " seconds");
                } else if (seconds < 3600) {
                    long mins = seconds / 60;

                    return mins + ((mins == 1)
                                   ? " minute"
                                   : " minutes");
                } else {
                    long hours = seconds / 3600;

                    return hours + ((hours == 1)
                                    ? " hour"
                                    : " hours");
                }
            }
        };

        new Timer(delay, taskPerformer).start();
    }

    private void setRunTimeEstimate() {
        percentCompleteLabel.setText(roundTwoDecimals(optimizer_threaded.getPercentCompleted() * 100) + "%");
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");

        return Double.valueOf(twoDForm.format(d));
    }

    private void pauseToggleClicked() {
        if (paused) {
            paused = false;

            schedulerStarted();
        } else {
            paused = true;

            schedulerPaused();
        }
    }

    public void schedulerFinished() {
        SchedulerSettingsPanel settings =
            (SchedulerSettingsPanel) ExamSchedulerMain.getInstance().getApplicationFrame().getPanel("Scheduler Settings");

        ExamSchedulerMain.getInstance().getApplicationFrame().enableContinue();
        log.info("Scheduler finished");

        paused = true;

        pauseButton.setEnabled(false);

        status = "scheduler finished";

        ExamSchedulerMain.getInstance().getApplicationFrame().updateProgress(this);
        setRunTimeEstimate();

        String[] opts = { "Yes", "No" };

        if ("Yes".equals(ExamSchedulerMain.getInstance().askUser("Schedule Found!", "The schedule has been saved, view it now?", opts))) {
            try {
                java.awt.Desktop.getDesktop().open(new File(settings.getSaveFilePath()));
            } catch (IOException e) {

                // TODO
                e.printStackTrace();
            }
        }
    }

    private void schedulerPaused() {
        log.info("Paused search for schedules");

        paused = true;

        setPauseResumeButton();

        status = "scheduler paused";

        ExamSchedulerMain.getInstance().getApplicationFrame().updateProgress(this);
        optimizer_threaded.suspend();    // deprecated due to deadlock risk, but I'm fairly certain this is safe for our program
    }

    private void schedulerStarted() {
        SchedulerSettingsPanel settings =
            (SchedulerSettingsPanel) ExamSchedulerMain.getInstance().getApplicationFrame().getPanel("Scheduler Settings");

        log.info("Searching for schedules");

        paused = false;

        if (firstStart) {
            setInfoLabel();
            startTimeLabel.setText(ExamSchedulerMain.getDateString());

            optimizer_threaded = new Optimizer(CourseDB.getCourseDB(), RoomDB.getRoomDB(), StudentDB.getStudentDB(), this,
                                               settings.getSaveFilePath(), settings.getStartDate());

            optimizer_threaded.start();

            firstStart = false;
        } else {
            optimizer_threaded.resume();    // deprecated due to deadlock risk, but I'm fairly certain this is safe for our program
        }

        setPauseResumeButton();
        ExamSchedulerMain.getInstance().getApplicationFrame().disableContinue();

        status = "scheduler running";

        ExamSchedulerMain.getInstance().getApplicationFrame().updateProgress(this);
    }

    private void setInfoLabel() {
        SchedulerSettingsPanel settings =
            (SchedulerSettingsPanel) ExamSchedulerMain.getInstance().getApplicationFrame().getPanel("Scheduler Settings");

        infoLabel.setText("The schedule will be automatically saved to: " + settings.getSaveFilePath());
    }

    private void setPauseResumeButton() {
        if (paused) {
            pauseButton.setText("Resume");
            pauseButton.setIcon(new ImageIcon(ScheduleRunningPanel.class.getResource("/ui/img/resume.png")));
            pauseButton.setToolTipText("Resume scheduler");
        } else {
            pauseButton.setText("Pause");
            pauseButton.setIcon(new ImageIcon(ScheduleRunningPanel.class.getResource("/ui/img/pause.png")));
            pauseButton.setToolTipText("Pause the scheduler (DO NOT close application!)");
        }
    }

    @Override
    public void active() {
        schedulerStarted();
    }

    @Override
    public void navigateNext() {}

    @Override
    public void userRequestedClose() {
        ExamSchedulerMain.getInstance().exit(0);
    }
}
