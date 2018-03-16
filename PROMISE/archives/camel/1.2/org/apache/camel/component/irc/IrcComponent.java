package org.apache.camel.component.irc;

import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.schwering.irc.lib.IRCConnection;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @version $Revision:$
 */
public class IrcComponent extends DefaultComponent<IrcExchange> {
    private static final transient Log LOG = LogFactory.getLog(IrcComponent.class);
    private IrcConfiguration configuration;
    private final Map<String, IRCConnection> connectionCache = new HashMap<String, IRCConnection>();

    public IrcComponent() {
        configuration = new IrcConfiguration();
    }

    public IrcComponent(IrcConfiguration configuration) {
        this.configuration = configuration;
    }

    public IrcComponent(CamelContext context) {
        super(context);
        configuration = new IrcConfiguration();
    }

    public static IrcComponent ircComponent() {
        return new IrcComponent();
    }

    protected IrcEndpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        IrcConfiguration config = getConfiguration().copy();
        config.configure(new URI(uri));

        final IrcEndpoint endpoint = new IrcEndpoint(uri, this, config);

        setProperties(endpoint.getConfiguration(), parameters);
        return endpoint;
    }

    public IrcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(IrcConfiguration configuration) {
        this.configuration = configuration;
    }

    public synchronized IRCConnection getIRCConnection(IrcConfiguration configuration) {
        final IRCConnection connection;
        if (connectionCache.containsKey(configuration.getCacheKey())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returning Cached Connection to " + configuration.getHostname() + " " + configuration.getTarget());
            }
            connection = connectionCache.get(configuration.getCacheKey());
        } else {
            connection = createConnection(configuration);
            connectionCache.put(configuration.getCacheKey(), connection);
        }
        return connection;
    }

    protected IRCConnection createConnection(IrcConfiguration configuration) {
        LOG.debug("Creating Connection to " + configuration.getHostname() + " destination: " + configuration.getTarget() + " nick: " + configuration.getNickname() + " user: "
                  + configuration.getUsername());

        final IRCConnection conn = new IRCConnection(configuration.getHostname(), configuration.getPorts(), configuration.getPassword(), configuration.getNickname(), configuration.getUsername(),
                                                     configuration.getRealname());
        conn.setEncoding("UTF-8");
        conn.setColors(configuration.isColors());
        conn.setPong(true);

        try {
            conn.connect();
        } catch (Exception e) {
            LOG.error("Failed to connect: " + e, e);

            throw new RuntimeCamelException(e);
        }
        return conn;
    }

    public void closeConnection(String key, IRCConnection connection) {
        try {
            connection.doQuit();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected synchronized void doStop() throws Exception {
        Map<String, IRCConnection> map = new HashMap<String, IRCConnection>(connectionCache);
        connectionCache.clear();
        for (Map.Entry<String, IRCConnection> entry : map.entrySet()) {
            closeConnection(entry.getKey(), entry.getValue());
        }
        super.doStop();
    }
}
