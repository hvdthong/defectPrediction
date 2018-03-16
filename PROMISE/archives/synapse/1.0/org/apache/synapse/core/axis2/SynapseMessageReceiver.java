package org.apache.synapse.core.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.FaultHandler;
import org.apache.synapse.statistics.StatisticsStack;
import org.apache.synapse.statistics.impl.ProxyServiceStatisticsStack;

/**
 * This message receiver should be configured in the Axis2 configuration as the
 * default message receiver, which will handle all incoming messages through the
 * synapse mediation
 */
public class SynapseMessageReceiver implements MessageReceiver {

    private static final Log log = LogFactory.getLog(SynapseMessageReceiver.class);

    public void receive(org.apache.axis2.context.MessageContext mc) throws AxisFault {

        if (log.isDebugEnabled()) {
            log.debug("Synapse received a new message for message mediation...");
            log.debug("Received To: " + (mc.getTo() != null ? mc.getTo().getAddress() : "null"));
            log.debug("SOAPAction: " + (mc.getSoapAction() != null ? mc.getSoapAction() : "null"));
            log.debug("WSA-Action: " + (mc.getWSAAction() != null ? mc.getWSAAction() : "null"));
            String[] cids = mc.getAttachmentMap().getAllContentIDs();
            if (cids != null && cids.length > 0) {
                for (int i=0; i<cids.length; i++) {
                    log.debug("Attachment : " + cids[i]);
                }
            }
            log.debug("Body : \n" + mc.getEnvelope());
        }

        MessageContext synCtx = MessageContextCreatorForAxis2.getSynapseMessageContext(mc);
        try {
            StatisticsStack synapseServiceStack =
                    (StatisticsStack) synCtx.getProperty(
                            org.apache.synapse.Constants.SYNAPSESERVICE_STATISTICS_STACK);
            if (synapseServiceStack == null) {
                synapseServiceStack = new ProxyServiceStatisticsStack();
                synCtx.setProperty(org.apache.synapse.Constants.SYNAPSESERVICE_STATISTICS_STACK,
                        synapseServiceStack);
            }
            String name = "SynapseService";
            boolean isFault = synCtx.getEnvelope().getBody().hasFault();
            synapseServiceStack.put(name, System.currentTimeMillis(), !synCtx.isResponse(),
                    true, isFault);

            synCtx.getEnvironment().injectMessage(synCtx);
        } catch (SynapseException syne) {
            if (!synCtx.getFaultStack().isEmpty()) {
                ((FaultHandler) synCtx.getFaultStack().pop()).handleFault(synCtx, syne);
            } else {
                log.error("Synapse encountered an exception, " +
                        "No error handlers found - [Message Dropped]\n" + syne.getMessage());
            }
        }
    }
}
