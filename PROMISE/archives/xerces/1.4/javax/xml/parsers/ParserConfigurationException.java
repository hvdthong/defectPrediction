package javax.xml.parsers;

/**
 * Indicates a serious configuration error.
 *
 * @since JAXP 1.0
 * @version 1.0
 */

public class ParserConfigurationException extends Exception {

    /**
     * Create a new <code>ParserConfigurationException</code> with no
     * detail mesage.
     */

    public ParserConfigurationException() {
        super();
    }

    /**
     * Create a new <code>ParserConfigurationException</code> with
     * the <code>String</code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    
    public ParserConfigurationException(String msg) {
        super(msg);
    }

}

