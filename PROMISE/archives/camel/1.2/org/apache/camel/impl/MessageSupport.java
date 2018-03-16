package org.apache.camel.impl;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.util.UuidGenerator;

/**
 * A base class for implementation inheritence providing the core
 * {@link Message} body handling features but letting the derived class deal
 * with headers.
 * 
 * Unless a specific provider wishes to do something particularly clever with
 * headers you probably want to just derive from {@link DefaultMessage}
 * 
 * @version $Revision: 564677 $
 */
public abstract class MessageSupport implements Message {
    private static final UuidGenerator DEFALT_ID_GENERATOR = new UuidGenerator();
    private Exchange exchange;
    private Object body;
    private String messageId = DEFALT_ID_GENERATOR.generateId();

    public Object getBody() {
        if (body == null) {
            body = createBody();
        }
        return body;
    }

    @SuppressWarnings({"unchecked" })
    public <T> T getBody(Class<T> type) {
        Exchange e = getExchange();
        if (e != null) {
            return e.getContext().getTypeConverter().convertTo(type, getBody());
        }
        return (T)getBody();
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public <T> void setBody(Object value, Class<T> type) {
        Exchange e = getExchange();
        if (e != null) {
            T v = e.getContext().getTypeConverter().convertTo(type, value);
            if (v != null) {
                value = v;
            }
        }
        setBody(value);
    }

    public Message copy() {
        Message answer = newInstance();
        answer.copyFrom(this);
        return answer;
    }

    public void copyFrom(Message that) {
        setMessageId(that.getMessageId());
        setBody(that.getBody());
        getHeaders().putAll(that.getHeaders());
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Returns a new instance
     * 
     * @return
     */
    public abstract Message newInstance();

    /**
     * A factory method to allow a provider to lazily create the message body
     * for inbound messages from other sources
     * 
     * @return the value of the message body or null if there is no value
     *         available
     */
    protected Object createBody() {
        return null;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return this.messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
