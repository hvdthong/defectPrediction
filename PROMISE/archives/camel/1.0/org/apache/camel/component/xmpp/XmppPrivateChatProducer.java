package org.apache.camel.component.xmpp;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 * @version $Revision: 534145 $
 */
public class XmppPrivateChatProducer extends DefaultProducer {
    private static final transient Log log = LogFactory.getLog(XmppPrivateChatProducer.class);
    private final XmppEndpoint endpoint;
    private final String participant;
    private Chat chat;

    public XmppPrivateChatProducer(XmppEndpoint endpoint, String participant) {
        super(endpoint);
        this.endpoint = endpoint;
        this.participant = participant;
        if (participant == null) {
            throw new IllegalArgumentException("No participant property specified");
        }
    }

    public void process(Exchange exchange) {
        Message message = chat.createMessage();
        message.setTo(participant);
        message.setFrom(endpoint.getUser());
        message.setThread(exchange.getExchangeId());
        message.setType(Message.Type.NORMAL);

        endpoint.getBinding().populateXmppMessage(message, exchange);
        if (log.isDebugEnabled()) {
            log.debug(">>>> message: " + message.getBody());
        }
        try {
            chat.sendMessage(message);
        }
        catch (XMPPException e) {
            throw new RuntimeXmppException(e);
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (chat == null) {
            chat = endpoint.getConnection().createChat(getParticipant());
        }
    }

    @Override
    protected void doStop() throws Exception {
        chat = null;
        super.doStop();
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getParticipant() {
        return participant;
    }
}
