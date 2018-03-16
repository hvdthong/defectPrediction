package org.apache.synapse.core.axis2;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.Constants;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.endpoints.utils.EndpointDefinition;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.statistics.StatisticsCollector;
import org.apache.synapse.statistics.StatisticsUtils;


/**
 * <p> This is the Axis2 implementation of the MessageContext
 */
public class Axis2SynapseEnvironment implements SynapseEnvironment {

    private static final Log log = LogFactory.getLog(Axis2SynapseEnvironment.class);

    private SynapseConfiguration synapseConfig;
    /**
     * The StatisticsCollector object
     */
    private StatisticsCollector statisticsCollector;

    public Axis2SynapseEnvironment() {
    }

    public Axis2SynapseEnvironment(ConfigurationContext cfgCtx,
                                   SynapseConfiguration synapseConfig) {
        this.synapseConfig = synapseConfig;
    }

    public void injectMessage(final MessageContext synCtx) {
        synCtx.setEnvironment(this);
        if (synCtx.isResponse()) {
            StatisticsUtils.processEndPointStatistics(synCtx);
            StatisticsUtils.processProxyServiceStatistics(synCtx);
            StatisticsUtils.processSequenceStatistics(synCtx);
        }

        if (synCtx.getProperty(Constants.PROXY_SERVICE) != null) {

            if (synCtx.getConfiguration().getProxyService((String) synCtx.getProperty(
                    Constants.PROXY_SERVICE)).getTargetOutSequence() != null) {

                String sequenceName = synCtx.getConfiguration().getProxyService((String) synCtx.
                        getProperty(Constants.PROXY_SERVICE)).getTargetOutSequence();
                Mediator outSequence = synCtx.getSequence(sequenceName);

                if (outSequence != null) {
                    log.debug("Using the sequence named " + sequenceName
                            + " for the outgoing message mediation of the proxy service "
                            + synCtx.getProperty(Constants.PROXY_SERVICE));
                    outSequence.mediate(synCtx);
                } else {
                    log.error("Unable to find the out-sequence " +
                            "specified by the name " + sequenceName);
                    throw new SynapseException("Unable to find the " +
                            "out-sequence specified by the name " + sequenceName);
                }

            } else if (synCtx.getConfiguration().getProxyService((String) synCtx.getProperty(
                    Constants.PROXY_SERVICE)).getTargetInLineOutSequence() != null) {

                log.debug("Using the anonymous out-sequence specified in the proxy service "
                        + synCtx.getProperty(Constants.PROXY_SERVICE)
                        + " for outgoing message mediation");
                synCtx.getConfiguration().getProxyService((String) synCtx.getProperty(
                        Constants.PROXY_SERVICE)).getTargetInLineOutSequence().mediate(synCtx);
            } else {

                log.debug("Proxy service " + synCtx.getProperty(Constants.PROXY_SERVICE)
                        + " does not specifies an out-sequence - sending the response back");
                Axis2Sender.sendBack(synCtx);
            }

        } else {
            synCtx.getMainSequence().mediate(synCtx);
        }
    }

    public void send(EndpointDefinition endpoint, MessageContext synCtx) {
        if (synCtx.isResponse())
            Axis2Sender.sendBack(synCtx);
        else
            Axis2Sender.sendOn(endpoint, synCtx);
    }

    public MessageContext createMessageContext() {
        org.apache.axis2.context.MessageContext axis2MC
                = new org.apache.axis2.context.MessageContext();
        MessageContext mc = new Axis2MessageContext(axis2MC, synapseConfig, this);
        return mc;
    }

    /**
     * This method returns the StatisticsCollector
     *
     * @return Retruns the StatisticsCollector
     */
    public StatisticsCollector getStatisticsCollector() {
        return statisticsCollector;
    }

    /**
     * To set the StatisticsCollector
     *
     * @param collector
     */
    public void setStatisticsCollector(StatisticsCollector collector) {
        this.statisticsCollector = collector;
    }

}
