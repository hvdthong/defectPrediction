package org.apache.camel.builder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * A builder to disable the use of an error handler so that any exceptions are thrown.
 * This not recommended in general, the
 * if you are unsure; however it can be useful sometimes to disable an error handler inside a complex route
 * so that exceptions bubble up to the parent {@link Processor}
 *
 * @version $Revision: 564295 $
 */
public class NoErrorHandlerBuilder extends ErrorHandlerBuilderSupport {
    public ErrorHandlerBuilder copy() {
        return this;
    }

    public Processor createErrorHandler(Processor processor) {
        return processor;
    }
}
