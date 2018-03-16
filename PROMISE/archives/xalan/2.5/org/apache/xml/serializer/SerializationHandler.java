package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import java.util.Vector;

import javax.xml.transform.Transformer;

import org.apache.xml.serializer.Serializer;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;

/**
 * This interface is the one that a serializer implements. It is a group of
 * other interfaces, such as ExtendedContentHandler, ExtendedLexicalHandler etc.
 * In addition there are other methods, such as reset().
 */
public interface SerializationHandler
    extends
        ExtendedContentHandler,
        ExtendedLexicalHandler,
        XSLOutputAttributes,
        DeclHandler,
        ErrorHandler,
        DOMSerializer,
        Serializer
{
    /**
     * Set the SAX Content handler that the serializer sends its output to. This
     * method only applies to a ToSAXHandler, not to a ToStream serializer.
     * 
     * @see org.apache.xml.serializer.Serializer#asContentHandler()
     * @see org.apache.xml.serializer.ToSAXHandler
     */
    public void setContentHandler(ContentHandler ch);
    
    public void close();

    /**
     * Notify that the serializer should take this DOM node as input to be
     * serialized.
     * 
     * @param node the DOM node to be serialized.
     * @throws IOException
     */
    public void serialize(Node node) throws IOException;
    /**
     * Turns special character escaping on/off. 
     * 
     * Note that characters will
     * never, even if this option is set to 'true', be escaped within
     * CDATA sections in output XML documents.
     * 
     * @param true if escaping is to be set on.
     */
    public boolean setEscaping(boolean escape) throws SAXException;

    /**
     * Set the number of spaces to indent for each indentation level.
     * @param spaces the number of spaces to indent for each indentation level.
     */
    public void setIndentAmount(int spaces);

    /**
     * Set the transformer associated with the serializer.
     * @param transformer the transformer associated with the serializer.
     */
    public void setTransformer(Transformer transformer);
    
    /**
     * Get the transformer associated with the serializer.
     * @return Transformer the transformer associated with the serializer.
     */
    public Transformer getTransformer();

    /** 
     * Used only by TransformerSnapshotImpl to restore the serialization 
     * to a previous state. 
     * 
     * @param NamespaceMappings
     */
    public void setNamespaceMappings(NamespaceMappings mappings);

    /**
     * Flush any pending events currently queued up in the serializer. This will
     * flush any input that the serializer has which it has not yet sent as
     * output.
     */
    public void flushPending();


}
