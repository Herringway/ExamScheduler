package ui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import ui.panels.ApplicationPanel;
import ui.panels.ReviewSchedulerSettingsPanel;
import ui.panels.ScheduleRunningPanel;
import ui.panels.SchedulerSettingsPanel;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * This is the main application frame. It uses a CardLayout to show
 * ApplicationPanels with their respective functionality.
 * @author Christian
 */
public class ApplicationFrame extends JFrame {
    private static final long serialVersionUID = 5887574321342228404L;
    public final String FRAME_NAME = "Exam Scheduler";
    private JPanel cardPanel;    // the ApplicationPanels are contained in here
    private CardLayout cardLayout;    // the ApplicationPanels are managed by this
    private JLabel progressLabel;    // used to indicate which panel is shown to the user (e.g. 1 of 3)
    private int numCards, shownCard;    // the number of ApplicationPanels, and the active ApplicationPanel
    private JButton continueButton;    // left button
    private JLabel statusLabel;    // indicate system status
    private JButton logButton;
    private JButton helpButton;
    private JPanel panel;
    private JPanel leftPanel;
    private JPanel northPanel;
    private JPanel southPanel;
    private static Logger log = Logger.getLogger(ApplicationFrame.class.getName());
    private HashMap<String, ApplicationPanel> cardPanels;

