package org.apache.ivy.plugins.circular;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;

public final class CircularDependencyHelper {

    /** CircularDependencyHelper is not designed to be an instance */
    private CircularDependencyHelper() {
        
    }
    
    /**
     * Returns a string representation of this circular dependency graph
     * 
     * @param descriptors
     *            in order of circular dependency
     * @return a string representation of this circular dependency graph
     */
    public static String formatMessage(final ModuleRevisionId[] mrids) {
        Set alreadyAdded = new HashSet();
        StringBuffer buff = new StringBuffer();
        buff.append(mrids[0]);
        alreadyAdded.add(mrids[0]);
        for (int i = 1; i < mrids.length; i++) {
            buff.append("->");
            if (alreadyAdded.add(mrids[i])) {
                buff.append(mrids[i]);
            } else {
                buff.append("...");
                break;
            }
        }
        return buff.toString();
    }

    public static String formatMessage(final ModuleDescriptor[] descriptors) {
        return formatMessage(toMrids(descriptors));
    }

    /**
     * @param loopElements
     *            a List<ModuleDescriptor>
     */
    public static String formatMessageFromDescriptors(List loopElements) {
        ModuleRevisionId[] mrids = new ModuleRevisionId[loopElements.size()];
        int pos = 0;
        for (Iterator it = loopElements.iterator(); it.hasNext();) {
            ModuleDescriptor descriptor = (ModuleDescriptor) it.next();
            mrids[pos] = descriptor.getModuleRevisionId();
            pos++;
        }
        return formatMessage(mrids);
    }

    public static ModuleRevisionId[] toMrids(ModuleDescriptor[] descriptors) {
        ModuleRevisionId[] mrids = new ModuleRevisionId[descriptors.length];
        for (int i = 0; i < descriptors.length; i++) {
            mrids[i] = descriptors[i].getModuleRevisionId();
        }
        return mrids;
    }

}
