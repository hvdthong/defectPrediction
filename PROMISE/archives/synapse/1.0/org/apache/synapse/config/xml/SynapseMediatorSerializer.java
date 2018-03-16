package org.apache.synapse.config.xml;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.base.SynapseMediator;

/**
 * <pre>
 * &lt;rules&gt;
 *   mediator+
 * &lt;rules&gt;
 * </pre>
 */
public class SynapseMediatorSerializer extends AbstractListMediatorSerializer
     {

    private static final Log log = LogFactory.getLog(SynapseMediatorSerializer.class);

    public OMElement serializeMediator(OMElement parent, Mediator m) {

        if (!(m instanceof SynapseMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }

        SynapseMediator mediator = (SynapseMediator) m;
        OMElement rules = fac.createOMElement("rules", synNS);
        finalizeSerialization(rules,mediator);

        serializeChildren(rules, mediator.getList());

        if (parent != null) {
            parent.addChild(rules);
        }
        return rules;
    }

    public String getMediatorClassName() {
        return SynapseMediator.class.getName();
    }

    private void handleException(String msg) {
        log.error(msg);
        throw new SynapseException(msg);
    }

}
