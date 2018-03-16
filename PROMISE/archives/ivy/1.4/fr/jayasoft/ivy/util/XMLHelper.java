package fr.jayasoft.ivy.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import fr.jayasoft.ivy.url.URLHandlerRegistry;

public abstract class XMLHelper {
    private static SAXParserFactory _validatingFactory = SAXParserFactory.newInstance();

    private static SAXParserFactory _factory = SAXParserFactory.newInstance();




    private static boolean _canUseSchemaValidation = true;

    static {
        _validatingFactory.setNamespaceAware(true);
        _validatingFactory.setValidating(true);
    }

    private static SAXParser newSAXParser(URL schema, InputStream schemaStream) throws ParserConfigurationException, SAXException {
        if (!_canUseSchemaValidation || schema == null) {
            return _factory.newSAXParser();
        }
        try {
            SAXParser parser = _validatingFactory.newSAXParser();
            parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            parser.setProperty(JAXP_SCHEMA_SOURCE, schemaStream);
            return parser;
        } catch (SAXNotRecognizedException ex) {
            System.err.println("WARNING: problem while setting JAXP validating property on SAXParser... XML validation will not be done: " + ex.getMessage());
            _canUseSchemaValidation = false;
            return _factory.newSAXParser();
        }
    }

    public static void parse(URL xmlURL, URL schema, DefaultHandler handler) throws SAXException, IOException, ParserConfigurationException {
    	parse(xmlURL, schema, handler, null);
    }
    
    public static void parse(URL xmlURL, URL schema, DefaultHandler handler, LexicalHandler lHandler) throws SAXException, IOException, ParserConfigurationException {
       InputStream xmlStream = URLHandlerRegistry.getDefault().openStream(xmlURL);
       try {
           parse(xmlStream, schema, handler, lHandler);
       } finally {
           try {
               xmlStream.close();
           } catch (IOException e) {}
       }
    } 
    
    public static void parse(InputStream xmlStream, URL schema, DefaultHandler handler, LexicalHandler lHandler) throws SAXException, IOException, ParserConfigurationException {
        InputStream schemaStream = null;
        try {
            if (schema != null) {
                schemaStream = URLHandlerRegistry.getDefault().openStream(schema);
            }
            SAXParser parser = XMLHelper.newSAXParser(schema, schemaStream);
            
            if (lHandler != null) {
            	try {
            	} catch (SAXException ex) {
                    System.err.println("WARNING: problem while setting the lexical handler property on SAXParser: " + ex.getMessage());
            	}
            }
            
            parser.parse(xmlStream, handler);
        } finally {
            if (schemaStream != null) {
                try {
                    schemaStream.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public static boolean canUseSchemaValidation() {
        return _canUseSchemaValidation;
    }

}