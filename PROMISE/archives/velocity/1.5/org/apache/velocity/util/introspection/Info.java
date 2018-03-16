public class Info
{
    private int line;
    private int column;
    private String templateName;

    /**
     * @param source Usually a template name.
     * @param line The line number from <code>source</code>.
     * @param column The column number from <code>source</code>.
     */
    public Info(String source, int line, int column)
    {
        this.templateName = source;
        this.line = line;
        this.column = column;
    }

    /**
     * Force callers to set the location information.
     */
    private Info()
    {
    }

    /**
     * @return The template name.
     */
    public String getTemplateName()
    {
        return templateName;
    }

    /**
     * @return The line number.
     */
    public int getLine()
    {
        return line;
    }

    /**
     * @return The column number.
     */
    public int getColumn()
    {
        return column;
    }

    /**
     * Formats a textual representation of this object as <code>SOURCE
     * [line X, column Y]</code>.
     *
     * @return String representing this object.
     */
    public String toString()
    {
        return getTemplateName() + " [line " + getLine() + ", column " +
            getColumn() + ']';
    }
}
