package org.apache.xalan.xsltc.runtime;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import java.util.Vector;
import java.util.Enumeration;

public class AttributeList implements org.xml.sax.Attributes {

    private final static String EMPTYSTRING = "";
    private final static String CDATASTRING = "CDATA";

    private Hashtable _attributes;
    private Vector    _names;
    private Vector    _qnames;
    private Vector    _values;
    private Vector    _uris;
    private int       _length;

    /**
     * AttributeList constructor
     */
    public AttributeList() {
	_attributes = new Hashtable();
	_names  = new Vector();
	_values = new Vector();
	_qnames = new Vector();
	_uris   = new Vector();
	_length = 0;
    }

    /**
     * Attributes clone constructor
     */
    public AttributeList(org.xml.sax.Attributes attributes) {
	this();
	if (attributes != null) {
	    final int count = attributes.getLength();
	    for (int i = 0; i < count; i++) {
		add(attributes.getQName(i),attributes.getValue(i));
	    }
	}
    }

    /**
     * SAX2: Return the number of attributes in the list. 
     */
    public int getLength() {
	return(_length);
    }

    /**
     * SAX2: Look up an attribute's Namespace URI by index.
     */
    public String getURI(int index) {
	if (index < _length)
	    return((String)_uris.elementAt(index));
	else
	    return(null);
    }

    /**
     * SAX2: Look up an attribute's local name by index.
     */
    public String getLocalName(int index) {
	if (index < _length)
	    return((String)_names.elementAt(index));
	else
	    return(null);
    }

    /**
     * Return the name of an attribute in this list (by position).
     */
    public String getQName(int pos) {
	if (pos < _length)
	    return((String)_qnames.elementAt(pos));
	else
	    return(null);
    }

    /**
     * SAX2: Look up an attribute's type by index.
     */
    public String getType(int index) {
	return(CDATASTRING);
    }

    /**
     * SAX2: Look up the index of an attribute by Namespace name.
     */
    public int getIndex(String namespaceURI, String localPart) {
	return(0);
    }

    /**
     * SAX2: Look up the index of an attribute by XML 1.0 qualified name.
     */
    public int getIndex(String qname) {
	return(0);
    }

    /**
     * SAX2: Look up an attribute's type by Namespace name.
     */
    public String getType(String uri, String localName) {
	return(CDATASTRING);
    }

    /**
     * SAX2: Look up an attribute's type by qname.
     */
    public String getType(String qname) {
	return(CDATASTRING);
    }

    /**
     * SAX2: Look up an attribute's value by index.
     */
    public String getValue(int pos) {
	if (pos < _length)
	    return((String)_values.elementAt(pos));
	else
	    return(null);
    }

    /**
     * SAX2: Look up an attribute's value by qname.
     */
    public String getValue(String qname) {
	final Integer obj = (Integer)_attributes.get(qname);
	if (obj == null) return null;
	return(getValue(obj.intValue()));
    }

    /**
     * SAX2: Look up an attribute's value by Namespace name - SLOW!
     */
    public String getValue(String uri, String localName) {
	return(getValue(uri+':'+localName));
    }

    /**
     * Adds an attribute to the list
     */
    public void add(String qname, String value) {
	Integer obj = (Integer)_attributes.get(qname);
	if (obj == null) {
	    _attributes.put(qname, obj = new Integer(_length++));
	    _qnames.addElement(qname);
	    _values.addElement(value);
	    int col = qname.lastIndexOf(':');
	    if (col > -1) {
		_uris.addElement(qname.substring(0,col));
		_names.addElement(qname.substring(col+1));
	    }
	    else {
		_uris.addElement(EMPTYSTRING);
		_names.addElement(qname);
	    }
	}
	else {
	    final int index = obj.intValue();
	    _values.set(index, value);
	}
    }

    /**
     * Clears the attribute list
     */
    public void clear() {
	_length = 0;
	_attributes.clear();
	_names.removeAllElements();
	_values.removeAllElements();
	_qnames.removeAllElements();
	_uris.removeAllElements();
    }
    
}
