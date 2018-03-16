import org.apache.velocity.context.Context;


/**
 * Event handlers implementing this interface will automatically
 * have the method setContext called before each event.  This
 * allows the event handler to use information in the latest context
 * when responding to the event.
 *
 * <P>Important Note: Only local event handlers attached to the context
 * (as opposed to global event handlers initialized in the velocity.properties
 * file) should implement ContextAware.  Since global event handlers are
 * singletons individual requests will not be able to count on the
 * correct context being loaded before a request.
 *
 * @author <a href="mailto:wglass@wglass@forio.com">Will Glass-Husain</a>
 * @version $Id: ContextAware.java 463298 2006-10-12 16:10:32Z henning $
 */
public interface  ContextAware
{
    /**
     * Initialize the EventHandler.
     * @param context
     */
    public void setContext( Context context );

}
