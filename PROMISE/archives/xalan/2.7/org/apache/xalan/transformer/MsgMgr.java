package org.apache.xalan.transformer;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;

import org.w3c.dom.Node;

/**
 * This class will manage error messages, warning messages, and other types of
 * message events.
 */
public class MsgMgr
{

  /**
   * Create a message manager object.
   *
   * @param transformer non transformer instance
   */
  public MsgMgr(TransformerImpl transformer)
  {
    m_transformer = transformer;
  }

  /** Transformer instance          */
  private TransformerImpl m_transformer;

  /**
   * Warn the user of a problem.
   * This is public for access by extensions.
   *
   * @param msg The message text to issue
   * @param terminate Flag indicating whether to terminate this process
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public void message(SourceLocator srcLctr, String msg, boolean terminate) throws TransformerException
  {

    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
    {
      errHandler.warning(new TransformerException(msg, srcLctr));
    }
    else
    {
      if (terminate)
        throw new TransformerException(msg, srcLctr);
      else
        System.out.println(msg);
    }
  }

  /**
   * Warn the user of a problem.
   *
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void warn(SourceLocator srcLctr, String msg) throws TransformerException
  {
    warn(srcLctr, null, null, msg, null);
  }

  /**
   * Warn the user of a problem.
   *
   * @param msg Message text to issue
   * @param args Arguments to pass to the message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void warn(SourceLocator srcLctr, String msg, Object[] args) throws TransformerException
  {
    warn(srcLctr, null, null, msg, args);
  }

  /**
   * Warn the user of a problem.
   *
   * 
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void warn(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg)
          throws TransformerException
  {
    warn(srcLctr, styleNode, sourceNode, msg, null);
  }

  /**
   * Warn the user of a problem.
   *
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @param args Arguments to pass to the message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void warn(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg, Object args[])
          throws TransformerException
  {

    String formattedMsg = XSLMessages.createWarning(msg, args);
    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.warning(new TransformerException(formattedMsg, srcLctr));
    else
      System.out.println(formattedMsg);
  }

  /* This method is not properly i18nized. We need to use the following method
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   *
  public void error(SourceLocator srcLctr, String msg) throws TransformerException
  {

    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.fatalError(new TransformerException(msg, srcLctr));
    else
      throw new TransformerException(msg, srcLctr);
  }

 * @xsl.usage internal
 */

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void error(SourceLocator srcLctr, String msg) throws TransformerException
  {
    error(srcLctr, null, null, msg, null);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @param args Arguments to be passed to the message 
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void error(SourceLocator srcLctr, String msg, Object[] args) throws TransformerException
  {
    error(srcLctr, null, null, msg, args);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @param e Exception to throw
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void error(SourceLocator srcLctr, String msg, Exception e) throws TransformerException
  {
    error(srcLctr, msg, null, e);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Message text to issue
   * @param args Arguments to use in message
   * @param e Exception to throw
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void error(SourceLocator srcLctr, String msg, Object args[], Exception e) throws TransformerException
  {

    String formattedMsg = XSLMessages.createMessage(msg, args);

    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.fatalError(new TransformerException(formattedMsg, srcLctr));
    else
      throw new TransformerException(formattedMsg, srcLctr);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void error(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg)
          throws TransformerException
  {
    error(srcLctr, styleNode, sourceNode, msg, null);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param styleNode Stylesheet node
   * @param sourceNode Source tree node
   * @param msg Message text to issue
   * @param args Arguments to use in message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   * @xsl.usage internal
   */
  public void error(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg, Object args[])
          throws TransformerException
  {

    String formattedMsg = XSLMessages.createMessage(msg, args);

    ErrorListener errHandler = m_transformer.getErrorListener();

    if (null != errHandler)
      errHandler.fatalError(new TransformerException(formattedMsg, srcLctr));
    else
      throw new TransformerException(formattedMsg, srcLctr);
  }
}
