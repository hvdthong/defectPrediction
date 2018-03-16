package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.ExtensionNamespaceSupport;
import org.apache.xalan.extensions.ExtensionNamespacesManager;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.StringVector;

/**
 * Implement the declaration of an extension element 
 * @xsl.usage internal
 */
public class ElemExtensionDecl extends ElemTemplateElement
{
    static final long serialVersionUID = -4692738885172766789L;

  /**
   * Constructor ElemExtensionDecl
   *
   */
  public ElemExtensionDecl()
  {

  }

  /** Prefix string for this extension element.
   *  @serial         */
  private String m_prefix = null;

  /**
   * Set the prefix for this extension element  
   *
   *
   * @param v Prefix to set for this extension element
   */
  public void setPrefix(String v)
  {
    m_prefix = v;
  }

  /**
   * Get the prefix for this extension element
   *
   *
   * @return Prefix for this extension element
   */
  public String getPrefix()
  {
    return m_prefix;
  }

  /** StringVector holding the names of functions defined in this extension.
   *  @serial     */
  private StringVector m_functions = new StringVector();

  /**
   * Set the names of functions defined in this extension  
   *
   *
   * @param v StringVector holding the names of functions defined in this extension
   */
  public void setFunctions(StringVector v)
  {
    m_functions = v;
  }

  /**
   * Get the names of functions defined in this extension
   *
   *
   * @return StringVector holding the names of functions defined in this extension
   */
  public StringVector getFunctions()
  {
    return m_functions;
  }

  /**
   * Get a function at a given index in this extension element 
   *
   *
   * @param i Index of function to get
   *
   * @return Name of Function at given index
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public String getFunction(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_functions)
      throw new ArrayIndexOutOfBoundsException();

    return (String) m_functions.elementAt(i);
  }

  /**
   * Get count of functions defined in this extension element
   *
   *
   * @return count of functions defined in this extension element
   */
  public int getFunctionCount()
  {
    return (null != m_functions) ? m_functions.size() : 0;
  }

  /** StringVector of elements defined in this extension.
   *  @serial         */
  private StringVector m_elements = null;

  /**
   * Set StringVector of elements for this extension
   *
   *
   * @param v StringVector of elements to set
   */
  public void setElements(StringVector v)
  {
    m_elements = v;
  }

  /**
   * Get StringVector of elements defined for this extension  
   *
   *
   * @return StringVector of elements defined for this extension
   */
  public StringVector getElements()
  {
    return m_elements;
  }

