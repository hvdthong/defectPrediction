package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.types.Commandline;

/**
 * The implementation of the rmic for Kaffe
 *
 * @since Ant 1.4
 */
public class KaffeRmic extends DefaultRmicAdapter {
    private static final String[] RMIC_CLASSNAMES = new String[] {
        "gnu.classpath.tools.rmi.rmic.RMIC",
        "gnu.java.rmi.rmic.RMIC",
        "kaffe.rmi.rmic.RMIC",
    };

    /**
     * the name of this adapter for users to select
     */
    public static final String COMPILER_NAME = "kaffe";


    public boolean execute() throws BuildException {
        getRmic().log("Using Kaffe rmic", Project.MSG_VERBOSE);
        Commandline cmd = setupRmicCommand();

        Class c = getRmicClass();
        if (c == null) {
            StringBuffer buf = new StringBuffer("Cannot use Kaffe rmic, as it"
                                                + " is not available.  None"
                                                + " of ");
            for (int i = 0; i < RMIC_CLASSNAMES.length; i++) {
                if (i != 0) {
                    buf.append(", ");
                }
                
                buf.append(RMIC_CLASSNAMES[i]);
            }
            buf.append(" have been found. A common solution is to set the"
                       + " environment variable JAVA_HOME or CLASSPATH.");
            throw new BuildException(buf.toString(),
                                     getRmic().getLocation());
        }

        cmd.setExecutable(c.getName());
        ExecuteJava ej = new ExecuteJava();
        ej.setJavaCommand(cmd);
        return ej.fork(getRmic()) == 0;
    }

    /**
     * test for kaffe being on the system
     * @return true if kaffe is on the current classpath
     */
    public static boolean isAvailable() {
        return getRmicClass() != null;
    }

    /**
     * tries to load Kaffe RMIC and falls back to the older class name
     * if necessary.
     *
     * @return null if neither class can get loaded.
     */
    private static Class getRmicClass() {
        for (int i = 0; i < RMIC_CLASSNAMES.length; i++) {
            try {
                return Class.forName(RMIC_CLASSNAMES[i]);
            } catch (ClassNotFoundException cnfe) {
            }
        }
        return null;
    }
}
