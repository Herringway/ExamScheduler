package ui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.awt.EventQueue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Singleton entry point of application.
 *
 * This class will be convenient when you need access to GUI components, but don't want
 * to supply them as parameters. Just use ExamSchedulerMain.instance().getApplicationFrame()...
 *
 * @author Christian
 */
public class ExamSchedulerMain {
    private static volatile ExamSchedulerMain instance;
    private ApplicationFrame applicationFrame;
    private static Logger log = Logger.getLogger(ExamSchedulerMain.class.getName());

    /**
     * Show the GUI.
     */
    private ExamSchedulerMain() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        applicationFrame = new ApplicationFrame();

        applicationFrame.setVisible(true);
    }

    /**
     * @return The single ExamSchedulerMain instance
     */
    public static ExamSchedulerMain getInstance() {
        if (instance == null) {
            synchronized (ExamSchedulerMain.class) {
                if (instance == null) {
                    instance = new ExamSchedulerMain();
                }
            }
        }

        return instance;
    }

    /**
     * @return Main ApplicationFrame
     */
    public ApplicationFrame getApplicationFrame() {
        return applicationFrame;
    }

    /**
     * @return True if the user really wants to exit, false if they decline
     */
    public boolean confirmExit() {
        if ("Exit".equals(askUser("Confirm Exit", "Are you sure you want to exit?", new String[] { "Cancel", "Exit" }))) {
            return true;
        }

        return false;
    }

    /**
     * Exit gracefully through this method (do clean up work here).
     * @param status Indicate exit status
     */
    public void exit(int status) {

        // TODO any housekeeping before exiting
        log.info("Closing application");
        System.exit(status);
    }

    /**
     * In case of system error, display a message to user in a JOptionPane, and exit
     * if necessary.
     * @param message Describe error in user friendly way
     * @param exit Whether or not to exit application
     */
    public void error(String message, boolean exit) {
        JOptionPane.showMessageDialog(applicationFrame, message);

        if (exit) {
            exit(1);
        }
    }

    /**
     * Show a JOptionPane to the user and return their response.
     * @param title JOptionPane title
     * @param question JOptionPane question text
     * @param options JOptionPane options for user to choose from
     * @return Chosen option
     */
    public Object askUser(String title, String question, Object[] options) {
        JOptionPane pane = new JOptionPane(question);

        pane.setOptions(options);

        JDialog dialog = pane.createDialog(applicationFrame, title);

        dialog.setVisible(true);

        Object obj = pane.getValue();
        int result = -1;

        for (int k = 0; k < options.length; k++) {
            if (options[k].equals(obj)) {
                result = k;
            }
        }

        if (result == -1) {
            return null;
        } else {
            return options[result];
        }
    }

    /**
     * @return yyyy/MM/dd HH:mm:ss a formatted date String
     */
    public static String getDateString() {
        return formatDate(new Date(), "dd MMMM yyyy HH:mm:ss a");
    }

    public static String formatDate(Date d, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.format(d);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        log.info("Application startup, initializing user interface.");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ExamSchedulerMain.getInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Failed to initialize: " + e.getMessage());
                }
            }
        });
    }
}
