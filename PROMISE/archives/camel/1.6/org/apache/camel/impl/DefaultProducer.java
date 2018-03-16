package org.apache.camel.impl;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;

/**
 * A default implementation of @{link Producer} for implementation inheritence
 *
 * @version $Revision: 640438 $
 */
public abstract class DefaultProducer<E extends Exchange> extends ServiceSupport implements Producer<E> {
    private Endpoint<E> endpoint;

    public DefaultProducer(Endpoint<E> endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String toString() {
        return "Producer[" + endpoint.getEndpointUri() + "]";
    }

    public Endpoint<E> getEndpoint() {
        return endpoint;
    }

    public E createExchange() {
        return endpoint.createExchange();
    }

    public E createExchange(ExchangePattern pattern) {
        return endpoint.createExchange(pattern);
    }

    public E createExchange(E exchange) {
        return endpoint.createExchange(exchange);
    }

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception {
    }
}
