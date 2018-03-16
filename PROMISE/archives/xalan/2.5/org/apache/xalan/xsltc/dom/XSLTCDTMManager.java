package org.apache.xalan.xsltc.dom;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMException;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.res.XMLErrorResources;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xalan.xsltc.trax.DOM2SAX;

import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * The default implementation for the DTMManager.
 */
public class XSLTCDTMManager extends DTMManagerDefault
{
	
    /** The default class name to use as the manager. */
    private static String defaultClassName =
        "org.apache.xalan.xsltc.dom.XSLTCDTMManager";

    /** Set this to true if you want a dump of the DTM after creation */
    private static final boolean DUMPTREE = false;
  
    /** Set this to true if you want basic diagnostics */
    private static final boolean DEBUG = false;


    /**
     * Constructor DTMManagerDefault
     *
     */
    public XSLTCDTMManager()
    {
        super();
    } 
  
    /**
     * Obtain a new instance of a <code>DTMManager</code>.
     * This static method creates a new factory instance.
     * The current implementation just returns a new XSLTCDTMManager instance.
     *
     * %REVISIT% Do we need the factory lookup mechanism for class loading here?
     * Factory lookup will add a lot of complexity and also has a performance hit.
     * There is currently no need to do it unless it is proved to be useful.
     */
    public static XSLTCDTMManager newInstance()
    {
        XSLTCDTMManager factoryImpl = new XSLTCDTMManager();
        return factoryImpl;
    } 

    /**
     * Get an instance of a DTM, loaded with the content from the
     * specified source.  If the unique flag is true, a new instance will
     * always be returned.  Otherwise it is up to the DTMManager to return a
     * new instance or an instance that it already created and may be being used
     * by someone else.
     * (I think more parameters will need to be added for error handling, and
     * entity resolution).
     *
     * @param source the specification of the source object.
     * @param unique true if the returned DTM must be unique, probably because it
     * is going to be mutated.
     * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
     *                         be null.
     * @param incremental true if the DTM should be built incrementally, if
     *                    possible.
     * @param doIndexing true if the caller considers it worth it to use
     *                   indexing schemes.
     *
     * @return a non-null DTM reference.
     */
    public DTM getDTM(Source source, boolean unique,
                      DTMWSFilter whiteSpaceFilter, boolean incremental,
                      boolean doIndexing)
    {
        return getDTM(source, unique, whiteSpaceFilter, incremental,
                      doIndexing, false, 0, true);
    }

    /**
     * Get an instance of a DTM, loaded with the content from the
     * specified source.  If the unique flag is true, a new instance will
     * always be returned.  Otherwise it is up to the DTMManager to return a
     * new instance or an instance that it already created and may be being used
     * by someone else.
     * (I think more parameters will need to be added for error handling, and
     * entity resolution).
     *
     * @param source the specification of the source object.
     * @param unique true if the returned DTM must be unique, probably because it
     * is going to be mutated.
     * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
     *                         be null.
     * @param incremental true if the DTM should be built incrementally, if
     *                    possible.
     * @param doIndexing true if the caller considers it worth it to use
     *                   indexing schemes.
     * @param buildIdIndex true if the id index table should be built.
     * 
     * @return a non-null DTM reference.
     */
    public DTM getDTM(Source source, boolean unique,
                      DTMWSFilter whiteSpaceFilter, boolean incremental,
                      boolean doIndexing, boolean buildIdIndex)
    {
        return getDTM(source, unique, whiteSpaceFilter, incremental,
                      doIndexing, false, 0, buildIdIndex);
    }
  
