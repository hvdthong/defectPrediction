package org.apache.camel.component.event;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.processor.loadbalancer.LoadBalancer;
import org.apache.camel.processor.loadbalancer.TopicLoadBalancer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

/**
 * for working with Spring ApplicationEvents
 *
 * @version $Revision: 655776 $
 */
public class EventEndpoint extends DefaultEndpoint<Exchange> implements ApplicationContextAware {
    private LoadBalancer loadBalancer;
    private ApplicationContext applicationContext;

    public EventEndpoint(String endpointUri, EventComponent component) {
        super(endpointUri, component);
        this.applicationContext = component.getApplicationContext();
    }

    public EventEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public boolean isSingleton() {
        return true;
    }

    public Producer<Exchange> createProducer() throws Exception {
        return new DefaultProducer<Exchange>(this) {
            public void process(Exchange exchange) throws Exception {
                ApplicationEvent event = toApplicationEvent(exchange);
                getApplicationContext().publishEvent(event);
            }
        };
    }

    public EventConsumer createConsumer(Processor processor) throws Exception {
        return new EventConsumer(this, processor);
    }

    public void onApplicationEvent(ApplicationEvent event) {
        Exchange exchange = createExchange();
        exchange.getIn().setBody(event);
        try {
            getLoadBalancer().process(exchange);
        } catch (Exception e) {
            throw new RuntimeCamelException(e);
        }
    }

    public LoadBalancer getLoadBalancer() {
        if (loadBalancer == null) {
            loadBalancer = createLoadBalancer();
        }
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public synchronized void consumerStarted(EventConsumer consumer) {
        getLoadBalancer().addProcessor(consumer.getProcessor());
    }

    public synchronized void consumerStopped(EventConsumer consumer) {
        getLoadBalancer().removeProcessor(consumer.getProcessor());
    }

    protected LoadBalancer createLoadBalancer() {
        return new TopicLoadBalancer();
    }

    protected ApplicationEvent toApplicationEvent(Exchange exchange) {
        ApplicationEvent event = exchange.getIn().getBody(ApplicationEvent.class);
        if (event == null) {
            event = new CamelEvent(this, exchange);
        }
        return event;
    }

}
