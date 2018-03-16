package org.apache.tools.ant.taskdefs.optional.jdepend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PathTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * Ant task to run JDepend tests.
 *
 * <p>JDepend is a tool to generate design quality metrics for each Java package.
 * It has been initially created by Mike Clark. JDepend can be found at <a
 *
 * The current implementation spawn a new Java VM.
 *
 * @author <a href="mailto:Jerome@jeromelacoste.com">Jerome Lacoste</a>
 */
public class JDependTask extends Task {
    private CommandlineJava commandline = new CommandlineJava();       
        
    private Path _sourcesPath;

    private File _outputFile;
    private File _dir;
    private Path _compileClasspath;
    private boolean _haltonerror = false;
    private boolean _fork = false;    
    
    public JDependTask() {
        commandline.setClassname("jdepend.textui.JDepend");
    }

/*
    public void setTimeout(Integer value) {
        _timeout = value;
    }
    
    public Integer getTimeout() {
        return _timeout;
    }
*/
    
    public void setOutputFile(File outputFile) {
        _outputFile = outputFile;
    }

    public File getOutputFile() {
        return _outputFile;
    }

    /**
     * Halt on Failure? default: false.
     */
    public void setHaltonerror(boolean value) {
        _haltonerror = value;
    }

    public boolean getHaltonerror() {
        return _haltonerror;
    }

    /**
     * Tells whether a JVM should be forked for the task. Default: false.
     * @param   value   <tt>true</tt> if a JVM should be forked, otherwise <tt>false<tt>
     */
    public void setFork(boolean value) {    
        _fork = value;            
    }    

    public boolean getFork() {
        return _fork;
    }

    /**
     * Set a new VM to execute the task. Default is <tt>java</tt>. Ignored if no JVM is forked.
     * @param   value   the new VM to use instead of <tt>java</tt>
     * @see #setFork(boolean)
     */
    public void setJvm(String value) {
        commandline.setVm(value);
    }
    
    /**
     * Maybe creates a nested classpath element.
     */
    public Path createSourcespath() {
        if (_sourcesPath == null) {
            _sourcesPath = new Path(project);
        }
        return _sourcesPath.createPath();
    }

    /** Gets the sourcepath. */
    public Path getSourcespath() {
        return _sourcesPath;
    }
 
    /**
     * The directory to invoke the VM in. Ignored if no JVM is forked.
     * @param   dir     the directory to invoke the JVM from.
     * @see #setFork(boolean)
     */
    public void setDir(File dir) {
        _dir = dir;
    }

    public File getDir() {
        return _dir;
    }

    /**
     * Set the classpath to be used for this compilation.
     */
    public void setClasspath(Path classpath) {
        if (_compileClasspath == null) {
            _compileClasspath = classpath;
        } else {
            _compileClasspath.append(classpath);
        }
    }

    /** Gets the classpath to be used for this compilation. */
    public Path getClasspath() {
        return _compileClasspath;
    }

    /**
     * Maybe creates a nested classpath element.
     */
    public Path createClasspath() {
        if (_compileClasspath == null) {
            _compileClasspath = new Path(project);
        }
        return _compileClasspath.createPath();
    }

    /**
     * Create a new JVM argument. Ignored if no JVM is forked.
     * @return  create a new JVM argument so that any argument can be passed to the JVM.
     * @see #setFork(boolean)
     */
    public Commandline.Argument createJvmarg() {
        return commandline.createVmArgument();
    }

    /**
     * Adds a reference to a CLASSPATH defined elsewhere.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * No problems with this test.
     */
    private static final int SUCCESS = 0;    
    /**
     * An error occured.
     */    
    private static final int ERRORS = 1;
    