    /**
     * Create the frame.
     */
    public ApplicationFrame() {
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(ApplicationFrame.class.getResource("/ui/img/calendar.png")));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (getActiveApplicationPanel() != null) {
                    getActiveApplicationPanel().userRequestedClose();
                } else {
                    ExamSchedulerMain.getInstance().exit(0);
                }
            }
        });
        setBounds(100, 100, 628, 337);

        JPanel contentPane = new JPanel();

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        cardLayout = new CardLayout();
        cardPanel = new JPanel();
        cardPanels = new HashMap<String, ApplicationPanel>();

        contentPane.add(cardPanel, BorderLayout.CENTER);
        cardPanel.setLayout(cardLayout);
        cardPanels.put("Scheduler Settings", new SchedulerSettingsPanel("Scheduler Settings"));
        cardPanels.put("Review Scheduler Settings", new ReviewSchedulerSettingsPanel("Review Scheduler Settings"));
        cardPanels.put("Building Schedules", new ScheduleRunningPanel("Building Schedules"));
        cardPanel.add("Scheduler Settings", cardPanels.get("Scheduler Settings"));    // 1st panel
        cardPanel.add("Review Scheduler Settings", cardPanels.get("Review Scheduler Settings"));    // 2nd panel
        cardPanel.add("Building Schedules", cardPanels.get("Building Schedules"));    // 3rd panel

        panel = new JPanel();

        contentPane.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        leftPanel = new JPanel();

        panel.add(leftPanel, BorderLayout.WEST);
        leftPanel.setLayout(new BorderLayout(0, 0));

        northPanel = new JPanel();

        leftPanel.add(northPanel, BorderLayout.NORTH);

        JLabel statusTextLabel = new JLabel("Status:");

        northPanel.add(statusTextLabel);

        statusLabel = new JLabel("");

        northPanel.add(statusLabel);

        progressLabel = new JLabel();

        northPanel.add(progressLabel);
        progressLabel.setText("(1 of 3)");
        progressLabel.setToolTipText("");

        southPanel = new JPanel();

        FlowLayout flowLayout = (FlowLayout) southPanel.getLayout();

        flowLayout.setAlignment(FlowLayout.LEFT);
        leftPanel.add(southPanel, BorderLayout.SOUTH);

        logButton = new JButton("Log");

        southPanel.add(logButton);
        logButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLog();
            }
        });
        logButton.setToolTipText("View a detailed log of events");
        logButton.setIcon(new ImageIcon(ApplicationFrame.class.getResource("/ui/img/log.png")));

        helpButton = new JButton("Help");

        southPanel.add(helpButton);
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelpInBrowser();
            }
        });
        helpButton.setToolTipText("Show help file in browser (no internet required)");
        helpButton.setIcon(new ImageIcon(ApplicationFrame.class.getResource("/ui/img/help.png")));

        JPanel rightPanel = new JPanel();

        panel.add(rightPanel, BorderLayout.EAST);

        continueButton = new JButton("Continue");

        continueButton.setToolTipText("Move on to next step");
        continueButton.setIcon(new ImageIcon(ApplicationFrame.class.getResource("/ui/img/arrow-right.png")));
        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userNavigatedNext(getActiveApplicationPanel());
            }
        });
        rightPanel.setLayout(new BorderLayout(0, 0));
        rightPanel.add(continueButton, BorderLayout.SOUTH);

        numCards = 3;
        shownCard = 1;    // assuming the first card is shown

        checkButtons();
        updateProgress(getActiveApplicationPanel());
        disableContinue();
    }

    public void disableContinue() {
        continueButton.setEnabled(false);
    }

    public void enableContinue() {
        continueButton.setEnabled(true);
    }

    /**
     * Show the current log (for this use session) to the user.
     */
    public void showLog() {
        @SuppressWarnings("rawtypes") Enumeration e = Logger.getRootLogger().getAllAppenders();

        while (e.hasMoreElements()) {
            Appender app = (Appender) e.nextElement();

            if (app instanceof FileAppender) {
                try {
                    java.awt.Desktop.getDesktop().open(new File(((FileAppender) app).getFile()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    log.error("Unable to show log file with system text editor: " + e1.getMessage());
                }
            }
        }
    }

    /**
     * Show help HTML in the system's default HTML viewer.
     */
    public void showHelpInBrowser() {
        URL help = ExamSchedulerMain.class.getResource("help");

        if (help == null) {
            log.error("Failed to show help (can not find help files)");

            return;
        }

        File f = null;

        try {
            f = new File(help.toURI());
        } catch (URISyntaxException ex) {
            f = new File(help.getPath());
        }

        String fp = f.getAbsolutePath();

        // main help needs to be at PATH_TO_HELP/help.html
        fp += File.separator + "help.html";

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        java.net.URI uri = null;

        try {
            fp = fp.replace(File.separator, "/");    // TODO this has been tested on Windows only
            uri = new java.net.URI("file:///" + fp);

            desktop.browse(uri);
        } catch (Exception ex) {
            log.error("Failed to show help: " + ex.getMessage());
        }
    }

    /**
     * Make sure we only show next/back buttons when they can be used.
     */
    private void checkButtons() {
        if (canNavigateNext()) {
            continueButton.setText("Continue");
            continueButton.setToolTipText("Move on to next step");
        } else {
            continueButton.setText("Finish");
            continueButton.setToolTipText("Exit the application");
            continueButton.setIcon(new ImageIcon(ApplicationFrame.class.getResource("/ui/img/finish.png")));
        }
    }

    /**
     * This will be called when the user clicks next.
     * @param activeApplicationPanel The active application panel
     */
    private void userNavigatedNext(ApplicationPanel activeApplicationPanel) {
        if (!canNavigateNext()) {
            if (getActiveApplicationPanel() != null) {
                getActiveApplicationPanel().userRequestedClose();
            } else {
                ExamSchedulerMain.getInstance().exit(0);
            }

            return;
        }

        activeApplicationPanel.navigateNext();
        cardLayout.next(cardPanel);

        shownCard++;

        updateProgress(getActiveApplicationPanel());
        checkButtons();
        getActiveApplicationPanel().active();
    }

    /**
     * @return The ApplicationPanel the user is currently viewing
     */
    public ApplicationPanel getActiveApplicationPanel() {
        for (Component comp : cardPanel.getComponents()) {
            if (comp.isVisible() == true) {
                if (!(comp instanceof ApplicationPanel)) {
                    log.error("Fatal error occurred (unexpected component encountered after navigation, cannot proceed)");
                    ExamSchedulerMain.getInstance().error("Fatal error occurred (unexpected component encountered after navigation, cannot proceed)",
                            true);
                }

                return ((ApplicationPanel) comp);
            }
        }

        return null;
    }

    public ApplicationPanel getPanel(String identifier) {
        return cardPanels.get(identifier);
    }

    /**
     * Update the JLabels that indicates progress to user.
     * @param activeApplicationPanel The active application panel
     */
    public void updateProgress(ApplicationPanel activeApplicationPanel) {
        statusLabel.setText(activeApplicationPanel.getStatus());
        setTitle(FRAME_NAME + ": " + activeApplicationPanel.getName());
        progressLabel.setText("(step " + shownCard + " of " + numCards + ")");
    }

    /**
     * @return True if there is a proceeding card
     */
    public boolean canNavigateNext() {
        return shownCard < numCards;
    }

    /**
     * @return True if there is a preceding card
     */
    public boolean canNavigateBack() {
        return shownCard > 1;
    }
}
