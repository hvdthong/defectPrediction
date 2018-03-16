package org.apache.poi.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Utilities to read hex from files.
 *
 * @author Marc Johnson
 * @author Glen Stampoultzis (glens at apache.org) */
public class HexRead
{
    /**
     * This method reads hex data from a filename and returns a byte array.
     * The file may contain line comments that are preceeded with a # symbol.
     *
     * @param filename  The filename to read
     * @return The bytes read from the file.
     * @throws IOException If there was a problem while reading the file.     */
    public static byte[] readData( String filename ) throws IOException
    {
        File file = new File( filename );
        FileInputStream stream = new FileInputStream( file );
        try
        {
            return readData( stream, -1 );
        }
        finally
        {
            stream.close();
        }
    }

    /**
     * Same as readData(String) except that this method allows you to specify sections within
     * a file.  Sections are referenced using section headers in the form:
     * <pre>
     *  [sectioname]
     * </pre>
     *
     * @see #readData(String)
     */
    public static byte[] readData( String filename, String section ) throws IOException
    {
        File file = new File( filename );
        FileInputStream stream = new FileInputStream( file );
        try
        {
            StringBuffer sectionText = new StringBuffer();
            boolean inSection = false;
            int c = stream.read();
            while ( c != -1 )
            {
                switch ( c )
                {
                    case '[':
                        inSection = true;
                        break;
                    case '\n':
                    case '\r':
                        inSection = false;
                        sectionText = new StringBuffer();
                        break;
                    case ']':
                        inSection = false;
                        if ( sectionText.toString().equals( section ) ) return readData( stream, '[' );
                        sectionText = new StringBuffer();
                        break;
                    default:
                        if ( inSection ) sectionText.append( (char) c );
                }
                c = stream.read();
            }
        }
        finally
        {
            stream.close();
        }
        throw new IOException( "Section '" + section + "' not found" );
    }

    static public byte[] readData( InputStream stream, int eofChar ) throws IOException
    {
        int characterCount = 0;
        byte b = (byte) 0;
        List bytes = new ArrayList();
        boolean done = false;
        while ( !done )
        {
            int count = stream.read();
            char baseChar = 'a';
            if ( count == eofChar ) break;
            switch ( count )
            {
                case '#':
                    readToEOL( stream );
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    b <<= 4;
                    b += (byte) ( count - '0' );
                    characterCount++;
                    if ( characterCount == 2 )
                    {
                        bytes.add( new Byte( b ) );
                        characterCount = 0;
                        b = (byte) 0;
                    }
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    baseChar = 'A';
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    b <<= 4;
                    b += (byte) ( count + 10 - baseChar );
                    characterCount++;
                    if ( characterCount == 2 )
                    {
                        bytes.add( new Byte( b ) );
                        characterCount = 0;
                        b = (byte) 0;
                    }
                    break;
                case -1:
                    done = true;
                    break;
                default :
                    break;
            }
        }
        Byte[] polished = (Byte[]) bytes.toArray( new Byte[0] );
        byte[] rval = new byte[polished.length];
        for ( int j = 0; j < polished.length; j++ )
        {
            rval[j] = polished[j].byteValue();
        }
        return rval;
    }

    static public byte[] readFromString(String data) throws IOException
    {
        return readData(new ByteArrayInputStream( data.getBytes() ), -1);
    }

    static private void readToEOL( InputStream stream ) throws IOException
    {
        int c = stream.read();
        while ( c != -1 && c != '\n' && c != '\r' )
        {
            c = stream.read();
        }
    }
}