    public void execute() throws BuildException {
        if (getSourcespath() == null)
            throw new BuildException("Missing Sourcepath required argument");
            
        int exitValue = JDependTask.ERRORS;
        boolean wasKilled = false;
        if (! getFork()) {
            exitValue = executeInVM();
        } else {
            ExecuteWatchdog watchdog = createWatchdog();                
            exitValue = executeAsForked(watchdog);
            if (watchdog != null) {
            }
        }

        boolean errorOccurred = exitValue == JDependTask.ERRORS;

        if (errorOccurred) {
            if  (getHaltonerror())
                throw new BuildException("JDepend failed",
                                         location);
            else
                log("JDepend FAILED", Project.MSG_ERR);
        }
    }        


            
        
    /**
     * Execute inside VM.
     */
    public int executeInVM() throws BuildException {
        jdepend.textui.JDepend jdepend = new jdepend.textui.JDepend();
       
        if (getOutputFile() != null) {
            FileWriter fw;
            try {
                fw = new FileWriter(getOutputFile().getPath());
            }
            catch (IOException e) {
                String msg = "JDepend Failed when creating the output file: " + e.getMessage();
                log(msg);
                throw new BuildException(msg);
            }
            jdepend.setWriter(new PrintWriter(fw));
            log("Ouptut to be stored in " + getOutputFile().getPath());
        }

        PathTokenizer sourcesPath = new PathTokenizer(getSourcespath().toString());
        while (sourcesPath.hasMoreTokens()) {
            File f = new File(sourcesPath.nextToken());
            
            if (! f.exists() || !f.isDirectory()) {
                String msg = "\""+ f.getPath() + "\" does not represent a valid directory. JDepend would fail.";
                log(msg);
                throw new BuildException(msg);
            }
            try {                  
                jdepend.addDirectory(f.getPath());                  
            }
            catch (IOException e) {
                String msg = "JDepend Failed when adding a source directory: " + e.getMessage();
                log(msg);
                throw new BuildException(msg);
            }
        }
        jdepend.analyze();
        return SUCCESS;
    }
    

    /**
     * Execute the task by forking a new JVM. The command will block until
     * it finishes. To know if the process was destroyed or not, use the
     * <tt>killedProcess()</tt> method of the watchdog class.
     * @param  watchdog   the watchdog in charge of cancelling the test if it
     * exceeds a certain amount of time. Can be <tt>null</tt>, in this case
     * the test could probably hang forever.
     */
    public int executeAsForked(ExecuteWatchdog watchdog) throws BuildException {      
        createClasspath();

        if (getClasspath().toString().length() > 0) {
            createJvmarg().setValue("-classpath");
            createJvmarg().setValue(getClasspath().toString());
        }

        if (getOutputFile() != null) {
            commandline.createArgument().setValue("-file");
            commandline.createArgument().setValue(_outputFile.getPath());
        }

        PathTokenizer sourcesPath = new PathTokenizer(getSourcespath().toString());
        while (sourcesPath.hasMoreTokens()) {
            File f = new File(sourcesPath.nextToken());
            
            if (! f.exists() || !f.isDirectory())
                throw new BuildException("\""+ f.getPath() + "\" does not represent a valid directory. JDepend would fail.");
            commandline.createArgument().setValue(f.getPath());
        }
        
        Execute execute = new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN), watchdog);        
        execute.setCommandline(commandline.getCommandline());
        if (getDir() != null) {
            execute.setWorkingDirectory(getDir());
            execute.setAntRun(project);
        }

        if (getOutputFile() != null) 
            log("Ouptut to be stored in " + getOutputFile().getPath());
        log("Executing: "+commandline.toString(), Project.MSG_VERBOSE);
        try {
            return execute.execute();
        } catch (IOException e) {
            throw new BuildException("Process fork failed.", e, location);
        }
    }

    /**
     * @return <tt>null</tt> if there is a timeout value, otherwise the
     * watchdog instance.
     */
    protected ExecuteWatchdog createWatchdog() throws BuildException {

        return null;
        /*
          if (getTimeout() == null){
          return null;
          }
          return new ExecuteWatchdog(getTimeout().intValue());
        */
    }
}
