package org.apache.camel.component.irc;

import org.apache.commons.logging.Log;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;

/**
 * A helper class which logs errors
 *
 * @version $Revision: 1.1 $
 */
public class IrcErrorLogger extends IRCEventAdapter {
    private Log log;

    public IrcErrorLogger(Log log) {
        this.log = log;
    }

    @Override
    public void onRegistered() {
        super.onRegistered();
        log.info("onRegistered");
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
        log.info("onDisconnected");
    }

    @Override
    public void onMode(String string, IRCUser ircUser, IRCModeParser ircModeParser) {
        super.onMode(string, ircUser, ircModeParser);
        log.info("onMode.string = " + string);
        log.info("onMode.ircUser = " + ircUser);
        log.info("onMode.ircModeParser = " + ircModeParser);
    }

    @Override
    public void onMode(IRCUser ircUser, String string, String string1) {
        super.onMode(ircUser, string, string1);
        log.info("onMode.ircUser = " + ircUser);
        log.info("onMode.string = " + string);
        log.info("onMode.string1 = " + string1);
    }

    @Override
    public void onPing(String string) {
        super.onPing(string);
        log.info("onPing.string = " + string);
    }

    @Override
    public void onError(String string) {
        log.info("onError.string = " + string);
    }

    @Override
    public void onError(int i, String string) {
        super.onError(i, string);
        log.error("onError.i = " + i);
        log.error("onError.string = " + string);
    }

    @Override
    public void unknown(String string, String string1, String string2, String string3) {
        super.unknown(string, string1, string2, string3);
        log.error("unknown.string = " + string);
        log.error("unknown.string1 = " + string1);
        log.error("unknown.string2 = " + string2);
        log.error("unknown.string3 = " + string3);
    }
}
