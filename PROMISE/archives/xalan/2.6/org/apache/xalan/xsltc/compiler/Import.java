package org.apache.xalan.xsltc.compiler;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;

import org.apache.xml.utils.SystemIDResolver;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Jacek Ambroziak
 * @author Morten Jorgensen
 * @author Erwin Bolwidt <ejb@klomp.org>
 * @author Gunnlaugur Briem <gthb@dimon.is>
 */
final class Import extends TopLevelElement {

    private Stylesheet _imported = null;

    public Stylesheet getImportedStylesheet() {
	return _imported;
    }

    public void parseContents(final Parser parser) {
	final XSLTC xsltc = parser.getXSLTC();
	final Stylesheet context = parser.getCurrentStylesheet();

	try {
	    String docToLoad = getAttribute("href");
	    if (context.checkForLoop(docToLoad)) {
		final ErrorMsg msg = new ErrorMsg(ErrorMsg.CIRCULAR_INCLUDE_ERR,
                                                  docToLoad, this);
		parser.reportError(Constants.FATAL, msg);
		return;
	    }

	    InputSource input = null;
	    XMLReader reader = null;
	    String currLoadedDoc = context.getSystemId();
	    SourceLoader loader = context.getSourceLoader();
            
	    if (loader != null) {
		input = loader.loadSource(docToLoad, currLoadedDoc, xsltc);
                if (input != null) {
                    docToLoad = input.getSystemId();
                    reader = xsltc.getXMLReader();
                }
	    }

            if (input == null) {
                docToLoad = SystemIDResolver.getAbsoluteURI(docToLoad, currLoadedDoc);
                input = new InputSource(docToLoad);
	    }

	    if (input == null) {
		final ErrorMsg msg = 
		    new ErrorMsg(ErrorMsg.FILE_NOT_FOUND_ERR, docToLoad, this);
		parser.reportError(Constants.FATAL, msg);
		return;
	    }
            
	    final SyntaxTreeNode root;
            if (reader != null) {
                root = parser.parse(reader,input);
            }
            else {
                root = parser.parse(input);
            }

	    if (root == null) return;
	    _imported = parser.makeStylesheet(root);
	    if (_imported == null) return;

	    _imported.setSourceLoader(loader);
	    _imported.setSystemId(docToLoad);
	    _imported.setParentStylesheet(context);
	    _imported.setImportingStylesheet(context);

	    final int currPrecedence = parser.getCurrentImportPrecedence();
	    final int nextPrecedence = parser.getNextImportPrecedence();
	    _imported.setImportPrecedence(currPrecedence);
	    context.setImportPrecedence(nextPrecedence);
	    parser.setCurrentStylesheet(_imported);
	    _imported.parseContents(parser);

	    final Enumeration elements = _imported.elements();
	    final Stylesheet topStylesheet = parser.getTopLevelStylesheet();
	    while (elements.hasMoreElements()) {
		final Object element = elements.nextElement();
		if (element instanceof TopLevelElement) {
		    if (element instanceof Variable) {
			topStylesheet.addVariable((Variable) element);
		    }
		    else if (element instanceof Param) {
			topStylesheet.addParam((Param) element);
		    }
		    else {
			topStylesheet.addElement((TopLevelElement) element);
		    }
		}
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally {
	    parser.setCurrentStylesheet(context);
	}
    }
    
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	return Type.Void;
    }
    
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
    }
}
