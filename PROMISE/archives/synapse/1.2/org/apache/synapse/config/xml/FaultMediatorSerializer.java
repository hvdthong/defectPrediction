package org.apache.synapse.config.xml;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.transform.FaultMediator;

/**
 * Serializer for {@link FaultMediator} instances.
 * 
 * @see FaultMediatorFactory
 */
public class FaultMediatorSerializer extends AbstractMediatorSerializer {

    private static final String SOAP11 = "soap11";

    private static final String SOAP12 = "soap12";

    private static final String POX = "pox";

    public OMElement serializeMediator(OMElement parent, Mediator m) {

        if (!(m instanceof FaultMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }

        FaultMediator mediator = (FaultMediator) m;
        OMElement fault = fac.createOMElement("makefault", synNS);
        saveTracingState(fault,mediator);

        if(mediator.getSoapVersion()==FaultMediator.SOAP11) {
           fault.addAttribute(fac.createOMAttribute(
                "version", nullNS, SOAP11));
        } else if(mediator.getSoapVersion()==FaultMediator.SOAP12) {
           fault.addAttribute(fac.createOMAttribute(
                "version", nullNS, SOAP12));
        } else if(mediator.getSoapVersion()==FaultMediator.POX) {
           fault.addAttribute(fac.createOMAttribute(
                "version", nullNS, POX));
        }

        OMElement code = fac.createOMElement("code", synNS, fault);
        if (mediator.getFaultCodeValue() != null) {
            code.addAttribute(fac.createOMAttribute(
                "value", nullNS, mediator.getFaultCodeValue().getPrefix() + ":"
                    + mediator.getFaultCodeValue().getLocalPart()));
            code.declareNamespace(mediator.getFaultCodeValue().getNamespaceURI(),
                    mediator.getFaultCodeValue().getPrefix());

        } else if (mediator.getFaultCodeExpr() != null) {
            SynapseXPathSerializer.serializeXPath(mediator.getFaultCodeExpr(), code, "expression");

        } else if (mediator.getSoapVersion() != FaultMediator.POX) {
            handleException("Fault code is required for a fault " +
                    "mediator unless it is a pox fault");
        }

        OMElement reason = fac.createOMElement("reason", synNS, fault);
        if (mediator.getFaultReasonValue() != null) {
            reason.addAttribute(fac.createOMAttribute(
                "value", nullNS, mediator.getFaultReasonValue()));

        } else if (mediator.getFaultReasonExpr() != null) {

            SynapseXPathSerializer.serializeXPath(
                mediator.getFaultReasonExpr(), reason, "expression");

        } else if (mediator.getSoapVersion() != FaultMediator.POX) {
            handleException("Fault reason is required for a fault " +
                    "mediator unless it is a pox fault");
        }


        if (mediator.getFaultNode() != null) {
            OMElement node = fac.createOMElement("node", synNS, fault);
            node.setText(mediator.getFaultNode().toString());
        }

        if (mediator.getFaultRole() != null) {
            OMElement role = fac.createOMElement("role", synNS, fault);
            role.setText(mediator.getFaultRole().toString());
        }

        if (mediator.getFaultDetailExpr() != null) {
            OMElement detail = fac.createOMElement("detail", synNS, fault);
            SynapseXPathSerializer.serializeXPath(
                    mediator.getFaultDetailExpr(), detail, "expression");            
        } else if (mediator.getFaultDetail() != null) {
            OMElement detail = fac.createOMElement("detail", synNS, fault);
            detail.setText(mediator.getFaultDetail());
        }

        if (parent != null) {
            parent.addChild(fault);
        }
        return fault;
    }

    public String getMediatorClassName() {
        return FaultMediator.class.getName();
    }
}