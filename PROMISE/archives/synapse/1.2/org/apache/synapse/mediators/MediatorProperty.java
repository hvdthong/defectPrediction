package org.apache.synapse.mediators;

import org.apache.synapse.MessageContext;
import org.apache.synapse.config.xml.XMLConfigConstants;
import org.apache.synapse.util.xpath.SynapseXPath;

import javax.xml.namespace.QName;

/**
 * A mediator property is a name-value or name-expression pair which could be supplied
 * for certain mediators. If expressions are supplied they are evaluated at the runtime
 * against the current message into literal String values.
 */
public class MediatorProperty {

    public static final QName PROPERTY_Q  = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "property");
    public static final QName ATT_NAME_Q  = new QName(XMLConfigConstants.NULL_NAMESPACE, "name");
    public static final QName ATT_VALUE_Q = new QName(XMLConfigConstants.NULL_NAMESPACE, "value");
    public static final QName ATT_EXPR_Q  = new QName(XMLConfigConstants.NULL_NAMESPACE, "expression");

    private String name;
    private String value;
    private SynapseXPath expression;

    public MediatorProperty() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SynapseXPath getExpression() {
        return expression;
    }

    public void setExpression(SynapseXPath expression) {
        this.expression = expression;
    }

    public String getEvaluatedExpression(MessageContext synCtx) {
        return expression.stringValueOf(synCtx);
    }

}
