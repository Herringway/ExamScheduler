package io.FileProcessing;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DSVScanner {
    private ArrayList<String> columns;
    private BufferedReader fileRef;
    private final int bufferSize = 8192;    // hardcoded for now. if anyone knows

    // how to actually get a
    // bufferedreader's buffer size,
    // change this
    private final Charset charset = Charset.forName("ISO_8859_1");
    private String delimiter;
    private int linesRead = 0;    // Number of lines read from the file
    private int entriesRead = 0;    // Number of entries read from the file

    /**
     * Opens a text file as exported by microsoft excel and prepares it for
     * reading. This file will have a header with column names and a body with
     * lists of values for each column name.
     *
     * @param path
     *            path to the file being read
     * @throws IOException
     * @author Cameron Ross
     */
    public DSVScanner(String path) throws IOException {
        delimiter = "\t";

        openFile(path);
    }

    /**
     * Opens a delimiter-separated values file. This file will have a header
     * with column names and a body with lists of values for each column name.
     *
     * @param path
     *            path to the file being read
     * @param inputDelimiter
     *            field delimiter to use for the file
     * @throws IOException
     * @author Cameron Ross
     */
    public DSVScanner(String path, String inputDelimiter) throws IOException {
        delimiter = inputDelimiter;

        openFile(path);
    }

    /**
     * Opens a text file, reads the column names, and ensures the column list
     * isn't empty.
     *
     * @param path
     *            path to the file being read
     * @throws IOException
     * @author Cameron Ross
     */
    private void openFile(String path) throws IOException {
        fileRef = Files.newBufferedReader(Paths.get(path), charset);

        readColumnList();

        linesRead++;

        if (columns.isEmpty()) {

            // System.err.println("Empty file");
            throw new IOException("Empty File");
        }
    }

    /**
     * Sets a new delimiter to parse the rest of the file with.
     *
     * @param newDelimiter
     *            new delimiter for the file
     */
    public void setDelimiter(String newDelimiter) {
        delimiter = newDelimiter;
    }

    /**
     * Returns the current delimiter in use.
     *
     * @return the current delimiter for the file
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Reads the columns from the file list for use as entry keys. Splits the
     * first line of the text file.
     *
     * @author Cameron Ross
     */
    private void readColumnList() {
        columns = new ArrayList<String>();

        String[] splitColumns = readDividedLine();

        if (splitColumns == null) {
            return;
        }

        for (String column : splitColumns) {
            columns.add(column.trim());
        }
    }

    /**
     * Reads an entry from the file, including extended entry if necessary.
     * Reads a split line from the loaded file, creates an entry in the map for
     * each column in the header even if the entry is empty. It will then peek
     * at the next entry to see if it is an extended entry and appends values to
     * the current entry with a delimiter.
     *
     * @return a key-value map containing the next entry in the file.
     * @author Cameron Ross
     * @throws Exception
     */
    public Map<String, String> getNextEntry() throws IOException {
        HashMap<String, String> output = new HashMap<String, String>();
        int i = 0;
        boolean needsAdditionalProcessing = false;
        String[] splitLine = readDividedLine();

        linesRead++;

        if (splitLine == null) {
            return null;
        }

        for (String value : splitLine) {
            if (value == null) {
                value = "";
            }

            if (i < columns.size()) {
                output.put(columns.get(i++), value);
            }
        }

        try {
            fileRef.mark(bufferSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        splitLine = readDividedLine();

        if (splitLine == null) {}
        else if ((splitLine.length == 0) || splitLine[0].equals("")) {
            needsAdditionalProcessing = true;
        }

        try {
            fileRef.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (needsAdditionalProcessing) {
            i = 0;
            splitLine = readDividedLine();

            if (splitLine == null) {
                break;
            }

            for (String value : splitLine) {
                if (value == null) {
                    value = "";
                }

                if (i < columns.size()) {
                    output.put(columns.get(i++), output.get(columns.get(i - 1)) + delimiter + value);
                }
            }

            try {
                fileRef.mark(bufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }

            splitLine = readDividedLine();

            linesRead++;

            if (splitLine == null) {
                break;
            }

            needsAdditionalProcessing = false;

            if ((splitLine.length == 0) || splitLine[0].equals("")) {
                needsAdditionalProcessing = true;
            }

            try {
                fileRef.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        entriesRead++;

        return output;
    }

    /**
     * Reads a line of text from the file and splits it into several smaller
     * strings.
     *
     * @return a list of strings found in the line
     * @author Cameron Ross
     */
    private String[] readDividedLine() {
        if (fileRef != null) {
            String line;

            try {
                line = fileRef.readLine();

                if (line == null) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }

            return line.split(delimiter);
        }

        return null;
    }

    /**
     * Determines if there is another entry in the file to read by marking the
     * current position, reading a line if possible, and resetting the position.
     *
     * @return true if there are more entries to read
     * @author Cameron Ross
     */
    public boolean hasNextEntry() {
        if (fileRef != null) {
            try {
                fileRef.mark(bufferSize);

                if (fileRef.readLine() == null) {
                    fileRef.close();

                    fileRef = null;

                    return false;
                }

                fileRef.reset();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error reading file");

                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Returns a count of the number of lines read. Will likely be different
     * than entries read due to multiline entries.
     *
     * @return The number of lines read by the scanner
     * @author Cameron Ross
     */
    public int getLinesRead() {
        return linesRead;
    }

    /**
     * Returns a count of the number of entries read.
     *
     * @return The number of entries read by the scanner
     * @author Cameron Ross
     */
    public int getEntriesRead() {
        return entriesRead;
    }
}
