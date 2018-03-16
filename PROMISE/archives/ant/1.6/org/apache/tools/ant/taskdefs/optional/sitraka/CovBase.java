package org.apache.tools.ant.taskdefs.optional.sitraka;

import java.io.File;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.util.FileUtils;

/**
 * Base class that deals with JProbe version incompatibilities.
 *
 * @since Ant 1.6
 *
 */
public abstract class CovBase extends Task {
    private File home;
    private static FileUtils fu = FileUtils.newFileUtils();
    private boolean isJProbe4 = false;
    private static boolean isDos = Os.isFamily("dos");

    /**
     * The directory where JProbe is installed.
     */
    public void setHome(File value) {
        this.home = value;
    }

    protected File getHome() {
        return home;
    }

    protected File findCoverageJar() {
        File loc = null;
        if (isJProbe4) {
            loc = fu.resolveFile(home, "lib/coverage.jar");
        } else {
            loc = fu.resolveFile(home, "coverage/coverage.jar");
            if (!loc.canRead()) {
                File newLoc = fu.resolveFile(home, "lib/coverage.jar");
                if (newLoc.canRead()) {
                    isJProbe4 = true;
                    loc = newLoc;
                }
            }
        }

        return loc;
    }

    protected String findExecutable(String relativePath) {
        if (isDos) {
            relativePath += ".exe";
        }

        File loc = null;
        if (isJProbe4) {
            loc = fu.resolveFile(home, "bin/" + relativePath);
        } else {
            loc = fu.resolveFile(home, relativePath);
            if (!loc.canRead()) {
                File newLoc = fu.resolveFile(home, "bin/" + relativePath);
                if (newLoc.canRead()) {
                    isJProbe4 = true;
                    loc = newLoc;
                }
            }
        }
        return loc.getAbsolutePath();
    }

    protected File createTempFile(String prefix) {
        return fu.createTempFile(prefix, ".tmp", null);
    }

    protected String getParamFileArgument() {
        return "-" + (!isJProbe4 ? "jp_" : "") + "paramfile=";
    }

    /**
     * Are we running on a version of JProbe 4.x or higher?
     */
    protected boolean isJProbe4Plus() {
        return isJProbe4;
    }
}
