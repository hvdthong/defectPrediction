package org.apache.html.dom;


import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * @version $Revision: 316847 $ $Date: 2001-01-31 07:58:06 +0800 (周三, 2001-01-31) $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLBaseFontElement
 * @see ElementImpl
 */
public class HTMLBaseFontElementImpl
    extends HTMLElementImpl
    implements HTMLBaseFontElement
{
    
    
    public String getColor()
    {
        return capitalize( getAttribute( "color" ) );
    }
    
    
    public void setColor( String color )
    {
        setAttribute( "color", color );
    }
    
    
    public String getFace()
    {
        return capitalize( getAttribute( "face" ) );
    }
    
    
    public void setFace( String face )
    {
        setAttribute( "face", face );
    }
    
    
    public String getSize()
    {
        return getAttribute( "size" );
    }
    
    
    public void setSize( String size )
    {
        setAttribute( "size", size );
    }

    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLBaseFontElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

