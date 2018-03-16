package org.apache.synapse.endpoints.dispatch;

import org.apache.synapse.endpoints.Endpoint;
import org.apache.synapse.MessageContext;

/**
 * Defines the behavior of session dispatchers. There can be two dispatcher types. Server intiated
 * session dispatchers and client initialted session dispatchers. In the former one, server generates
 * the session ID and sends it to the client in the first RESPONSE. In the later case, client should
 * generate the session ID and send it to the server in the first REQUEST. A dispatcher object will
 * be created for each session affinity load balance endpoint.
 */
public interface Dispatcher {

    /**
     * Dispatcher should check the session id pattern in the synapseMessageContext and return the
     * matching endpoint for that session id, if availabale. If the session id in the given
     * synapseMessageContext is not found it should return null.
     *
     * @param synCtx client -> esb message context.
     * @return Endpoint Endpoint associated with this session.
     */
    public Endpoint getEndpoint(MessageContext synCtx);
    
    /**
     * Updates the session maps. This will be called in the first client -> synapse -> server flow
     * for client initiated sessions. For server initiated sessions, this will be called in the first
     * server -> synapse -> client flow.
     *
     * @param synCtx SynapseMessageContext
     * @param endpoint Selected endpoint for this session.
     */
    public void updateSession(MessageContext synCtx, Endpoint endpoint);

    /**
     * Removes the session belonging to the given message context.
     *
     * @param synCtx MessageContext containing an session ID.         
     */
    public void unbind(MessageContext synCtx);

    /**
     * Determine whether the session supported by the implementing dispatcher is intiated by the
     * server (e.g. soap session) or by the client. This can be used for optimizing session updates.
     *
     * @return true, if the session is initiated by the server. false, otherwise.
     */
    public boolean isServerInitiatedSession();
}
