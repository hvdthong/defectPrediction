package org.apache.xalan.xsltc.cmdline;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.dom.SAXImpl;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.Constants;
import org.apache.xalan.xsltc.runtime.Parameter;
import org.apache.xalan.xsltc.runtime.TransletLoader;
import org.apache.xalan.xsltc.runtime.output.TransletOutputHandlerFactory;
import org.apache.xml.serializer.SerializationHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

final public class Transform {

    private SerializationHandler _handler;

    private String  _fileName;
    private String  _className;
    private String  _jarFileSrc;
    private boolean _isJarFileSpecified = false;
    private Vector  _params = null;
    private boolean _uri, _debug;
    private int     _iterations;

    private static boolean _allowExit = true;

    public Transform(String className, String fileName,
		     boolean uri, boolean debug, int iterations) {
	_fileName = fileName;
	_className = className;
	_uri = uri;
	_debug = debug;
	_iterations = iterations;
  }
  
   public String getFileName(){return _fileName;}
   public String getClassName(){return _className;}

    public void setParameters(Vector params) {
	_params = params;
    }

    private void setJarFileInputSrc(boolean flag,  String jarFile) {
	_isJarFileSpecified = flag;
	_jarFileSrc = jarFile;	
    }

    private Class loadTranslet(String name) throws ClassNotFoundException {
	try {
	    return Class.forName(name);
	}
	catch (ClassNotFoundException e) {
	}

	TransletLoader loader = new TransletLoader();
	return loader.loadTranslet(name);
    }

    private void doTransform() {
	try {
	    
	    final Class clazz = loadTranslet(_className);
	    final AbstractTranslet translet = (AbstractTranslet)clazz.newInstance();

	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    try {
		factory.setFeature(Constants.NAMESPACE_FEATURE,true);
	    }
	    catch (Exception e) {
		factory.setNamespaceAware(true);
	    }
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();

            XSLTCDTMManager dtmManager = XSLTCDTMManager.newInstance();

            final SAXImpl dom = (SAXImpl)dtmManager.getDTM(
                             new SAXSource(reader, new InputSource(_fileName)),
                             false, null, true, false, translet.hasIdCall());

	    dom.setDocumentURI(_fileName);
            translet.prepassDocument(dom);

	    int n = _params.size();
	    for (int i = 0; i < n; i++) {
		Parameter param = (Parameter) _params.elementAt(i);
		translet.addParameter(param._name, param._value);
	    }

	    TransletOutputHandlerFactory tohFactory = 
		TransletOutputHandlerFactory.newInstance();
	    tohFactory.setOutputType(TransletOutputHandlerFactory.STREAM);
	    tohFactory.setEncoding(translet._encoding);
	    tohFactory.setOutputMethod(translet._method);

	    if (_iterations == -1) {
		translet.transform(dom, tohFactory.getSerializationHandler());
	    }
	    else if (_iterations > 0) {
		long mm = System.currentTimeMillis();
		for (int i = 0; i < _iterations; i++) {
		    translet.transform(dom,
				       tohFactory.getSerializationHandler());
		}
		mm = System.currentTimeMillis() - mm;

		System.err.println("\n<!--");
		System.err.println("  transform  = "
                                   + (((double) mm) / ((double) _iterations))
                                   + " ms");
		System.err.println("  throughput = "
                                   + (1000.0 / (((double) mm)
                                                 / ((double) _iterations)))
                                   + " tps");
		System.err.println("-->");
	    }
	}
	catch (TransletException e) {
	    if (_debug) e.printStackTrace();
	    System.err.println(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY)+
			       e.getMessage());
	    if (_allowExit) System.exit(-1);	    
	}
	catch (RuntimeException e) {
	    if (_debug) e.printStackTrace();
	    System.err.println(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY)+
			       e.getMessage());
	    if (_allowExit) System.exit(-1);
	}
	catch (FileNotFoundException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.FILE_NOT_FOUND_ERR, _fileName);
	    System.err.println(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY)+
			       err.toString());
	    if (_allowExit) System.exit(-1);
	}
	catch (MalformedURLException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_URI_ERR, _fileName);
	    System.err.println(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY)+
			       err.toString());
	    if (_allowExit) System.exit(-1);
	}
	catch (ClassNotFoundException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err= new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR,_className);
	    System.err.println(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY)+
			       err.toString());
	    if (_allowExit) System.exit(-1);
	}
        catch (UnknownHostException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_URI_ERR, _fileName);
	    System.err.println(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY)+
			       err.toString());
	    if (_allowExit) System.exit(-1);
        }
	catch (SAXException e) {
	    Exception ex = e.getException();
	    if (_debug) {
		if (ex != null) ex.printStackTrace();
		e.printStackTrace();
	    }
	    System.err.print(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY));
	    if (ex != null)
		System.err.println(ex.getMessage());
	    else
		System.err.println(e.getMessage());
	    if (_allowExit) System.exit(-1);
	}
	catch (Exception e) {
	    if (_debug) e.printStackTrace();
	    System.err.println(new ErrorMsg(ErrorMsg.RUNTIME_ERROR_KEY)+
			       e.getMessage());
	    if (_allowExit) System.exit(-1);
	}
    }

    public static void printUsage() {
	System.err.println(new ErrorMsg(ErrorMsg.TRANSFORM_USAGE_STR));
	if (_allowExit) System.exit(-1);
    }

    public static void main(String[] args) {
	try {
	    if (args.length > 0) {
		int i;
		int iterations = -1;
		boolean uri = false, debug = false;
		boolean isJarFileSpecified = false;
		String  jarFile = null;

		for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
		    if (args[i].equals("-u")) {
			uri = true;
		    }
		    else if (args[i].equals("-x")) {
			debug = true;
		    }
		    else if (args[i].equals("-s")) {
			_allowExit = false;
		    }
		    else if (args[i].equals("-j")) {
			isJarFileSpecified = true;	
			jarFile = args[++i];
		    }
		    else if (args[i].equals("-n")) {
			try {
			    iterations = Integer.parseInt(args[++i]);
			}
			catch (NumberFormatException e) {
			}
		    }
		    else {
			printUsage();
		    }
		}

		if (args.length - i < 2) printUsage();

		Transform handler = new Transform(args[i+1], args[i], uri,
		    debug, iterations);
		handler.setJarFileInputSrc(isJarFileSpecified,	jarFile);

		Vector params = new Vector();
		for (i += 2; i < args.length; i++) {
		    final int equal = args[i].indexOf('=');
		    if (equal > 0) {
			final String name  = args[i].substring(0, equal);
			final String value = args[i].substring(equal+1);
			params.addElement(new Parameter(name, value));
		    }
		    else {
			printUsage();
		    }
		}

		if (i == args.length) {
		    handler.setParameters(params);
		    handler.doTransform();
		    if (_allowExit) System.exit(0);
		}
	    } else {
		printUsage();
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
