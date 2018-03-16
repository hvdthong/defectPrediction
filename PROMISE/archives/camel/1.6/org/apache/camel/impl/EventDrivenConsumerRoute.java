package org.apache.camel.impl;

import java.util.List;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.Service;

/**
 * A {@link Route} which starts with an
 *
 * @version $Revision: 702954 $
 */
public class EventDrivenConsumerRoute<E extends Exchange> extends Route<E> {
    private Processor processor;

    public EventDrivenConsumerRoute(Endpoint endpoint, Processor processor) {
        super(endpoint);
        this.processor = processor;
    }

    @Override
    public String toString() {
        return "EventDrivenConsumerRoute[" + getEndpoint() + " -> " + processor + "]";
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    /**
     * Factory method to lazily create the complete list of services required for this route
     * such as adding the processor or consumer
     */
    @Override
    protected void addServices(List<Service> services) throws Exception {
        Endpoint<E> endpoint = getEndpoint();
        Consumer<E> consumer = endpoint.createConsumer(processor);
        if (consumer != null) {
            services.add(consumer);
        }
        Processor processor = getProcessor();
        if (processor instanceof Service) {
            services.add((Service)processor);
        }
    }
}
