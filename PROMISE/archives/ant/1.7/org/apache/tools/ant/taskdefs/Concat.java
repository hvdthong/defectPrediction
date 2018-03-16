package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.types.resources.selectors.Not;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.ConcatResourceInputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ReaderInputStream;
import org.apache.tools.ant.util.StringUtils;

/**
 * This class contains the 'concat' task, used to concatenate a series
 * of files into a single stream. The destination of this stream may
 * be the system console, or a file. The following is a sample
 * invocation:
 *
 * <pre>
 * &lt;concat destfile=&quot;${build.dir}/index.xml&quot;
 *   append=&quot;false&quot;&gt;
 *
 *   &lt;fileset dir=&quot;${xml.root.dir}&quot;
 *     includes=&quot;*.xml&quot; /&gt;
 *
 * &lt;/concat&gt;
 * </pre>
 *
 */
public class Concat extends Task implements ResourceCollection {

    private static final int BUFFER_SIZE = 8192;

    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    private static final ResourceSelector EXISTS = new Exists();
    private static final ResourceSelector NOT_EXISTS = new Not(EXISTS);

    /**
     * sub element points to a file or contains text
     */
    public static class TextElement extends ProjectComponent {
        private String   value = "";
        private boolean  trimLeading = false;
        private boolean  trim = false;
        private boolean  filtering = true;
        private String   encoding = null;

        /**
         * whether to filter the text in this element
         * or not.
         *
         * @param filtering true if the text should be filtered.
         *                  the default value is true.
         */
        public void setFiltering(boolean filtering) {
            this.filtering = filtering;
        }

        /** return the filtering attribute */
        private boolean getFiltering() {
            return filtering;
        }

