package org.apache.camel.rest.util;



import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONJAXBContext;

import org.apache.camel.model.RouteType;
import org.apache.camel.model.RoutesType;
import org.apache.camel.rest.model.EndpointLink;
import org.apache.camel.rest.model.Endpoints;

/**
 * @version $Revision: 700513 $
 */
@Provider
public final class JAXBContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext context;

    public JAXBContextResolver() throws Exception {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(JSONJAXBContext.JSON_ROOT_UNWRAPPING, Boolean.TRUE);
        props.put(JSONJAXBContext.JSON_NON_STRINGS, "[\"number\"]");

        this.context = new JSONJAXBContext(getJaxbClasses(), props);
    }

    protected Class[] getJaxbClasses() {
        return new Class[]{RoutesType.class, RouteType.class,
                           Endpoints.class, EndpointLink.class};
    }

    public JAXBContext getContext(Class<?> objectType) {
        String name = objectType.getPackage().getName();
        if (name.startsWith("org.apache.camel.model") || name.startsWith("org.apache.camel.rest.model")) {
            return context;
        }
        return null;
    }
}
