package org.apache.poi.poifs.storage;

import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.IOUtils;

import java.io.*;

/**
 * A big block created from an InputStream, holding the raw data
 *
 * @author Marc Johnson (mjohnson at apache dot org
 */

public class RawDataBlock
    implements ListManagedBlock
{
    private byte[]  _data;
    private boolean _eof;

    /**
     * Constructor RawDataBlock
     *
     * @param stream the InputStream from which the data will be read
     *
     * @exception IOException on I/O errors, and if an insufficient
     *            amount of data is read
     */

    public RawDataBlock(final InputStream stream)
        throws IOException
    {
        _data = new byte[ POIFSConstants.BIG_BLOCK_SIZE ];
        int count = IOUtils.readFully(stream, _data);

        if (count == -1)
        {
            _eof = true;
        }
        else if (count != POIFSConstants.BIG_BLOCK_SIZE)
        {
            String type = " byte" + ((count == 1) ? ("")
                                                  : ("s"));

            throw new IOException("Unable to read entire block; " + count
                                  + type + " read; expected "
                                  + POIFSConstants.BIG_BLOCK_SIZE + " bytes");
        }
        else
        {
            _eof = false;
        }
    }

    /**
     * When we read the data, did we hit end of file?
     *
     * @return true if no data was read because we were at the end of
     *         the file, else false
     *
     * @exception IOException
     */

    public boolean eof()
        throws IOException
    {
        return _eof;
    }

    /* ********** START implementation of ListManagedBlock ********** */

    /**
     * Get the data from the block
     *
     * @return the block's data as a byte array
     *
     * @exception IOException if there is no data
     */

    public byte [] getData()
        throws IOException
    {
        if (eof())
        {
            throw new IOException("Cannot return empty data");
        }
        return _data;
    }

    /* **********  END  implementation of ListManagedBlock ********** */

