package org.apache.camel.component.bean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.camel.Endpoint;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;

/**
 * An {@link java.lang.reflect.InvocationHandler} which invokes a
 * message exchange on a camel {@link Endpoint}
 *
 * @version $Revision: 655516 $
 */
public class CamelInvocationHandler implements InvocationHandler {
    private final Endpoint endpoint;
    private final Producer producer;

    public CamelInvocationHandler(Endpoint endpoint, Producer producer) {
        this.endpoint = endpoint;
        this.producer = producer;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BeanInvocation invocation = new BeanInvocation(method, args);
        BeanExchange exchange = new BeanExchange(endpoint.getCamelContext(), ExchangePattern.InOut);
        exchange.setInvocation(invocation);

        producer.process(exchange);
        Throwable fault = exchange.getException();
        if (fault != null) {
            throw new InvocationTargetException(fault);
        }
        return exchange.getOut(true).getBody();
    }
}

