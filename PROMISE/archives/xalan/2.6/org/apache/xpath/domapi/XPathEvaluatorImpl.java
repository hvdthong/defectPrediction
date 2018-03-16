package org.apache.xpath.domapi;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.res.XPATHErrorResources;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;

/**
 *
 * The class provides an implementation of XPathEvaluator according 
 * to the DOM L3 XPath Specification, Working Draft 28, March 2002.
 *
 * 
 * </p>The evaluation of XPath expressions is provided by 
 * <code>XPathEvaluator</code>, which will provide evaluation of XPath 1.0 
 * expressions with no specialized extension functions or variables. It is 
 * expected that the <code>XPathEvaluator</code> interface will be 
 * implemented on the same object which implements the <code>Document</code> 
 * interface in an implementation which supports the XPath DOM module. 
 * <code>XPathEvaluator</code> implementations may be available from other 
 * sources that may provide support for special extension functions or 
 * variables which are not defined in this specification.</p>
 * 
 * @see org.w3c.dom.xpath.XPathEvaluator
 * 
 * @xsl.usage experimental
 */
public class XPathEvaluatorImpl implements XPathEvaluator {

	/**
	 * This prefix resolver is created whenever null is passed to the 
	 * evaluate method.  Its purpose is to satisfy the DOM L3 XPath API
	 * requirement that if a null prefix resolver is used, an exception 
	 * should only be thrown when an attempt is made to resolve a prefix.
	 */
	class DummyPrefixResolver implements PrefixResolver {

		/**
		 * Constructor for DummyPrefixResolver.
		 */
		public DummyPrefixResolver() {}
			
		/**
		 * @exception DOMException
    	 *   NAMESPACE_ERR: Always throws this exceptionn
		 *
		 * @see org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(String, Node)
		 */
		public String getNamespaceForPrefix(String prefix, Node context) {
            String fmsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_NULL_RESOLVER, null);       
		}

		/**
		 * @exception DOMException
    	 *   NAMESPACE_ERR: Always throws this exceptionn
    	 * 
		 * @see org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(String)
		 */
		public String getNamespaceForPrefix(String prefix) {
			return getNamespaceForPrefix(prefix,null);
		}

		/**
		 * @see org.apache.xml.utils.PrefixResolver#handlesNullPrefixes()
		 */
		public boolean handlesNullPrefixes() {
			return false;
		}

