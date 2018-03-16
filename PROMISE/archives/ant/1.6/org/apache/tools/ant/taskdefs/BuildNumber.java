package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;

/**
 * Read, increment, and write a build number in a file
 * It will first
 * attempt to read a build number from a file, then set the property
 * "build.number" to the value that was read in (or 0 if no such value). Then
 * it will increment the build number by one and write it back out into the
 * file.
 *
 * @since Ant 1.5
 * @ant.task name="buildnumber"
 */
public class BuildNumber
     extends Task {
    /**
     * The name of the property in which the build number is stored.
     */
    private static final String DEFAULT_PROPERTY_NAME = "build.number";

    /** The default filename to use if no file specified.  */
    private static final String DEFAULT_FILENAME = DEFAULT_PROPERTY_NAME;

    /** The File in which the build number is stored.  */
    private File myFile;


    /**
     * The file in which the build number is stored. Defaults to
     * "build.number" if not specified.
     *
     * @param file the file in which build number is stored.
     */
    public void setFile(final File file) {
        myFile = file;
    }


    /**
     * Run task.
     *
     * @exception BuildException if an error occurs
     */
    public void execute()
         throws BuildException {

        validate();

        final Properties properties = loadProperties();
        final int buildNumber = getBuildNumber(properties);

        properties.put(DEFAULT_PROPERTY_NAME,
            String.valueOf(buildNumber + 1));

        FileOutputStream output = null;

        try {
            output = new FileOutputStream(myFile);

            final String header = "Build Number for ANT. Do not edit!";

            properties.store(output, header);
        } catch (final IOException ioe) {
            final String message = "Error while writing " + myFile;

            throw new BuildException(message, ioe);
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (final IOException ioe) {
                    getProject().log("error closing output stream " + ioe, Project.MSG_ERR);
                }
            }
            myFile = savedFile;
        }

        getProject().setNewProperty(DEFAULT_PROPERTY_NAME,
            String.valueOf(buildNumber));
    }


    /**
     * Utility method to retrieve build number from properties object.
     *
     * @param properties the properties to retrieve build number from
     * @return the build number or if no number in properties object
     * @throws BuildException if build.number property is not an integer
     */
    private int getBuildNumber(final Properties properties)
         throws BuildException {
        final String buildNumber =
            properties.getProperty(DEFAULT_PROPERTY_NAME, "0").trim();

        try {
            return Integer.parseInt(buildNumber);
        } catch (final NumberFormatException nfe) {
            final String message =
                myFile + " contains a non integer build number: " + buildNumber;

            throw new BuildException(message, nfe);
        }
    }


    /**
     * Utility method to load properties from file.
     *
     * @return the loaded properties
     * @throws BuildException
     */
    private Properties loadProperties()
         throws BuildException {
        FileInputStream input = null;

        try {
            final Properties properties = new Properties();

            input = new FileInputStream(myFile);
            properties.load(input);
            return properties;
        } catch (final IOException ioe) {
            throw new BuildException(ioe);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (final IOException ioe) {
                    getProject().log("error closing input stream " + ioe, Project.MSG_ERR);
                }
            }
        }
    }


    /**
     * Validate that the task parameters are valid.
     *
     * @throws BuildException if parameters are invalid
     */
    private void validate()
         throws BuildException {
        if (null == myFile) {
            myFile = getProject().resolveFile(DEFAULT_FILENAME);
        }

        if (!myFile.exists()) {
            try {
                FileUtils.newFileUtils().createNewFile(myFile);
            } catch (final IOException ioe) {
                final String message =
                    myFile + " doesn't exist and new file can't be created.";

                throw new BuildException(message, ioe);
            }
        }

        if (!myFile.canRead()) {
            final String message = "Unable to read from " + myFile + ".";

            throw new BuildException(message);
        }

        if (!myFile.canWrite()) {
            final String message = "Unable to write to " + myFile + ".";

            throw new BuildException(message);
        }
    }
}

