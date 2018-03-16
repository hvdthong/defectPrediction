package org.apache.xalan.xsltc.runtime;


import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.Hashtable;

public class TransletOutputBase implements TransletOutputHandler {
    public void startDocument() throws TransletException {}
    public void endDocument() throws TransletException {}
    public void characters(String characters) throws TransletException {}
    public void characters(char[] characters, int offset, int length)
	throws TransletException {}
    public void startElement(String elementName) throws TransletException {}
    public void endElement(String elementName) throws TransletException {}
    public void attribute(String attributeName, String attributeValue)
	throws TransletException {}
    public void namespace(String prefix, String uri)
	throws TransletException {}
    public void comment(String comment) throws TransletException {}
    public void processingInstruction(String target, String data)
	throws TransletException {}
    public boolean setEscaping(boolean escape) 
	throws TransletException { return true; }
    public void startCDATA() throws TransletException {}
    public void endCDATA() throws TransletException {}
    public String expandQName(String withPrefix) { return(withPrefix); }
    public void setType(int type) {}
    public void setIndent(boolean indent) {}
    public void omitHeader(boolean value) {}
    public void setCdataElements(Hashtable elements) {}
    public void setDoctype(String system, String pub) {}
    public void setMediaType(String mediaType) {}
    public void setStandalone(String standalone) {}
    public void setVersion(String version) {}
    public void close() {}
}
