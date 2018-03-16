package org.apache.xpath.patterns;

import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.objects.XObject;
/**
 * Special context node pattern matcher.
 */
public class ContextMatchStepPattern extends StepPattern
{

  /**
   * Construct a ContextMatchStepPattern.
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public ContextMatchStepPattern(int axis, int paxis)
  {
    super(DTMFilter.SHOW_ALL, axis, paxis);
  }

  /**
   * Execute this pattern step, including predicates.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    if (xctxt.getIteratorRoot() == xctxt.getCurrentNode())
      return getStaticScore();
    else
      return this.SCORE_NONE;
  }
  
  /**
   * Execute the match pattern step relative to another step.
   *
   *
   * @param xctxt The XPath runtime context.
   * NEEDSDOC @param prevStep
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject executeRelativePathPattern(
          XPathContext xctxt, StepPattern prevStep)
            throws javax.xml.transform.TransformerException
  {

    XObject score = NodeTest.SCORE_NONE;
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);

    if (null != dtm)
    {
      int predContext = xctxt.getCurrentNode();
      DTMAxisTraverser traverser;
      
      int axis = m_axis;
      
      boolean needToTraverseAttrs = WalkerFactory.isDownwardAxisOfMany(axis);
      boolean iterRootIsAttr = (dtm.getNodeType(xctxt.getIteratorRoot()) 
                                 == DTM.ATTRIBUTE_NODE);

      if((Axis.PRECEDING == axis) && iterRootIsAttr)
      {
        axis = Axis.PRECEDINGANDANCESTOR;
      }
      
      traverser = dtm.getAxisTraverser(axis);

      for (int relative = traverser.first(context); DTM.NULL != relative;
              relative = traverser.next(context, relative))
      {
        try
        {
          xctxt.pushCurrentNode(relative);

          score = execute(xctxt);

          if (score != NodeTest.SCORE_NONE)
          {
	      if (executePredicates(xctxt, dtm, context))
		  return score;
	      
	      score = NodeTest.SCORE_NONE;
          }
          
          if(needToTraverseAttrs && iterRootIsAttr
             && (DTM.ELEMENT_NODE == dtm.getNodeType(relative)))
          {
            int xaxis = Axis.ATTRIBUTE;
            for (int i = 0; i < 2; i++) 
            {            
              DTMAxisTraverser atraverser = dtm.getAxisTraverser(xaxis);
        
              for (int arelative = atraverser.first(relative); 
                      DTM.NULL != arelative;
                      arelative = atraverser.next(relative, arelative))
              {
                try
                {
                  xctxt.pushCurrentNode(arelative);
        
                  score = execute(xctxt);
        
                  if (score != NodeTest.SCORE_NONE)
                  {
        
                    if (score != NodeTest.SCORE_NONE)
                      return score;
                  }
                }
                finally
                {
                  xctxt.popCurrentNode();
                }
              }
              xaxis = Axis.NAMESPACE;
            }
          }

        }
        finally
        {
          xctxt.popCurrentNode();
        }
      }

    }

    return score;
  }

}
