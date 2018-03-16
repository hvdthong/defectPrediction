package org.apache.tools.ant.taskdefs.condition;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.ReflectWrapper;
import org.apache.tools.ant.util.StringUtils;

/**
 * &lt;hasfreespace&gt;
 * <p>Condition returns true if selected partition
 * has the requested space, false otherwise.</p>
 * @since Ant 1.7
 */
public class HasFreeSpace implements Condition {

    private String partition;
    private String needed;

    /**
     * Evaluate the condition.
     * @return true if there enough free space.
     * @throws BuildException if there is a problem.
     */
    public boolean eval() throws BuildException {
        validate();
        try {
            if (JavaEnvUtils.isAtLeastJavaVersion("1.6")) {
                File fs = new File(partition);
                ReflectWrapper w = new ReflectWrapper(fs);
                long free = ((Long) w.invoke("getFreeSpace")).longValue();
                return free >= StringUtils.parseHumanSizes(needed);
            } else {
                throw new BuildException("HasFreeSpace condition not supported on Java5 or less.");
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    private void validate() throws BuildException {
        if (null == partition) {
            throw new BuildException("Please set the partition attribute.");
        }
        if (null == needed) {
            throw new BuildException("Please set the needed attribute.");
        }
    }

    /**
     * The partition/device to check
     * @return the partition.
     */
    public String getPartition() {
        return partition;
    }

    /**
     * Set the partition name.
     * @param partition the name to use.
     */
    public void setPartition(String partition) {
        this.partition = partition;
    }

    /**
     * The amount of free space required
     * @return the amount required
     */
    public String getNeeded() {
        return needed;
    }

    /**
     * Set the amount of space required.
     * @param needed the amount required.
     */
    public void setNeeded(String needed) {
        this.needed = needed;
    }
}
