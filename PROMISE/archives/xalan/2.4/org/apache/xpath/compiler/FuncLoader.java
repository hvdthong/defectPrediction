package org.apache.xpath.compiler;

import java.lang.Class;

import org.apache.xpath.res.XPATHErrorResources;

import org.w3c.dom.Node;

import java.util.Vector;

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.functions.Function;

/**
 * <meta name="usage" content="advanced"/>
 * Lazy load of functions into the function table as needed, so we don't 
 * have to load all the functions allowed in XPath and XSLT on startup.
 */
public class FuncLoader
{

  /** The function ID, which may correspond to one of the FUNC_XXX values 
   *  found in {@link org.apache.xpath.compiler.FunctionTable}, but may 
   *  be a value installed by an external module.  */
  private int m_funcID;

  /** The class name of the function.  Must not be null.   */
  private String m_funcName;

  /**
   * Get the local class name of the function class.  If function name does 
   * not have a '.' in it, it is assumed to be relative to 
   * 'org.apache.xpath.functions'.
   *
   * @return The class name of the {org.apache.xpath.functions.Function} class.
   */
  public String getName()
  {
    return m_funcName;
  }

  /**
   * Construct a function loader
   *
   * @param funcName The class name of the {org.apache.xpath.functions.Function} 
   *             class, which, if it does not have a '.' in it, is assumed to 
   *             be relative to 'org.apache.xpath.functions'. 
   * @param funcID  The function ID, which may correspond to one of the FUNC_XXX 
   *    values found in {@link org.apache.xpath.compiler.FunctionTable}, but may 
   *    be a value installed by an external module. 
   */
  public FuncLoader(String funcName, int funcID)
  {

    super();

    m_funcID = funcID;
    m_funcName = funcName;
  }

  /**
   * Get a Function instance that this instance is liaisoning for.
   *
   * @return non-null reference to Function derivative.
   *
   * @throws javax.xml.transform.TransformerException if ClassNotFoundException, 
   *    IllegalAccessException, or InstantiationException is thrown.
   */
  public Function getFunction() throws javax.xml.transform.TransformerException
  {

    try
    {
      Class function;

      if (m_funcName.indexOf(".") < 0)
      {

        String classname = "org.apache.xpath.functions." + m_funcName;

        function = Class.forName(classname);
      }
      else
        function = Class.forName(m_funcName);

      Function func = (Function) function.newInstance();

      return func;
    }
    catch (ClassNotFoundException e)
    {
      throw new javax.xml.transform.TransformerException(e);
    }
    catch (IllegalAccessException e)
    {
      throw new javax.xml.transform.TransformerException(e);
    }
    catch (InstantiationException e)
    {
      throw new javax.xml.transform.TransformerException(e);
    }
  }
}
