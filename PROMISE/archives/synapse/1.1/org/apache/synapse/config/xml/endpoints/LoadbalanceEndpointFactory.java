package org.apache.synapse.config.xml.endpoints;

import org.apache.synapse.endpoints.Endpoint;
import org.apache.synapse.endpoints.LoadbalanceEndpoint;
import org.apache.synapse.endpoints.algorithms.LoadbalanceAlgorithm;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.config.xml.endpoints.utils.LoadbalanceAlgorithmFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAttribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Creates LoadbalanceEndpoint using an XML configuration.
 *
 * <endpoint [name="name"]>
 *    <loadbalance policy="load balance algorithm">
 *       <endpoint>+
 *    </loadbalance>
 * </endpoint>
 */
public class LoadbalanceEndpointFactory implements EndpointFactory {

    private static Log log = LogFactory.getLog(LoadbalanceEndpointFactory.class);

    private static LoadbalanceEndpointFactory instance = new LoadbalanceEndpointFactory();

    private LoadbalanceEndpointFactory() {}

    public static LoadbalanceEndpointFactory getInstance() {
        return instance;
    }

    public Endpoint createEndpoint(OMElement epConfig, boolean anonymousEndpoint) {


        OMElement loadbalanceElement =  null;
        loadbalanceElement = epConfig.getFirstChildWithName
                (new QName(SynapseConstants.SYNAPSE_NAMESPACE, "loadbalance"));

        if(loadbalanceElement != null) {

            LoadbalanceEndpoint loadbalanceEndpoint = new LoadbalanceEndpoint();

            OMAttribute name = epConfig.getAttribute(new QName(
                    org.apache.synapse.config.xml.XMLConfigConstants.NULL_NAMESPACE, "name"));

            if (name != null) {
                loadbalanceEndpoint.setName(name.getAttributeValue());
            }

            ArrayList endpoints = getEndpoints(loadbalanceElement, loadbalanceEndpoint);
            loadbalanceEndpoint.setEndpoints(endpoints);

            LoadbalanceAlgorithm algorithm = LoadbalanceAlgorithmFactory.
                    createLoadbalanceAlgorithm(loadbalanceElement, endpoints);
            loadbalanceEndpoint.setAlgorithm(algorithm);

            String failover = loadbalanceElement.getAttributeValue(new QName("failover"));
            if (failover != null && failover.equalsIgnoreCase("false")) {
                loadbalanceEndpoint.setFailover(false);
            }

            return loadbalanceEndpoint;
        }

    }

    public Object getObjectFromOMNode(OMNode om) {
        if (om instanceof OMElement) {
            return createEndpoint((OMElement) om, false);
        } else {
            handleException("Invalid XML configuration for an Endpoint. OMElement expected");
        }
        return null;
    }

    private ArrayList getEndpoints(OMElement loadbalanceElement, Endpoint parent) {

        ArrayList endpoints = new ArrayList();
        Iterator iter = loadbalanceElement.getChildrenWithName
                (org.apache.synapse.config.xml.XMLConfigConstants.ENDPOINT_ELT);
        while (iter.hasNext()) {

            OMElement endptElem = (OMElement) iter.next();

            EndpointFactory epFac = EndpointAbstractFactory.getEndpointFactroy(endptElem);
            Endpoint endpoint = epFac.createEndpoint(endptElem, true);
            endpoint.setParentEndpoint(parent);
            endpoints.add(endpoint);
        }

        return endpoints;
    }

    private static void handleException(String msg) {
        log.error(msg);
        throw new SynapseException(msg);
    }

    private static void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new SynapseException(msg, e);
    }
}
