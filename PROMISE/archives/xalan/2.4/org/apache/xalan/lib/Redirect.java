package org.apache.xalan.lib;

import java.util.*;
import java.io.*;
import java.net.URL;

import org.xml.sax.ContentHandler;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPath;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;

import org.w3c.dom.*;

/**
 * Implements three extension elements to allow an XSLT transformation to
 * redirect its output to multiple output files.
 *
 * a namespace for the extension prefix (such as xmlns:redirect="org.apache.xalan.lib.Redirect"),
 * and declare the extension namespace as an extension (extension-element-prefixes="redirect").
 *
 * <p>You can either just use redirect:write, in which case the file will be
 * opened and immediately closed after the write, or you can bracket the
 * write calls by redirect:open and redirect:close, in which case the
 * file will be kept open for multiple writes until the close call is
 * encountered.  Calls can be nested.  
 *
 * <p>Calls can take a 'file' attribute
 * and/or a 'select' attribute in order to get the filename.  If a select
 * attribute is encountered, it will evaluate that expression for a string
 * that indicates the filename.  If the string evaluates to empty, it will
 * attempt to use the 'file' attribute as a default.  Filenames can be relative
 * or absolute.  If they are relative, the base directory will be the same as
 * the base directory for the output document.  This is obtained by calling
 * getOutputTarget() on the TransformerImpl.  You can set this base directory
 * by calling TransformerImpl.setOutputTarget() or it is automatically set
 * when using the two argument form of transform() or transformNode().
 *
 * <p>Calls to redirect:write and redirect:open also take an optional 
 * attribute append="true|yes", which will attempt to simply append 
 * to an existing file instead of always opening a new file.  The 
 * default behavior of always overwriting the file still happens 
 * if you do not specify append.
 * <p><b>Note:</b> this may give unexpected results when using xml 
 * or html output methods, since this is <b>not</b> coordinated 
 * with the serializers - hence, you may get extra xml decls in 
 * the middle of your file after appending to it.
 *
 * <p>Example:</p>
 * <PRE>
 * &lt;?xml version="1.0"?>
 *                 xmlns:redirect="org.apache.xalan.lib.Redirect"
 *                 extension-element-prefixes="redirect">
 *
 *   &lt;xsl:template match="/">
 *     &lt;out>
 *       default output.
 *     &lt;/out>
 *     &lt;redirect:open file="doc3.out"/>
 *     &lt;redirect:write file="doc3.out">
 *       &lt;out>
 *         &lt;redirect:write file="doc1.out">
 *           &lt;out>
 *             doc1 output.
 *             &lt;redirect:write file="doc3.out">
 *               Some text to doc3
 *             &lt;/redirect:write>
 *           &lt;/out>
 *         &lt;/redirect:write>
 *         &lt;redirect:write file="doc2.out">
 *           &lt;out>
 *             doc2 output.
 *             &lt;redirect:write file="doc3.out">
 *               Some more text to doc3
 *               &lt;redirect:write select="doc/foo">
 *                 text for doc4
 *               &lt;/redirect:write>
 *             &lt;/redirect:write>
 *           &lt;/out>
 *         &lt;/redirect:write>
 *       &lt;/out>
 *     &lt;/redirect:write>
 *     &lt;redirect:close file="doc3.out"/>
 *   &lt;/xsl:template>
 *
 * &lt;/xsl:stylesheet>
 * </PRE>
 *
 * @author Scott Boag
 * @version 1.0
 * @see <a href="../../../../../../extensions.html#ex-redirect" target="_top">Example with Redirect extension</a>
 */
public class Redirect
{
  /**
   * List of formatter listeners indexed by filename.
   */
  protected Hashtable m_formatterListeners = new Hashtable ();

  /**
   * List of output streams indexed by filename.
   */
  protected Hashtable m_outputStreams = new Hashtable ();

  /** 
   * Default append mode for bare open calls.  
   * False for backwards compatibility (I think). 
   */
  public static final boolean DEFAULT_APPEND_OPEN = false;

  /** 
   * Default append mode for bare write calls.  
   * False for backwards compatibility. 
   */
  public static final boolean DEFAULT_APPEND_WRITE = false;

  /**
   * Open the given file and put it in the XML, HTML, or Text formatter listener's table.
   */
  public void open(XSLProcessorContext context, ElemExtensionCall elem)
    throws java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException,
           javax.xml.transform.TransformerException
  {
    String fileName = getFilename(context, elem);
    Object flistener = m_formatterListeners.get(fileName);
    if(null == flistener)
    {
      String mkdirsExpr 
        = elem.getAttribute ("mkdirs", context.getContextNode(), 
                                                  context.getTransformer());
      boolean mkdirs = (mkdirsExpr != null)
                       ? (mkdirsExpr.equals("true") || mkdirsExpr.equals("yes")) : true;

      String appendExpr = elem.getAttribute("append", context.getContextNode(), context.getTransformer());
	  boolean append = (appendExpr != null)
                       ? (appendExpr.equals("true") || appendExpr.equals("yes")) : DEFAULT_APPEND_OPEN;

      Object ignored = makeFormatterListener(context, elem, fileName, true, mkdirs, append);
    }
  }
  
