package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;

import java.io.File;

/**
 * A base class for creating tasks for executing commands on ClearCase.
 * <p>
 * The class extends the 'exec' task as it operates by executing the cleartool program
 * supplied with ClearCase. By default the task expects the cleartool executable to be
 * in the path, * you can override this be specifying the cleartooldir attribute.
 * </p>
 * <p>
 * This class provides set and get methods for the 'viewpath' attribute. It
 * also contains constants for the flags that can be passed to cleartool.
 * </p>
 *
 * @author Curtis White
 */
public abstract class ClearCase extends Task {
    private String m_ClearToolDir = "";
    private String m_viewPath = null;

    /**
     * Set the directory where the cleartool executable is located
     *
     * @param dir the directory containing the cleartool executable
     */
    public final void setClearToolDir(String dir) {
        m_ClearToolDir = project.translatePath(dir);
    }

    /**
     * Builds and returns the command string to execute cleartool
     *
     * @return String containing path to the executable
     */
    protected final String getClearToolCommand() {
        String toReturn = m_ClearToolDir;
        if ( !toReturn.equals("") && !toReturn.endsWith("/") ) {
            toReturn += "/";
        }

        toReturn += CLEARTOOL_EXE;

        return toReturn;
    }

    /**
     * Set the path to the item in a clearcase view to operate on
     *
     * @param viewPath Path to the view directory or file
     */
    public final void setViewPath(String viewPath) {
        m_viewPath = viewPath;
    }

    /**
     * Get the path to the item in a clearcase view
     *
     * @return m_viewPath
     */
    public String getViewPath() {
        return m_viewPath;
    }


    protected int run(Commandline cmd) {
        try {
            Project aProj = getProject();
            Execute exe = new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN));
            exe.setAntRun(aProj);
            exe.setWorkingDirectory(aProj.getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            return exe.execute();
        }
        catch (java.io.IOException e) {
            throw new BuildException(e, location);
        }
    }

    /**
     * Constant for the thing to execute
     */
    private static final String CLEARTOOL_EXE = "cleartool";

    /**
     * The 'Update' command
     */
    public static final String COMMAND_UPDATE = "update";
    /**
     * The 'Checkout' command
     */
    public static final String COMMAND_CHECKOUT = "checkout";
    /**
     * The 'Checkin' command
     */
    public static final String COMMAND_CHECKIN = "checkin";
    /**
     * The 'UndoCheckout' command
     */
    public static final String COMMAND_UNCHECKOUT = "uncheckout";

}

