package io.FileProcessing;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.Map;

/**
 * Class used for writing DSV files.
 * @author Cameron Ross
 */
public class DSVWriter {
    private String[] columns;    // Columns that exist in this file
    private BufferedWriter fileRef;    // The file to write to.
    private final Charset charset = Charset.forName("ISO_8859_1");
    private String delimiter;    // String used to separate values
    private String enclosure;    // String used to contain entries with delimiters
    private String entryDelimiter = "\n";    // String used to separate records
    private int linesWritten = 0;    // Number of lines written to the file
    private int entriesWritten = 0;    // Number of entries written to the file
    private boolean writeHeader = true;    // Whether or not to write a header with the next entry

    /**
     * Create a file and writer to go along with it
     * @param path path to the file
     * @throws IOException
     * @author Cameron Ross
     */
    public DSVWriter(String path) throws IOException {
        this(path, ",", "\"");
    }

    /**
     * Create a writer for an already-existing output stream
     * @param output Stream to write data to
     * @author Cameron Ross
     */
    public DSVWriter(Writer output) {
        this(output, ",", "\"");
    }

    /**
     * Create a file and writer to go along with it
     * @param path path to the file
     * @param outputDelimiter string used to separate values
     * @throws IOException
     * @author Cameron Ross
     */
    public DSVWriter(String path, String outputDelimiter) throws IOException {
        this(path, outputDelimiter, "\"");
    }

    /**
     * Create a writer for an already-existing output stream
     * @param output Stream to write data to
     * @param outputDelimiter string used to separate values
     * @author Cameron Ross
     */
    public DSVWriter(Writer output, String outputDelimiter) {
        this(output, outputDelimiter, "\"");
    }

    /**
     * Create a file and writer to go along with it
     * @param path path to the file
     * @param outputDelimiter string used to separate values
     * @param outputEnclosure string used to enclose entries with delimiters
     * @throws IOException
     * @author Cameron Ross
     */
    public DSVWriter(String path, String outputDelimiter, String outputEnclosure) throws IOException {
        delimiter = outputDelimiter;
        enclosure = outputEnclosure;
        fileRef = Files.newBufferedWriter(Paths.get(path), charset, StandardOpenOption.CREATE_NEW);    // Create file for writing. fails if file exists already
    }

    /**
     * Create a writer for an already-existing output stream
     * @param output Stream to write data to
     * @param outputDelimiter string used to separate values
     * @param outputEnclosure string used to enclose entries with delimiters
     * @author Cameron Ross
     */
    public DSVWriter(Writer output, String outputDelimiter, String outputEnclosure) {
        delimiter = outputDelimiter;
        enclosure = outputEnclosure;
        fileRef = new BufferedWriter(output);
    }

    /**
     * Writes an entry to the stream. If first entry written, writes the header as well.
     * @param values Key-Value pairs to be written to the stream.
     * @throws IOException
     * @author Cameron Ross
     */
    public void writeEntry(Map<String, String> values) throws Exception {
        if (writeHeader) {
            columns = new String[values.size()];

            int i = 0;

            for (String key : values.keySet()) {
                columns[i++] = key;
            }

            writeValues(columns);

            writeHeader = false;
        }

        String[] buf = new String[columns.length];
        int i = 0;

        for (String column : columns) {
            buf[i++] = values.get(column);
        }

        writeValues(buf);

        entriesWritten++;
    }

    /**
     * Counts number of lines written to the stream thus far.
     * @return Number of lines written
     * @author Cameron Ross
     */
    public int getLinesWritten() {
        return linesWritten;
    }

    /**
     * Counts number of entries written to the stream thus far.
     * @return Number of entries written
     * @author Cameron Ross
     */
    public int getEntriesWritten() {
        return entriesWritten;
    }

    /**
     * Writes a record to the file using an array of strings. If any string contains
     * a delimiter, it will be contained with a specific enclosure character.
     * @param values Strings to write for the current record.
     * @throws Exception
     * @author Cameron Ross
     */
    private void writeValues(String[] values) throws Exception {
        if (!writeHeader) {
            fileRef.append(entryDelimiter);
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                if (values[i].contains(entryDelimiter)) {
                    throw new Exception("Entry Delimiter found in entry!");
                }

                if (values[i].contains(delimiter) || values[i].contains(enclosure)) {
                    fileRef.append(enclosure);
                }

                fileRef.append(values[i]);

                if (values[i].contains(delimiter) || values[i].contains(enclosure)) {
                    fileRef.append(enclosure);
                }
            }

            if (i < values.length - 1) {
                fileRef.append(delimiter);
            }
        }

        fileRef.flush();

        linesWritten++;
    }

    /**
     * Enables or disables header writing.
     * @param input Whether or not to enable header writing
     * @throws Exception
     * @author Cameron Ross
     */
    public void setHeaderMode(boolean input) throws Exception {
        if (this.getEntriesWritten() >= 1) {
            throw new Exception("Cannot write header after first entry");
        }

        this.writeHeader = input;
    }
}
