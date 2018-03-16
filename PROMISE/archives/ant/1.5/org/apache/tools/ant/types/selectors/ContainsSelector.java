package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.BuildException;

/**
 * Selector that filters files based on whether they contain a
 * particular string.
 *
 * @author <a href="mailto:bruce@callenish.com">Bruce Atherton</a>
 * @since 1.5
 */
public class ContainsSelector extends BaseExtendSelector {

    private String contains = null;
    private boolean casesensitive = true;
    public final static String CONTAINS_KEY = "text";
    public final static String CASE_KEY = "casesensitive";


    public ContainsSelector() {
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("{containsselector text: ");
        buf.append(contains);
        buf.append(" casesensitive: ");
        if (casesensitive) {
            buf.append("true");
        } else {
            buf.append("false");
        }
        buf.append("}");
        return buf.toString();
    }

    /**
     * The string to search for within a file.
     *
     * @param contains the string that a file must contain to be selected.
     */
    public void setText(String contains) {
        this.contains = contains;
    }

    /**
     * Whether to ignore case in the string being searched.
     *
     * @param casesensitive whether to pay attention to case sensitivity
     */
    public void setCasesensitive(boolean casesensitive) {
        this.casesensitive = casesensitive;
    }

    /**
     * When using this as a custom selector, this method will be called.
     * It translates each parameter into the appropriate setXXX() call.
     *
     * @param parameters the complete set of parameters for this selector
     */
    public void setParameters(Parameter[] parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                String paramname = parameters[i].getName();
                if (CONTAINS_KEY.equalsIgnoreCase(paramname)) {
                    setText(parameters[i].getValue());
                }
                else if (CASE_KEY.equalsIgnoreCase(paramname)) {
                    setCasesensitive(Project.toBoolean(
                        parameters[i].getValue()));
                }
                else {
                    setError("Invalid parameter " + paramname);
                }
            }
        }
    }

    /**
     * Checks to make sure all settings are kosher. In this case, it
     * means that the pattern attribute has been set.
     *
     */
    public void verifySettings() {
        if (contains == null) {
            setError("The text attribute is required");
        }
    }

    /**
     * The heart of the matter. This is where the selector gets to decide
     * on the inclusion of a file in a particular fileset.
     *
     * @param basedir the base directory the scan is being done from
     * @param filename is the name of the file to check
     * @param file is a java.io.File object the selector can use
     * @return whether the file should be selected or not
     */
    public boolean isSelected(File basedir, String filename, File file) {

        validate();

        if (file.isDirectory()) {
            return true;
        }

        String userstr = contains;
        if (!casesensitive) {
            userstr = contains.toLowerCase();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            String teststr = in.readLine();
            while (teststr != null) {
                if (!casesensitive) {
                    teststr = teststr.toLowerCase();
                }
                if (teststr.indexOf(userstr) > -1) {
                    return true;
                }
                teststr = in.readLine();
            }
            return false;
        }
        catch (IOException ioe) {
            throw new BuildException("Could not read file " + filename);
        }
        finally {
            try {
                in.close();
            }
            catch (Exception e) {
                throw new BuildException("Could not close file " + filename);
            }
        }
    }

}
