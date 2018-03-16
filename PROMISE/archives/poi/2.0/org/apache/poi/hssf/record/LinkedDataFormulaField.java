package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.util.LittleEndian;

import java.util.Stack;
import java.util.Iterator;

/**
 * Not implemented yet. May commit it anyway just so people can see
 * where I'm heading.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class LinkedDataFormulaField
        implements CustomField
{
    Stack formulaTokens = new Stack();

    public int getSize()
    {
        int size = 0;
        for ( Iterator iterator = formulaTokens.iterator(); iterator.hasNext(); )
        {
            Ptg token = (Ptg) iterator.next();
            size += token.getSize();
        }
        return size + 2;
    }

    public int fillField( byte[] data, short size, int offset )
    {
        short tokenSize = LittleEndian.getShort(data, offset);
        formulaTokens = getParsedExpressionTokens(data, size, offset + 2);

        return tokenSize + 2;
    }

    public void toString( StringBuffer buffer )
    {
        for ( int k = 0; k < formulaTokens.size(); k++ )
        {
            buffer.append( "Formula " )
                    .append( k )
                    .append( "=" )
                    .append( formulaTokens.get( k ).toString() )
                    .append( "\n" )
                    .append( ( (Ptg) formulaTokens.get( k ) ).toDebugString() )
                    .append( "\n" );
        }
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        toString( b );
        return b.toString();
    }

    public int serializeField( int offset, byte[] data )
    {
        int size = getSize();
        LittleEndian.putShort(data, offset, (short)(size - 2));
        int pos = offset + 2;
        for ( Iterator iterator = formulaTokens.iterator(); iterator.hasNext(); )
        {
            Ptg ptg = (Ptg) iterator.next();
            ptg.writeBytes(data, pos);
            pos += ptg.getSize();
        }
        return size;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            return null;
        }
    }

    private Stack getParsedExpressionTokens( byte[] data, short size,
                                             int offset )
    {
        Stack stack = new Stack();
        int pos = offset;

        while ( pos < size )
        {
            Ptg ptg = Ptg.createPtg( data, pos );
            pos += ptg.getSize();
            stack.push( ptg );
        }
        return stack;
    }

    public void setFormulaTokens( Stack formulaTokens )
    {
        this.formulaTokens = (Stack) formulaTokens.clone();
    }

    public Stack getFormulaTokens()
    {
        return (Stack)this.formulaTokens.clone();
    }

}
