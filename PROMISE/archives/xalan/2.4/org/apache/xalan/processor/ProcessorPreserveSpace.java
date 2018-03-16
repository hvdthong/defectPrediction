package org.apache.xalan.processor;

import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.WhiteSpaceInfo;
import org.apache.xpath.XPath;

import java.util.Vector;

/**
 * TransformerFactory for xsl:preserve-space markup.
 * <pre>
 * <!ELEMENT xsl:preserve-space EMPTY>
 * <!ATTLIST xsl:preserve-space elements CDATA #REQUIRED>
 * </pre>
 */
class ProcessorPreserveSpace extends XSLTElementProcessor
{

  /**
   * Receive notification of the start of an preserve-space element.
   *
   * @param handler The calling StylesheetHandler/TemplatesBuilder.
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param rawName The raw XML 1.0 name (with prefix), or the
   *        empty string if raw names are not available.
   * @param attributes The attributes attached to the element.  If
   *        there are no attributes, it shall be an empty
   *        Attributes object.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, 
          Attributes attributes)
            throws org.xml.sax.SAXException
  {
    Stylesheet thisSheet = handler.getStylesheet();
	WhitespaceInfoPaths paths = new WhitespaceInfoPaths(thisSheet);
    setPropertiesFromAttributes(handler, rawName, attributes, paths);

    Vector xpaths = paths.getElements();

    for (int i = 0; i < xpaths.size(); i++)
    {
      WhiteSpaceInfo wsi = new WhiteSpaceInfo((XPath) xpaths.elementAt(i), false, thisSheet);
      wsi.setUid(handler.nextUid());

      thisSheet.setPreserveSpaces(wsi);
    }
    paths.clearElements();
  }
}
