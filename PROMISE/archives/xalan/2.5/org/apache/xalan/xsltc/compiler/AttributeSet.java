package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.util.AttributeSetMethodGenerator;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class AttributeSet extends TopLevelElement {

    private static final String AttributeSetPrefix = "$as$";
    
    private QName            _name;
    private UseAttributeSets _useSets;
    private AttributeSet     _mergeSet;
    private String           _method;
    private boolean          _ignore = false;
    
    /**
     * Returns the QName of this attribute set
     */
    public QName getName() {
	return _name;
    }

    /**
     * Returns the method name of this attribute set. This method name is
     * generated by the compiler (XSLTC)
     */
    public String getMethodName() {
	return _method;
    }

    /**
     * Call this method to prevent a method for being compiled for this set.
     * This is used in case several <xsl:attribute-set...> elements constitute
     * a single set (with one name). The last element will merge itself with
     * any previous set(s) with the same name and disable the other set(s).
     */
    public void ignore() {
	_ignore = true;
    }

    /**
     * Parse the contents of this attribute set. Recognised attributes are
     * "name" (required) and "use-attribute-sets" (optional).
     */
    public void parseContents(Parser parser) {
	
	_name = parser.getQNameIgnoreDefaultNs(getAttribute("name"));
	if ((_name == null) || (_name.equals(EMPTYSTRING))) {
	    ErrorMsg msg = new ErrorMsg(ErrorMsg.UNNAMED_ATTRIBSET_ERR, this);
	    parser.reportError(Constants.ERROR, msg);
	}

	final String useSets = getAttribute("use-attribute-sets");
	if (useSets.length() > 0) {
	    _useSets = new UseAttributeSets(useSets, parser);
	}

	final Vector contents = getContents();
	final int count = contents.size();
	for (int i=0; i<count; i++) {
	    SyntaxTreeNode child = (SyntaxTreeNode)contents.elementAt(i);
	    if (child instanceof XslAttribute) {
		parser.getSymbolTable().setCurrentNode(child);
		child.parseContents(parser);
	    }
	    else if (child instanceof Text) {
	    }
	    else {
		ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_CHILD_ERR, this);
		parser.reportError(Constants.ERROR, msg);
	    }
	}

	parser.getSymbolTable().setCurrentNode(this);
    }

    /**
     * Type check the contents of this element
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

	if (_ignore) return (Type.Void);

	_mergeSet = stable.addAttributeSet(this);

	_method = AttributeSetPrefix + getXSLTC().nextAttributeSetSerial();

	if (_useSets != null) _useSets.typeCheck(stable);
	typeCheckContents(stable);
	return Type.Void;
    }

    /**
     * Compile a method that outputs the attributes in this set
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	if (_ignore) return;

	methodGen = new AttributeSetMethodGenerator(_method, classGen);

        if (_mergeSet != null) {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            final String methodName = _mergeSet.getMethodName();

            il.append(classGen.loadTranslet());
            il.append(methodGen.loadHandler());
            il.append(methodGen.loadIterator());
            final int method = cpg.addMethodref(classGen.getClassName(),
                                                methodName, ATTR_SET_SIG);
            il.append(new INVOKESPECIAL(method));
        }

	if (_useSets != null) _useSets.translate(classGen, methodGen);

	final Enumeration attributes = elements();
	while (attributes.hasMoreElements()) {
	    SyntaxTreeNode element = (SyntaxTreeNode)attributes.nextElement();
	    if (element instanceof XslAttribute) {
		final XslAttribute attribute = (XslAttribute)element;
		attribute.translate(classGen, methodGen);
	    }
	}
	final InstructionList il = methodGen.getInstructionList();
	il.append(RETURN);
	
	methodGen.stripAttributes(true);
	methodGen.setMaxLocals();
	methodGen.setMaxStack();
	methodGen.removeNOPs();
	classGen.addMethod(methodGen.getMethod());
    }

    public String toString() {
	StringBuffer buf = new StringBuffer("attribute-set: ");
	final Enumeration attributes = elements();
	while (attributes.hasMoreElements()) {
	    final XslAttribute attribute =
		(XslAttribute)attributes.nextElement();
	    buf.append(attribute);
	}
	return(buf.toString());
    }
}
