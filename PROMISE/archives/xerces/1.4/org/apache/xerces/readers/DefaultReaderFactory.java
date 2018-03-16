package org.apache.xerces.readers;

import org.apache.xerces.framework.XMLErrorReporter;
import org.apache.xerces.utils.ChunkyByteArray;
import org.apache.xerces.utils.StringPool;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Stack;

public class DefaultReaderFactory implements XMLEntityReaderFactory {
    private static final boolean USE_CHAR_READER_FOR_UTF8 = false;
    private static final boolean USE_BYTE_READER_FOR_UTF8 = true;

    private boolean fSendCharDataAsCharArray = false;
    private boolean fAllowJavaEncodingName = false;
    private Stack fRecognizers = null;

    /**
        * Constructor
        */
    public DefaultReaderFactory() {
    }

    /**
        * Adds a recognizer.
        *
        * @param recognizer The XML recognizer to add.
        */
    public void addRecognizer(XMLDeclRecognizer recognizer) {
        if (fRecognizers == null) {
            fRecognizers = new Stack();
            XMLDeclRecognizer.registerDefaultRecognizers(fRecognizers);
        }
        fRecognizers.push(recognizer);
    }

    /**
        * Set char data processing preference.
        */
    public void setSendCharDataAsCharArray(boolean flag) {
        fSendCharDataAsCharArray = flag;
    }

    /**
        *
        */
    public void setAllowJavaEncodingName(boolean flag) {
        fAllowJavaEncodingName = flag;
    }

    /**
        *
        */
    public boolean getAllowJavaEncodingName() {
        return fAllowJavaEncodingName;
    }

    /**
        * Create a reader
        */
    public XMLEntityHandler.EntityReader createReader(XMLEntityHandler entityHandler,
                                                            XMLErrorReporter errorReporter,
                                                            InputSource source,
                                                      String systemId, boolean xmlDecl, StringPool stringPool) throws Exception {

        if (source.getCharacterStream() != null) {
            return createCharReader(entityHandler, errorReporter, fSendCharDataAsCharArray, source.getCharacterStream(), stringPool);
        }

        if (source.getEncoding() != null && source.getByteStream() != null) {
            java.io.Reader reader = new InputStreamReader(source.getByteStream(), source.getEncoding());
            return createCharReader(entityHandler, errorReporter, fSendCharDataAsCharArray, reader, stringPool);
        }

        InputStream is = source.getByteStream();
        if (is == null) {

            URL url = new URL(systemId);
            is = url.openStream();
        }

        ChunkyByteArray data = new ChunkyByteArray(is);
        if (fRecognizers == null) {
            fRecognizers = new Stack();
            XMLDeclRecognizer.registerDefaultRecognizers(fRecognizers);
        }
        for (int i = fRecognizers.size() - 1; i >= 0; i--) {
            XMLDeclRecognizer recognizer = (XMLDeclRecognizer)fRecognizers.elementAt(i);
            XMLEntityHandler.EntityReader reader = recognizer.recognize(this, entityHandler, errorReporter, fSendCharDataAsCharArray, stringPool, data, xmlDecl, fAllowJavaEncodingName);
            if (reader != null) {
                return reader;
            }
        }
        return createUTF8Reader(entityHandler, errorReporter, fSendCharDataAsCharArray, data, stringPool);
    }

    /**
        * Create an entity reader for a character stream.
        *
        * @param enityHandler The entity handler.
        * @param errorReporter The error reporter.
        * @param sendCharDataAsCharArray true if char data should be reported using
        *                                char arrays instead of string handles.
        * @param reader The character stream.
        * @param stringPool The string pool.
        * @return The reader that will process the character data.
        * @exception java.lang.Exception
        */
    public XMLEntityHandler.EntityReader createCharReader(XMLEntityHandler entityHandler,
                                                            XMLErrorReporter errorReporter,
                                                            boolean sendCharDataAsCharArray,
                                                            Reader reader,
                                                            StringPool stringPool) throws Exception
    {
        return new CharReader(entityHandler, errorReporter, sendCharDataAsCharArray, reader, stringPool);
    }

    /**
        * Create an entity reader for a byte stream encoded in UTF-8.
        *
        * @param enityHandler The entity handler.
        * @param errorReporter The error reporter.
        * @param sendCharDataAsCharArray true if char data should be reported using
        *                                char arrays instead of string handles.
        * @param data The byte stream.
        * @param stringPool The string pool.
        * @return The reader that will process the UTF-8 data.
        * @exception java.lang.Exception
        */
    public XMLEntityHandler.EntityReader createUTF8Reader(XMLEntityHandler entityHandler,
                                                            XMLErrorReporter errorReporter,
                                                            boolean sendCharDataAsCharArray,
                                                            InputStream data,
                                                            StringPool stringPool) throws Exception
    {
        XMLEntityHandler.EntityReader reader;
        if (USE_CHAR_READER_FOR_UTF8) {
            reader = new CharReader(entityHandler, errorReporter, sendCharDataAsCharArray, new InputStreamReader(data, "UTF8"), stringPool);
        } else if (USE_BYTE_READER_FOR_UTF8) {
            reader = new UTF8Reader(entityHandler, errorReporter, sendCharDataAsCharArray, data, stringPool);
        } else {
            reader = new UTF8CharReader(entityHandler, errorReporter, sendCharDataAsCharArray, data, stringPool);
        }
        return reader;
    }

    /**
        * Create an entity reader for data from a String.
        *
        * @param entityHandler The current entity handler.
        * @param errorReporter The current error reporter.
        * @param sendCharDataAsCharArray true if char data should be reported using
        *                                char arrays instead of string handles.
        * @param lineNumber The line number to return as our position.
        * @param columnNumber The column number to return as our position.
        * @param stringHandle The StringPool handle for the data to process.
        * @param stringPool The string pool.
        * @param addEnclosingSpaces If true, treat the data to process as if
        *                           there were a leading and trailing space
        *                           character enclosing the string data.
        * @return The reader that will process the string data.
        * @exception java.lang.Exception
        */
    public XMLEntityHandler.EntityReader createStringReader(XMLEntityHandler entityHandler,
                                                            XMLErrorReporter errorReporter,
                                                            boolean sendCharDataAsCharArray,
                                                            int lineNumber,
                                                            int columnNumber,
                                                            int stringHandle,
                                                            StringPool stringPool,
                                                            boolean addEnclosingSpaces) throws Exception
    {
        return StringReader.createStringReader(entityHandler, errorReporter, sendCharDataAsCharArray,
                                                lineNumber, columnNumber, stringHandle, stringPool, addEnclosingSpaces);
    }
}
