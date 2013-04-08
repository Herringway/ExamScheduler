package ui;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Loads and displays past running times from a properties file.
 * @author Christian
 */
public class RunHistory extends JFrame {
    private static final long serialVersionUID = 6750591687142766820L;
    private static Logger log = Logger.getLogger(RunHistory.class.getName());
    private Properties history;
    private JTable table;

    /**
     * RunHistory constructor.
     */
    public RunHistory() {
        setSize(300, 400);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(RunHistory.class.getResource("/ui/img/clock2.png")));

        history = new Properties();

        try {
            FileInputStream fis = new FileInputStream(new File(getClass().getResource("history.properties").getPath()));

            history.load(fis);
            fis.close();
        } catch (Exception e) {
            log.error("Unable to load previous elapsed running times: "+e.getMessage());
            e.printStackTrace();
        }

        setTitle("Previous Running Times");

        JPanel panel = new JPanel();

        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        table = getTable();

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * @return JTable displaying previous elapsed running times
     */
    private JTable getTable() {

        // Create columns names
        String[] columnNames = { "Date", "Time Taken" };

        // Create data
        String[][] dataValues = new String[history.stringPropertyNames().size()][2];
        int i = 0;

        if (history != null) {
            for (String time : history.stringPropertyNames()) {
                dataValues[i][0] = time;
                dataValues[i][1] = history.getProperty(time);

                i++;
            }
        }

        JTable t = new JTable();

        t.setModel(new DefaultTableModel(dataValues, columnNames) {
            private static final long serialVersionUID = -6850173070786062231L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        return t;
    }

    /**
     * Store an elapsed running time to the properties file.
     * @param date
     * @param runTime
     */
    public void store(String date, String runTime) {
        history.setProperty(date, runTime);

        try {
            FileOutputStream fos = new FileOutputStream(new File(getClass().getResource("history.properties").getPath()));

            history.store(fos, null);
            fos.close();
        } catch (Exception e) {
            log.error("Unable to record elapsed running time: "+e.getMessage());
        }
    }
}
