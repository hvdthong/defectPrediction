package org.apache.xalan.xsltc.compiler;

import java.util.ArrayList;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.FilterGenerator;
import org.apache.xalan.xsltc.compiler.util.IntType;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NumberType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.ResultTreeType;
import org.apache.xalan.xsltc.compiler.util.TestGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Predicate extends Expression implements Closure {

    private boolean _nthPositionFilter = false;
    private boolean _nthDescendant = false;
    private boolean _canOptimize = true;
    private int     _ptype = -1;

    private String _className = null;
    private ArrayList _closureVars = null;
    private Closure _parentClosure = null;

    public Predicate(Expression exp) {
	(_exp = exp).setParent(this);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_exp.setParser(parser);
    }

    public boolean isNthDescendant() {
	return _nthDescendant;
    }

    public boolean isNthPositionFilter() {
	return _nthPositionFilter;
    }

    public void dontOptimize() {
	_canOptimize = false;
    }


    /**
     * Returns true if this closure is compiled in an inner class (i.e.
     * if this is a real closure).
     */
    public boolean inInnerClass() {
	return (_className != null);
    }

    /**
     * Returns a reference to its parent closure or null if outermost.
     */
    public Closure getParentClosure() {
	if (_parentClosure == null) {
	    SyntaxTreeNode node = getParent();
	    do {
		if (node instanceof Closure) {
		    _parentClosure = (Closure) node;
		    break;
		}
		if (node instanceof TopLevelElement) {
		}
		node = node.getParent();
	    } while (node != null);
	}
	return _parentClosure;
    }

    /**
     * Returns the name of the auxiliary class or null if this predicate 
     * is compiled inside the Translet.
     */
    public String getInnerClassName() {
	return _className;
    }

    /**
     * Add new variable to the closure.
     */
    public void addVariable(VariableRefBase variableRef) {
	if (_closureVars == null) {
	    _closureVars = new ArrayList();
	}

	if (!_closureVars.contains(variableRef)) {
	    _closureVars.add(variableRef);

	    Closure parentClosure = getParentClosure();
	    if (parentClosure != null) {
		parentClosure.addVariable(variableRef);
	    }
	}
    }


    public int getPosType() {
	if (_ptype == -1) {
	    SyntaxTreeNode parent = getParent();
	    if (parent instanceof StepPattern) {
		_ptype = ((StepPattern)parent).getNodeType();
	    }
	    else if (parent instanceof AbsoluteLocationPath) {
		AbsoluteLocationPath path = (AbsoluteLocationPath)parent;
		Expression exp = path.getPath();
		if (exp instanceof Step) {
		    _ptype = ((Step)exp).getNodeType();
		}
	    }
	    else if (parent instanceof VariableRefBase) {
		final VariableRefBase ref = (VariableRefBase)parent;
		final VariableBase var = ref.getVariable();
		final Expression exp = var.getExpression();
		if (exp instanceof Step) {
		    _ptype = ((Step)exp).getNodeType();
		}
	    }
	    else if (parent instanceof Step) {
		_ptype = ((Step)parent).getNodeType();
	    }
	}
	return _ptype;
    }

    public boolean parentIsPattern() {
	return (getParent() instanceof Pattern);
    }

    public Expression getExpr() {
	return _exp;
    }

    public String toString() {
	if (isNthPositionFilter())
	    return "pred([" + _exp + "],"+getPosType()+")";
	else
	    return "pred(" + _exp + ')';
    }
	
    /**
     * Type check a predicate expression. If the type of the expression is 
     * number convert it to boolean by adding a comparison with position().
     * Note that if the expression is a parameter, we cannot distinguish
     * at compile time if its type is number or not. Hence, expressions of 
     * reference type are always converted to booleans.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

	Type texp = _exp.typeCheck(stable);

	if (texp instanceof ReferenceType) {
	    _exp = new CastExpr(_exp, texp = Type.Real);
	}

	if (texp instanceof ResultTreeType) {
	    _exp = new CastExpr(_exp, Type.Boolean);
	    _exp = new CastExpr(_exp, Type.Real);
	    texp = _exp.typeCheck(stable);
	}

	if (texp instanceof NumberType) {

	    if (texp instanceof IntType == false) {
		_exp = new CastExpr(_exp, Type.Int);
	    }

	    SyntaxTreeNode parent = getParent();

	    if ((_exp instanceof LastCall) ||
		(parent instanceof Pattern) ||
		(parent instanceof FilterExpr)) {

		if (parent instanceof Pattern && !(_exp instanceof LastCall)) {
 		    _nthPositionFilter = _canOptimize;
		}
		else if (parent instanceof FilterExpr) {
		    FilterExpr filter = (FilterExpr)parent;
		    Expression fexp = filter.getExpr();

		    if (fexp instanceof KeyCall)
			_canOptimize = false;
		    else if (fexp instanceof VariableRefBase)
		        _canOptimize = false;
		    else if (fexp instanceof ParentLocationPath)
			_canOptimize = false;
		    else if (fexp instanceof UnionPathExpr)
			_canOptimize = false;
		    else if (_exp.hasPositionCall() && _exp.hasLastCall())
			_canOptimize = false;
		    else if (filter.getParent() instanceof FilterParentPath)
			_canOptimize = false;
		    if (_canOptimize)
			_nthPositionFilter = true;
		}

                if (_nthPositionFilter) {
                   return _type = Type.NodeSet;
                } else {
                   final QName position =
                                getParser().getQNameIgnoreDefaultNs("position");

                   final PositionCall positionCall =
                                               new PositionCall(position);
                   positionCall.setParser(getParser());
                   positionCall.setParent(this);

                   _exp = new EqualityExpr(EqualityExpr.EQ, positionCall,
                                            _exp);
                   if (_exp.typeCheck(stable) != Type.Boolean) {
                       _exp = new CastExpr(_exp, Type.Boolean);
                   }

                   return _type = Type.Boolean;
                }
	    }
	    else {
		if ((parent != null) && (parent instanceof Step)) {
		    parent = parent.getParent();
		    if ((parent != null) &&
			(parent instanceof AbsoluteLocationPath)) {
			_nthDescendant = true;
			return _type = Type.NodeSet;
		    }
		}
		_nthPositionFilter = true;
		return _type = Type.NodeSet;
	    }
	}
	else if (texp instanceof BooleanType) {
	    if (_exp.hasPositionCall())
		_nthPositionFilter = true;
	}
	else {
	    _exp = new CastExpr(_exp, Type.Boolean);
	}
	_nthPositionFilter = false;

	return _type = Type.Boolean;
    }
	
    /**
     * Create a new "Filter" class implementing
     * <code>CurrentNodeListFilter</code>. Allocate registers for local 
     * variables and local parameters passed in the closure to test().
     * Notice that local variables need to be "unboxed".
     */
    private void compileFilter(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	TestGenerator testGen;
	LocalVariableGen local;
	FilterGenerator filterGen;

	_className = getXSLTC().getHelperClassName();
	filterGen = new FilterGenerator(_className,
					"java.lang.Object",
					toString(), 
					ACC_PUBLIC | ACC_SUPER,
					new String[] {
					    CURRENT_NODE_LIST_FILTER
					},
					classGen.getStylesheet());	

	final ConstantPoolGen cpg = filterGen.getConstantPool();
	final int length = (_closureVars == null) ? 0 : _closureVars.size();

	for (int i = 0; i < length; i++) {
	    VariableBase var = ((VariableRefBase) _closureVars.get(i)).getVariable();

	    filterGen.addField(new Field(ACC_PUBLIC, 
					cpg.addUtf8(var.getVariable()),
					cpg.addUtf8(var.getType().toSignature()),
					null, cpg.getConstantPool()));
	}

	final InstructionList il = new InstructionList();
	testGen = new TestGenerator(ACC_PUBLIC | ACC_FINAL,
				    org.apache.bcel.generic.Type.BOOLEAN, 
				    new org.apache.bcel.generic.Type[] {
					org.apache.bcel.generic.Type.INT,
					org.apache.bcel.generic.Type.INT,
					org.apache.bcel.generic.Type.INT,
					org.apache.bcel.generic.Type.INT,
					Util.getJCRefType(TRANSLET_SIG),
					Util.getJCRefType(NODE_ITERATOR_SIG)
				    },
				    new String[] {
					"node",
					"position",
					"last",
					"current",
					"translet",
					"iterator"
				    },
				    "test", _className, il, cpg);
		
	local = testGen.addLocalVariable("document",
					 Util.getJCRefType(DOM_INTF_SIG),
					 null, null);
	final String className = classGen.getClassName();
	il.append(filterGen.loadTranslet());
	il.append(new CHECKCAST(cpg.addClass(className)));
	il.append(new GETFIELD(cpg.addFieldref(className,
					       DOM_FIELD, DOM_INTF_SIG)));
	il.append(new ASTORE(local.getIndex()));

	testGen.setDomIndex(local.getIndex());

	_exp.translate(filterGen, testGen);
	il.append(IRETURN);
	
	testGen.stripAttributes(true);
	testGen.setMaxLocals();
	testGen.setMaxStack();
	testGen.removeNOPs();
	filterGen.addEmptyConstructor(ACC_PUBLIC);
	filterGen.addMethod(testGen.getMethod());
		
	getXSLTC().dumpClass(filterGen.getJavaClass());
    }

    /**
     * Returns true if the predicate is a test for the existance of an
     * element or attribute. All we have to do is to get the first node
     * from the step, check if it is there, and then return true/false.
     */
    public boolean isBooleanTest() {
	return (_exp instanceof BooleanExpr);
    }

    /**
     * Method to see if we can optimise the predicate by using a specialised
     * iterator for expressions like '/foo/bar[@attr = $var]', which are
     * very common in many stylesheets
     */
    public boolean isNodeValueTest() {
	if (!_canOptimize) return false;
	return (getStep() != null && getCompareValue() != null);
    }

    private Expression _value = null;
    private Step _step = null;

    /**
     * Utility method for optimisation. See isNodeValueTest()
     */
    public Expression getCompareValue() {
	if (_value != null) return _value;
	if (_exp == null) return null;

	if (_exp instanceof EqualityExpr) {
	    EqualityExpr exp = (EqualityExpr)_exp;
	    Expression left = exp.getLeft();
	    Expression right = exp.getRight();

	    Type tleft = left.getType();
	    Type tright = right.getType();

	    
	    if (left instanceof CastExpr) left = ((CastExpr)left).getExpr();
	    if (right instanceof CastExpr) right = ((CastExpr)right).getExpr();
	    
	    try {
		if ((tleft == Type.String) && (!(left instanceof Step)))
		    _value = exp.getLeft();
		if (left instanceof VariableRefBase) 
		    _value = new CastExpr(left, Type.String);
		if (_value != null) return _value;
	    }
	    catch (TypeCheckError e) { }

	    try {
		if ((tright == Type.String) && (!(right instanceof Step)))
		    _value = exp.getRight();
		if (right instanceof VariableRefBase)
		    _value = new CastExpr(right, Type.String);
		if (_value != null) return _value;
	    }
	    catch (TypeCheckError e) { }

	}
	return null;
    }

    /**
     * Utility method for optimisation. See isNodeValueTest()
     */
    public Step getStep() {
	if (_step != null) return _step;
	if (_exp == null) return null;

	if (_exp instanceof EqualityExpr) {
	    EqualityExpr exp = (EqualityExpr)_exp;
	    Expression left = exp.getLeft();
	    Expression right = exp.getRight();

	    if (left instanceof CastExpr) left = ((CastExpr)left).getExpr();
	    if (left instanceof Step) _step = (Step)left;
	    
	    if (right instanceof CastExpr) right = ((CastExpr)right).getExpr();
	    if (right instanceof Step) _step = (Step)right;
	}
	return _step;
    }

    /**
     * Translate a predicate expression. This translation pushes
     * two references on the stack: a reference to a newly created
     * filter object and a reference to the predicate's closure.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_nthPositionFilter || _nthDescendant) {
	    _exp.translate(classGen, methodGen);
	}
	else if (isNodeValueTest() && (getParent() instanceof Step)) {
	    _value.translate(classGen, methodGen);
	    il.append(new CHECKCAST(cpg.addClass(STRING_CLASS)));
	    il.append(new PUSH(cpg, ((EqualityExpr)_exp).getOp()));
	}
	else {
	    translateFilter(classGen, methodGen);
	}
    }

    /**
     * Translate a predicate expression. This translation pushes
     * two references on the stack: a reference to a newly created
     * filter object and a reference to the predicate's closure.
     */
    public void translateFilter(ClassGenerator classGen,
				MethodGenerator methodGen) 
    {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	compileFilter(classGen, methodGen);
	
	il.append(new NEW(cpg.addClass(_className)));
	il.append(DUP);
	il.append(new INVOKESPECIAL(cpg.addMethodref(_className,
						     "<init>", "()V")));

	final int length = (_closureVars == null) ? 0 : _closureVars.size();

	for (int i = 0; i < length; i++) {
	    VariableRefBase varRef = (VariableRefBase) _closureVars.get(i);
	    VariableBase var = varRef.getVariable();
	    Type varType = var.getType();

	    il.append(DUP);

	    Closure variableClosure = _parentClosure;
	    while (variableClosure != null) {
		if (variableClosure.inInnerClass()) break;
		variableClosure = variableClosure.getParentClosure();
	    }

	    if (variableClosure != null) {
		il.append(ALOAD_0);
		il.append(new GETFIELD(
		    cpg.addFieldref(variableClosure.getInnerClassName(), 
			var.getVariable(), varType.toSignature())));
	    }
	    else {
		il.append(var.loadInstruction());
	    }

	    il.append(new PUTFIELD(
		    cpg.addFieldref(_className, var.getVariable(), 
			varType.toSignature())));
	}
    }
}
