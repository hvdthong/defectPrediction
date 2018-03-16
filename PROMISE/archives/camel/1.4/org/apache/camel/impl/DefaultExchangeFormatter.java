package org.apache.camel.impl;

import org.apache.camel.Exchange;
import org.apache.camel.processor.interceptor.ExchangeFormatter;

/**
 * A default {@link ExchangeFormatter} which just uses the {@link org.apache.camel.Exchange#toString()} method
 *
 * @version $Revision: 673954 $
 */
public class DefaultExchangeFormatter implements ExchangeFormatter {
    protected static final DefaultExchangeFormatter INSTANCE = new DefaultExchangeFormatter();

    public static DefaultExchangeFormatter getInstance() {
        return INSTANCE;
    }

    public Object format(Exchange exchange) {
        return exchange;
    }
}
