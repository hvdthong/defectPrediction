package org.apache.camel.impl;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.Injector;
import org.apache.camel.util.ObjectHelper;

/**
 * A simple implementation of {@link Injector} which just uses reflection to
 * instantiate new objects using their zero argument constructor. For more
 * complex implementations try the Spring or Guice implementations.
 * 
 * @version $Revision: 580248 $
 */
public class ReflectionInjector implements Injector {

    public <T> T newInstance(Class<T> type) {
        return ObjectHelper.newInstance(type);
    }
}
