package org.apache.xpath.axes;

import java.util.Stack;

import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.PsuedoNames;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.VariableStack;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTMAxisTraverser;

/**
 * This class treats a 
 * filtered iteration over the tree, evaluating each node in a super axis 
 * traversal against the LocationPath interpreted as a match pattern.  This 
 * class is useful to find nodes in document order that are complex paths 
 * whose steps probably criss-cross each other.
 */
public class MatchPatternIterator extends LocPathIterator
{

  /** This is the select pattern, translated into a match pattern. */
  protected StepPattern m_pattern;

  /** The traversal axis from where the nodes will be filtered. */
  protected int m_superAxis = -1;

  /** The DTM inner traversal class, that corresponds to the super axis. */
  protected DTMAxisTraverser m_traverser;
  
  /** DEBUG flag for diagnostic dumps. */
  private static final boolean DEBUG = false;
  

  /**
   * Create a LocPathIterator object, including creation
   * of step walkers from the opcode list, and call back
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   * @param analysis Analysis bits that give general information about the 
   * LocationPath.
   *
   * @throws javax.xml.transform.TransformerException
   */
  MatchPatternIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, false);

    int firstStepPos = compiler.getFirstChildPos(opPos);

    m_pattern = WalkerFactory.loadSteps(this, compiler, firstStepPos, 0); 

    boolean fromRoot = false;
    boolean walkBack = false;
    boolean walkDescendants = false;
    boolean walkAttributes = false;

    if (0 != (analysis & (WalkerFactory.BIT_ROOT | 
                          WalkerFactory.BIT_ANY_DESCENDANT_FROM_ROOT)))
      fromRoot = true;
      
    if (0 != (analysis
              & (WalkerFactory.BIT_ANCESTOR
                 | WalkerFactory.BIT_ANCESTOR_OR_SELF
                 | WalkerFactory.BIT_PRECEDING
                 | WalkerFactory.BIT_PRECEDING_SIBLING 
                 | WalkerFactory.BIT_FOLLOWING
                 | WalkerFactory.BIT_FOLLOWING_SIBLING
                 | WalkerFactory.BIT_PARENT | WalkerFactory.BIT_FILTER)))
      walkBack = true;

    if (0 != (analysis
              & (WalkerFactory.BIT_DESCENDANT_OR_SELF
                 | WalkerFactory.BIT_DESCENDANT
                 | WalkerFactory.BIT_CHILD)))
      walkDescendants = true;

    if (0 != (analysis
              & (WalkerFactory.BIT_ATTRIBUTE | WalkerFactory.BIT_NAMESPACE)))
      walkAttributes = true;
      
    if(false || DEBUG)
    {
      System.out.print("analysis: "+Integer.toBinaryString(analysis));
      System.out.println(", "+WalkerFactory.getAnalysisString(analysis));
    }
      
    if(fromRoot || walkBack)
    {
      if(walkAttributes)
      {
        m_superAxis = Axis.ALL;
      }
      else
      {
        m_superAxis = Axis.DESCENDANTSFROMROOT;
      }
    }
    else if(walkDescendants)
    {
      if(walkAttributes)
      {
        m_superAxis = Axis.ALLFROMNODE;
      }
      else
      {
        m_superAxis = Axis.DESCENDANTORSELF;
      }
    }
    else
    {
      m_superAxis = Axis.ALL;
    }
    if(false || DEBUG)
    {
      System.out.println("axis: "+Axis.names[m_superAxis]);
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
    m_traverser = m_cdtm.getAxisTraverser(m_superAxis);
  }
  
  /**
   * Get the next node via getNextXXX.  Bottlenecked for derived class override.
   * @return The next node on the axis, or DTM.NULL.
   */
  protected int getNextNode()
  {
    m_lastFetched = (DTM.NULL == m_lastFetched)
                     ? m_traverser.first(m_context)
                     : m_traverser.next(m_context, m_lastFetched);
    return m_lastFetched;
  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {      
  	if(m_foundLast)
  		return DTM.NULL;

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
      if(DEBUG)
        System.out.println("m_pattern"+m_pattern.toString());

      do
      {
        next = getNextNode();
  
        if (DTM.NULL != next)
        {
          if(DTMIterator.FILTER_ACCEPT == acceptNode(next, m_execContext))
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
        if(DEBUG)
        {
          System.out.println("next: "+next);
          System.out.println("name: "+m_cdtm.getNodeName(next));
        }
        incrementCurrentPos();
  
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
   *  Test whether a specified node is visible in the logical view of a
   * TreeWalker or NodeIterator. This function will be called by the
   * implementation of TreeWalker and NodeIterator; it is not intended to
   * be called directly from user code.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(int n, XPathContext xctxt)
  {

    try
    {
      xctxt.pushCurrentNode(n);
      xctxt.pushIteratorRoot(m_context);
      if(DEBUG)
      {
        System.out.println("traverser: "+m_traverser);
        System.out.print("node: "+n);
        System.out.println(", "+m_cdtm.getNodeName(n));
        System.out.println("pattern: "+m_pattern.toString());
        m_pattern.debugWhatToShow(m_pattern.getWhatToShow());
      }
      
      XObject score = m_pattern.execute(xctxt);
      
      if(DEBUG)
      {
        System.out.println("score: "+score);
        System.out.println("skip: "+(score == NodeTest.SCORE_NONE));
      }

      return (score == NodeTest.SCORE_NONE) ? DTMIterator.FILTER_SKIP 
                    : DTMIterator.FILTER_ACCEPT;
    }
    catch (javax.xml.transform.TransformerException se)
    {

      throw new RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.popIteratorRoot();
    }

  }

}
