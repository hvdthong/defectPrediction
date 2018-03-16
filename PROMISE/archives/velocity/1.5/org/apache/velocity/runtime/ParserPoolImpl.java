import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.util.SimplePool;


/**
 * This wraps the original parser SimplePool class.  It also handles
 * instantiating ad-hoc parsers if none are available.
 *
 * @author <a href="mailto:sergek@lokitech.com">Serge Knystautas</a>
 * @version $Id: RuntimeInstance.java 384374 2006-03-08 23:19:30Z nbubna $
 */
public class ParserPoolImpl implements ParserPool {

    RuntimeServices rsvc = null;
    SimplePool pool = null;
    int max = RuntimeConstants.NUMBER_OF_PARSERS;

    /**
     * Create the underlying "pool".
     * @param rsvc
     */
    public void initialize(RuntimeServices rsvc)
    {
        this.rsvc = rsvc;
        max = rsvc.getInt(RuntimeConstants.PARSER_POOL_SIZE, RuntimeConstants.NUMBER_OF_PARSERS);
        pool = new SimplePool(max);

        for (int i = 0; i < max; i++)
        {
            pool.put(rsvc.createNewParser());
        }

        if (rsvc.getLog().isDebugEnabled())
        {
            rsvc.getLog().debug("Created '" + max + "' parsers.");
        }
    }

    /**
     * Call the wrapped pool.  If none are available, it will create a new
     * temporary one.
     * @return A parser Object.
     */
    public Parser get()
    {
        Parser parser = (Parser) pool.get();
        if (parser == null)
        {
            rsvc.getLog().debug("Created new " +
                    "parser (pool exhausted).  Consider " +
                    "increasing pool size.");
            parser = rsvc.createNewParser();
        }
        return parser;
    }

    /**
     * Call the wrapped pool.
     * @param parser
     */
    public void put(Parser parser)
    {
        pool.put(parser);
    }
}