  /**
   * Get the element at the given index
   *
   *
   * @param i Index of element to get
   *
   * @return The element at the given index
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public String getElement(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_elements)
      throw new ArrayIndexOutOfBoundsException();

    return (String) m_elements.elementAt(i);
  }

  /**
   * Return the count of elements defined for this extension element 
   *
   *
   * @return the count of elements defined for this extension element
   */
  public int getElementCount()
  {
    return (null != m_elements) ? m_elements.size() : 0;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_EXTENSIONDECL;
  }
  
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    super.compose(sroot);
    String prefix = getPrefix();
    String declNamespace = getNamespaceForPrefix(prefix);
    String lang = null;
    String srcURL = null;
    String scriptSrc = null;
    if (null == declNamespace)
      throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_NAMESPACE_DECL, new Object[]{prefix})); 
    for (ElemTemplateElement child = getFirstChildElem(); child != null;
          child = child.getNextSiblingElem())
    {
      if (Constants.ELEMNAME_EXTENSIONSCRIPT == child.getXSLToken())
      {
        ElemExtensionScript sdecl = (ElemExtensionScript) child;
        lang = sdecl.getLang();
        srcURL = sdecl.getSrc();
        ElemTemplateElement childOfSDecl = sdecl.getFirstChildElem();
        if (null != childOfSDecl)
        {
          if (Constants.ELEMNAME_TEXTLITERALRESULT
                  == childOfSDecl.getXSLToken())
          {
            ElemTextLiteral tl = (ElemTextLiteral) childOfSDecl;
            char[] chars = tl.getChars();
            scriptSrc = new String(chars);
            if (scriptSrc.trim().length() == 0)
              scriptSrc = null;
          }
        }
      }
    }
    if (null == lang)
      lang = "javaclass";
    if (lang.equals("javaclass") && (scriptSrc != null))
        throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_ELEM_CONTENT_NOT_ALLOWED, new Object[]{scriptSrc})); 

    ExtensionNamespaceSupport extNsSpt = null;
    ExtensionNamespacesManager extNsMgr = sroot.getExtensionNamespacesManager();
    if (extNsMgr.namespaceIndex(declNamespace,
                                extNsMgr.getExtensions()) == -1)
    {
      if (lang.equals("javaclass"))
      {
        if (null == srcURL)
        {
           extNsSpt = extNsMgr.defineJavaNamespace(declNamespace);
        }
        else if (extNsMgr.namespaceIndex(srcURL,
                                         extNsMgr.getExtensions()) == -1)
        {
          extNsSpt = extNsMgr.defineJavaNamespace(declNamespace, srcURL);
        }
      }
      {
        String handler = "org.apache.xalan.extensions.ExtensionHandlerGeneral";
        Object [] args = {declNamespace, this.m_elements, this.m_functions,
                          lang, srcURL, scriptSrc, getSystemId()};
        extNsSpt = new ExtensionNamespaceSupport(declNamespace, handler, args);
      }
    }
    if (extNsSpt != null)
      extNsMgr.registerExtension(extNsSpt);
  }

  
  /**
   * This function will be called on top-level elements
   * only, just before the transform begins.
   *
   * @param transformer The XSLT TransformerFactory.
   *
   * @throws TransformerException
   */  
  public void runtimeInit(TransformerImpl transformer) throws TransformerException
  {
    String lang = null;
    String srcURL = null;
    String scriptSrc = null;
    String prefix = getPrefix();
    String declNamespace = getNamespaceForPrefix(prefix);

    if (null == declNamespace)
      throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_NAMESPACE_DECL, new Object[]{prefix})); 

    for (ElemTemplateElement child = getFirstChildElem(); child != null;
            child = child.getNextSiblingElem())
    {
      if (Constants.ELEMNAME_EXTENSIONSCRIPT == child.getXSLToken())
      {
        ElemExtensionScript sdecl = (ElemExtensionScript) child;

        lang = sdecl.getLang();
        srcURL = sdecl.getSrc();

        ElemTemplateElement childOfSDecl = sdecl.getFirstChildElem();

        if (null != childOfSDecl)
        {
          if (Constants.ELEMNAME_TEXTLITERALRESULT
                  == childOfSDecl.getXSLToken())
          {
            ElemTextLiteral tl = (ElemTextLiteral) childOfSDecl;
            char[] chars = tl.getChars();

            scriptSrc = new String(chars);

            if (scriptSrc.trim().length() == 0)
              scriptSrc = null;
          }
        }
      }
    }

    if (null == lang)
      lang = "javaclass";

    if (lang.equals("javaclass") && (scriptSrc != null))
      throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_ELEM_CONTENT_NOT_ALLOWED, new Object[]{scriptSrc})); 
    
    ExtensionsTable etable = transformer.getExtensionsTable();    
    ExtensionHandler nsh = etable.get(declNamespace);

    if (null == nsh)
    {
      if (lang.equals("javaclass"))
      {
        if (null == srcURL)
        {
          nsh = etable.makeJavaNamespace(declNamespace);
        }
        else
        {
          nsh = etable.get(srcURL);

          if (null == nsh)
          {
            nsh = etable.makeJavaNamespace(srcURL);
          }
        }
      }
      {
        nsh = new ExtensionHandlerGeneral(declNamespace, this.m_elements,
                                          this.m_functions, lang, srcURL,
                                          scriptSrc, getSystemId());

      }

      etable.addExtensionNamespace(declNamespace, nsh);
    }*/
  }
}
