package org.apache.xalan.xsltc;

import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.DTMAxisIterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xml.serializer.SerializationHandler;

public interface DOM {
    public final static int  FIRST_TYPE             = 0;

    public final static int  NO_TYPE                = -1;
    
    public final static int NULL     = 0;

    public final static int RETURN_CURRENT = 0;
    public final static int RETURN_PARENT  = 1;
    
    public final static int SIMPLE_RTF   = 0;
    public final static int ADAPTIVE_RTF = 1;
    public final static int TREE_RTF     = 2;
    
    /** returns singleton iterator containg the document root */
    public DTMAxisIterator getIterator();
    public String getStringValue();
	
    public DTMAxisIterator getChildren(final int node);
    public DTMAxisIterator getTypedChildren(final int type);
    public DTMAxisIterator getAxisIterator(final int axis);
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type);
    public DTMAxisIterator getNthDescendant(int node, int n, boolean includeself);
    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns);
    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iter, int returnType,
					     String value, boolean op);
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node);
    public String getNodeName(final int node);
    public String getNodeNameX(final int node);
    public String getNamespaceName(final int node);
    public int getExpandedTypeID(final int node);
    public int getNamespaceType(final int node);
    public int getParent(final int node);
    public int getAttributeNode(final int gType, final int element);
    public String getStringValueX(final int node);
    public void copy(final int node, SerializationHandler handler)
	throws TransletException;
    public void copy(DTMAxisIterator nodes, SerializationHandler handler)
	throws TransletException;
    public String shallowCopy(final int node, SerializationHandler handler)
	throws TransletException;
    public boolean lessThan(final int node1, final int node2);
    public void characters(final int textNode, SerializationHandler handler)
	throws TransletException;
    public Node makeNode(int index);
    public Node makeNode(DTMAxisIterator iter);
    public NodeList makeNodeList(int index);
    public NodeList makeNodeList(DTMAxisIterator iter);
    public String getLanguage(int node);
    public int getSize();
    public String getDocumentURI(int node);
    public void setFilter(StripFilter filter);
    public void setupMapping(String[] names, String[] namespaces);
    public boolean isElement(final int node);
    public boolean isAttribute(final int node);
    public String lookupNamespace(int node, String prefix)
	throws TransletException;
    public int getNodeIdent(final int nodehandle);
    public int getNodeHandle(final int nodeId);
    public DOM getResultTreeFrag(int initialSize, int rtfType);
    public SerializationHandler getOutputDomBuilder();
    public int getNSType(int node);
    public int getDocument();
    public String getUnparsedEntityURI(String name);
    public Hashtable getElementsWithIDs();
}
