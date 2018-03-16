package org.apache.camel.component.xmpp;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

/**
 * An XMPP Endpoint
 * 
 * @version $Revision:520964 $
 */
public class XmppEndpoint extends DefaultEndpoint<XmppExchange> {
    private static final transient Log LOG = LogFactory.getLog(XmppEndpoint.class);
    private XmppBinding binding;
    private XMPPConnection connection;
    private String host;
    private int port;
    private String user;
    private String password;
    private String resource = "Camel";
    private boolean login = true;
    private PacketFilter filter;
    private boolean createAccount;
    private String room;
    private String participant;

    public XmppEndpoint(String uri, XmppComponent component) {
        super(uri, component);
    }

    public Producer<XmppExchange> createProducer() throws Exception {
        if (room != null) {
            return createGroupChatProducer(room);
        } else {
            if (participant == null) {
                throw new IllegalArgumentException("No room or participant configured on this endpoint: " + this);
            }
            return createPrivateChatProducer(participant);
        }
    }

    public Producer<XmppExchange> createGroupChatProducer(String room) throws Exception {
        return new XmppGroupChatProducer(this, room);
    }

    public Producer<XmppExchange> createPrivateChatProducer(String participant) throws Exception {
        return new XmppPrivateChatProducer(this, participant);
    }

    public Consumer<XmppExchange> createConsumer(Processor processor) throws Exception {
        return new XmppConsumer(this, processor);
    }

    @Override
    public XmppExchange createExchange(ExchangePattern pattern) {
        return new XmppExchange(getContext(), pattern, getBinding());
    }

    public XmppExchange createExchange(Message message) {
        return new XmppExchange(getContext(), getExchangePattern(), getBinding(), message);
    }

    public XmppBinding getBinding() {
        if (binding == null) {
            binding = new XmppBinding();
        }
        return binding;
    }

    /**
     * Sets the binding used to convert from a Camel message to and from an XMPP
     * message
     * 
     * @param binding the binding to use
     */
    public void setBinding(XmppBinding binding) {
        this.binding = binding;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public PacketFilter getFilter() {
        return filter;
    }

    public void setFilter(PacketFilter filter) {
        this.filter = filter;
    }

    public boolean isCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(boolean createAccount) {
        this.createAccount = createAccount;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public XMPPConnection getConnection() throws XMPPException {
        if (connection == null) {
            connection = createConnection();
        }
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

    protected XMPPConnection createConnection() throws XMPPException {
        XMPPConnection connection;
        if (port > 0) {
            connection = new XMPPConnection(host, port);
        } else {
            connection = new XMPPConnection(host);
        }
        if (login && !connection.isAuthenticated()) {
            if (user != null) {
                LOG.info("Logging in to XMPP as user: " + user + " on connection: " + connection);
                if (password == null) {
                    LOG.warn("No password configured for user: " + user);
                }

                if (createAccount) {
                    AccountManager accountManager = new AccountManager(connection);
                    accountManager.createAccount(user, password);
                }
                if (resource != null) {
                    connection.login(user, password, resource);
                } else {
                    connection.login(user, password);
                }
            } else {
                LOG.info("Logging in anonymously to XMPP on connection: " + connection);
                connection.loginAnonymously();
            }

            connection.sendPacket(new Presence(Presence.Type.AVAILABLE));
        }
        return connection;
    }

    public boolean isSingleton() {
        return true;
    }

}
