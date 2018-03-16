package org.apache.camel.component.mail;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.net.URI;
import java.util.Map;

/**
 * @version $Revision:520964 $
 */
public class MailComponent extends DefaultComponent<MailExchange> {
    private MailConfiguration configuration;

    public MailComponent() {
        this.configuration = new MailConfiguration();
    }

    public MailComponent(MailConfiguration configuration) {
        this.configuration = configuration;
    }

    public MailComponent(CamelContext context) {
        super(context);
        this.configuration = new MailConfiguration();
    }

    /**
     * Static builder method
     */
    public static MailComponent mailComponent() {
        return new MailComponent();
    }

    /**
     * Static builder method
     */
    public static MailComponent mailComponent(MailConfiguration configuration) {
        return new MailComponent(configuration);
    }

    @Override
    protected Endpoint<MailExchange> createEndpoint(String uri, String remaining, Map parameters) throws Exception {

        MailConfiguration config = getConfiguration().copy();
        config.configure(new URI(uri));

        MailEndpoint endpoint = new MailEndpoint(uri, this, config);

        setProperties(endpoint.getConfiguration(), parameters);
        return endpoint;
    }

    public MailConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the Mail configuration
     *
     * @param configuration the configuration to use by default for endpoints
     */
    public void setConfiguration(MailConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * A strategy method allowing the URI destination to be translated into the actual Mail destination name
     * (say by looking up in JNDI or something)
     */
    protected String convertPathToActualDestination(String path) {
        return path;
    }
}
