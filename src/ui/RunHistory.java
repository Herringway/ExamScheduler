package ui;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class RunHistory extends JFrame {
    private static final long serialVersionUID = 6750591687142766820L;
    private Properties history;
    private JTable table;
    private String propURL;

    public RunHistory() {
        setSize(300, 400);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(RunHistory.class.getResource("/ui/img/clock2.png")));

        history = new Properties();

        try {
            System.out.println(getClass().getResource("history.properties").getPath());

            FileInputStream fis = new FileInputStream(new File(getClass().getResource("history.properties").getPath()));

            history.load(fis);
            fis.close();
        } catch (IOException e) {

            // TODO Auto-generated catch block
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

    private JTable getTable() {

        // Create columns names
        String[] columnNames = { "Date", "Time Taken" };

        // Create data
        String[][] dataValues = new String[history.stringPropertyNames().size()][2];
        int i = 0;

        for (String time : history.stringPropertyNames()) {
            dataValues[i][0] = time;
            dataValues[i][1] = history.getProperty(time);

            i++;
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

    public void store(String date, String runTime) {
        history.setProperty(date, runTime);

        try {
            FileOutputStream fos = new FileOutputStream(new File(getClass().getResource("history.properties").getPath()));

            history.store(fos, null);
            fos.close();
        } catch (FileNotFoundException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
