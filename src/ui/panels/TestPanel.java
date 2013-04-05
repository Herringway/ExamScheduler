package ui.panels;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import javax.swing.JLabel;

/**
 * A basic ApplicationPanel to use in tests.
 * @author Christian
 */
public class TestPanel extends ApplicationPanel {
    private static final long serialVersionUID = -3381786414175736233L;
    private static Logger log = Logger.getLogger(TestPanel.class.getName());

    /**
     * Constructs TestPanel.
     * @param name TestPanel name
     */
    public TestPanel(String name) {
        super(name);

        JLabel nameLabel = new JLabel(name);

        add(nameLabel);

        status = "fantastic";
    }

    @Override
    public void active() {
        log.debug("TestPanel activated");
    }

    @Override
    public void navigateNext() {}
}
