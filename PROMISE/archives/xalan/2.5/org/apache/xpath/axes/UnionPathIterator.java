package org.apache.xpath.axes;

import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpCodes;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSetDTM, which implements DTMIterator,
 * and fetches nodes one at a time in document order based on a XPath
 * As each node is iterated via nextNode(), the node is also stored
 * in the NodeVector, so that previousNode() can easily be done.
 */
public class UnionPathIterator extends LocPathIterator
        implements Cloneable, DTMIterator, java.io.Serializable, PathComponent
{

  /**
   * Constructor to create an instance which you can add location paths to.
   */
  public UnionPathIterator()
  {

    super();

    m_iterators = null;
    m_exprs = null;
  }

  /**
   * Initialize the context values for this expression 
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this 
   * transformation.
   */
  public void setRoot(int context, Object environment)
  {
    super.setRoot(context, environment);

    try
    {
      if (null != m_exprs)
      {
        int n = m_exprs.length;
        DTMIterator newIters[] = new DTMIterator[n];
  
        for (int i = 0; i < n; i++)
        {
          DTMIterator iter = m_exprs[i].asIterator(m_execContext, context);
          newIters[i] = iter;
          iter.nextNode();
        }
        m_iterators = newIters;
      }
    }
    catch(Exception e)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(e);
    }
  }
  
  /**
   * Add an iterator to the union list.
   *
   * @param iter non-null reference to a location path iterator.
   */
  public void addIterator(DTMIterator expr)
  {

    if (null == m_iterators)
    {
      m_iterators = new DTMIterator[1];
      m_iterators[0] = expr;
    }
    else
    {
      DTMIterator[] exprs = m_iterators;
      int len = m_iterators.length;

      m_iterators = new DTMIterator[len + 1];

      System.arraycopy(exprs, 0, m_iterators, 0, len);

      m_iterators[len] = expr;
    }
    expr.nextNode();
    if(expr instanceof Expression)
    	((Expression)expr).exprSetParent(this);
  }
  
  /**
   *  Detaches the iterator from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state. After<code>detach</code> has been invoked, calls to
   * <code>nextNode</code> or<code>previousNode</code> will raise the
   * exception INVALID_STATE_ERR.
   */
  public void detach()
  {    
    if(null != m_iterators)
    {
    	int n = m_iterators.length;
    	for(int i = 0; i < n; i++)
    	{
    		m_iterators[i].detach();
    	}
    	m_iterators = null;
    }
  }


  /**
   * Create a UnionPathIterator object, including creation 
   * of location path iterators from the opcode list, and call back 
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   * opcode list from the compiler.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public UnionPathIterator(Compiler compiler, int opPos)
          throws javax.xml.transform.TransformerException
  {

    super();

    opPos = compiler.getFirstChildPos(opPos);

    loadLocationPaths(compiler, opPos, 0);
  }
  
  /**
   * This will return an iterator capable of handling the union of paths given.
   * 
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   * opcode list from the compiler.
   * 
   * @return Object that is derived from LocPathIterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public static LocPathIterator createUnionIterator(Compiler compiler, int opPos)
          throws javax.xml.transform.TransformerException
  {
  	UnionPathIterator upi = new UnionPathIterator(compiler, opPos);
  	int nPaths = upi.m_exprs.length;
  	boolean isAllChildIterators = true;
  	for(int i = 0; i < nPaths; i++)
  	{
  		LocPathIterator lpi = upi.m_exprs[i];
  		
  		if(lpi.getAxis() != Axis.CHILD)
  		{
  			isAllChildIterators = false;
  			break;
  		}
  		else
  		{
  			if(HasPositionalPredChecker.check(lpi))
  			{
  				isAllChildIterators = false;
  				break;
  			}
  		}
  	}
  	if(isAllChildIterators)
  	{
  		UnionChildIterator uci = new UnionChildIterator();
  		
	  	for(int i = 0; i < nPaths; i++)
	  	{
	  		PredicatedNodeTest lpi = upi.m_exprs[i];
	  		uci.addNodeTest(lpi);
	  	}
	  	return uci;
  		
  	}
  	else
  		return upi;
  }
  
  /** 
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  public int getAnalysisBits()
  {
    int bits = 0;
    
    if (m_exprs != null)
    {
      int n = m_exprs.length;

      for (int i = 0; i < n; i++)
      {
      	int bit = m_exprs[i].getAnalysisBits();
        bits |= bit;
      }
    }

    return bits;
  }
  
  /**
   * Read the object from a serialization stream.
   *
   * @param stream Input stream to read from
   *
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   */
  private void readObject(java.io.ObjectInputStream stream)
          throws java.io.IOException, javax.xml.transform.TransformerException
  {
    try
    {
      stream.defaultReadObject();
      m_clones =  new IteratorPool(this);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new javax.xml.transform.TransformerException(cnfe);
    }
  }

  /**
   * Get a cloned LocPathIterator that holds the same 
   * position as this iterator.
   *
   * @return A clone of this iterator that holds the same node position.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    UnionPathIterator clone = (UnionPathIterator) super.clone();

    return clone;
  }
  
  
  /**
   * Create a new location path iterator.
   *
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   *
   * @return New location path iterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected LocPathIterator createDTMIterator(
          Compiler compiler, int opPos) throws javax.xml.transform.TransformerException
  {
    LocPathIterator lpi = (LocPathIterator)WalkerFactory.newDTMIterator(compiler, opPos, 
                                      (compiler.getLocationPathDepth() <= 0));
    return lpi;
  }

  /**
   * Initialize the location path iterators.  Recursive.
   *
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   * opcode list from the compiler.
   * @param count The insert position of the iterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void loadLocationPaths(Compiler compiler, int opPos, int count)
          throws javax.xml.transform.TransformerException
  {

    int steptype = compiler.getOp(opPos);

    if (steptype == OpCodes.OP_LOCATIONPATH)
    {
      loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

      m_exprs[count] = createDTMIterator(compiler, opPos);
      m_exprs[count].exprSetParent(this);
    }
    else
    {

      switch (steptype)
      {
      case OpCodes.OP_VARIABLE :
      case OpCodes.OP_EXTFUNCTION :
      case OpCodes.OP_FUNCTION :
      case OpCodes.OP_GROUP :
        loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

        WalkingIterator iter =
          new WalkingIterator(compiler.getNamespaceContext());
        iter.exprSetParent(this);
          
        if(compiler.getLocationPathDepth() <= 0)
          iter.setIsTopLevel(true);

        iter.m_firstWalker = new org.apache.xpath.axes.FilterExprWalker(iter);

        iter.m_firstWalker.init(compiler, opPos, steptype);

        m_exprs[count] = iter;
        break;
      default :
        m_exprs = new LocPathIterator[count];
      }
    }
  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a DTMIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {
  	if(m_foundLast)
  		return DTM.NULL;

    int earliestNode = DTM.NULL;

    if (null != m_iterators)
    {
      int n = m_iterators.length;
      int iteratorUsed = -1;

      for (int i = 0; i < n; i++)
      {
        int node = m_iterators[i].getCurrentNode();

        if (DTM.NULL == node)
          continue;
        else if (DTM.NULL == earliestNode)
        {
          iteratorUsed = i;
          earliestNode = node;
        }
        else
        {
          if (node == earliestNode)
          {

            m_iterators[i].nextNode();
          }
          else
          {
            DTM dtm = getDTM(node);

            if (dtm.isNodeAfter(node, earliestNode))
            {
              iteratorUsed = i;
              earliestNode = node;
            }
          }
        }
      }

      if (DTM.NULL != earliestNode)
      {
        m_iterators[iteratorUsed].nextNode();

        incrementCurrentPos();
      }
      else
        m_foundLast = true;
    }

    m_lastFetched = earliestNode;

    return earliestNode;
  }
            
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    for (int i = 0; i < m_exprs.length; i++) 
    {
      m_exprs[i].fixupVariables(vars, globalsSize);
    }
    
  }
  
  /**
   * The location path iterators, one for each
   * path</a> contained in the union expression.
   * @serial
   */
  protected LocPathIterator[] m_exprs;

    
  /**
   * The location path iterators, one for each
   * path</a> contained in the union expression.
   * @serial
   */
  protected DTMIterator[] m_iterators;
      
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    return -1;
  }
  
  class iterOwner implements ExpressionOwner
  {
  	int m_index;
  	
  	iterOwner(int index)
  	{
  		m_index = index;
  	}
  	
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_exprs[m_index];
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	
    	if(!(exp instanceof LocPathIterator))
    	{
    		WalkingIterator wi = new WalkingIterator(getPrefixResolver());
    		FilterExprWalker few = new FilterExprWalker(wi);
    		wi.setFirstWalker(few);
    		few.setInnerExpression(exp);
    		wi.exprSetParent(UnionPathIterator.this);
    		few.exprSetParent(wi);
    		exp.exprSetParent(few);
    		exp = wi;
    	}
    	else
    		exp.exprSetParent(UnionPathIterator.this);
    	m_exprs[m_index] = (LocPathIterator)exp;
    }

  }

  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	 	if(visitor.visitUnionPath(owner, this))
  	 	{
  	 		if(null != m_exprs)
  	 		{
  	 			int n = m_exprs.length;
  	 			for(int i = 0; i < n; i++)
  	 			{
  	 				m_exprs[i].callVisitors(new iterOwner(i), visitor);
  	 			}
  	 		}
  	 	}
  }
  
    /**
     * @see Expression#deepEquals(Expression)
     */
    public boolean deepEquals(Expression expr)
    {
      if (!super.deepEquals(expr))
            return false;

      UnionPathIterator upi = (UnionPathIterator) expr;

      if (null != m_exprs)
      {
        int n = m_exprs.length;
        
        if((null == upi.m_exprs) || (upi.m_exprs.length != n))
        	return false;
        
        for (int i = 0; i < n; i++)
        {
          if(!m_exprs[i].deepEquals(upi.m_exprs[i]))
          	return false;
        }
      }
      else if (null != upi.m_exprs)
      {
          return false;
      }

      return true;
    }


}
