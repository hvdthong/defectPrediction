package org.apache.xalan.templates;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:call-template.
 * <pre>
 * &amp;!ELEMENT xsl:call-template (xsl:with-param)*>
 * &amp;!ATTLIST xsl:call-template
 *   name %qname; #REQUIRED
 * &amp;
 * </pre>
 */
public class ElemCallTemplate extends ElemForEach
{

  /**
   * An xsl:call-template element invokes a template by name;
   * it has a required name attribute that identifies the template to be invoked.
   * @serial
   */
  public QName m_templateName = null;

  /**
   * Set the "name" attribute.
   * An xsl:call-template element invokes a template by name;
   * it has a required name attribute that identifies the template to be invoked.
   *
   * @param name Name attribute to set
   */
  public void setName(QName name)
  {
    m_templateName = name;
  }

  /**
   * Get the "name" attribute.
   * An xsl:call-template element invokes a template by name;
   * it has a required name attribute that identifies the template to be invoked.
   *
   * @return Name attribute of this element
   */
  public QName getName()
  {
    return m_templateName;
  }

  /**
   * The template which is named by QName.
   * @serial
   */
  private ElemTemplate m_template = null;

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element 
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_CALLTEMPLATE;
  }

  /**
   * Return the node name.
   *
   * @return The name of this element
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_CALLTEMPLATE_STRING;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    super.compose(sroot);
    
    int length = getParamElemCount();
    for (int i = 0; i < length; i++) 
    {
      ElemWithParam ewp = getParamElem(i);
      ewp.compose(sroot);
    }
    
    if ((null != m_templateName) && (null == m_template))
    {
      m_template =
        this.getStylesheetRoot().getTemplateComposed(m_templateName);
        
      if(null == m_template)
    
      length = getParamElemCount();
      for (int i = 0; i < length; i++) 
      {
        ElemWithParam ewp = getParamElem(i);
        ewp.m_index = -1;
        int etePos = 0;
        for (ElemTemplateElement ete = m_template.getFirstChildElem(); 
             null != ete; ete = ete.getNextSiblingElem()) 
        {
          if(ete.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE)
          {
            ElemParam ep = (ElemParam)ete;
            if(ep.getName().equals(ewp.getName()))
            {
              ewp.m_index = etePos;
            }
          }
          else
            break;
          etePos++;
        }
        
      }
    }
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
    int length = getParamElemCount();
    for (int i = 0; i < length; i++) 
    {
      ElemWithParam ewp = getParamElem(i);
      ewp.endCompose(sroot);
    }    
    
    super.endCompose(sroot);
  }

  /**
   * Invoke a named template.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer)
            throws TransformerException
  {

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);

    if (null != m_template)
    {
      XPathContext xctxt = transformer.getXPathContext();
      VariableStack vars = xctxt.getVarStack();

      int thisframe = vars.getStackFrame();
      int nextFrame = vars.link(m_template.m_frameSize);
      
      if(m_template.m_inArgsSize > 0)
      {
        vars.clearLocalSlots(0, m_template.m_inArgsSize);
      
        if(null != m_paramElems)
        {
          int currentNode = xctxt.getCurrentNode();
          vars.setStackFrame(thisframe);
          int size = m_paramElems.length;
          
          for (int i = 0; i < size; i++) 
          {
            ElemWithParam ewp = m_paramElems[i];
            if(ewp.m_index >= 0)
            {
              XObject obj = ewp.getValue(transformer, currentNode);
              
              vars.setLocalVariable(ewp.m_index, obj, nextFrame);
            }
          }
          vars.setStackFrame(nextFrame);
        }
      }
      
      SourceLocator savedLocator = xctxt.getSAXLocator();

      try
      {
        xctxt.setSAXLocator(m_template);

        transformer.pushElemTemplateElement(m_template);
        m_template.execute(transformer);
      }
      finally
      {
        transformer.popElemTemplateElement();
        xctxt.setSAXLocator(savedLocator);
        vars.unlink(thisframe);
      }
    }
    else
    {
      transformer.getMsgMgr().error(this, XSLTErrorResources.ER_TEMPLATE_NOT_FOUND,
    }
    
    if (TransformerImpl.S_DEBUG)
	  transformer.getTraceManager().fireTraceEndEvent(this); 

  }
  
  /** Vector of xsl:param elements associated with this element. 
   *  @serial */
  protected ElemWithParam[] m_paramElems = null;

  /**
   * Get the count xsl:param elements associated with this element.
   * @return The number of xsl:param elements.
   */
  public int getParamElemCount()
  {
    return (m_paramElems == null) ? 0 : m_paramElems.length;
  }

  /**
   * Get a xsl:param element associated with this element.
   *
   * @param i Index of element to find
   *
   * @return xsl:param element at given index
   */
  public ElemWithParam getParamElem(int i)
  {
    return m_paramElems[i];
  }

  /**
   * Set a xsl:param element associated with this element.
   *
   * @param ParamElem xsl:param element to set. 
   */
  public void setParamElem(ElemWithParam ParamElem)
  {
    if (null == m_paramElems)
    {
      m_paramElems = new ElemWithParam[1];
      m_paramElems[0] = ParamElem;
    }
    else
    {
      int length = m_paramElems.length;
      ElemWithParam[] ewp = new ElemWithParam[length + 1];
      System.arraycopy(m_paramElems, 0, ewp, 0, length);
      m_paramElems = ewp;
      ewp[length] = ParamElem;
    }
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
   * <!ATTLIST xsl:apply-templates
   *   select %expr; "node()"
   *   mode %qname; #IMPLIED
   * >
   *
   * @param newChild Child to add to this node's children list
   *
   * @return The child that was just added the children list 
   *
   * @throws DOMException
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    int type = ((ElemTemplateElement) newChild).getXSLToken();

    if (Constants.ELEMNAME_WITHPARAM == type)
    {
      setParamElem((ElemWithParam) newChild);
    }

    return super.appendChild(newChild);
  }
  
    /**
     * Call the children visitors.
     * @param visitor The visitor whose appropriate method will be called.
     */
    public void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
    {

      super.callChildVisitors(visitor, callAttrs);
    }
}
