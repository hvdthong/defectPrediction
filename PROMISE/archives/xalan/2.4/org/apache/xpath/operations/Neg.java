package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.XPathContext;

/**
 * The unary '-' operation expression executer.
 */
public class Neg extends UnaryOperation
{

  /**
   * Apply the operation to two operands, and return the result.
   *
   *
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject right) throws javax.xml.transform.TransformerException
  {
    return new XNumber(-right.num());
  }
  
  /**
   * Evaluate this operation directly to a double.
   *
   * @param xctxt The runtime execution context.
   *
   * @return The result of the operation as a double.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    return -(m_right.num(xctxt));
  }

}
