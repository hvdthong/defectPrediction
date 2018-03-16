package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class CurrentCall extends FunctionCall {
    public CurrentCall(QName fname) {
	super(fname);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	methodGen.getInstructionList().append(methodGen.loadCurrentNode());
    }
}
