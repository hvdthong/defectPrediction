package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class AlternativePattern extends Pattern {
    private final Pattern _left;
    private final Pattern _right;
		
    /**
     * Construct an alternative pattern. The method <code>setParent</code>
     * should not be called in this case.
     */
    public AlternativePattern(Pattern left, Pattern right) {
	_left = left;
	_right = right;
    }
		
    public void setParser(Parser parser) {
	super.setParser(parser);
	_left.setParser(parser);
	_right.setParser(parser);
    }
    
    public Pattern getLeft() {
	return _left;
    }

    public Pattern getRight() {
	return _right;
    }

    /**
     * The type of an '|' is not really defined, hence null is returned.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	_left.typeCheck(stable);
	_right.typeCheck(stable);
	return null;
    }

    public double getPriority() {
	double left = _left.getPriority();
	double right = _right.getPriority();
	
	if (left < right)
	    return(left);
	else
	    return(right);
    }

    public String toString() {
	return "alternative(" + _left + ", " + _right + ')';
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final InstructionList il = methodGen.getInstructionList();

	_left.translate(classGen, methodGen);
	final InstructionHandle gotot = il.append(new GOTO(null));
	il.append(methodGen.loadContextNode());
	_right.translate(classGen, methodGen);

	_left._trueList.backPatch(gotot);
	_left._falseList.backPatch(gotot.getNext());

	_trueList.append(_right._trueList.add(gotot));
	_falseList.append(_right._falseList);
    }
}
