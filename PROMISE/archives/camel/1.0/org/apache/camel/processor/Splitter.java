package org.apache.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.converter.ObjectConverter;
import org.apache.camel.impl.ServiceSupport;
import static org.apache.camel.util.ObjectHelper.notNull;
import org.apache.camel.util.ServiceHelper;

import java.util.Iterator;

/**
 * where an expression is evaluated to iterate through each of the parts of a message and then each part is then send to some endpoint.
 *
 * @version $Revision: 534145 $
 */
public class Splitter extends ServiceSupport implements Processor {
    private final Processor processor;
    private final Expression expression;

    public Splitter(Processor destination, Expression expression) {
        this.processor = destination;
        this.expression = expression;
        notNull(destination, "destination");
        notNull(expression, "expression");
    }

    @Override
    public String toString() {
        return "Splitter[on: " + expression + " to: " + processor + "]";
    }

    public void process(Exchange exchange) throws Exception {
        Object value = expression.evaluate(exchange);
        Iterator iter = ObjectConverter.iterator(value);
        while (iter.hasNext()) {
            Object part = iter.next();
            Exchange newExchange = exchange.copy();
            newExchange.getIn().setBody(part);
            processor.process(newExchange);
        }
    }

    protected void doStart() throws Exception {
        ServiceHelper.startServices(processor);
    }

    protected void doStop() throws Exception {
        ServiceHelper.stopServices(processor);
    }
}
