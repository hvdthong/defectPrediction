package javax.xml.parsers;

/**
 * Thrown when a problem with configuration with the Parser Factories
 * exists. This error will typically be thrown when the class of a
 * parser factory specified in the system properties cannot be found
 * or instantiated.
 *
 * @since JAXP 1.0
 * @version 1.0
 */

public class FactoryConfigurationError extends Error {

    private Exception exception;

    /**
     * Create a new <code>FactoryConfigurationError</code> with no
     * detail mesage.
     */

     public FactoryConfigurationError() {
         super();
    	 this.exception = null; 
     }

    /**
     * Create a new <code>FactoryConfigurationError</code> with
     * the <code>String </code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    
    public FactoryConfigurationError(String msg) {
        super(msg);
        this.exception = null;
    }


    /**
     * Create a new <code>FactoryConfigurationError</code> with a
     * given <code>Exception</code> base cause of the error.
     *
     * @param e The exception to be encapsulated in a
     * FactoryConfigurationError.
     */
    
    public FactoryConfigurationError(Exception e) {
        super(e.toString());
        this.exception = e;
    }

    /**
     * Create a new <code>FactoryConfigurationError</code> with the
     * given <code>Exception</code> base cause and detail message.
     *
     * @param e The exception to be encapsulated in a
     * FactoryConfigurationError
     * @param msg The detail message.
     * @param e The exception to be wrapped in a FactoryConfigurationError
     */
    
    public FactoryConfigurationError(Exception e, String msg) {
        super(msg);
        this.exception = e;
    }


    /**
     * Return the message (if any) for this error . If there is no
     * message for the exception and there is an encapsulated
     * exception then the message of that exception, if it exists will be 
     * returned. Else the name of the encapsulated exception will be
     * returned.
     *
     * @return The error message.
     */
    
    public String getMessage () {
        String message = super.getMessage ();
  
        if (message == null && exception != null) {
            return exception.getMessage();
        }

        return message;
    }
  
    /**
     * Return the actual exception (if any) that caused this exception to
     * be raised.
     *
     * @return The encapsulated exception, or null if there is none.
     */
    
    public Exception getException () {
        return exception;
    }
}







