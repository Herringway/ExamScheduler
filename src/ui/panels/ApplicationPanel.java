package ui.panels;

//~--- non-JDK imports --------------------------------------------------------

import ui.ExamSchedulerMain;

//~--- JDK imports ------------------------------------------------------------

import javax.swing.JPanel;

/**
 * The base class of JPanels that will be displayed sequentially to the user.
 * @author Christian
 */
public abstract class ApplicationPanel extends JPanel {
    private static final long serialVersionUID = 1228435725216147366L;
    protected String status = "";

    /** ApplicationPanel name */
    protected String name;

    /**
     * Constructs ApplicationPanel.
     * @param name ApplicationPanel name
     */
    public ApplicationPanel(String name) {
        this.name = name;
    }
    
    /**
     * Update the status of this ApplicationPanel and have the main Frame show changes.
     * @param status
     */
    public void updateStatus(String status) {
    	this.status = status;
        ExamSchedulerMain.getInstance().getApplicationFrame().updateProgress(this);
    }

    /**
     * Indicate status of this panel.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Called when a user navigates to this panel.
     */
    public abstract void active();

    /**
     * This will be called whenever the user navigates away from this
     * ApplicationPanel (to next panel). E.g. submit data.
     */
    public abstract void navigateNext();

    /**
     * Called if the user tried to close the application (ask by default).
     */
    public void userRequestedClose() {
        if (ExamSchedulerMain.getInstance().confirmExit()) {
            ExamSchedulerMain.getInstance().exit(0);
        }
    }

    /**
     * @return ApplicationPanel name
     */
    public String getName() {
        return name;
    }
}
