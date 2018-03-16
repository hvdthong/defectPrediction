public class Escape
{
    /**
     *
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Empty constructor
     */
    public Escape()
    {
    }

    /**
     * Do the escaping.
     * @param st
     * @return The escaped text.
     */
    public static final String getText(String st)
    {
        StringBuffer buff = new StringBuffer();
        char[] block = st.toCharArray();
        String stEntity = null;
        int i, last;

        for (i=0, last=0; i < block.length; i++)
        {
            switch(block[i])
            {
                case '<' :
                    stEntity = "&lt;";
                    break;
                case '>' :
                    stEntity = "&gt;";
                    break;
                case '&' :
                    stEntity = "&amp;";
                    break;
                case '"' :
                    stEntity = "&quot;";
                    break;
                case '\n' :
                    stEntity = LINE_SEPARATOR;
                    break;
                default :
                    /* no-op */
                    break;
            }
            if (stEntity != null)
            {
                buff.append(block, last, i - last);
                buff.append(stEntity);
                stEntity = null;
                last = i + 1;
            }
        }
        if(last < block.length)
        {
            buff.append(block, last, i - last);
        }
        return buff.toString();
    }
}