		/**
		 * @see org.apache.xml.utils.PrefixResolver#getBaseIdentifier()
		 */
		public String getBaseIdentifier() {
			return null;
		}

	}

    /**
     * The document to be searched to parallel the case where the XPathEvaluator
     * is obtained by casting a Document.
     */  
    private Document m_doc = null;
    
	/**
	 * Constructor for XPathEvaluatorImpl.
	 */
	public XPathEvaluatorImpl() {
		super();
	}
    
     /**
     * Constructor for XPathEvaluatorImpl.
     * 
     * @param doc The document to be searched, to parallel the case where''
     *            the XPathEvaluator is obtained by casting the document.
     */
    public XPathEvaluatorImpl(Document doc) {
        m_doc = doc;
    }

	/**
     * Creates a parsed XPath expression with resolved namespaces. This is 
     * useful when an expression will be reused in an application since it 
     * makes it possible to compile the expression string into a more 
     * efficient internal form and preresolve all namespace prefixes which 
     * occur within the expression.
     * 
     * @param expression The XPath expression string to be parsed.
     * @param resolver The <code>resolver</code> permits translation of 
     *   prefixes within the XPath expression into appropriate namespace URIs
     *   . If this is specified as <code>null</code>, any namespace prefix 
     *   within the expression will result in <code>DOMException</code> 
     *   being thrown with the code <code>NAMESPACE_ERR</code>.
     * @return The compiled form of the XPath expression.
     * @exception XPathException
     *   INVALID_EXPRESSION_ERR: Raised if the expression is not legal 
     *   according to the rules of the <code>XPathEvaluator</code>i
     * @exception DOMException
     *   NAMESPACE_ERR: Raised if the expression contains namespace prefixes 
     *   which cannot be resolved by the specified 
     *   <code>XPathNSResolver</code>.	
     *  
	 * @see org.w3c.dom.xpath.XPathEvaluator#createExpression(String, XPathNSResolver)
	 */
	public XPathExpression createExpression(
		String expression,
		XPathNSResolver resolver)
		throws XPathException, DOMException {
		
		try {
			
			XPath xpath =  new XPath(expression,null,
			     ((null == resolver) ? new DummyPrefixResolver() : ((PrefixResolver)resolver)), 
			      XPath.SELECT);
                  
            return new XPathExpressionImpl(xpath, m_doc);
			      
		} catch (TransformerException e) {
			throw new DOMException(XPathException.INVALID_EXPRESSION_ERR,e.getMessageAndLocation());
		}
	}

	/**
     * Adapts any DOM node to resolve namespaces so that an XPath expression 
     * can be easily evaluated relative to the context of the node where it 
     * appeared within the document. This adapter works like the DOM Level 3 
     * method <code>lookupNamespaceURI</code> on nodes in resolving the 
     * namespaceURI from a given prefix using the current information available 
     * in the node's hierarchy at the time lookupNamespaceURI is called, also 
     * correctly resolving the implicit xml prefix.
     *
     * @param nodeResolver The node to be used as a context for namespace 
     *   resolution.
     * @return <code>XPathNSResolver</code> which resolves namespaces with 
     *   respect to the definitions in scope for a specified node.
     *  
	 * @see org.w3c.dom.xpath.XPathEvaluator#createNSResolver(Node)
	 */
	public XPathNSResolver createNSResolver(Node nodeResolver) {
	
		return new XPathNSResolverImpl((nodeResolver.getNodeType() == Node.DOCUMENT_NODE)
	           ? ((Document) nodeResolver).getDocumentElement() : nodeResolver);
	}

	/**
     * Evaluates an XPath expression string and returns a result of the 
     * specified type if possible.
     * 
     * @param expression The XPath expression string to be parsed and 
     *   evaluated.
     * @param contextNode The <code>context</code> is context node for the 
     *   evaluation of this XPath expression. If the XPathEvaluator was 
     *   obtained by casting the <code>Document</code> then this must be 
     *   owned by the same document and must be a <code>Document</code>, 
     *   <code>Element</code>, <code>Attribute</code>, <code>Text</code>, 
     *   <code>CDATASection</code>, <code>Comment</code>, 
     *   <code>ProcessingInstruction</code>, or <code>XPathNamespace</code> 
     *   node. If the context node is a <code>Text</code> or a 
     *   <code>CDATASection</code>, then the context is interpreted as the 
     *   whole logical text node as seen by XPath, unless the node is empty 
     *   in which case it may not serve as the XPath context.
     * @param resolver The <code>resolver</code> permits translation of 
     *   prefixes within the XPath expression into appropriate namespace URIs
     *   . If this is specified as <code>null</code>, any namespace prefix 
     *   within the expression will result in <code>DOMException</code> 
     *   being thrown with the code <code>NAMESPACE_ERR</code>.
     * @param type If a specific <code>type</code> is specified, then the 
     *   result will be coerced to return the specified type relying on 
     *   XPath type conversions and fail if the desired coercion is not 
     *   possible. This must be one of the type codes of 
     *   <code>XPathResult</code>.
     * @param result The <code>result</code> specifies a specific result 
     *   object which may be reused and returned by this method. If this is 
     *   specified as <code>null</code>or the implementation does not reuse 
     *   the specified result, a new result object will be constructed and 
     *   returned.For XPath 1.0 results, this object will be of type 
     *   <code>XPathResult</code>.
     * @return The result of the evaluation of the XPath expression.For XPath 
     *   1.0 results, this object will be of type <code>XPathResult</code>.
     * @exception XPathException
     *   INVALID_EXPRESSION_ERR: Raised if the expression is not legal 
     *   according to the rules of the <code>XPathEvaluator</code>i
     *   <br>TYPE_ERR: Raised if the result cannot be converted to return the 
     *   specified type.
     * @exception DOMException
     *   NAMESPACE_ERR: Raised if the expression contains namespace prefixes 
     *   which cannot be resolved by the specified 
     *   <code>XPathNSResolver</code>.
     *   <br>WRONG_DOCUMENT_ERR: The Node is from a document that is not 
     *   supported by this XPathEvaluator.
     *   <br>NOT_SUPPORTED_ERR: The Node is not a type permitted as an XPath 
     *   context node.
	 * 
	 * @see org.w3c.dom.xpath.XPathEvaluator#evaluate(String, Node, XPathNSResolver, short, XPathResult)
	 */
	public Object evaluate(
		String expression,
		Node contextNode,
		XPathNSResolver resolver,
		short type,
		Object result)
		throws XPathException, DOMException {
			
		XPathExpression xpathExpression = createExpression(expression, resolver);
		
		return	xpathExpression.evaluate(contextNode, type, result);
	}

}
