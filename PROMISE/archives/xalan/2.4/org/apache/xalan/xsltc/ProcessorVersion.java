package org.apache.xalan.xsltc;


/**
 * Admin class that assigns a version number to the XSLTC software.
 * The version number is made up from three fields as in:
 * MAJOR.MINOR[.DELTA]. Fields are incremented based on the following:
 * DELTA field: changes for each bug fix, developer fixing the bug should
 *		increment this field.
 * MINOR field: API changes or a milestone culminating from several
 *              bug fixes. DELTA field goes to zero and MINOR is
 *		incremented such as: {1.0,1.0.1,1.0.2,1.0.3,...1.0.18,1.1}
 * MAJOR field: milestone culminating in fundamental API changes or 
 *              architectural changes.  MINOR field goes to zero
 *		and MAJOR is incremented such as: {...,1.1.14,1.2,2.0}
 * Stability of a release follows: X.0 > X.X > X.X.X  
 */
public class ProcessorVersion {
    private static int MAJOR = 1;
    private static int MINOR = 0;
    private static int DELTA = 0;

    public static void main(String[] args) {
	System.out.println("XSLTC version " + MAJOR + "." + MINOR +
	    ((DELTA > 0) ? ("."+DELTA) : ("")));
    }
}