  /**
   * Write the evalutation of the element children to the given file. Then close the file
   * unless it was opened with the open extension element and is in the formatter listener's table.
   */
  public void write(XSLProcessorContext context, ElemExtensionCall elem)
    throws java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException,
           javax.xml.transform.TransformerException
  {
    String fileName = getFilename(context, elem);
    Object flObject = m_formatterListeners.get(fileName);
    ContentHandler formatter;
    boolean inTable = false;
    if(null == flObject)
    {
      String mkdirsExpr 
        = ((ElemExtensionCall)elem).getAttribute ("mkdirs", 
                                                  context.getContextNode(), 
                                                  context.getTransformer());
      boolean mkdirs = (mkdirsExpr != null)
                       ? (mkdirsExpr.equals("true") || mkdirsExpr.equals("yes")) : true;

      String appendExpr = elem.getAttribute("append", context.getContextNode(), context.getTransformer());
	  boolean append = (appendExpr != null)
                       ? (appendExpr.equals("true") || appendExpr.equals("yes")) : DEFAULT_APPEND_WRITE;

      formatter = makeFormatterListener(context, elem, fileName, true, mkdirs, append);
    }
    else
    {
      inTable = true;
      formatter = (ContentHandler)flObject;
    }
    
    TransformerImpl transf = context.getTransformer();
    
    transf.executeChildTemplates(elem,
                                 context.getContextNode(),
                                 context.getMode(), formatter);
    
    if(!inTable)
    {
      OutputStream ostream = (OutputStream)m_outputStreams.get(fileName);
      if(null != ostream)
      {
        try
        {
          formatter.endDocument();
        }
        catch(org.xml.sax.SAXException se)
        {
          throw new TransformerException(se);
        }
        ostream.close();
        m_outputStreams.remove(fileName);
        m_formatterListeners.remove(fileName);
      }
    }
  }


  /**
   * Close the given file and remove it from the formatter listener's table.
   */
  public void close(XSLProcessorContext context, ElemExtensionCall elem)
    throws java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException,
    javax.xml.transform.TransformerException
  {
    String fileName = getFilename(context, elem);
    Object formatterObj = m_formatterListeners.get(fileName);
    if(null != formatterObj)
    {
      ContentHandler fl = (ContentHandler)formatterObj;
      try
      {
        fl.endDocument();
      }
      catch(org.xml.sax.SAXException se)
      {
        throw new TransformerException(se);
      }
      OutputStream ostream = (OutputStream)m_outputStreams.get(fileName);
      if(null != ostream)
      {
        ostream.close();
        m_outputStreams.remove(fileName);
      }
      m_formatterListeners.remove(fileName);
    }
  }

  /**
   * Get the filename from the 'select' or the 'file' attribute.
   */
  private String getFilename(XSLProcessorContext context, ElemExtensionCall elem)
    throws java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException,
    javax.xml.transform.TransformerException
  {
    String fileName;
    String fileNameExpr 
      = ((ElemExtensionCall)elem).getAttribute ("select", 
                                                context.getContextNode(), 
                                                context.getTransformer());
    if(null != fileNameExpr)
    {
      org.apache.xpath.XPathContext xctxt 
        = context.getTransformer().getXPathContext();
      XPath myxpath = new XPath(fileNameExpr, elem, xctxt.getNamespaceContext(), XPath.SELECT);
      XObject xobj = myxpath.execute(xctxt, context.getContextNode(), elem);
      fileName = xobj.str();
      if((null == fileName) || (fileName.length() == 0))
      {
        fileName = elem.getAttribute ("file", 
                                      context.getContextNode(), 
                                      context.getTransformer());
      }
    }
    else
    {
      fileName = elem.getAttribute ("file", context.getContextNode(), 
                                                               context.getTransformer());
    }
    if(null == fileName)
    {
      context.getTransformer().getMsgMgr().error(elem, elem, 
                                     context.getContextNode(), 
                                     XSLTErrorResources.ER_REDIRECT_COULDNT_GET_FILENAME);
    }
    return fileName;
  }
  
  private String urlToFileName(String base)
  {
    if(null != base)
    {
      {
        base = base.substring(7);
      }
      {
        base = base.substring(6);
      }
      {
      }
      else if(base.startsWith("file:/"))
      {
        base = base.substring(5);
      }
      else if(base.startsWith("file:"))
      {
        base = base.substring(4);
      }
    }
    return base;
  }

  /**
   * Create a new ContentHandler, based on attributes of the current ContentHandler.
   */
  private ContentHandler makeFormatterListener(XSLProcessorContext context,
                                               ElemExtensionCall elem,
                                               String fileName,
                                               boolean shouldPutInTable,
                                               boolean mkdirs, 
                                               boolean append)
    throws java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException,
    javax.xml.transform.TransformerException
  {
    File file = new File(fileName);
    TransformerImpl transformer = context.getTransformer();

    if(!file.isAbsolute())
    {

      Result outputTarget = transformer.getOutputTarget();
      if ( (null != outputTarget) && ((base = outputTarget.getSystemId()) != null) ) {
        base = urlToFileName(base);
      }
      else
      {
        base = urlToFileName(transformer.getBaseURLOfSource());
      }

      if(null != base)
      {
        File baseFile = new File(base);
        file = new File(baseFile.getParent(), fileName);
      }
    }

    if(mkdirs)
    {
      String dirStr = file.getParent();
      if((null != dirStr) && (dirStr.length() > 0))
      {
        File dir = new File(dirStr);
        dir.mkdirs();
      }
    }

    OutputProperties format = transformer.getOutputFormat();

    FileOutputStream ostream = new FileOutputStream(file.getPath(), append);
    
    try
    {
      ContentHandler flistener 
        = transformer.createResultContentHandler(new StreamResult(ostream), format);
      try
      {
        flistener.startDocument();
      }
      catch(org.xml.sax.SAXException se)
      {
        throw new TransformerException(se);
      }
      if(shouldPutInTable)
      {
        m_outputStreams.put(fileName, ostream);
        m_formatterListeners.put(fileName, flistener);
      }
      return flistener;
    }
    catch(TransformerException te)
    {
      throw new javax.xml.transform.TransformerException(te);
    }
    
  }
}