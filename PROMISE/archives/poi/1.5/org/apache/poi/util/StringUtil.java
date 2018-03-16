package org.apache.poi.util;

import java.io.UnsupportedEncodingException;

import java.text.NumberFormat;
import java.text.FieldPosition;

/**
 * Title:        String Utility
 * Description:  Collection of string handling utilities
 * @author       Andrew C. Oliver
 * @version 1.0
 */

public class StringUtil
{
    private StringUtil()
    {
    }

    /**
     * given a byte array of 16-bit unicode characters, compress to
     * 8-bit and return a string
     *
     * @param string the byte array to be converted
     * @param offset the initial offset into the byte array. it is
     *               assumed that string[ offset ] and string[ offset
     *               + 1 ] contain the first 16-bit unicode character
     *               @len the length of the final string
     *
     * @param len
     * @return the converted string
     *
     * @exception ArrayIndexOutOfBoundsException if offset is out of
     *            bounds for the byte array (i.e., is negative or is
     *            greater than or equal to string.length)
     * @exception IllegalArgumentException if len is too large (i.e.,
     *            there is not enough data in string to create a
     *            String of that length)
     */

    public static String getFromUnicode(final byte [] string,
                                        final int offset, final int len)
        throws ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        if ((offset < 0) || (offset >= string.length))
        {
            throw new ArrayIndexOutOfBoundsException("Illegal offset");
        }
        if ((len < 0) || (((string.length - offset) / 2) < len))
        {
            throw new IllegalArgumentException("Illegal length");
        }
        byte[] bstring = new byte[ len ];

        for (int k = 0; k < len; k++)
        {
            bstring[ k ] = string[ index ];
            index        += 2;
        }
        return new String(bstring);
    }

    /**
     * given a byte array of 16-bit unicode characters, compress to
     * 8-bit and return a string
     *
     * @param string the byte array to be converted
     *
     * @return the converted string
     */

    public static String getFromUnicode(final byte [] string)
    {
        return getFromUnicode(string, 0, string.length / 2);
    }

    /**
     * write compressed unicode
     *
     * @param input the String containing the data to be written
     * @param output the byte array to which the data is to be written
     * @param offset an offset into the byte arrat at which the data
     *               is start when written
     */

    public static void putCompressedUnicode(final String input,
                                            final byte [] output,
                                            final int offset)
    {
        int strlen = input.length();

        for (int k = 0; k < strlen; k++)
        {
            output[ offset + k ] = ( byte ) input.charAt(k);
        }
    }

    /**
     * Write uncompressed unicode
     *
     * @param input the String containing the unicode data to be
     *              written
     * @param output the byte array to hold the uncompressed unicode
     * @param offset the offset to start writing into the byte array
     */

    public static void putUncompressedUnicode(final String input,
                                              final byte [] output,
                                              final int offset)
    {
        int strlen = input.length();

        for (int k = 0; k < strlen; k++)
        {
            char c = input.charAt(k);

            output[ offset + (2 * k) ]     = ( byte ) c;
            output[ offset + (2 * k) + 1 ] = ( byte ) (c >> 8);
        }
    }

    public static String format(String message, Object [] params)
    {
        int          currentParamNumber = 0;
        StringBuffer formattedMessage   = new StringBuffer();

        for (int i = 0; i < message.length(); i++)
        {
            if (message.charAt(i) == '%')
            {
                if (currentParamNumber >= params.length)
                {
                    formattedMessage.append("?missing data?");
                }
                else if ((params[ currentParamNumber ] instanceof Number)
                         && (i + 1 < message.length()))
                {
                    i += matchOptionalFormatting(
                        ( Number ) params[ currentParamNumber++ ],
                        message.substring(i + 1), formattedMessage);
                }
                else
                {
                    formattedMessage
                        .append(params[ currentParamNumber++ ].toString());
                }
            }
            else
            {
                if ((message.charAt(i) == '\\') && (i + 1 < message.length())
                        && (message.charAt(i + 1) == '%'))
                {
                    formattedMessage.append('%');
                    i++;
                }
                else
                {
                    formattedMessage.append(message.charAt(i));
                }
            }
        }
        return formattedMessage.toString();
    }

    private static int matchOptionalFormatting(Number number,
                                               String formatting,
                                               StringBuffer outputTo)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();

        if ((0 < formatting.length())
                && Character.isDigit(formatting.charAt(0)))
        {
            numberFormat
                .setMinimumIntegerDigits(Integer
                    .parseInt(formatting.charAt(0) + ""));
            if ((2 < formatting.length()) && (formatting.charAt(1) == '.')
                    && Character.isDigit(formatting.charAt(2)))
            {
                numberFormat
                    .setMaximumFractionDigits(Integer
                        .parseInt(formatting.charAt(2) + ""));
                numberFormat.format(number, outputTo, new FieldPosition(0));
                return 3;
            }
            numberFormat.format(number, outputTo, new FieldPosition(0));
            return 1;
        }
        else if ((0 < formatting.length()) && (formatting.charAt(0) == '.'))
        {
            if ((1 < formatting.length())
                    && Character.isDigit(formatting.charAt(1)))
            {
                numberFormat
                    .setMaximumFractionDigits(Integer
                        .parseInt(formatting.charAt(1) + ""));
                numberFormat.format(number, outputTo, new FieldPosition(0));
                return 2;
            }
        }
        numberFormat.format(number, outputTo, new FieldPosition(0));
        return 1;
    }
}
