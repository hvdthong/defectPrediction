package org.apache.camel.component.irc;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;

public class IrcConsumer extends DefaultConsumer<IrcExchange> {
    private static final transient Log log = LogFactory.getLog(IrcConsumer.class);
    final private IrcEndpoint endpoint;
    final private IRCConnection connection;
    final IrcConfiguration configuration;
    private FilteredIRCEventAdapter listener = null;

    public IrcConsumer(IrcEndpoint endpoint, Processor processor, IRCConnection connection) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.connection = connection;
        configuration = endpoint.getConfiguration();
    }

    @Override
    protected void doStop() throws Exception {
        String target = endpoint.getConfiguration().getTarget();
        connection.doPart(target);
        connection.removeIRCEventListener(listener);

        super.doStop();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        String target = endpoint.getConfiguration().getTarget();
        connection.addIRCEventListener(new FilteredIRCEventAdapter(target));

        log.debug("joining: " + target);
        connection.doJoin(target);
    }

    public IRCConnection getConnection() {
        return connection;
    }

    class FilteredIRCEventAdapter extends IRCEventAdapter {
        final String target;

        public FilteredIRCEventAdapter(String target) {
            this.target = target;
        }

        @Override
        public void onNick(IRCUser user, String newNick) {
            if (configuration.isOnNick()) {
                IrcExchange exchange = endpoint.createOnNickExchange(user, newNick);
                try {
                    getProcessor().process(exchange);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onQuit(IRCUser user, String msg) {
            if (configuration.isOnQuit()) {
                IrcExchange exchange = endpoint.createOnQuitExchange(user, msg);
                try {
                    getProcessor().process(exchange);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onJoin(String channel, IRCUser user) {
            if (configuration.isOnJoin()) {
                if (channel.equals(configuration.getTarget())) {
                    IrcExchange exchange = endpoint.createOnJoinExchange(channel, user);
                    try {
                        getProcessor().process(exchange);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onKick(String channel, IRCUser user, String passiveNick, String msg) {
            if (configuration.isOnKick()) {
                if (channel.equals(configuration.getTarget())) {
                    IrcExchange exchange = endpoint.createOnKickExchange(channel, user, passiveNick, msg);
                    try {
                        getProcessor().process(exchange);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onMode(String channel, IRCUser user, IRCModeParser modeParser) {
            if (configuration.isOnMode()) {
                if (channel.equals(configuration.getTarget())) {
                    IrcExchange exchange = endpoint.createOnModeExchange(channel, user, modeParser);
                    try {
                        getProcessor().process(exchange);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onPart(String channel, IRCUser user, String msg) {
            if (configuration.isOnPart()) {
                if (channel.equals(configuration.getTarget())) {
                    IrcExchange exchange = endpoint.createOnPartExchange(channel, user, msg);
                    try {
                        getProcessor().process(exchange);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onTopic(String channel, IRCUser user, String topic) {
            if (configuration.isOnTopic()) {
                if (channel.equals(configuration.getTarget())) {
                    IrcExchange exchange = endpoint.createOnTopicExchange(channel, user, topic);
                    try {
                        getProcessor().process(exchange);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onPrivmsg(String target, IRCUser user, String msg) {
            if (configuration.isOnPrivmsg()) {
                if (target.equals(configuration.getTarget())) {
                    IrcExchange exchange = endpoint.createOnPrivmsgExchange(target, user, msg);
                    try {
                        getProcessor().process(exchange);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
