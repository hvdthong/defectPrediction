package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;

import org.apache.xalan.xsltc.compiler.Constants;
import org.apache.xalan.xsltc.compiler.FlowList;

import org.apache.xml.dtm.DTM;

public final class ReferenceType extends Type {
    protected ReferenceType() {}

    public String toString() {
	return "reference";
    }

    public boolean identicalTo(Type other) {
	return this == other;
    }

    public String toSignature() {
	return "Ljava/lang/Object;";
    }

    public org.apache.bcel.generic.Type toJCType() {
	return org.apache.bcel.generic.Type.OBJECT;
    }

    /**
     * Translates a reference to an object of internal type <code>type</code>.
     * The translation to int is undefined since references
     * are always converted to reals in arithmetic expressions.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    Type type) {
	if (type == Type.String) {
	    translateTo(classGen, methodGen, (StringType) type);
	}
	else if (type == Type.Real) {
	    translateTo(classGen, methodGen, (RealType) type);
	}
	else if (type == Type.Boolean) {
	    translateTo(classGen, methodGen, (BooleanType) type);
	}
	else if (type == Type.NodeSet) {
	    translateTo(classGen, methodGen, (NodeSetType) type);
	}
	else if (type == Type.Node) {
	    translateTo(classGen, methodGen, (NodeType) type);
	}
	else if (type == Type.ResultTree) {
	    translateTo(classGen, methodGen, (ResultTreeType) type);
	}
	else if (type == Type.Object) {
	    translateTo(classGen, methodGen, (ObjectType) type);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.INTERNAL_ERR, type.toString());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Translates reference into object of internal type <code>type</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    StringType type) {
	final int current = methodGen.getLocalIndex("current");
	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();

	if (current < 0) {
	}
	else {
	    il.append(new ILOAD(current));
	}
	il.append(methodGen.loadDOM());
	final int stringF = cpg.addMethodref(BASIS_LIBRARY_CLASS,
					     "stringF",
					     "("
					     + OBJECT_SIG
					     + NODE_SIG
					     + DOM_INTF_SIG
					     + ")" + STRING_SIG);
	il.append(new INVOKESTATIC(stringF));
    }

    /**
     * Translates a reference into an object of internal type <code>type</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    RealType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	il.append(methodGen.loadDOM());
	int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "numberF",
				     "("
				     + OBJECT_SIG
				     + DOM_INTF_SIG
				     + ")D");
	il.append(new INVOKESTATIC(index));
    }

    /**
     * Translates a reference to an object of internal type <code>type</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    BooleanType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "booleanF",
				     "("
				     + OBJECT_SIG
				     + ")Z");
	il.append(new INVOKESTATIC(index));
    }

    /**
     * Casts a reference into a NodeIterator.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    NodeSetType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "referenceToNodeSet",
				     "("
				     + OBJECT_SIG
				     + ")"
				     + NODE_ITERATOR_SIG);
	il.append(new INVOKESTATIC(index));

	index = cpg.addInterfaceMethodref(NODE_ITERATOR, RESET, RESET_SIG);
	il.append(new INVOKEINTERFACE(index, 1));
    }

    /**
     * Casts a reference into a Node.
     *
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    NodeType type) {
	translateTo(classGen, methodGen, Type.NodeSet);
	Type.NodeSet.translateTo(classGen, methodGen, type);
    }

    /**
     * Casts a reference into a ResultTree.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    ResultTreeType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "referenceToResultTree",
				     "(" + OBJECT_SIG + ")" + DOM_INTF_SIG);
	il.append(new INVOKESTATIC(index));
    }

    /**
     * Subsume reference into ObjectType.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    ObjectType type) {
	methodGen.getInstructionList().append(NOP);
    }

    /**
     * Translates a reference into the Java type denoted by <code>clazz</code>.
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
			    Class clazz) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (clazz.getName().equals("java.lang.Object")) {
	    il.append(NOP);
	}
	else if (clazz == Double.TYPE) {
	    translateTo(classGen, methodGen, Type.Real);
	}
	else if (clazz.getName().equals("java.lang.String")) {
	    translateTo(classGen, methodGen, Type.String);
	}
	else if (clazz.getName().equals("org.w3c.dom.Node")) {
	    int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "referenceToNode",
				         "("
				         + OBJECT_SIG
				         + DOM_INTF_SIG
				         + ")"
				         + "Lorg/w3c/dom/Node;");
	    il.append(methodGen.loadDOM());
	    il.append(new INVOKESTATIC(index));
	}
	else if (clazz.getName().equals("org.w3c.dom.NodeList")) {
	    int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "referenceToNodeList",
				         "("
				         + OBJECT_SIG
				         + DOM_INTF_SIG
				         + ")"
				         + "Lorg/w3c/dom/NodeList;");
	    il.append(methodGen.loadDOM());
	    il.append(new INVOKESTATIC(index));
	}
	else if (clazz.getName().equals("org.apache.xalan.xsltc.DOM")) {
	    translateTo(classGen, methodGen, Type.ResultTree);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), clazz.getName());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Translates an external Java type into a reference. Only conversion
     * allowed is from java.lang.Object.
     */
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen,
			      Class clazz) {
	if (clazz.getName().equals("java.lang.Object")) {
	    methodGen.getInstructionList().append(NOP);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
				toString(), clazz.getName());
	    classGen.getParser().reportError(Constants.FATAL, err);
        }
    }

    /**
     * Expects a reference on the stack and translates it to a non-synthesized
     * boolean. It does not push a 0 or a 1 but instead returns branchhandle
     * list to be appended to the false list.
     *
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateToDesynthesized
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen,
					     MethodGenerator methodGen,
					     BooleanType type) {
	InstructionList il = methodGen.getInstructionList();
	translateTo(classGen, methodGen, type);
	return new FlowList(il.append(new IFEQ(null)));
    }

    /**
     * Translates an object of this type to its boxed representation.
     */
    public void translateBox(ClassGenerator classGen,
			     MethodGenerator methodGen) {
    }

    /**
     * Translates an object of this type to its unboxed representation.
     */
    public void translateUnBox(ClassGenerator classGen,
			       MethodGenerator methodGen) {
    }


    public Instruction LOAD(int slot) {
	return new ALOAD(slot);
    }

    public Instruction STORE(int slot) {
	return new ASTORE(slot);
    }
}

