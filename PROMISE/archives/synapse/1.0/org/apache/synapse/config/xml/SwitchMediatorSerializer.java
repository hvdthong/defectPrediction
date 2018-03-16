package org.apache.synapse.config.xml;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.filters.SwitchMediator;

import java.util.Iterator;

/**
 * <pre>
 * &lt;switch source="xpath"&gt;
 *   &lt;case regex="string"&gt;
 *     mediator+
 *   &lt;/case&gt;+
 *   &lt;default&gt;
 *     mediator+
 *   &lt;/default&gt;?
 * &lt;/switch&gt;
 * </pre>
 */
public class SwitchMediatorSerializer extends AbstractMediatorSerializer {

    private static final Log log = LogFactory.getLog(SwitchMediatorSerializer.class);

    public OMElement serializeMediator(OMElement parent, Mediator m) {

        if (!(m instanceof SwitchMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }

        SwitchMediator mediator = (SwitchMediator) m;
        OMElement switchMed = fac.createOMElement("switch", synNS);
        finalizeSerialization(switchMed, mediator);

        if (mediator.getSource() != null) {
            switchMed.addAttribute(fac.createOMAttribute(
                    "source", nullNS, mediator.getSource().toString()));
            super.serializeNamespaces(switchMed, mediator.getSource());

        } else {
            handleException("Invalid switch mediator. Source required");
        }

        Iterator iter = mediator.getCases().iterator();
        while (iter.hasNext()) {
            OMElement caseElem = fac.createOMElement("case", synNS);
            SwitchCase aCase = ((SwitchCase) iter.next());
            if (aCase.getRegex() != null) {
                caseElem.addAttribute(fac.createOMAttribute(
                        "regex", nullNS, aCase.getRegex().pattern()));
            } else {
                handleException("Invalid switch case. Regex required");
            }
            AnonymousListMediator caseMediator = aCase.getCaseMediator();
            if (caseMediator != null) {
                AnonymousListMediatorSerializer.serializeAnonymousListMediator(
                        caseElem, caseMediator);
                switchMed.addChild(caseElem);
            }
        }
        SwitchCase defaultCase = mediator.getDefaultCase();
        if (defaultCase != null) {
            OMElement caseDefaultElem = fac.createOMElement("default", synNS);
            AnonymousListMediator caseDefaultMediator = defaultCase.getCaseMediator();
            if (caseDefaultMediator != null) {
                AnonymousListMediatorSerializer.serializeAnonymousListMediator(
                        caseDefaultElem, caseDefaultMediator);
                switchMed.addChild(caseDefaultElem);
            }
        }
        if (parent != null) {
            parent.addChild(switchMed);
        }
        return switchMed;
    }

    public String getMediatorClassName() {
        return SwitchMediator.class.getName();
    }

    private void handleException(String msg) {
        log.error(msg);
        throw new SynapseException(msg);
    }

}
