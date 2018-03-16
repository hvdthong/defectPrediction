package org.apache.camel.model;

import org.apache.camel.Endpoint;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.impl.RouteContext;
import org.apache.camel.processor.ChoiceProcessor;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.camel.processor.FilterProcessor;
import org.apache.camel.util.CollectionStringBuffer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name = "choice")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChoiceType extends ProcessorType<ChoiceType> {
    @XmlElementRef
    private List<InterceptorType> interceptors = new ArrayList<InterceptorType>();
    @XmlElementRef
    private List<WhenType> whenClauses = new ArrayList<WhenType>();
    @XmlElement(required = false)
    private OtherwiseType otherwise;

    @Override
    public String toString() {
        return "Choice[ " + getWhenClauses() + " " + getOtherwise() + "]";
    }

    @Override
    public Processor createProcessor(RouteContext routeContext) throws Exception {
        List<FilterProcessor> filters = new ArrayList<FilterProcessor>();
        for (WhenType whenClaus : whenClauses) {
            filters.add(whenClaus.createProcessor(routeContext));
        }
        Processor otherwiseProcessor = null;
        if (otherwise != null) {
            otherwiseProcessor = otherwise.createProcessor(routeContext);
        }
        return new ChoiceProcessor(filters, otherwiseProcessor);
    }

    public ChoiceType when(Predicate predicate) {
        getWhenClauses().add(new WhenType(predicate));
        return this;
    }

    public OtherwiseType otherwise() {
        OtherwiseType answer = new OtherwiseType();
        setOtherwise(answer);
        return answer;
    }


    @Override
    public String getLabel() {
        CollectionStringBuffer buffer = new CollectionStringBuffer();
        List<WhenType> list = getWhenClauses();
        for (WhenType whenType : list) {
            buffer.append(whenType.getLabel());
        }
        return buffer.toString();
    }

    public List<WhenType> getWhenClauses() {
        return whenClauses;
    }

    public void setWhenClauses(List<WhenType> whenClauses) {
        this.whenClauses = whenClauses;
    }

    public List<ProcessorType<?>> getOutputs() {
        if (otherwise != null) {
            return otherwise.getOutputs();
        }
        else if (whenClauses.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        else {
            WhenType when = whenClauses.get(whenClauses.size() - 1);
            return when.getOutputs();
        }
    }

    public OtherwiseType getOtherwise() {
        return otherwise;
    }

    public void setOtherwise(OtherwiseType otherwise) {
        this.otherwise = otherwise;
    }

    public List<InterceptorType> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<InterceptorType> interceptors) {
        this.interceptors = interceptors;
    }
}