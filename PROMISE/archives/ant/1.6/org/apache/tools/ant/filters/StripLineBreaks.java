package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

/**
 * Filter to flatten the stream to a single line.
 *
 * Example:
 *
 * <pre>&lt;striplinebreaks/&gt;</pre>
 *
 * Or:
 *
 * <pre>&lt;filterreader
 *   classname=&quot;org.apache.tools.ant.filters.StripLineBreaks&quot;/&gt;</pre>
 *
 */
public final class StripLineBreaks
    extends BaseParamFilterReader
    implements ChainableReader {
    /**
     * Line-breaking characters.
     * What should we do on funny IBM mainframes with odd line endings?
     */
    private static final String DEFAULT_LINE_BREAKS = "\r\n";

    /** Parameter name for the line-breaking characters parameter. */
    private static final String LINE_BREAKS_KEY = "linebreaks";

    /** The characters that are recognized as line breaks. */
    private String lineBreaks = DEFAULT_LINE_BREAKS;

    /**
     * Constructor for "dummy" instances.
     *
     * @see BaseFilterReader#BaseFilterReader()
     */
    public StripLineBreaks() {
        super();
    }

    /**
     * Creates a new filtered reader.
     *
     * @param in A Reader object providing the underlying stream.
     *           Must not be <code>null</code>.
     */
    public StripLineBreaks(final Reader in) {
        super(in);
    }

    /**
     * Returns the next character in the filtered stream, only including
     * characters not in the set of line-breaking characters.
     *
     * @return the next character in the resulting stream, or -1
     * if the end of the resulting stream has been reached
     *
     * @exception IOException if the underlying stream throws an IOException
     * during reading
     */
    public final int read() throws IOException {
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }

        int ch = in.read();
        while (ch != -1) {
            if (lineBreaks.indexOf(ch) == -1) {
                break;
            } else {
                ch = in.read();
            }
        }
        return ch;
    }

    /**
     * Sets the line-breaking characters.
     *
     * @param lineBreaks A String containing all the characters to be
     *                   considered as line-breaking.
     */
    public final void setLineBreaks(final String lineBreaks) {
        this.lineBreaks = lineBreaks;
    }

    /**
     * Returns the line-breaking characters as a String.
     *
     * @return a String containing all the characters considered as
     *         line-breaking
     */
    private final String getLineBreaks() {
        return lineBreaks;
    }

    /**
     * Creates a new StripLineBreaks using the passed in
     * Reader for instantiation.
     *
     * @param rdr A Reader object providing the underlying stream.
     *            Must not be <code>null</code>.
     *
     * @return a new filter based on this configuration, but filtering
     *         the specified reader
     */
    public final Reader chain(final Reader rdr) {
        StripLineBreaks newFilter = new StripLineBreaks(rdr);
        newFilter.setLineBreaks(getLineBreaks());
        newFilter.setInitialized(true);
        return newFilter;
    }

    /**
     * Parses the parameters to set the line-breaking characters.
     */
    private final void initialize() {
        String userDefinedLineBreaks = null;
        Parameter[] params = getParameters();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (LINE_BREAKS_KEY.equals(params[i].getName())) {
                    userDefinedLineBreaks = params[i].getValue();
                    break;
                }
            }
        }
        if (userDefinedLineBreaks != null) {
            lineBreaks = userDefinedLineBreaks;
        }
    }
}
