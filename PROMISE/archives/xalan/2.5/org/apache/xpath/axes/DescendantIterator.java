package org.apache.xpath.axes;

import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.patterns.NodeTest;

/**
 * <meta name="usage" content="advanced"/>
 * This class implements an optimized iterator for
 * @see org.apache.xpath.axes.WalkerFactory#newLocPathIterator
 */
public class DescendantIterator extends LocPathIterator
{
  /**
   * Create a DescendantIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  DescendantIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, false);

    int firstStepPos = compiler.getFirstChildPos(opPos);
    int stepType = compiler.getOp(firstStepPos);

    boolean orSelf = (OpCodes.FROM_DESCENDANTS_OR_SELF == stepType);
    boolean fromRoot = false;
    if (OpCodes.FROM_SELF == stepType)
    {
      orSelf = true;
    }
    else if(OpCodes.FROM_ROOT == stepType)
    {
      fromRoot = true;
      int nextStepPos = compiler.getNextStepPos(firstStepPos);
      if(compiler.getOp(nextStepPos) == OpCodes.FROM_DESCENDANTS_OR_SELF)
        orSelf = true;
    }
    
    int nextStepPos = firstStepPos;
    while(true)
    {
      nextStepPos = compiler.getNextStepPos(nextStepPos);
      if(nextStepPos > 0)
      {
        int stepOp = compiler.getOp(nextStepPos);
        if(OpCodes.ENDOP != stepOp)
          firstStepPos = nextStepPos;
        else
          break;
      }
      else
        break;
      
    }
    
    if((analysis & WalkerFactory.BIT_CHILD) != 0)
      orSelf = false;
      
    if(fromRoot)
    {
      if(orSelf)
        m_axis = Axis.DESCENDANTSORSELFFROMROOT;
      else
        m_axis = Axis.DESCENDANTSFROMROOT;
    }
    else if(orSelf)
      m_axis = Axis.DESCENDANTORSELF;
    else
      m_axis = Axis.DESCENDANT;

    int whatToShow = compiler.getWhatToShow(firstStepPos);

    if ((0 == (whatToShow
               & (DTMFilter.SHOW_ATTRIBUTE | DTMFilter.SHOW_ELEMENT
                  | DTMFilter.SHOW_PROCESSING_INSTRUCTION))) || 
                   (whatToShow == DTMFilter.SHOW_ALL))
      initNodeTest(whatToShow);
    else
    {
      initNodeTest(whatToShow, compiler.getStepNS(firstStepPos),
                              compiler.getStepLocalName(firstStepPos));
    }
    initPredicateInfo(compiler, firstStepPos);
  }
  
  /**
   * Create a DescendantIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DescendantIterator()
  {
    super(null);
    m_axis = Axis.DESCENDANTSORSELFFROMROOT;
    int whatToShow = DTMFilter.SHOW_ALL;
    initNodeTest(whatToShow);
  }

  
  /**
   *  Get a cloned Iterator that is reset to the beginning
   *  of the query.
   * 
   *  @return A cloned NodeIterator set of the start of the query.
   * 
   *  @throws CloneNotSupportedException
   */
  public DTMIterator cloneWithReset() throws CloneNotSupportedException
  {

    DescendantIterator clone = (DescendantIterator) super.cloneWithReset();
    clone.m_traverser = m_traverser;

    clone.resetProximityPositions();

    return clone;
  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   *
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   *
   * @throws DOMException
   *    INVALID_STATE_ERR: Raised if this method is called after the
   *   <code>detach</code> method was invoked.
   */
  public int nextNode()
  {
   	if(m_foundLast)
  		return DTM.NULL;

    if(DTM.NULL == m_lastFetched)
    {
      resetProximityPositions();
    }

    int next;
    
    org.apache.xpath.VariableStack vars;
    int savedStart;
    if (-1 != m_stackFrame)
    {
      vars = m_execContext.getVarStack();

      savedStart = vars.getStackFrame();

      vars.setStackFrame(m_stackFrame);
    }
    else
    {
      vars = null;
      savedStart = 0;
    }
    
    try
    {
      do
      {
        if(0 == m_extendedTypeID)
        {
          next = m_lastFetched = (DTM.NULL == m_lastFetched)
                       ? m_traverser.first(m_context)
                       : m_traverser.next(m_context, m_lastFetched);
        }
        else
        {
          next = m_lastFetched = (DTM.NULL == m_lastFetched)
                       ? m_traverser.first(m_context, m_extendedTypeID)
                       : m_traverser.next(m_context, m_lastFetched, 
                                          m_extendedTypeID);
        }
  
        if (DTM.NULL != next)
        {
          if(DTMIterator.FILTER_ACCEPT == acceptNode(next))
            break;
          else
            continue;
        }
        else
          break;
      }
      while (next != DTM.NULL);
  
      if (DTM.NULL != next)
      {
      	m_pos++;
        return next;
      }
      else
      {
        m_foundLast = true;
  
        return DTM.NULL;
      }
    }
    finally
    {
      if (-1 != m_stackFrame)
      {
        vars.setStackFrame(savedStart);
      }
    }
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
    m_traverser = m_cdtm.getAxisTraverser(m_axis);
    
    String localName = getLocalName();
    String namespace = getNamespace();
    int what = m_whatToShow;
    if(DTMFilter.SHOW_ALL == what
       || localName == NodeTest.WILD
       || namespace == NodeTest.WILD)
    {
      m_extendedTypeID = 0;
    }
    else
    {
      int type = getNodeTypeTest(what);
      m_extendedTypeID = m_cdtm.getExpandedTypeID(namespace, localName, type);
    }
    
  }
  
  /**
   * Return the first node out of the nodeset, if this expression is 
   * a nodeset expression.  This is the default implementation for 
   * nodesets.
   * <p>WARNING: Do not mutate this class from this function!</p>
   * @param xctxt The XPath runtime context.
   * @return the first node out of the nodeset, or DTM.NULL.
   */
  public int asNode(XPathContext xctxt)
    throws javax.xml.transform.TransformerException
  {
    if(getPredicateCount() > 0)
      return super.asNode(xctxt);

    int current = xctxt.getCurrentNode();
    
    DTM dtm = xctxt.getDTM(current);
    DTMAxisTraverser traverser = dtm.getAxisTraverser(m_axis);
    
    String localName = getLocalName();
    String namespace = getNamespace();
    int what = m_whatToShow;
    
    
    if(DTMFilter.SHOW_ALL == what
       || localName == NodeTest.WILD
       || namespace == NodeTest.WILD)
    {
      return traverser.first(current);
    }
    else
    {
      int type = getNodeTypeTest(what);
      int extendedType = dtm.getExpandedTypeID(namespace, localName, type);
      return traverser.first(current, extendedType);
    }
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
    m_traverser = null;    
    m_extendedTypeID = 0;
    
    super.detach();
  }
  
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    return m_axis;
  }
  
  
  /** The traverser to use to navigate over the descendants. */
  transient protected DTMAxisTraverser m_traverser;
  
  /** The axis that we are traversing. */
  protected int m_axis;
  
  /** The extended type ID, not set until setRoot. */
  protected int m_extendedTypeID;
  
  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!super.deepEquals(expr))
  		return false;
  		
  	if(m_axis != ((DescendantIterator)expr).m_axis)
  		return false;
  		
  	return true;
  }

  
}
