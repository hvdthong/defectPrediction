package org.apache.xalan.templates;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:use.
 * This acts as a superclass for ElemCopy, ElemAttributeSet,
 * ElemElement, and ElemLiteralResult, on order to implement
 * shared behavior the use-attribute-sets attribute.
 */
public class ElemUse extends ElemTemplateElement
{

  /**
   * The value of the "use-attribute-sets" attribute.
   * @serial
   */
  private QName m_attributeSetsNames[] = null;

  /**
   * Set the "use-attribute-sets" attribute.
   * Attribute sets are used by specifying a use-attribute-sets
   * attribute on xsl:element, xsl:copy (see [7.5 Copying]) or
   * xsl:attribute-set elements. The value of the use-attribute-sets
   * attribute is a whitespace-separated list of names of attribute
   * sets. Each name is specified as a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @param v The value to set for the "use-attribute-sets" attribute. 
   */
  public void setUseAttributeSets(Vector v)
  {

    int n = v.size();

    m_attributeSetsNames = new QName[n];

    for (int i = 0; i < n; i++)
    {
      m_attributeSetsNames[i] = (QName) v.elementAt(i);
    }
  }

  /**
   * Set the "use-attribute-sets" attribute.
   * Attribute sets are used by specifying a use-attribute-sets
   * attribute on xsl:element, xsl:copy (see [7.5 Copying]) or
   * xsl:attribute-set elements. The value of the use-attribute-sets
   * attribute is a whitespace-separated list of names of attribute
   * sets. Each name is specified as a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @param v The value to set for the "use-attribute-sets" attribute. 
   */
  public void setUseAttributeSets(QName[] v)
  {
    m_attributeSetsNames = v;
  }

  /**
   * Get the "use-attribute-sets" attribute.
   * Attribute sets are used by specifying a use-attribute-sets
   * attribute on xsl:element, xsl:copy (see [7.5 Copying]) or
   * xsl:attribute-set elements, or a xsl:use-attribute-sets attribute on
   * Literal Result Elements.
   * The value of the use-attribute-sets
   * attribute is a whitespace-separated list of names of attribute
   * sets. Each name is specified as a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @return The value of the "use-attribute-sets" attribute. 
   */
  public QName[] getUseAttributeSets()
  {
    return m_attributeSetsNames;
  }
  
  /**
   * Add the attributes from the named attribute sets to the attribute list.
   * TODO: Error handling for: "It is an error if there are two attribute sets
   * with the same expanded-name and with equal import precedence and that both
   * contain the same attribute unless there is a definition of the attribute
   * set with higher import precedence that also contains the attribute."
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param stylesheet The owning root stylesheet
   * @param attributeSetsNames List of attribute sets names to apply
   *
   * @throws TransformerException
   */
  public void applyAttrSets(
          TransformerImpl transformer, StylesheetRoot stylesheet)
            throws TransformerException
  {
    applyAttrSets(transformer, stylesheet, m_attributeSetsNames);
  }

  /**
   * Add the attributes from the named attribute sets to the attribute list.
   * TODO: Error handling for: "It is an error if there are two attribute sets
   * with the same expanded-name and with equal import precedence and that both
   * contain the same attribute unless there is a definition of the attribute
   * set with higher import precedence that also contains the attribute."
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param stylesheet The owning root stylesheet
   * @param attributeSetsNames List of attribute sets names to apply
   *
   * @throws TransformerException
   */
  private void applyAttrSets(
          TransformerImpl transformer, StylesheetRoot stylesheet, QName attributeSetsNames[])
            throws TransformerException
  {

    if (null != attributeSetsNames)
    {
      int nNames = attributeSetsNames.length;

      for (int i = 0; i < nNames; i++)
      {
        QName qname = attributeSetsNames[i];
        Vector attrSets = stylesheet.getAttributeSetComposed(qname);

        if (null != attrSets)
        {
          int nSets = attrSets.size();

          for (int k = nSets-1; k >= 0 ; k--)
          {
            ElemAttributeSet attrSet =
              (ElemAttributeSet) attrSets.elementAt(k);

            attrSet.execute(transformer);
          }
        }
      }
    }
  }

  /**
   * Copy attributes specified by use-attribute-sets to the result tree.
   * Specifying a use-attribute-sets attribute is equivalent to adding
   * xsl:attribute elements for each of the attributes in each of the
   * named attribute sets to the beginning of the content of the element
   * with the use-attribute-sets attribute, in the same order in which
   * the names of the attribute sets are specified in the use-attribute-sets
   * attribute. It is an error if use of use-attribute-sets attributes
   * on xsl:attribute-set elements causes an attribute set to directly
   * or indirectly use itself.
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

    if (null != m_attributeSetsNames)
    {
      applyAttrSets(transformer, getStylesheetRoot(),
                    m_attributeSetsNames);
    }
    
    if (TransformerImpl.S_DEBUG)
	  transformer.getTraceManager().fireTraceEndEvent(this); 
  }
}
