package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class XslAttribute extends Instruction {

    private String _prefix;
    private AttributeValueTemplate _namespace = null;
    private boolean _ignore = false;

    /**
     * Returns the name of the attribute
     */
    public AttributeValue getName() {
	return _name;
    }

    /**
     * Displays the contents of the attribute
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Attribute " + _name);
	displayContents(indent + IndentIncrement);
    }
		
    /**
     * Parses the attribute's contents. Special care taken for namespaces.
     */
    public void parseContents(Parser parser) {
	boolean generated = false;
	final SymbolTable stable = parser.getSymbolTable();

	String name = getAttribute("name");
	String namespace = getAttribute("namespace");
	QName qname = parser.getQName(name, false);
	final String prefix = qname.getPrefix();

	if ((prefix != null) && (prefix.equals(XMLNS_PREFIX))) {
	    reportError(this, parser, ErrorMsg.ILLEGAL_ATTR_NAME_ERR, name);
	    return;
	}

	final SyntaxTreeNode parent = getParent();
	final Vector siblings = parent.getContents();
	for (int i = 0; i < parent.elementCount(); i++) {
	    SyntaxTreeNode item = (SyntaxTreeNode)siblings.elementAt(i);
	    if (item == this) break;

	    if (item instanceof XslAttribute) continue;
	    if (item instanceof UseAttributeSets) continue;
	    if (item instanceof LiteralAttribute) continue;
	    if (item instanceof Text) continue;

	    if (item instanceof If) continue;
	    if (item instanceof Choose) continue;
 	    if (item instanceof CopyOf) continue;
 	    if (item instanceof VariableBase) continue;

	    reportWarning(this, parser, ErrorMsg.STRAY_ATTRIBUTE_ERR, name);
	}

	if (namespace != null && namespace != Constants.EMPTYSTRING) {
	    _prefix = lookupPrefix(namespace);
	    _namespace = new AttributeValueTemplate(namespace, parser, this);
	}
	else if (prefix != null && prefix != Constants.EMPTYSTRING) {
	    _prefix = prefix;
	    namespace = lookupNamespace(prefix);
	    if (namespace != null) {
		_namespace = new AttributeValueTemplate(namespace, parser, this);
	    }
	}
	
	if (_namespace != null) {
	    if (_prefix == null || _prefix == Constants.EMPTYSTRING) {
		if (prefix != null) {
		    _prefix = prefix;
		}
		else {
		    _prefix = stable.generateNamespacePrefix();
		    generated = true;
		}
	    }
	    else if (prefix != null && !prefix.equals(_prefix)) {
		_prefix = prefix;
	    }

	    name = _prefix + ":" + qname.getLocalPart();

	    /*
	     * TODO: The namespace URI must be passed to the parent 
	     * element but we don't yet know what the actual URI is 
	     * (as we only know it as an attribute value template). 
	     */
	    if ((parent instanceof LiteralElement) && (!generated)) {
		((LiteralElement)parent).registerNamespace(_prefix,
							   namespace,
							   stable, false);
	    }
	}

	if (name.equals(XMLNS_PREFIX)) {
	    reportError(this, parser, ErrorMsg.ILLEGAL_ATTR_NAME_ERR, name);
	    return;
	}

	if (parent instanceof LiteralElement) {
	    ((LiteralElement)parent).addAttribute(this);
	}

	_name = AttributeValue.create(this, name, parser);
	parseChildren(parser);
    }
	
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (!_ignore) {
	    _name.typeCheck(stable);
	    if (_namespace != null) {
		_namespace.typeCheck(stable);
	    }
	    typeCheckContents(stable);
	}
	return Type.Void;
    }

    /**
     *
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_ignore) return;
	_ignore = true;

	if (_namespace != null) {
	    il.append(methodGen.loadHandler());
	    il.append(new PUSH(cpg,_prefix));
	    _namespace.translate(classGen,methodGen);
	    il.append(methodGen.namespace());
	}

	il.append(methodGen.loadHandler());
	

	if ((elementCount() == 1) && (elementAt(0) instanceof Text)) {
	    il.append(new PUSH(cpg, ((Text)elementAt(0)).getText()));
	}
	else {
	    il.append(classGen.loadTranslet());
	    il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
						   "stringValueHandler",
						   STRING_VALUE_HANDLER_SIG)));
	    il.append(DUP);
	    il.append(methodGen.storeHandler());
	    translateContents(classGen, methodGen);
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(STRING_VALUE_HANDLER,
							 "getValue",
							 "()" + STRING_SIG)));
	}

	il.append(methodGen.attribute());
	il.append(methodGen.storeHandler());
    }
}
