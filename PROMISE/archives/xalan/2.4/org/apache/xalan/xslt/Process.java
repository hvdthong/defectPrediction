package org.apache.xalan.xslt;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xalan.processor.XSLProcessorVersion;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.trace.PrintTraceListener;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.XalanProperties;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.res.XResourceBundle;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <meta name="usage" content="general"/>
 * The main() method handles the Xalan command-line interface.
 */
public class Process
{

  /**
   * Prints argument options.
   *
   * @param resbundle Resource bundle
   */
  protected static void printArgOptions(ResourceBundle resbundle)
  {




    
    System.out.println(resbundle.getString("optionMEDIA"));
    System.out.println(resbundle.getString("optionFLAVOR"));
    System.out.println(resbundle.getString("optionDIAG"));
    System.out.println(resbundle.getString("optionINCREMENTAL"));
    System.out.println(resbundle.getString("optionNOOPTIMIMIZE"));
    System.out.println(resbundle.getString("optionRL"));
  }
  
  /**
   * Command line interface to transform an XML document according to
   * the instructions found in an XSL stylesheet.  
   * <p>The Process class provides basic functionality for 
   * performing transformations from the command line.  To see a 
   * list of arguments supported, call with zero arguments.</p>
   * <p>To set stylesheet parameters from the command line, use 
   * <code>-PARAM name expression</code>. If you want to set the 
   * parameter to a string value, simply pass the string value 
   * as-is, and it will be interpreted as a string.  (Note: if 
   * the value has spaces in it, you may need to quote it depending 
   * on your shell environment).</p>
   *
   * @param argv Input parameters from command line
   */
  public static void main(String argv[])
  {
    
    boolean doStackDumpOnError = false;
    boolean setQuietMode = false;
    boolean doDiag = false;


    /**
     * The default diagnostic writer...
     */
    java.io.PrintWriter diagnosticsWriter = new PrintWriter(System.err, true);
    java.io.PrintWriter dumpWriter = diagnosticsWriter;
    ResourceBundle resbundle =
      (XSLMessages.loadResourceBundle(
        org.apache.xml.utils.res.XResourceBundle.ERROR_RESOURCES));
    String flavor = "s2s";

    if (argv.length < 1)
    {
      printArgOptions(resbundle);
    }
    else
    {
      TransformerFactory tfactory;

      try
      {
        tfactory = TransformerFactory.newInstance();
      }
      catch (TransformerFactoryConfigurationError pfe)
      {
        pfe.printStackTrace(dumpWriter);
        diagnosticsWriter.println(
          XSLMessages.createMessage(


        doExit(-1);
      }

      boolean formatOutput = false;
      boolean useSourceLocation = false;
      String inFileName = null;
      String outFileName = null;
      String dumpFileName = null;
      String xslFileName = null;
      String treedumpFileName = null;
      PrintTraceListener tracer = null;
      String outputType = null;
      String media = null;
      Vector params = new Vector();
      boolean quietConflictWarnings = false;
      URIResolver uriResolver = null;
      EntityResolver entityResolver = null;
      ContentHandler contentHandler = null;
      int recursionLimit=-1;

      for (int i = 0; i < argv.length; i++)
      {
        if ("-TT".equalsIgnoreCase(argv[i]))
        {
          if (null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);

          tracer.m_traceTemplates = true;

        }
        else if ("-TG".equalsIgnoreCase(argv[i]))
        {
          if (null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);

          tracer.m_traceGeneration = true;

        }
        else if ("-TS".equalsIgnoreCase(argv[i]))
        {
          if (null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);

          tracer.m_traceSelection = true;

        }
        else if ("-TTC".equalsIgnoreCase(argv[i]))
        {
          if (null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);

          tracer.m_traceElements = true;

        }
        else if ("-INDENT".equalsIgnoreCase(argv[i]))
        {
          int indentAmount;

          if (((i + 1) < argv.length) && (argv[i + 1].charAt(0) != '-'))
          {
            indentAmount = Integer.parseInt(argv[++i]);
          }
          else
          {
            indentAmount = 0;
          }

        }
        else if ("-IN".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
            inFileName = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }
        else if ("-MEDIA".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
            media = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }
        else if ("-OUT".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
            outFileName = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }
        else if ("-XSL".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
            xslFileName = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }
        else if ("-FLAVOR".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            flavor = argv[++i];
          }
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }
        else if ("-PARAM".equalsIgnoreCase(argv[i]))
        {
          if (i + 2 < argv.length)
          {
            String name = argv[++i];

            params.addElement(name);

            String expression = argv[++i];

            params.addElement(expression);
          }
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }
        {
          if (i + 1 < argv.length)
            treedumpFileName = argv[++i];
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }
        {
          formatOutput = true;
        }
        else if ("-E".equalsIgnoreCase(argv[i]))
        {

        }
        else if ("-V".equalsIgnoreCase(argv[i]))
        {
                                    + XSLProcessorVersion.S_VERSION + ", " +

          /* xmlProcessorLiaison.getParserDescription()+ */
        }
        else if ("-QC".equalsIgnoreCase(argv[i]))
        {
          quietConflictWarnings = true;
        }
        else if ("-Q".equalsIgnoreCase(argv[i]))
        {
          setQuietMode = true;
        }

        /*
        else if("-VALIDATE".equalsIgnoreCase(argv[i]))
        {
          String shouldValidate;
          if(((i+1) < argv.length) && (argv[i+1].charAt(0) != '-'))
          {
            shouldValidate = argv[++i];
          }
          else
          {
            shouldValidate = "yes";
          }

        }
        */
        else if ("-DIAG".equalsIgnoreCase(argv[i]))
        {
          doDiag = true;
        }
        else if ("-XML".equalsIgnoreCase(argv[i]))
        {
          outputType = "xml";
        }
        else if ("-TEXT".equalsIgnoreCase(argv[i]))
        {
          outputType = "text";
        }
        else if ("-HTML".equalsIgnoreCase(argv[i]))
        {
          outputType = "html";
        }
        else if ("-EDUMP".equalsIgnoreCase(argv[i]))
        {
          doStackDumpOnError = true;

          if (((i + 1) < argv.length) && (argv[i + 1].charAt(0) != '-'))
          {
            dumpFileName = argv[++i];
          }
        }
        else if ("-URIRESOLVER".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            try
            {
              uriResolver =
                (URIResolver) Class.forName(argv[++i]).newInstance();

              tfactory.setURIResolver(uriResolver);
            }
            catch (Exception cnfe)
            {
              System.err.println(
                XSLMessages.createMessage(
                  XSLTErrorResources.ER_CLASS_NOT_FOUND_FOR_OPTION,
                  new Object[]{ "-URIResolver" }));
              doExit(-1);
            }
          }
          else
          {
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
            doExit(-1);
          }
        }
        else if ("-ENTITYRESOLVER".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            try
            {
              entityResolver =
                (EntityResolver) Class.forName(argv[++i]).newInstance();
            }
            catch (Exception cnfe)
            {
              System.err.println(
                XSLMessages.createMessage(
                  XSLTErrorResources.ER_CLASS_NOT_FOUND_FOR_OPTION,
                  new Object[]{ "-EntityResolver" }));
              doExit(-1);
            }
          }
          else
          {
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
            doExit(-1);
          }
        }
        else if ("-CONTENTHANDLER".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
          {
            try
            {
              contentHandler =
                (ContentHandler) Class.forName(argv[++i]).newInstance();
            }
            catch (Exception cnfe)
            {
              System.err.println(
                XSLMessages.createMessage(
                  XSLTErrorResources.ER_CLASS_NOT_FOUND_FOR_OPTION,
                  new Object[]{ "-ContentHandler" }));
              doExit(-1);
            }
          }
          else
          {
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
            doExit(-1);
          }
        }
        else if ("-L".equalsIgnoreCase(argv[i]))
          useSourceLocation = true;
        else if ("-INCREMENTAL".equalsIgnoreCase(argv[i]))
        {
          tfactory.setAttribute
             java.lang.Boolean.TRUE);
        }
        else if ("-NOOPTIMIZE".equalsIgnoreCase(argv[i]))
        {
          tfactory.setAttribute
             java.lang.Boolean.FALSE);
	}
        else if ("-RL".equalsIgnoreCase(argv[i]))
        {
          if (i + 1 < argv.length)
            recursionLimit = Integer.parseInt(argv[++i]);
          else
            System.err.println(
              XSLMessages.createMessage(
                XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION,
        }

        else
          System.err.println(
            XSLMessages.createMessage(
      }

      try
      {
        long start = System.currentTimeMillis();

        if (null != dumpFileName)
        {
          dumpWriter = new PrintWriter(new FileWriter(dumpFileName));
        }

        Templates stylesheet = null;

        if (null != xslFileName)
        {
          if (flavor.equals("d2d"))
          {

            DocumentBuilderFactory dfactory =
              DocumentBuilderFactory.newInstance();

            dfactory.setNamespaceAware(true);

            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
            Node xslDOM = docBuilder.parse(new InputSource(xslFileName));

            stylesheet = tfactory.newTemplates(new DOMSource(xslDOM,
                    xslFileName));
          }
          else
          {
            stylesheet = tfactory.newTemplates(new StreamSource(xslFileName));
          }
        }

        PrintWriter resultWriter;
        StreamResult strResult;

        if (null != outFileName)
        {
          strResult = new StreamResult(new FileOutputStream(outFileName));
          strResult.setSystemId(outFileName);
        }
        else
        {
          strResult = new StreamResult(System.out);
        }

        SAXTransformerFactory stf = (SAXTransformerFactory) tfactory;
        
        if (useSourceLocation)
           stf.setAttribute(XalanProperties.SOURCE_LOCATION, Boolean.TRUE);        

        if (null == stylesheet)
        {
          Source source =
            stf.getAssociatedStylesheet(new StreamSource(inFileName), media,
                                        null, null);

          if (null != source)
            stylesheet = tfactory.newTemplates(source);
          else
          {
            if (null != media)
            else
          }
        }

        if (null != stylesheet)
        {
          Transformer transformer = flavor.equals("th") ? null : stylesheet.newTransformer();

          if (null != outputType)
          {
            transformer.setOutputProperty(OutputKeys.METHOD, outputType);
          }

          if (transformer instanceof TransformerImpl)
          {
            TransformerImpl impl = ((TransformerImpl) transformer);
            TraceManager tm = impl.getTraceManager();

            if (null != tracer)
              tm.addTraceListener(tracer);

            impl.setQuietConflictWarnings(quietConflictWarnings);

            if (useSourceLocation)
              impl.setProperty(XalanProperties.SOURCE_LOCATION, Boolean.TRUE);

	    if(recursionLimit>0)
	      impl.setRecursionLimit(recursionLimit);

          }

          int nParams = params.size();

          for (int i = 0; i < nParams; i += 2)
          {
            transformer.setParameter((String) params.elementAt(i),
                                     (String) params.elementAt(i + 1));
          }

          if (uriResolver != null)
            transformer.setURIResolver(uriResolver);

          if (null != inFileName)
          {
            if (flavor.equals("d2d"))
            {

              DocumentBuilderFactory dfactory =
                DocumentBuilderFactory.newInstance();

              dfactory.setCoalescing(true);
              dfactory.setNamespaceAware(true);

              DocumentBuilder docBuilder = dfactory.newDocumentBuilder();

              if (entityResolver != null)
                docBuilder.setEntityResolver(entityResolver);

              Node xmlDoc = docBuilder.parse(new InputSource(inFileName));
              Document doc = docBuilder.newDocument();
              org.w3c.dom.DocumentFragment outNode =
                doc.createDocumentFragment();

              transformer.transform(new DOMSource(xmlDoc, inFileName),
                                    new DOMResult(outNode));

              Transformer serializer = stf.newTransformer();
              Properties serializationProps =
                stylesheet.getOutputProperties();

              serializer.setOutputProperties(serializationProps);

              if (contentHandler != null)
              {
                SAXResult result = new SAXResult(contentHandler);

                serializer.transform(new DOMSource(outNode), result);
              }
              else
                serializer.transform(new DOMSource(outNode), strResult);
            }
            else if (flavor.equals("th"))
            {
              {

              XMLReader reader = null;

              try
              {
                javax.xml.parsers.SAXParserFactory factory =
                  javax.xml.parsers.SAXParserFactory.newInstance();

                factory.setNamespaceAware(true);

                javax.xml.parsers.SAXParser jaxpParser =
                  factory.newSAXParser();

                reader = jaxpParser.getXMLReader();
              }
              catch (javax.xml.parsers.ParserConfigurationException ex)
              {
                throw new org.xml.sax.SAXException(ex);
              }
              catch (javax.xml.parsers.FactoryConfigurationError ex1)
              {
                throw new org.xml.sax.SAXException(ex1.toString());
              }
              catch (NoSuchMethodError ex2){}
              catch (AbstractMethodError ame){}

              if (null == reader)
              {
                reader = XMLReaderFactory.createXMLReader();
              }
              
              stf.setAttribute(org.apache.xalan.processor.TransformerFactoryImpl.FEATURE_INCREMENTAL, 
                 Boolean.TRUE);
                 
              TransformerHandler th = stf.newTransformerHandler(stylesheet);
              
              reader.setContentHandler(th);
              reader.setDTDHandler(th);
              
              if(th instanceof org.xml.sax.ErrorHandler)
                reader.setErrorHandler((org.xml.sax.ErrorHandler)th);
              
              try
              {
                reader.setProperty(
              }
              catch (org.xml.sax.SAXNotRecognizedException e){}
              catch (org.xml.sax.SAXNotSupportedException e){}
              try
              {
                                  true);
              } catch (org.xml.sax.SAXException se) {}
        
              try
              {
                                  true);
              } catch (org.xml.sax.SAXException se) {}
              
              th.setResult(strResult);
              
              {
                reader.parse(new InputSource(inFileName));
              }
              
              


              }
            }
            else
            {
              if (entityResolver != null)
              {
                XMLReader reader = null;

                try
                {
                  javax.xml.parsers.SAXParserFactory factory =
                    javax.xml.parsers.SAXParserFactory.newInstance();

                  factory.setNamespaceAware(true);

                  javax.xml.parsers.SAXParser jaxpParser =
                    factory.newSAXParser();

                  reader = jaxpParser.getXMLReader();
                }
                catch (javax.xml.parsers.ParserConfigurationException ex)
                {
                  throw new org.xml.sax.SAXException(ex);
                }
                catch (javax.xml.parsers.FactoryConfigurationError ex1)
                {
                  throw new org.xml.sax.SAXException(ex1.toString());
                }
                catch (NoSuchMethodError ex2){}
                catch (AbstractMethodError ame){}

                if (null == reader)
                {
                  reader = XMLReaderFactory.createXMLReader();
                }

                reader.setEntityResolver(entityResolver);

                if (contentHandler != null)
                {
                  SAXResult result = new SAXResult(contentHandler);

                  transformer.transform(
                    new SAXSource(reader, new InputSource(inFileName)),
                    result);
                }
                else
                {
                  transformer.transform(
                    new SAXSource(reader, new InputSource(inFileName)),
                    strResult);
                }
              }
              else if (contentHandler != null)
              {
                SAXResult result = new SAXResult(contentHandler);

                transformer.transform(new StreamSource(inFileName), result);
              }
              else
              {
                transformer.transform(new StreamSource(inFileName),
                                      strResult);
              }
            }
          }
          else
          {
            StringReader reader =
              new StringReader("<?xml version=\"1.0\"?> <doc/>");

            transformer.transform(new StreamSource(reader), strResult);
          }
        }
        else
        {
          diagnosticsWriter.println(
            XSLMessages.createMessage(
          doExit(-1);
        }

        long stop = System.currentTimeMillis();
        long millisecondsDuration = stop - start;

        if (doDiag)
        {
        	Object[] msgArgs = new Object[]{ inFileName, xslFileName, new Long(millisecondsDuration) };
        	String msg = XSLMessages.createMessage("diagTiming", msgArgs);
        	diagnosticsWriter.println('\n');
          	diagnosticsWriter.println(msg);
        }
          
      }
      catch (Throwable throwable)
      {
        while (throwable
               instanceof org.apache.xml.utils.WrappedRuntimeException)
        {
          throwable =
            ((org.apache.xml.utils.WrappedRuntimeException) throwable).getException();
        }

        if ((throwable instanceof NullPointerException)
                || (throwable instanceof ClassCastException))
          doStackDumpOnError = true;

        diagnosticsWriter.println();

        if (doStackDumpOnError)
          throwable.printStackTrace(dumpWriter);
        else
        {
          DefaultErrorHandler.printLocation(diagnosticsWriter, throwable);
          diagnosticsWriter.println(
            XSLMessages.createMessage(XSLTErrorResources.ER_XSLT_ERROR, null)
            + " (" + throwable.getClass().getName() + "): "
            + throwable.getMessage());
        }

        if (null != dumpFileName)
        {
          dumpWriter.close();
        }

        doExit(-1);
      }

      if (null != dumpFileName)
      {
        dumpWriter.close();
      }

      if (null != diagnosticsWriter)
      {

      }

    }
  }
  
  /** It is _much_ easier to debug under VJ++ if I can set a single breakpoint 
   * before this blows itself out of the water...
   * (I keep checking this in, it keeps vanishing. Grr!)
   * */
  static void doExit(int i)
  {
          System.exit(i);
  }
}
