package org.apache.camel.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.camel.Intercept;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.processor.Interceptor;
import org.apache.camel.spi.RouteContext;


/**
 * Represents an XML &lt;intercept/&gt; element
 *
 * @version $Revision: 699876 $
 */
@XmlRootElement(name = "intercept")
@XmlAccessorType(XmlAccessType.FIELD)
public class InterceptType extends OutputType<ProcessorType> {
  
    @XmlTransient
    private ProceedType proceed = new ProceedType();
    @XmlTransient
    private Boolean stopIntercept = Boolean.FALSE;
    @XmlTransient
    private Boolean usePredicate = Boolean.FALSE;

    @Override
    public String toString() {
        return "Intercept[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "intercept";
    }

    @Override
    public String getLabel() {
        return "intercept";
    }

    @Override
    public Processor createProcessor(RouteContext routeContext) throws Exception {
        Interceptor interceptor = new Interceptor();
        routeContext.intercept(interceptor);

        final Processor interceptRoute = createOutputsProcessor(routeContext);
        interceptor.setInterceptorLogic(interceptRoute);

        return interceptor;
    }

    /**
     * Applies this interceptor only if the given predicate is true
     */
    public ChoiceType when(Predicate predicate) {
        usePredicate = Boolean.TRUE;
        ChoiceType choice = choice().when(PredicateBuilder.not(predicate));
        choice.addOutput(proceed);
        return choice.otherwise();
    }

    public ProceedType getProceed() {
        return proceed;
    }

    public void stopIntercept() {
        setStopIntercept(Boolean.TRUE);
    }

    @XmlElement(name = "stop", required = false)
    public void setStop(String elementValue /* not used */) {
        stopIntercept();
    }    
    
    public InterceptType createProxy() {
        InterceptType answer = new InterceptType();
        answer.getOutputs().addAll(this.getOutputs());
        
        answer.setStopIntercept(getStopIntercept());

        if (answer.getOutputs().size() > 0) {
            ChoiceType choice = null;
            for (ProcessorType processor : answer.getOutputs()) {
                if (processor instanceof ChoiceType) {
                    choice = (ChoiceType) processor;

                    if (usePredicate.booleanValue() && getStopIntercept().booleanValue()) {
                        WhenType when = choice.getWhenClauses().get(0);
                        when.getOutputs().remove(this.getProceed());
                    }

                    addProceedProxy(this.getProceed(), answer.getProceed(),
                        choice.getWhenClauses().get(choice.getWhenClauses().size() - 1), usePredicate.booleanValue() && !getStopIntercept().booleanValue());

                    addProceedProxy(this.getProceed(), answer.getProceed(), choice.getOtherwise(), !getStopIntercept().booleanValue());

                    if (getStopIntercept().booleanValue()) {
                        addProceedProxy(this.getProceed(), answer.getProceed(),
                            choice.getWhenClauses().get(choice.getWhenClauses().size() - 1), usePredicate.booleanValue());
                    }

                    break;
                }
            }
            if (choice == null) {
                addProceedProxy(this.getProceed(), answer.getProceed(), answer, !getStopIntercept().booleanValue());
            }
        }

        return answer;
    }

    private void addProceedProxy(ProceedType orig, ProceedType proxy, ProcessorType<?> processor, boolean force) {
        int index = processor.getOutputs().indexOf(orig);
        if (index >= 0) {
            processor.addOutput(proxy);
            List<ProcessorType<?>> outs = processor.getOutputs();
            outs.remove(proxy);
            outs.set(index, proxy);
        } else if (force) {
            processor.addOutput(proxy);
        }
    }

    public void setStopIntercept(Boolean stop) {
        this.stopIntercept = stop;
    }

    public Boolean getStopIntercept() {
        return stopIntercept;
    }

}
