package org.apache.tools.ant.taskdefs.optional.junit;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * This class is not used by the framework any more.
 * We plan to remove it in Ant 1.8
 * @deprecated since Ant 1.7
 *
 */
abstract class XalanExecutor {
    private static final String PACKAGE =
        "org.apache.tools.ant.taskdefs.optional.junit.";

    /** the transformer caller */
    protected AggregateTransformer caller;

    /** set the caller for this object. */
    private void setCaller(AggregateTransformer caller) {
        this.caller = caller;
    }

    /** get the appropriate stream based on the format (frames/noframes) */
    protected final OutputStream getOutputStream() throws IOException {
        if (AggregateTransformer.FRAMES.equals(caller.format)) {
            return new ByteArrayOutputStream();
        } else {
            return new BufferedOutputStream(
                new FileOutputStream(new File(caller.toDir, "junit-noframes.html")));
        }
    }

    /** override to perform transformation */
    abstract void execute() throws Exception;

    /**
     * Create a valid Xalan executor. It checks if Xalan2 is
     * present. If none is available, it fails.
     * @param caller object containing the transformation information.
     * @throws BuildException thrown if it could not find a valid xalan
     * executor.
     */
    static XalanExecutor newInstance(AggregateTransformer caller)
        throws BuildException {
        XalanExecutor executor = null;
        try {
            Class clazz = Class.forName(PACKAGE + "Xalan2Executor");
            executor = (XalanExecutor) clazz.newInstance();
        } catch (Exception xsltcApacheMissing) {
            caller.task.log(xsltcApacheMissing.toString());
                throw new BuildException("Could not find xstlc nor xalan2 "
                                         + "in the classpath. Check "
        }
        String classNameImpl = executor.getImplementation();
        String version = executor.getProcVersion(classNameImpl);
        caller.task.log("Using " + version, Project.MSG_VERBOSE);
        executor.setCaller(caller);
        return executor;
    }

    /**
     * This methods should return the classname implementation of the
     * underlying xslt processor
     * @return the classname of the implementation, for example:
     * org.apache.xalan.processor.TransformerFactoryImpl
     * @see #getProcVersion(String)
     */
    protected abstract String getImplementation();

    /**
     * Try to discover the xslt processor version based on the
     * className. There is nothing carved in stone and it can change
     * anytime, so this is just for the sake of giving additional
     * information if we can find it.
     * @param classNameImpl the classname of the underlying xslt processor
     * @return a string representing the implementation version.
     * @throws BuildException
     */
    protected abstract String getProcVersion(String classNameImpl)
        throws BuildException;

    /** a bit simplistic but xsltc data are conveniently private non final */
    protected final String getXSLTCVersion(String procVersionClassName)
        throws ClassNotFoundException {
        Class procVersion = Class.forName(procVersionClassName);
        Package pkg = procVersion.getPackage();
        return pkg.getName() + " " + pkg.getImplementationTitle()
            + " " + pkg.getImplementationVersion();
    }

    /** pretty useful data (Xalan version information) to display. */
    protected final String getXalanVersion(String procVersionClassName)
        throws ClassNotFoundException {
        Class procVersion = Class.forName(procVersionClassName);
        String pkg = procVersion.getPackage().getName();
        try {
            Field f = procVersion.getField("S_VERSION");
            return pkg + " " + f.get(null).toString();
        } catch (Exception e) {
            return pkg + " ?.?";
        }
    }
}
