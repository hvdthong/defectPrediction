package org.apache.camel.component.cxf;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.Destination;

/**
 * An {@link Exchange} for working with Apache CXF which expoes the underlying
 * CXF messages via {@link #getInMessage()} and {@link #getOutMessage()} along with the
 * {@link #getExchange()} 
 *
 * @version $Revision: 550760 $
 */
public class CxfExchange extends DefaultExchange {
    private final CxfBinding binding;
    private Exchange exchange;

    public CxfExchange(CamelContext context, CxfBinding binding) {
        super(context);
        this.binding = binding;
    }

    public CxfExchange(CamelContext context, CxfBinding binding, Exchange exchange) {
        super(context);
        this.binding = binding;
        this.exchange = exchange;

        setIn(new CxfMessage(exchange.getInMessage()));
        setOut(new CxfMessage(exchange.getOutMessage()));
        setFault(new CxfMessage(exchange.getInFaultMessage()));
    }

    public CxfExchange(CamelContext context, CxfBinding binding, Message inMessage) {
        super(context);
        this.binding = binding;
        this.exchange = inMessage.getExchange();

        setIn(new CxfMessage(inMessage));
        if (exchange != null) {
            setOut(new CxfMessage(exchange.getOutMessage()));
            setFault(new CxfMessage(exchange.getInFaultMessage()));
        }
    }

    @Override
    public CxfMessage getIn() {
        return (CxfMessage) super.getIn();
    }

    @Override
    public CxfMessage getOut() {
        return (CxfMessage) super.getOut();
    }

    @Override
    public CxfMessage getOut(boolean lazyCreate) {
        return (CxfMessage) super.getOut(lazyCreate);
    }

    @Override
    public CxfMessage getFault() {
        return (CxfMessage) super.getFault();
    }

    /**
     * @return the Camel <-> JBI binding
     */
    public CxfBinding getBinding() {
        return binding;
    }


    /**
     * Returns the underlying CXF message exchange for an inbound exchange
     * or null for outbound messages
     *
     * @return the inbound message exchange
     */
    public Exchange getExchange() {
        return exchange;
    }

    public Message getInMessage() {
        return getIn().getMessage();
    }

    public Message getOutMessage() {
        return getOut().getMessage();
    }

    public Message getOutFaultMessage() {
        return getExchange().getOutFaultMessage();
    }

    public Message getInFaultMessage() {
        return getExchange().getInFaultMessage();
    }

    public Destination getDestination() {
        return getExchange().getDestination();
    }

    public Conduit getConduit(Message message) {
        return getExchange().getConduit(message);
    }

    @Override
    protected CxfMessage createInMessage() {
        return new CxfMessage();
    }

    @Override
    protected CxfMessage createOutMessage() {
        return new CxfMessage();
    }
}