    /**
     * Get an instance of a DTM, loaded with the content from the
     * specified source.  If the unique flag is true, a new instance will
     * always be returned.  Otherwise it is up to the DTMManager to return a
     * new instance or an instance that it already created and may be being used
     * by someone else.
     * (I think more parameters will need to be added for error handling, and
     * entity resolution).
     *
     * @param source the specification of the source object.
     * @param unique true if the returned DTM must be unique, probably because it
     * is going to be mutated.
     * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
     *                         be null.
     * @param incremental true if the DTM should be built incrementally, if
     *                    possible.
     * @param doIndexing true if the caller considers it worth it to use
     *                   indexing schemes.
     * @param hasUserReader true if <code>source</code> is a
     *                      <code>SAXSource</code> object that has an
     *                      <code>XMLReader</code>, that was specified by the
     *                      user.
     * @param size  Specifies initial size of tables that represent the DTM
     * @param buildIdIndex true if the id index table should be built.
     *
     * @return a non-null DTM reference.
     */
    public DTM getDTM(Source source, boolean unique,
                      DTMWSFilter whiteSpaceFilter, boolean incremental,
                      boolean doIndexing, boolean hasUserReader, int size,
                      boolean buildIdIndex)
    {
        if(DEBUG && null != source) {
            System.out.println("Starting "+
			 (unique ? "UNIQUE" : "shared")+
			 " source: "+source.getSystemId());
        }

        int dtmPos = getFirstFreeDTMID();
        int documentID = dtmPos << IDENT_DTM_NODE_BITS;

        if ((null != source) && source instanceof DOMSource)
        {
            final DOMSource domsrc = (DOMSource) source;
            final org.w3c.dom.Node node = domsrc.getNode();
            final DOM2SAX dom2sax = new DOM2SAX(node);
      
            SAXImpl dtm;

            if (size <= 0) {
                dtm = new SAXImpl(this, source, documentID,
                          whiteSpaceFilter, null, doIndexing, buildIdIndex);
            } else {
                dtm = new SAXImpl(this, source, documentID,
                          whiteSpaceFilter, null, doIndexing, size, buildIdIndex);
            }
      
            dtm.setDocumentURI(source.getSystemId());

            addDTM(dtm, dtmPos, 0);
      
            dom2sax.setContentHandler(dtm);
      
            try {
                dom2sax.parse();
            }
            catch (RuntimeException re) {
                throw re;
            }
            catch (Exception e) {
                throw new org.apache.xml.utils.WrappedRuntimeException(e);
            }
      
            return dtm;
        }
        else
        {
            boolean isSAXSource = (null != source)
                                  ? (source instanceof SAXSource) : true;
            boolean isStreamSource = (null != source)
                                  ? (source instanceof StreamSource) : false;

            if (isSAXSource || isStreamSource) {
                XMLReader reader;
                InputSource xmlSource;

                if (null == source) {
                    xmlSource = null;
                    reader = null;
                }
                else {
                    reader = getXMLReader(source);
                    xmlSource = SAXSource.sourceToInputSource(source);

                    String urlOfSource = xmlSource.getSystemId();

                    if (null != urlOfSource) {
                        try {
                            urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
                        }
                        catch (Exception e) {
                            System.err.println("Can not absolutize URL: " + urlOfSource);
                        }

                        xmlSource.setSystemId(urlOfSource);
                    }
                }

                SAXImpl dtm;
                if (size <= 0) {
                    dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter,
                                      null, doIndexing, buildIdIndex);
                } else {
                    dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter,
                                      null, doIndexing, size, buildIdIndex);
                }

                addDTM(dtm, dtmPos, 0);

                if (null == reader) {
                    return dtm;
                }

                reader.setContentHandler(dtm.getBuilder());
                
                if (!hasUserReader || null == reader.getDTDHandler()) {
                    reader.setDTDHandler(dtm);
                }
                
                if(!hasUserReader || null == reader.getErrorHandler()) {
                    reader.setErrorHandler(dtm);
                }

                try {
                }
                catch (SAXNotRecognizedException e){}
                catch (SAXNotSupportedException e){}

                try {
                    reader.parse(xmlSource);
                }
                catch (RuntimeException re) {
                    throw re;
                }
                catch (Exception e) {
                    throw new org.apache.xml.utils.WrappedRuntimeException(e);
                }

                if (DUMPTREE) {
                    System.out.println("Dumping SAX2DOM");
                    dtm.dumpDTM(System.err);
                }

                return dtm;
            }
            else {
                throw new DTMException(XMLMessages.createXMLMessage(XMLErrorResources.ER_NOT_SUPPORTED, new Object[]{source}));
            }
        }
    }
}
