package org.apache.xalan.templates;

import java.util.Hashtable;

import org.apache.xalan.transformer.KeyManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Key() function.
 */
public class FuncKey extends Function2Args
{

  /** Dummy value to be used in usedrefs hashtable           */
  static private Boolean ISTRUE = new Boolean(true);

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

    TransformerImpl transformer = (TransformerImpl) xctxt.getOwnerObject();
    XNodeSet nodes = null;
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);
    int docContext = dtm.getDocumentRoot(context);

    if (DTM.NULL == docContext)
    {

    }

    String xkeyname = getArg0().execute(xctxt).str();
    QName keyname = new QName(xkeyname, xctxt.getNamespaceContext());
    XObject arg = getArg1().execute(xctxt);
    boolean argIsNodeSetDTM = (XObject.CLASS_NODESET == arg.getType());
    KeyManager kmgr = transformer.getKeyManager();
    
    if(argIsNodeSetDTM)
    {
    	XNodeSet ns = (XNodeSet)arg;
    	ns.setShouldCacheNodes(true);
    	int len = ns.getLength();
    	if(len <= 1)
    		argIsNodeSetDTM = false;
    }

    if (argIsNodeSetDTM)
    {
      Hashtable usedrefs = null;
      DTMIterator ni = arg.iter();
      int pos;
      UnionPathIterator upi = new UnionPathIterator();
      upi.exprSetParent(this);

      while (DTM.NULL != (pos = ni.nextNode()))
      {
        dtm = xctxt.getDTM(pos);
        XMLString ref = dtm.getStringValue(pos);

        if (null == ref)
          continue;

        if (null == usedrefs)
          usedrefs = new Hashtable();

        if (usedrefs.get(ref) != null)
        {
        }
        else
        {

          usedrefs.put(ref, ISTRUE);
        }

        XNodeSet nl =
          kmgr.getNodeSetDTMByKey(xctxt, docContext, keyname, ref,
                               xctxt.getNamespaceContext());
                               
        nl.setRoot(xctxt.getCurrentNode(), xctxt);

          upi.addIterator(nl);
      }

      int current = xctxt.getCurrentNode();
      upi.setRoot(current, xctxt);

      nodes = new XNodeSet(upi);
    }
    else
    {
      XMLString ref = arg.xstr();
      nodes = kmgr.getNodeSetDTMByKey(xctxt, docContext, keyname,
                                                ref,
                                                xctxt.getNamespaceContext());
      nodes.setRoot(xctxt.getCurrentNode(), xctxt);
    }

    return nodes;
  }
}
