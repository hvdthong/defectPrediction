package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

/**
 * Condition that tests whether a given string evals to true
 *
 * @author Steve Loughran
 * @since Ant 1.5
 */
public class IsTrue extends ProjectComponent implements Condition {
    /**  
     * what we eval
     */ 
    private Boolean value=null;

    /**
     * set the value to be tested; let ant eval it to true/false
     */ 
    public void setValue(boolean value) {
        this.value = new Boolean(value);
    }

    /**
     * return the value;
     * @throws BuildException if someone forgot to spec a value
     */ 
    public boolean eval() throws BuildException {
        if (value == null) {
            throw new BuildException("Nothing to test for truth");
        }
        return value.booleanValue();
    }

}

