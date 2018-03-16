package org.apache.xalan.xsltc.compiler;

import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.transform.OutputKeys;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.serializer.Encodings;

final class Output extends TopLevelElement {


    private String  _version;
    private String  _method;
    private String  _encoding;
    private boolean _omitHeader = false;
    private String  _standalone;
    private String  _doctypePublic;
    private String  _doctypeSystem;
    private String  _cdata;
    private boolean _indent = false;
    private String  _mediaType;
    private String  _cdataToMerge;

    private boolean _disabled = false;

    private final static String STRING_SIG = "Ljava/lang/String;";
    private final static String XML_VERSION = "1.0";
    private final static String HTML_VERSION = "4.0";

    /**
     * Displays the contents of this element (for debugging)
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Output " + _method);
    }

    /**
     * Disables this <xsl:output> element in case where there are some other
     * <xsl:output> element (from a different imported/included stylesheet)
     * with higher precedence.
     */
    public void disable() {
	_disabled = true;
    }

    public boolean enabled() {
	return !_disabled;
    }

    public String getCdata() {
	return _cdata;
    }

    public void mergeCdata(String cdata) {
	_cdataToMerge = cdata;
    }

    /**
     * Scans the attribute list for the xsl:output instruction
     */
    public void parseContents(Parser parser) {
	final Properties outputProperties = new Properties();

	parser.setOutput(this);

	if (_disabled) return;

	String attrib = null;

	_version = getAttribute("version");
	if (_version == null || _version.equals(Constants.EMPTYSTRING)) {
	    _version = null;
	}
	else {
	    outputProperties.setProperty(OutputKeys.VERSION, _version);
	}

	_method = getAttribute("method");
	if (_method.equals(Constants.EMPTYSTRING)) {
	    _method = null;
	}
	if (_method != null) {
	    _method = _method.toLowerCase();
	    outputProperties.setProperty(OutputKeys.METHOD, _method);
	}

	_encoding = getAttribute("encoding");
	if (_encoding.equals(Constants.EMPTYSTRING)) {
	    _encoding = null;
	}
	else {
	    try {
        String canonicalEncoding;
        canonicalEncoding = Encodings.convertMime2JavaEncoding(_encoding);
		OutputStreamWriter writer =
		    new OutputStreamWriter(System.out, canonicalEncoding); 
	    }
	    catch (java.io.UnsupportedEncodingException e) {
		ErrorMsg msg = new ErrorMsg(ErrorMsg.UNSUPPORTED_ENCODING,
					    _encoding, this);
		parser.reportError(Constants.WARNING, msg);
	    }
	    outputProperties.setProperty(OutputKeys.ENCODING, _encoding);
	}

	attrib = getAttribute("omit-xml-declaration");
	if (attrib != null && !attrib.equals(Constants.EMPTYSTRING)) {
	    if (attrib.equals("yes")) {
		_omitHeader = true;
	    }
	    outputProperties.setProperty(OutputKeys.OMIT_XML_DECLARATION, attrib);
	}

	_standalone = getAttribute("standalone");
	if (_standalone.equals(Constants.EMPTYSTRING)) {
	    _standalone = null;
	}
	else {
	    outputProperties.setProperty(OutputKeys.STANDALONE, _standalone);
	}

	_doctypeSystem = getAttribute("doctype-system");
	if (_doctypeSystem.equals(Constants.EMPTYSTRING)) {
	    _doctypeSystem = null;
	}
	else {
	    outputProperties.setProperty(OutputKeys.DOCTYPE_SYSTEM, _doctypeSystem);
	}


	_doctypePublic = getAttribute("doctype-public");
	if (_doctypePublic.equals(Constants.EMPTYSTRING)) {
	    _doctypePublic = null;
	}
	else {
	    outputProperties.setProperty(OutputKeys.DOCTYPE_PUBLIC, _doctypePublic);
	}

	_cdata = getAttribute("cdata-section-elements");
	if (_cdata != null && _cdata.equals(Constants.EMPTYSTRING)) {
	    _cdata = null;
	}
	else {
	    StringBuffer expandedNames = new StringBuffer();
	    StringTokenizer tokens = new StringTokenizer(_cdata);

	    while (tokens.hasMoreTokens()) {
		expandedNames.append(
		   parser.getQName(tokens.nextToken()).toString()).append(' ');
	    }
	    _cdata = expandedNames.toString();
	    if (_cdataToMerge != null) {
		_cdata = _cdata + _cdataToMerge;
	    }
	    outputProperties.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, 
		_cdata);
	}

	attrib = getAttribute("indent");
	if (attrib != null && !attrib.equals(EMPTYSTRING)) {
	    if (attrib.equals("yes")) {
		_indent = true;
	    }
	    outputProperties.setProperty(OutputKeys.INDENT, attrib);
	}
	else if (_method != null && _method.equals("html")) {
	    _indent = true;
	}

	_mediaType = getAttribute("media-type");
	if (_mediaType.equals(Constants.EMPTYSTRING)) {
	    _mediaType = null;
	}
	else {
	    outputProperties.setProperty(OutputKeys.MEDIA_TYPE, _mediaType);
	}

	if (_method != null) {
	    if (_method.equals("html")) {
		if (_version == null) {
		    _version = HTML_VERSION;
		}
		if (_mediaType == null) {
		    _mediaType = "text/html";
		}
	    }
	    else if (_method.equals("text")) {
		if (_mediaType == null) {
		    _mediaType = "text/plain";
		}
	    }
	}

	parser.getCurrentStylesheet().setOutputProperties(outputProperties);
    }

    /**
     * Compile code that passes the information in this <xsl:output> element
     * to the appropriate fields in the translet
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	if (_disabled) return;

	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();

	int field = 0;
        il.append(classGen.loadTranslet());

	if ((_version != null) && (!_version.equals(XML_VERSION))) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_version", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _version));
	    il.append(new PUTFIELD(field));
	}

	if (_method != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_method", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _method));
	    il.append(new PUTFIELD(field));
	}

	if (_encoding != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_encoding", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _encoding));
	    il.append(new PUTFIELD(field));
	}

	if (_omitHeader) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_omitHeader", "Z");
	    il.append(DUP);
	    il.append(new PUSH(cpg, _omitHeader));
	    il.append(new PUTFIELD(field));
	}

	if (_standalone != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_standalone", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _standalone));
	    il.append(new PUTFIELD(field));
	}

	field = cpg.addFieldref(TRANSLET_CLASS,"_doctypeSystem",STRING_SIG);
	il.append(DUP);
	il.append(new PUSH(cpg, _doctypeSystem));
	il.append(new PUTFIELD(field));
	field = cpg.addFieldref(TRANSLET_CLASS,"_doctypePublic",STRING_SIG);
	il.append(DUP);
	il.append(new PUSH(cpg, _doctypePublic));
	il.append(new PUTFIELD(field));
	
	if (_mediaType != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_mediaType", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _mediaType));
	    il.append(new PUTFIELD(field));
	}

	if (_indent) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_indent", "Z");
	    il.append(DUP);
	    il.append(new PUSH(cpg, _indent));
	    il.append(new PUTFIELD(field));
	}

	if (_cdata != null) {
	    int index = cpg.addMethodref(TRANSLET_CLASS,
					 "addCdataElement",
					 "(Ljava/lang/String;)V");

	    StringTokenizer tokens = new StringTokenizer(_cdata);
	    while (tokens.hasMoreTokens()) {
		il.append(DUP);
		il.append(new PUSH(cpg, tokens.nextToken()));
		il.append(new INVOKEVIRTUAL(index));
	    }
	}
    }

}