        /**
         * The encoding of the text element
         *
         * @param encoding the name of the charset used to encode
         */
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        /**
         * set the text using a file
         * @param file the file to use
         * @throws BuildException if the file does not exist, or cannot be
         *                        read
         */
        public void setFile(File file) throws BuildException {
            if (!file.exists()) {
                throw new BuildException("File " + file + " does not exist.");
            }

            BufferedReader reader = null;
            try {
                if (this.encoding == null) {
                    reader = new BufferedReader(new FileReader(file));
                } else {
                    reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file),
                                              this.encoding));
                }
                value = FileUtils.safeReadFully(reader);
            } catch (IOException ex) {
                throw new BuildException(ex);
            } finally {
                FileUtils.close(reader);
            }
        }

        /**
         * set the text using inline
         * @param value the text to place inline
         */
        public void addText(String value) {
            this.value += getProject().replaceProperties(value);
        }

        /**
         * s:^\s*:: on each line of input
         * @param strip if true do the trim
         */
        public void setTrimLeading(boolean strip) {
            this.trimLeading = strip;
        }

        /**
         * whether to call text.trim()
         * @param trim if true trim the text
         */
        public void setTrim(boolean trim) {
            this.trim = trim;
        }

        /**
         * @return the text, after possible trimming
         */
        public String getValue() {
            if (value == null) {
                value = "";
            }
            if (value.trim().length() == 0) {
                value = "";
            }
            if (trimLeading) {
                char[] current = value.toCharArray();
                StringBuffer b = new StringBuffer(current.length);
                boolean startOfLine = true;
                int pos = 0;
                while (pos < current.length) {
                    char ch = current[pos++];
                    if (startOfLine) {
                        if (ch == ' ' || ch == '\t') {
                            continue;
                        }
                        startOfLine = false;
                    }
                    b.append(ch);
                    if (ch == '\n' || ch == '\r') {
                        startOfLine = true;
                    }
                }
                value = b.toString();
            }
            if (trim) {
                value = value.trim();
            }
            return value;
        }
    }

    private interface ReaderFactory {
        Reader getReader(Object o) throws IOException;
    }

    /**
     * This class reads from each of the source files in turn.
     * The concatentated result can then be filtered as
     * a single stream.
     */
    private final class MultiReader extends Reader {
        private Reader reader = null;
        private int    lastPos = 0;
        private char[] lastChars = new char[eolString.length()];
        private boolean needAddSeparator = false;
        private Iterator readerSources;
        private ReaderFactory factory;

        private MultiReader(Iterator readerSources, ReaderFactory factory) {
            this.readerSources = readerSources;
            this.factory = factory;
        }

        private Reader getReader() throws IOException {
            if (reader == null && readerSources.hasNext()) {
                reader = factory.getReader(readerSources.next());
                Arrays.fill(lastChars, (char) 0);
            }
            return reader;
        }

        private void nextReader() throws IOException {
            close();
            reader = null;
        }

        /**
         * Read a character from the current reader object. Advance
         * to the next if the reader is finished.
         * @return the character read, -1 for EOF on the last reader.
         * @exception IOException - possibly thrown by the read for a reader
         *            object.
         */
        public int read() throws IOException {
            if (needAddSeparator) {
                int ret = eolString.charAt(lastPos++);
                if (lastPos >= eolString.length()) {
                    lastPos = 0;
                    needAddSeparator = false;
                }
                return ret;
            }
            while (getReader() != null) {
                int ch = getReader().read();
                if (ch == -1) {
                    nextReader();
                    if (isFixLastLine() && isMissingEndOfLine()) {
                        needAddSeparator = true;
                        lastPos = 0;
                    }
                } else {
                    addLastChar((char) ch);
                    return ch;
                }
            }
            return -1;
        }

        /**
         * Read into the buffer <code>cbuf</code>.
         * @param cbuf The array to be read into.
         * @param off The offset.
         * @param len The length to read.
         * @exception IOException - possibly thrown by the reads to the
         *            reader objects.
         */
        public int read(char[] cbuf, int off, int len)
            throws IOException {

            int amountRead = 0;
            while (getReader() != null || needAddSeparator) {
                if (needAddSeparator) {
                    cbuf[off] = eolString.charAt(lastPos++);
                    if (lastPos >= eolString.length()) {
                        lastPos = 0;
                        needAddSeparator = false;
                    }
                    len--;
                    off++;
                    amountRead++;
                    if (len == 0) {
                        return amountRead;
                    }
                    continue;
                }
                int nRead = getReader().read(cbuf, off, len);
                if (nRead == -1 || nRead == 0) {
                    nextReader();
                    if (isFixLastLine() && isMissingEndOfLine()) {
                        needAddSeparator = true;
                        lastPos = 0;
                    }
                } else {
                    if (isFixLastLine()) {
                        for (int i = nRead;
                                 i > (nRead - lastChars.length);
                                 --i) {
                            if (i <= 0) {
                                break;
                            }
                            addLastChar(cbuf[off + i - 1]);
                        }
                    }
                    len -= nRead;
                    off += nRead;
                    amountRead += nRead;
                    if (len == 0) {
                        return amountRead;
                    }
                }
            }
            if (amountRead == 0) {
                return -1;
            } else {
                return amountRead;
            }
        }

        /**
         * Close the current reader
         */
        public void close() throws IOException {
            if (reader != null) {
                reader.close();
            }
        }

        /**
         * if checking for end of line at end of file
         * add a character to the lastchars buffer
         */
        private void addLastChar(char ch) {
            for (int i = lastChars.length - 2; i >= 0; --i) {
                lastChars[i] = lastChars[i + 1];
            }
            lastChars[lastChars.length - 1] = ch;
        }

        /**
         * return true if the lastchars buffer does
         * not contain the lineseparator
         */
        private boolean isMissingEndOfLine() {
            for (int i = 0; i < lastChars.length; ++i) {
                if (lastChars[i] != eolString.charAt(i)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isFixLastLine() {
            return fixLastLine && textBuffer == null;
        }
    }

    private final class ConcatResource extends Resource {
        private ResourceCollection c;

        private ConcatResource(ResourceCollection c) {
            this.c = c;
        }
        public InputStream getInputStream() throws IOException {
            if (binary) {
                ConcatResourceInputStream result = new ConcatResourceInputStream(c);
                result.setManagingComponent(this);
                return result;
            }
            Reader resourceReader = getFilteredReader(
                    new MultiReader(c.iterator(), resourceReaderFactory));
            Reader rdr;
            if (header == null && footer == null) {
                rdr = resourceReader;
            } else {
                int readerCount = 1;
                if (header != null) {
                    readerCount++;
                }
                if (footer != null) {
                    readerCount++;
                }
                Reader[] readers = new Reader[readerCount];
                int pos = 0;
                if (header != null) {
                    readers[pos] = new StringReader(header.getValue());
                    if (header.getFiltering()) {
                        readers[pos] = getFilteredReader(readers[pos]);
                    }
                    pos++;
                }
                readers[pos++] = resourceReader;
                if (footer != null) {
                    readers[pos] = new StringReader(footer.getValue());
                    if (footer.getFiltering()) {
                        readers[pos] = getFilteredReader(readers[pos]);
                    }
                }
                rdr = new MultiReader(Arrays.asList(readers).iterator(),
                        identityReaderFactory);
            }
            return outputEncoding == null ? new ReaderInputStream(rdr)
                    : new ReaderInputStream(rdr, outputEncoding);
        }
        public String getName() {
            return "concat (" + String.valueOf(c) + ")";
        }
    }


    /**
     * The destination of the stream. If <code>null</code>, the system
     * console is used.
     */
    private File destinationFile;

    /**
     * Whether or not the stream should be appended if the destination file
     * exists.
     * Defaults to <code>false</code>.
     */
    private boolean append;

    /**
     * Stores the input file encoding.
     */
    private String encoding;

    /** Stores the output file encoding. */
    private String outputEncoding;

    /** Stores the binary attribute */
    private boolean binary;


    /**
     * This buffer stores the text within the 'concat' element.
     */
    private StringBuffer textBuffer;

    /**
     * Stores a collection of file sets and/or file lists, used to
     * select multiple files for concatenation.
     */
    private ResourceCollection rc;

    /** for filtering the concatenated */
    private Vector filterChains;
    /** ignore dates on input files */
    private boolean forceOverwrite = true;
    /** String to place at the start of the concatented stream */
    private TextElement footer;
    /** String to place at the end of the concatented stream */
    private TextElement header;
    /** add missing line.separator to files **/
    private boolean fixLastLine = false;
    /** endofline for fixlast line */
    private String eolString;
    /** outputwriter */
    private Writer outputWriter = null;

    private ReaderFactory resourceReaderFactory  = new ReaderFactory() {
        public Reader getReader(Object o) throws IOException {
            InputStream is = ((Resource) o).getInputStream();
            return new BufferedReader(encoding == null
                ? new InputStreamReader(is)
                : new InputStreamReader(is, encoding));
        }
    };

    private ReaderFactory identityReaderFactory = new ReaderFactory() {
        public Reader getReader(Object o) {
            return (Reader) o;
        }
    };

    /**
     * Construct a new Concat task.
     */
    public Concat() {
        reset();
    }

    /**
     * Reset state to default.
     */
    public void reset() {
        append = false;
        forceOverwrite = true;
        destinationFile = null;
        encoding = null;
        outputEncoding = null;
        fixLastLine = false;
        filterChains = null;
        footer = null;
        header = null;
        binary = false;
        outputWriter = null;
        textBuffer = null;
        eolString = StringUtils.LINE_SEP;
        rc = null;
    }


    /**
     * Sets the destination file, or uses the console if not specified.
     * @param destinationFile the destination file
     */
    public void setDestfile(File destinationFile) {
        this.destinationFile = destinationFile;
    }

    /**
     * Sets the behavior when the destination file exists. If set to
     * <code>true</code> the stream data will be appended to the
     * existing file, otherwise the existing file will be
     * overwritten. Defaults to <code>false</code>.
     * @param append if true append to the file.
     */
    public void setAppend(boolean append) {
        this.append = append;
    }

    /**
     * Sets the character encoding
     * @param encoding the encoding of the input stream and unless
     *        outputencoding is set, the outputstream.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        if (outputEncoding == null) {
            outputEncoding = encoding;
        }
    }

    /**
     * Sets the character encoding for outputting
     * @param outputEncoding the encoding for the output file
     * @since Ant 1.6
     */
    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    /**
     * Force overwrite existing destination file
     * @param force if true always overwrite, otherwise only overwrite
     *              if the output file is older any of the input files.
     * @since Ant 1.6
     */
    public void setForce(boolean force) {
        this.forceOverwrite = force;
    }


    /**
     * Path of files to concatenate.
     * @return the path used for concatenating
     * @since Ant 1.6
     */
     public Path createPath() {
        Path path = new Path(getProject());
        add(path);
        return path;
    }

    /**
     * Set of files to concatenate.
     * @param set the set of files
     */
    public void addFileset(FileSet set) {
        add(set);
    }

    /**
     * List of files to concatenate.
     * @param list the list of files
     */
    public void addFilelist(FileList list) {
        add(list);
    }

    /**
     * Add an arbitrary ResourceCollection.
     * @param c the ResourceCollection to add.
     * @since Ant 1.7
     */
    public synchronized void add(ResourceCollection c) {
        if (rc == null) {
            rc = c;
            return;
        }
        if (!(rc instanceof Resources)) {
            Resources newRc = new Resources();
            newRc.setProject(getProject());
            newRc.add(rc);
            rc = newRc;
        }
        ((Resources) rc).add(c);
    }

    /**
     * Adds a FilterChain.
     * @param filterChain a filterchain to filter the concatenated input
     * @since Ant 1.6
     */
    public void addFilterChain(FilterChain filterChain) {
        if (filterChains == null) {
            filterChains = new Vector();
        }
        filterChains.addElement(filterChain);
    }

    /**
     * This method adds text which appears in the 'concat' element.
     * @param text the text to be concated.
     */
    public void addText(String text) {
        if (textBuffer == null) {
            textBuffer = new StringBuffer(text.length());
        }

        textBuffer.append(text);
    }

    /**
     * Add a header to the concatenated output
     * @param headerToAdd the header
     * @since Ant 1.6
     */
    public void addHeader(TextElement headerToAdd) {
        this.header = headerToAdd;
    }

    /**
     * Add a footer to the concatenated output
     * @param footerToAdd the footer
     * @since Ant 1.6
     */
    public void addFooter(TextElement footerToAdd) {
        this.footer = footerToAdd;
    }

    /**
     * Append line.separator to files that do not end
     * with a line.separator, default false.
     * @param fixLastLine if true make sure each input file has
     *                    new line on the concatenated stream
     * @since Ant 1.6
     */
    public void setFixLastLine(boolean fixLastLine) {
        this.fixLastLine = fixLastLine;
    }

    /**
     * Specify the end of line to find and to add if
     * not present at end of each input file. This attribute
     * is used in conjunction with fixlastline.
     * @param crlf the type of new line to add -
     *              cr, mac, lf, unix, crlf, or dos
     * @since Ant 1.6
     */
    public void setEol(FixCRLF.CrLf crlf) {
        String s = crlf.getValue();
        if (s.equals("cr") || s.equals("mac")) {
            eolString = "\r";
        } else if (s.equals("lf") || s.equals("unix")) {
            eolString = "\n";
        } else if (s.equals("crlf") || s.equals("dos")) {
            eolString = "\r\n";
        }
    }

    /**
     * Set the output writer. This is to allow
     * concat to be used as a nested element.
     * @param outputWriter the output writer.
     * @since Ant 1.6
     */
    public void setWriter(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    /**
     * Set the binary attribute. If true, concat will concatenate the files
     * byte for byte. This mode does not allow any filtering or other
     * modifications to the input streams. The default value is false.
     * @since Ant 1.6.2
     * @param binary if true, enable binary mode.
     */
    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    /**
     * Execute the concat task.
     */
    public void execute() {
        validate();
        if (binary && destinationFile == null) {
            throw new BuildException(
                "destfile attribute is required for binary concatenation");
        }
        ResourceCollection c = getResources();
        if (isUpToDate(c)) {
            log(destinationFile + " is up-to-date.", Project.MSG_VERBOSE);
            return;
        }
        if (c.size() == 0) {
            return;
        }
        OutputStream out;
        if (destinationFile == null) {
            out = new LogOutputStream(this, Project.MSG_WARN);
        } else {
            try {
                File parent = destinationFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                out = new FileOutputStream(destinationFile.getPath(), append);
            } catch (Throwable t) {
                throw new BuildException("Unable to open "
                    + destinationFile + " for writing", t);
            }
        }
        InputStream catStream;
        try {
            catStream = new ConcatResource(c).getInputStream();
        } catch (IOException e) {
            throw new BuildException("error getting concatenated resource content", e);
        }
        pump(catStream, out);
    }

    /**
     * Implement ResourceCollection.
     * @return Iterator<Resource>.
     */
    public Iterator iterator() {
        validate();
        return Collections.singletonList(new ConcatResource(getResources())).iterator();
    }

    /**
     * Implement ResourceCollection.
     * @return 1.
     */
    public int size() {
        return 1;
    }

    /**
     * Implement ResourceCollection.
     * @return false.
     */
    public boolean isFilesystemOnly() {
        return false;
    }

    /**
     * Validate configuration options.
     */
    private void validate() {

        sanitizeText();

        if (binary) {
            if (textBuffer != null) {
                throw new BuildException(
                    "Nested text is incompatible with binary concatenation");
            }
            if (encoding != null || outputEncoding != null) {
                throw new BuildException(
                    "Setting input or output encoding is incompatible with binary"
                    + " concatenation");
            }
            if (filterChains != null) {
                throw new BuildException(
                    "Setting filters is incompatible with binary concatenation");
            }
            if (fixLastLine) {
                throw new BuildException(
                    "Setting fixlastline is incompatible with binary concatenation");
            }
            if (header != null || footer != null) {
                throw new BuildException(
                    "Nested header or footer is incompatible with binary concatenation");
            }
        }
        if (destinationFile != null && outputWriter != null) {
            throw new BuildException(
                "Cannot specify both a destination file and an output writer");
        }
        if (rc == null && textBuffer == null) {
            throw new BuildException(
                "At least one resource must be provided, or some text.");
        }
        if (rc != null && textBuffer != null) {
            throw new BuildException(
                "Cannot include inline text when using resources.");
        }
    }

    /**
     * Get the resources to concatenate.
     */
    private ResourceCollection getResources() {
        if (rc == null) {
            return new StringResource(getProject(), textBuffer.toString());
        }
        Restrict noexistRc = new Restrict();
        noexistRc.add(NOT_EXISTS);
        noexistRc.add(rc);
        for (Iterator i = noexistRc.iterator(); i.hasNext();) {
            log(i.next() + " does not exist.", Project.MSG_ERR);
        }
        if (destinationFile != null) {
            for (Iterator i = rc.iterator(); i.hasNext();) {
                Object o = i.next();
                if (o instanceof FileResource) {
                    File f = ((FileResource) o).getFile();
                    if (FILE_UTILS.fileNameEquals(f, destinationFile)) {
                        throw new BuildException("Input file \""
                            + f + "\" is the same as the output file.");
                    }
                }
            }
        }
        Restrict result = new Restrict();
        result.add(EXISTS);
        result.add(rc);
        return result;
    }

    private boolean isUpToDate(ResourceCollection c) {
        if (destinationFile == null || forceOverwrite) {
            return false;
        }
        for (Iterator i = c.iterator(); i.hasNext();) {
            Resource r = (Resource) i.next();
            if (r.getLastModified() == 0L
                 || r.getLastModified() > destinationFile.lastModified()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Treat empty nested text as no text.
     *
     * <p>Depending on the XML parser, addText may have been called
     * for &quot;ignorable whitespace&quot; as well.</p>
     */
    private void sanitizeText() {
        if (textBuffer != null && "".equals(textBuffer.toString().trim())) {
            textBuffer = null;
        }
    }

    /**
     * Transfer the contents of <code>in</code> to <code>out</code>.
     * @param in InputStream
     * @param out OutputStream
     */
    private void pump(InputStream in, OutputStream out) {
        Thread t = new Thread(new StreamPumper(in, out));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            try {
                t.join();
            } catch (InterruptedException ee) {
            }
        } finally {
            FileUtils.close(in);
            FileUtils.close(out);
        }
    }

    private Reader getFilteredReader(Reader r) {
        if (filterChains == null) {
            return r;
        }
        ChainReaderHelper helper = new ChainReaderHelper();
        helper.setBufferSize(BUFFER_SIZE);
        helper.setPrimaryReader(r);
        helper.setFilterChains(filterChains);
        helper.setProject(getProject());
        return helper.getAssembledReader();
    }

}
