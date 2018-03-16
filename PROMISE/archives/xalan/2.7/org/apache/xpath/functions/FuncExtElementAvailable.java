package org.apache.xpath.functions;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.ExtensionsProvider;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * Execute the ExtElementAvailable() function.
 * @xsl.usage advanced
 */
public class FuncExtElementAvailable extends FunctionOneArg
{
    static final long serialVersionUID = -472533699257968546L;

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    String prefix;
    String namespace;
    String methName;

    String fullName = m_arg0.execute(xctxt).str();
    int indexOfNSSep = fullName.indexOf(':');

    if (indexOfNSSep < 0)
    {
      prefix = "";
      namespace = Constants.S_XSLNAMESPACEURL;
      methName = fullName;
    }
    else
    {
      prefix = fullName.substring(0, indexOfNSSep);
      namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
      if (null == namespace)
        return XBoolean.S_FALSE;
      methName= fullName.substring(indexOfNSSep + 1);
    }

    if (namespace.equals(Constants.S_XSLNAMESPACEURL)
    ||  namespace.equals(Constants.S_BUILTIN_EXTENSIONS_URL))
    {
      try
      {
        TransformerImpl transformer = (TransformerImpl) xctxt.getOwnerObject();
        return transformer.getStylesheet().getAvailableElements().containsKey(
                                                            new QName(namespace, methName))
               ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      }
      catch (Exception e)
      {
        return XBoolean.S_FALSE;
      }
    }
    else
    {
      ExtensionsProvider extProvider = (ExtensionsProvider)xctxt.getOwnerObject();
      return extProvider.elementAvailable(namespace, methName)
             ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
  }
}
