package org.apache.xerces.tree;


import java.io.CharArrayWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Vector;

import org.w3c.dom.*;

import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;

import org.apache.xerces.tree.AttributeListEx;


/**
 * Class representing an XML attribute list.
 *
 * <P> This couples slightly with the Sun XML parser, in that it optionally
 * uses an extended SAX 1.0 API to see if an attribute was specified in the
 * document or was instead defaulted by attribute processing.
 *
 * @author David Brownell
 * @version $Revision: 315677 $
 */
final
class AttributeSet implements NamedNodeMap, XmlWritable
{
    private boolean	readonly;
    private Vector	list;
    private ElementNode	nameScope;
        
    /* Constructs an attribute list, with associated name scope. */
    AttributeSet (ElementNode nameScope)
    {
	list = new Vector (5);
	this.nameScope = nameScope;
    }

    /*
     * Constructs a copy of an attribute list, for use in cloning.
     * name scopes are set separately.
     */
    AttributeSet (AttributeSet original, boolean deep)
    {
	int		size = original.getLength ();

	list = new Vector (size);
	for (int i = 0; i < size; i++) {
	    Node	node = original.item (i);

	    if (!(node instanceof AttributeNode))
		throw new IllegalArgumentException (((NodeBase)node).
						getMessage ("A-003"));
	    node = node.cloneNode (deep);

	    ((AttributeNode)node).setNameScope (null);
	    list.addElement (node);
	}
    }

    AttributeSet (AttributeList source)
    throws DOMException
    {
	int			len = source.getLength ();
	AttributeListEx		ex = null;

	list = new Vector (len);
	if (source instanceof AttributeListEx)
	    ex = (AttributeListEx) source;

	for (int i = 0; i < len; i++) {
	    list.addElement (new AttributeNode (
		    source.getName (i),
		    source.getValue (i),
			? true
			: ex.isSpecified (i),
			? null
			: ex.getDefault (i)
		    ));
	}
	list.trimToSize ();
    }

    /**
     * <b>DOM2:</b> Create DOM NamedNodeMap from SAX2 Attributes object
     */
    AttributeSet(Attributes source) throws DOMException {
	int			len = source.getLength();
	AttributeListEx		ex = null;

	list = new Vector(len);
	    ex = (AttributeListEx) source;
        }

