package org.apache.tools.ant.input;

import org.apache.tools.ant.BuildException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads input from a property file, the file name is read from the
 * system property ant.input.properties, the prompt is the key for input.
 *
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @version $Revision: 274041 $
 * @since Ant 1.5
 */
public class PropertyFileInputHandler implements InputHandler {
    private Properties props = null;

    /**
     * Name of the system property we expect to hold the file name.
     */
    public static final String FILE_NAME_KEY = "ant.input.properties";

    /**
     * Empty no-arg constructor.
     */
    public PropertyFileInputHandler() {
    }

    /**
     * Picks up the input from a property, using the prompt as the
     * name of the property.
     *
     * @exception BuildException if no property of that name can be found.
     */
    public void handleInput(InputRequest request) throws BuildException {
        readProps();
        
        Object o = props.get(request.getPrompt());
        if (o == null) {
            throw new BuildException("Unable to find input for \'"
                                     + request.getPrompt()+"\'");
        }
        request.setInput(o.toString());
        if (!request.isInputValid()) {
            throw new BuildException("Found invalid input " + o
                                     + " for \'" + request.getPrompt() + "\'");
        }
    }

    /**
     * Reads the properties file if it hasn't already been read.
     */
    private synchronized void readProps() throws BuildException {
        if (props == null) {
            String propsFile = System.getProperty(FILE_NAME_KEY);
            if (propsFile == null) {
                throw new BuildException("System property "
                                         + FILE_NAME_KEY
                                         + " for PropertyFileInputHandler not"
                                         + " set");
            }
            
            props = new Properties();
            
            try {
                props.load(new FileInputStream(propsFile));
            } catch (IOException e) {
                throw new BuildException("Couldn't load " + propsFile, e);
            }
        }
    }

}
