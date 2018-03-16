import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.util.TemplateNumber;

/**
 * Handles arg1 &lt; arg2<br><br>
 *
 * Only subclasses of Number can be compared.<br><br>
 *
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @author <a href="mailto:wglass@forio.com">Will Glass-Husain</a>
 * @author <a href="mailto:pero@antaramusic.de">Peter Romianowski</a>
 */

public class ASTLTNode extends SimpleNode
{
    /**
     * @param id
     */
    public ASTLTNode(int id)
    {
        super(id);
    }

    /**
     * @param p
     * @param id
     */
    public ASTLTNode(Parser p, int id)
    {
        super(p, id);
    }


    /**
     * @see org.apache.velocity.runtime.parser.node.SimpleNode#jjtAccept(org.apache.velocity.runtime.parser.node.ParserVisitor, java.lang.Object)
     */
    public Object jjtAccept(ParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.SimpleNode#evaluate(org.apache.velocity.context.InternalContextAdapter)
     */
    public boolean evaluate(InternalContextAdapter context)
        throws MethodInvocationException
    {
        /*
         *  get the two args
         */

        Object left = jjtGetChild(0).value( context );
        Object right = jjtGetChild(1).value( context );

        /*
         *  if either is null, lets log and bail
         */

        if (left == null || right == null)
        {
            log.error((left == null ? "Left" : "Right")
                           + " side ("
                           + jjtGetChild( (left == null? 0 : 1) ).literal()
                           + ") of '<' operation has null value."
                           + " Operation not possible. "
                           + Log.formatFileString(this));
            return false;
        }

        /*
         *  convert to Number if applicable
         */
        if (left instanceof TemplateNumber)
        {
           left = ( (TemplateNumber) left).getAsNumber();
        }
        if (right instanceof TemplateNumber)
        {
           right = ( (TemplateNumber) right).getAsNumber();
        }

        /*
         *  Only compare Numbers
         */

        if ( !( left instanceof Number )  || !( right instanceof Number ))
        {
            log.error((!(left instanceof Number) ? "Left" : "Right")
                           + " side of '>=' operation is not a valid Number. "
                           + Log.formatFileString(this));

            return false;
        }

        return MathUtils.compare ( (Number)left,(Number)right) == -1;

    }

    /**
     * @see org.apache.velocity.runtime.parser.node.SimpleNode#value(org.apache.velocity.context.InternalContextAdapter)
     */
    public Object value(InternalContextAdapter context)
        throws MethodInvocationException
    {
        boolean val = evaluate(context);

        return val ? Boolean.TRUE : Boolean.FALSE;
    }

}
