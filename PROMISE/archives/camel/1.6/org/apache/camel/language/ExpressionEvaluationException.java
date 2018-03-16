package org.apache.camel.language;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.RuntimeCamelException;

/**
 * An exception thrown if evaluation of the expression failed.
 *
 * @version $Revision: 659760 $
 */
public class ExpressionEvaluationException extends RuntimeCamelException {
    private final Expression<Exchange> expression;
    private final Exchange exchange;

    public ExpressionEvaluationException(Expression<Exchange> expression, Exchange exchange, Throwable cause) {
        super(cause);
        this.expression = expression;
        this.exchange = exchange;
    }

    public Expression<Exchange> getExpression() {
        return expression;
    }

    public Exchange getExchange() {
        return exchange;
    }
    
}
