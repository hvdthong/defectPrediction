package org.apache.tools.ant.taskdefs.optional.vss;


import java.io.File;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;

/**
 * A base class for creating tasks for executing commands on Visual SourceSafe.
 * <p>
 * The class extends the 'exec' task as it operates by executing the ss.exe program
 * supplied with SourceSafe. By default the task expects ss.exe to be in the path,
 * you can override this be specifying the ssdir attribute.
 * </p>
 * <p>
 * This class provides set and get methods for 'login' and 'vsspath' attributes. It
 * also contains constants for the flags that can be passed to SS.
 * </p>
 *
 * @author Craig Cottingham
 * @author Andrew Everitt
 */
public abstract class MSVSS extends Task {

    private String m_SSDir = "";
    private String m_vssLogin = null;
    private String m_vssPath = null;

    /**
     * Set the directory where ss.exe is located
     *
     * @param dir the directory containing ss.exe
     */
    public final void setSsdir(String dir) {
        m_SSDir = project.translatePath(dir);
    }
    
    /**
     * Builds and returns the command string to execute ss.exe
     */
    public final String getSSCommand() {
        String toReturn = m_SSDir;
        if ( !toReturn.equals("") && !toReturn.endsWith("\\") ) {
            toReturn += "\\";
        }
        toReturn += SS_EXE;
        
        return toReturn;
    }

    /**
     * Set the login to use when accessing vss.
     * <p>
     * Should be formatted as username,password
     *
     * @param login the login string to use
     */
    public final void setLogin(String login) {
        m_vssLogin = login;
    }

    /**
     * @return the appropriate login command if the 'login' attribute was specified, otherwise an empty string
     */
    public void getLoginCommand(Commandline cmd) {
        if ( m_vssLogin == null ) {
            return;
        } else {
            cmd.createArgument().setValue(FLAG_LOGIN + m_vssLogin);
        }
    }

    /**
     * Set the path to the item in vss to operate on
     * <p>
     * Ant can't cope with a '$' sign in an attribute so we have to add it here.
     *
     * @param vssPath
     */
    public final void setVsspath(String vssPath) {
            m_vssPath= PROJECT_PREFIX + vssPath.substring(5);
        } else {
            m_vssPath = PROJECT_PREFIX + vssPath;
        }
    }

    /**
     * @return m_vssPath
     */
    public String getVsspath() {
        return m_vssPath;
    }

    protected int run(Commandline cmd) {
        try {
            Execute exe = new Execute(new LogStreamHandler(this, 
                                                           Project.MSG_INFO,
                                                           Project.MSG_WARN));
            exe.setAntRun(project);
            exe.setWorkingDirectory(project.getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            return exe.execute();
        } catch (java.io.IOException e) {
            throw new BuildException(e, location);
        }
    }

    /**
     * Constant for the thing to execute
     */
    private static final String SS_EXE = "ss";
    /** */
    public static final String PROJECT_PREFIX = "$";

    /**
     * The 'Get' command
     */
    public static final String COMMAND_GET = "Get";
    /**
     * The 'Checkout' command
     */
    public static final String COMMAND_CHECKOUT = "Checkout";
    /**
     * The 'Label' command
     */
    public static final String COMMAND_LABEL = "Label";
    /**
     * The 'History' command
     */
    public static final String COMMAND_HISTORY = "History";

    /** */
    public static final String FLAG_LOGIN = "-Y";
    /** */
    public static final String FLAG_OVERRIDE_WORKING_DIR = "-GL";
    /** */
    public static final String FLAG_AUTORESPONSE_DEF = "-I-";
    /** */
    public static final String FLAG_AUTORESPONSE_YES = "-I-Y";
    /** */
    public static final String FLAG_AUTORESPONSE_NO = "-I-N";
    /** */
    public static final String FLAG_RECURSION = "-R";
    /** */
    public static final String FLAG_VERSION = "-V";
    /** */
    public static final String FLAG_VERSION_DATE = "-Vd";
    /** */
    public static final String FLAG_VERSION_LABEL = "-VL";
    /** */
    public static final String FLAG_WRITABLE = "-W";
    /** */
    public static final String VALUE_NO = "-N";
    /** */
    public static final String VALUE_YES = "-Y";
}

