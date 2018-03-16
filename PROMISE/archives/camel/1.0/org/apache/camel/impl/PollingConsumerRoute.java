package org.apache.camel.impl;

import org.apache.camel.Route;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Service;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;

import java.util.List;

/**
 * A {@link Route} which starts with a
 *
 * @version $Revision: 1.1 $
 */
public class PollingConsumerRoute<E extends Exchange> extends Route<E> {
    private Processor processor;

    public PollingConsumerRoute(Endpoint endpoint, Processor processor) {
        super(endpoint);
        this.processor = processor;
    }

    @Override
    public String toString() {
        return "PollingConsumerRoute[" + getEndpoint() + " -> " + processor + "]";
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
    protected void addServices(List<Service> services) throws Exception {
        Processor processor = getProcessor();
        if (processor instanceof Service) {
            Service service = (Service) processor;
            services.add(service);
        }
        Endpoint<E> endpoint = getEndpoint();
        PollingConsumer<E> consumer = endpoint.createPollingConsumer();
        if (consumer != null) {
            services.add(consumer);
        }
    }
}
