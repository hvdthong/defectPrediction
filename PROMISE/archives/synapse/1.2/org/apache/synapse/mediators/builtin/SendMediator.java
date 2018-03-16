package org.apache.synapse.mediators.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.endpoints.Endpoint;
import org.apache.synapse.endpoints.SALoadbalanceEndpoint;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.clustering.ClusterManager;

import java.util.List;
import java.util.Iterator;

/**
 * SendMediator sends a message using specified semantics. If it contains an endpoint it will
 * send the message to that endpoint. Once a message is sent to the endpoint further sending
 * behaviors are completely governed by that endpoint. If there is no endpoint available,
 * SendMediator will send the message to the implicitly stated destination.
 */
public class SendMediator extends AbstractMediator {

    private Endpoint endpoint = null;

    /**
     * This will call the send method on the messages with implicit message parameters
     * or else if there is an endpoint, with that endpoint parameters
     *
     * @param synCtx the current message to be sent
     * @return false always as this is a leaf mediator
     */
    public boolean mediate(MessageContext synCtx) {

        boolean traceOn = isTraceOn(synCtx);
        boolean traceOrDebugOn = isTraceOrDebugOn(traceOn);

        if (traceOrDebugOn) {
            traceOrDebug(traceOn, "Start : Send mediator");

            if (traceOn && trace.isTraceEnabled()) {
                trace.trace("Message : " + synCtx.getEnvelope());
            }
        }

        if (synCtx.isResponse()) {

            Axis2MessageContext axis2MsgCtx = (Axis2MessageContext) synCtx;
            OperationContext opCtx = axis2MsgCtx.getAxis2MessageContext().getOperationContext();

            boolean isClusteringEnable = false;

            org.apache.axis2.context.MessageContext axisMC =
                    axis2MsgCtx.getAxis2MessageContext();
            ConfigurationContext cc = axisMC.getConfigurationContext();


            ClusterManager clusterManager = cc.getAxisConfiguration().getClusterManager();
            if (clusterManager != null &&
                    clusterManager.getContextManager() != null) {
                isClusteringEnable = true;
            }

            if (isClusteringEnable) {
                Object epNames = opCtx.getPropertyNonReplicable(SALoadbalanceEndpoint.ENDPOINT_NAME_LIST);
                if (epNames != null && epNames instanceof List) {

                    List epNameList = (List) epNames;
                    Object obj = epNameList.remove(0);
                    if (obj != null && obj instanceof String) {
                        Object rootEPObj = opCtx.getPropertyNonReplicable(
                                SALoadbalanceEndpoint.ROOT_ENDPOINT);

                        if (rootEPObj != null && rootEPObj instanceof Endpoint) {
                            String name = ((Endpoint) rootEPObj).getName();

                            if (name != null && name.equals(obj)) {
                                Endpoint rootEP = ((Endpoint) rootEPObj);

                                if (rootEP instanceof SALoadbalanceEndpoint) {
                                    SALoadbalanceEndpoint salEP = (SALoadbalanceEndpoint) rootEP;
                                    salEP.updateSession(synCtx, epNameList,
                                            isClusteringEnable);
                                }
                            }
                        }

                    }
                    opCtx.setProperty(SALoadbalanceEndpoint.ENDPOINT_NAME_LIST, epNames);
                }

            } else {
                Object o = opCtx.getProperty(SALoadbalanceEndpoint.ENDPOINT_LIST);
                if (o != null && o instanceof List) {
                    List epList = (List) o;
                    Object e = epList.remove(0);

                    if (e != null) {
                        if (e instanceof SALoadbalanceEndpoint) {
                            SALoadbalanceEndpoint salEP = (SALoadbalanceEndpoint) e;
                            salEP.updateSession(synCtx, epList, isClusteringEnable);
                        }
                    }
                }
            }

        }

        if (endpoint == null) {

            if (traceOrDebugOn) {
                StringBuffer sb = new StringBuffer();
                sb.append("Sending " + (synCtx.isResponse() ? "response" : "request")
                        + " message using implicit message properties..");
                sb.append("\nSending To: " + (synCtx.getTo() != null ?
                        synCtx.getTo().getAddress() : "null"));
                sb.append("\nSOAPAction: " + (synCtx.getWSAAction() != null ?
                        synCtx.getWSAAction() : "null"));
                traceOrDebug(traceOn, sb.toString());
            }

            if (traceOn && trace.isTraceEnabled()) {
                trace.trace("Envelope : " + synCtx.getEnvelope());
            }
            synCtx.getEnvironment().send(null, synCtx);

        } else {
            endpoint.send(synCtx);
        }

        if (traceOrDebugOn) {
            traceOrDebug(traceOn, "End : Send mediator");
        }

        return true;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }
}
