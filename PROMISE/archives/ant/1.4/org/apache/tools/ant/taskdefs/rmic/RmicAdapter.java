package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;

/**
 * The interface that all rmic adapters must adher to.  
 *
 * <p>A rmic adapter is an adapter that interprets the rmic's
 * parameters in preperation to be passed off to the compiler this
 * adapter represents.  As all the necessary values are stored in the
 * Rmic task itself, the only thing all adapters need is the rmic
 * task, the execute command and a parameterless constructor (for
 * reflection).</p>
 *
 * @author Takashi Okamoto <tokamoto@rd.nttdata.co.jp>
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a> 
 */

public interface RmicAdapter {

    /**
     * Sets the rmic attributes, which are stored in the Rmic task.
     */
    public void setRmic( Rmic attributes );

    /**
     * Executes the task.
     *
     * @return has the compilation been successful
     */
    public boolean execute() throws BuildException;

    /**
     * Maps source class files to the files generated by this rmic
     * implementation.
     */
    public FileNameMapper getMapper();

    /**
     * The CLASSPATH this rmic process will use.
     */
    public Path getClasspath();
}