	for (int i = 0; i < len; i++) {
            String uri = source.getURI(i);
            if (uri.equals("")) {
                uri = null;
            }

            AttributeNode attrNode =
                new AttributeNode(uri,
                                  source.getQName(i),
                                  source.getValue(i),
                                  ? true
                                  : ex.isSpecified(i),
                                  ? null
                                  : ex.getDefault(i));
	    list.addElement(attrNode);
	}
	list.trimToSize();
    }

    void trimToSize () { list.trimToSize (); }

    public void setReadonly ()
    {
	readonly = true;
	for (int i = 0; i < list.size (); i++)
	    ((AttributeNode)list.elementAt (i)).setReadonly (true);
    }

    public boolean isReadonly () {
    	if (readonly)
	    return true;
	for (int i = 0; i < list.size (); i++) {
	    if (((AttributeNode)list.elementAt (i)).isReadonly ()) {
	   	return true; 
	    }
	}
	return false;
    }

    void setNameScope (ElementNode e)
    {
	if (e != null && nameScope != null)
	    throw new IllegalStateException (e.getMessage ("A-004"));
	nameScope = e;

	int	length = list.size ();

	for (int i = 0; i < length; i++) {
	    AttributeNode	node;

	    node = (AttributeNode) list.elementAt (i);
	    node.setNameScope (null);
	    node.setNameScope (e);
	}
    }

    ElementNode getNameScope ()
    {
	return nameScope;
    }


    String getValue (String name)
    {
	Attr	attr = (Attr) getNamedItem (name);

	if (attr == null)
	    return "";
	else
	    return attr.getValue ();
    }

    public Node getNamedItem (String name)
    {
	int	length = list.size ();
	Node	value;

	for (int i = 0; i < length; i++) {
	    value = item (i);
	    if (value.getNodeName ().equals (name))
		return value;
	}
	return null;
    }

    /**
     * <b>DOM2:</b>
     */
    public Node getNamedItemNS(String namespaceURI, String localName) {
	for (int i = 0; i < list.size(); i++) {
	    Node value = item(i);
            String iLocalName = value.getLocalName();
	    if (iLocalName != null && iLocalName.equals(localName)) {
                String iNamespaceURI = value.getNamespaceURI();
                if (iNamespaceURI != null
                    && iNamespaceURI.equals(namespaceURI)) {
                    return value;
                }
            }
	}
	return null;
    }

    public int getLength ()
    {
	return list.size ();
    }

    public Node item (int index)
    {
	if (index < 0 || index >= list.size ())
	    return null;
	return (Node) list.elementAt (index);
    }

    public Node removeNamedItem (String name)
    throws DOMException
    {
	int		length = list.size ();
	Node		value;

	if (readonly)
	    throw new DomEx (DomEx.NO_MODIFICATION_ALLOWED_ERR);
	for (int i = 0; i < length; i++) {
	    value = item (i);
	    if (value.getNodeName ().equals (name)) {
		AttributeNode	att = (AttributeNode) value;

		if (att.getDefaultValue () != null) {
		    att = new AttributeNode (att);
		    att.setOwnerDocument ((XmlDocument)
			    nameScope.getOwnerDocument ());
		    list.setElementAt (att, i);
		} else
		    list.removeElementAt (i);

		att.setNameScope (null);
		return value;
	    }
	}
	throw new DomEx (DomEx.NOT_FOUND_ERR);
    }

    /**
     * <b>DOM2:</b>
     */
    public Node removeNamedItemNS(String namespaceURI, String localName)
        throws DOMException
    {
	if (readonly) {
	    throw new DomEx(DomEx.NO_MODIFICATION_ALLOWED_ERR);
        }

	for (int i = 0; i < list.size(); i++) {
	    Node value = item(i);
            String iLocalName = value.getLocalName();
	    if (iLocalName != null && iLocalName.equals(localName)) {
                String iNamespaceURI = value.getNamespaceURI();
                if (iNamespaceURI != null
                    && iNamespaceURI.equals(namespaceURI)) {
                    AttributeNode attr = (AttributeNode) value;
                    if (attr.getDefaultValue() != null) {
                        attr = new AttributeNode(attr);
                        list.setElementAt(attr, i);
                    } else {
                        list.removeElementAt(i);
                    }
                    return value;
                }
            }
	}
	throw new DomEx(DomEx.NOT_FOUND_ERR);
    }

    public Node setNamedItem (Node value)
    throws DOMException
    {
	AttributeNode	node;

	if (readonly)
	    throw new DomEx (DomEx.NO_MODIFICATION_ALLOWED_ERR);
	if (!(value instanceof AttributeNode)
		|| value.getOwnerDocument ()
			!= nameScope.getOwnerDocument ())
	    throw new DomEx (DomEx.WRONG_DOCUMENT_ERR);
	node = (AttributeNode)value;
	if (node.getNameScope () != null)
	    throw new DomEx (DomEx.INUSE_ATTRIBUTE_ERR);

	int		length = list.size ();
	AttributeNode	oldValue;

	for (int i = 0; i < length; i++) {
	    oldValue = (AttributeNode) item (i);
	    if (oldValue.getNodeName ().equals (value.getNodeName ())) {
	    	if (oldValue.isReadonly ())
	    	    throw new DomEx (DomEx.NO_MODIFICATION_ALLOWED_ERR);
		node.setNameScope (nameScope);
		list.setElementAt (value, i);
		oldValue.setNameScope (null);
		return oldValue;
	    }
	}
	node.setNameScope (nameScope);
	list.addElement (value);
	return null;
    }
    
    /**
     * <b>DOM2:</b>
     * XXX spec allows Element nodes also, but this code assumes Attr nodes
     * only
     */
    public Node setNamedItemNS(Node arg) throws DOMException {
	if (readonly) {
	    throw new DomEx(DomEx.NO_MODIFICATION_ALLOWED_ERR);
        }

	AttributeNode attr = (AttributeNode) arg;
        if (attr.getOwnerElement() != null) {
	    throw new DomEx(DomEx.INUSE_ATTRIBUTE_ERR);
        }

	for (int i = 0; i < list.size(); i++) {
	    AttributeNode oldNode = (AttributeNode) item(i);
            String localName = oldNode.getLocalName();
            String namespaceURI = oldNode.getNamespaceURI();
            if (arg.getLocalName().equals(localName)
                && arg.getNamespaceURI().equals(namespaceURI)) {
                list.setElementAt(arg, i);
                return oldNode;
            }
	}

	list.addElement(arg);
	return null;
    }

    /**
     * Writes out the attribute list.  Attributes known to have been
     * derived from the DTD are not (at this time) written out.  Part
     * of writing standalone XML is first ensuring that all attributes
     * are flagged as being specified in the "printed" form (or else
     * are defaulted only in the internal DTD subset).
     */
    public void writeXml (XmlWriteContext context) throws IOException
    {
	Writer		out = context.getWriter ();
	int		length = list.size ();
	AttributeNode	tmp;

	for (int i = 0; i < length; i++) {
	    tmp = (AttributeNode) list.elementAt (i);
	    if (tmp.getSpecified ()) {
		out.write (' ');
		tmp.writeXml (context);
	    }
	}
    }

    /**
     * Does nothing; this type of node has no children.
     */
    public void writeChildrenXml (XmlWriteContext context) throws IOException
    {
    }

    public String toString ()
    {
	try {
	    CharArrayWriter w = new CharArrayWriter ();
	    XmlWriteContext x = new XmlWriteContext (w);
	    writeXml (x);
	    return w.toString ();

	} catch (IOException e) {
	    return super.toString ();
	}
    }
}
