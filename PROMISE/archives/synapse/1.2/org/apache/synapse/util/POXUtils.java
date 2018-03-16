package org.apache.synapse.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;

/**
 *
 */
public class POXUtils {

    private static final Log log = LogFactory.getLog(POXUtils.class);

    public static void convertSOAPFaultToPOX(MessageContext msgCtx) {

        SOAPBody body = msgCtx.getEnvelope().getBody();
        SOAPFault fault = body.getFault();
        if (fault != null) {

            OMFactory fac = msgCtx.getEnvelope().getOMFactory();
            OMElement faultPayload = fac.createOMElement(new QName("Exception"));

            if (fault.getDetail() != null && !fault.getDetail().getText().equals("")) {

                String faultDetail = fault.getDetail().getText();

                if (log.isDebugEnabled()) {
                    log.debug("Setting the fault detail : " + faultDetail + " as athe POX Fault");
                }
                faultPayload.setText(faultDetail);

            } else if (fault.getReason() != null && !fault.getReason().getText().equals("")) {

                String faultReasonValue = fault.getReason().getText();

                if (log.isDebugEnabled()) {
                    log.debug("Setting the fault reason : "
                        + faultReasonValue + " as athe POX Fault");
                }
                faultPayload.setText(faultReasonValue);

            } else if (log.isDebugEnabled()) {
                
                log.debug("Couldn't find the fault detail or reason to compose POX Fault");
            }

            if (body.getFirstElement() != null) {
                body.getFirstElement().detach();
            }

            msgCtx.setProcessingFault(true);

            if (log.isDebugEnabled()) {

                String msg = "Original SOAP Message : " + msgCtx.getEnvelope().toString() +
                    "POXFault Message created : " + faultPayload.toString();
                log.debug(msg);
                
                if (log.isTraceEnabled()) {
                    log.trace(msg);
                }
            }

            body.addChild(faultPayload);
        }
    }
}