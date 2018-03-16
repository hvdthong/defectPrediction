package org.apache.tools.ant.types.resources.selectors;

import java.util.Stack;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collections;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;

/**
 * ResourceSelector container.
 * @since Ant 1.7
 */
public class ResourceSelectorContainer extends DataType {

    private Vector v = new Vector();

    /**
     * Default constructor.
     */
    public ResourceSelectorContainer() {
    }

    /**
     * Construct a new ResourceSelectorContainer with the specified array of selectors.
     * @param r the ResourceSelector[] to add.
     */
    public ResourceSelectorContainer(ResourceSelector[] r) {
        for (int i = 0; i < r.length; i++) {
            add(r[i]);
        }
    }

    /**
     * Add a ResourceSelector to the container.
     * @param s the ResourceSelector to add.
     */
    public void add(ResourceSelector s) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (s == null) {
            return;
        }
        v.add(s);
        setChecked(false);
    }

    /**
     * Learn whether this ResourceSelectorContainer has selectors.
     * @return boolean indicating whether selectors have been added to the container.
     */
    public boolean hasSelectors() {
        if (isReference()) {
            return ((ResourceSelectorContainer) getCheckedRef()).hasSelectors();
        }
        dieOnCircularReference();
        return !v.isEmpty();
    }

    /**
     * Get the count of nested selectors.
     * @return the selector count as int.
     */
    public int selectorCount() {
        if (isReference()) {
            return ((ResourceSelectorContainer) getCheckedRef()).selectorCount();
        }
        dieOnCircularReference();
        return v.size();
    }

    /**
     * Return an Iterator over the nested selectors.
     * @return Iterator of ResourceSelectors.
     */
    public Iterator getSelectors() {
        if (isReference()) {
            return ((ResourceSelectorContainer) getCheckedRef()).getSelectors();
        }
        dieOnCircularReference();
        return Collections.unmodifiableList(v).iterator();
    }

    /**
     * Overrides the version from DataType to recurse on nested ResourceSelectors.
     * @param stk the Stack of references.
     * @param p   the Project to resolve against.
     * @throws BuildException on error.
     */
    protected void dieOnCircularReference(Stack stk, Project p)
        throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (Iterator i = v.iterator(); i.hasNext();) {
                Object o = i.next();
                if (o instanceof DataType) {
                    stk.push(o);
                    invokeCircularReferenceCheck((DataType) o, stk, p);
                }
            }
            setChecked(true);
        }
    }

}
