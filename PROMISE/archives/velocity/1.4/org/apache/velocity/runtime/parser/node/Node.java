package org.apache.velocity.runtime.parser.node;

import java.io.Writer;
import java.io.IOException;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.Token;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 *  All AST nodes must implement this interface.  It provides basic
 *  machinery for constructing the parent and child relationships
 *  between nodes. 
 */

public interface Node
{

    /**
     *  This method is called after the node has been made the current
     *  node.  It indicates that child nodes can now be added to it. 
     */
    public void jjtOpen();

    /** 
     *  This method is called after all the child nodes have been
     *  added. 
     */
    public void jjtClose();

    /** 
     *  This pair of methods are used to inform the node of its
     *  parent. 
     */
    public void jjtSetParent(Node n);
    public Node jjtGetParent();

    /** 
     *  This method tells the node to add its argument to the node's
     *   list of children.  
     */
    public void jjtAddChild(Node n, int i);

    /** 
     *  This method returns a child node.  The children are numbered
     *  from zero, left to right. 
     */
    public Node jjtGetChild(int i);

    /** Return the number of children the node has. */
    public int jjtGetNumChildren();

    /** Accept the visitor. **/
    public Object jjtAccept(ParserVisitor visitor, Object data);

    public Object childrenAccept(ParserVisitor visitor, Object data);

    public Token getFirstToken();
    public Token getLastToken();
    public int getType();

    public Object init( InternalContextAdapter context, Object data) throws Exception;

    public boolean evaluate( InternalContextAdapter context)
        throws MethodInvocationException;

    public Object value( InternalContextAdapter context)
        throws MethodInvocationException;

    public boolean render( InternalContextAdapter context, Writer writer)
        throws IOException,MethodInvocationException, ParseErrorException, ResourceNotFoundException;

    public Object execute(Object o, InternalContextAdapter context)
      throws MethodInvocationException;

    public void setInfo(int info);
    public int getInfo();

    public String literal();
    public void setInvalid();
    public boolean isInvalid();
    public int getLine();
    public int getColumn();
}
