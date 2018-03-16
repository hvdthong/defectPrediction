   Copyright 2002-2004   Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at


   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */


package org.apache.poi.poifs.eventfilesystem;

import java.io.*;

import java.util.*;

import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.HeaderBlockReader;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.SmallBlockTableReader;

/**
 * An event-driven reader for POIFS file systems. Users of this class
 * first create an instance of it, then use the registerListener
 * methods to register POIFSReaderListener instances for specific
 * documents. Once all the listeners have been registered, the read()
 * method is called, which results in the listeners being notified as
 * their documents are read.
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class POIFSReader
{
    private POIFSReaderRegistry registry;
    private boolean             registryClosed;

    /**
     * Create a POIFSReader
     */

    public POIFSReader()
    {
        registry       = new POIFSReaderRegistry();
        registryClosed = false;
    }

    /**
     * Read from an InputStream and process the documents we get
     *
     * @param stream the InputStream from which to read the data
     *
     * @exception IOException on errors reading, or on invalid data
     */

    public void read(final InputStream stream)
        throws IOException
    {
        registryClosed = true;

        HeaderBlockReader header_block_reader = new HeaderBlockReader(stream);

        RawDataBlockList  data_blocks         = new RawDataBlockList(stream);

        new BlockAllocationTableReader(header_block_reader.getBATCount(),
                                       header_block_reader.getBATArray(),
                                       header_block_reader.getXBATCount(),
                                       header_block_reader.getXBATIndex(),
                                       data_blocks);

        PropertyTable properties =
            new PropertyTable(header_block_reader.getPropertyStart(),
                              data_blocks);

        processProperties(SmallBlockTableReader
            .getSmallDocumentBlocks(data_blocks, properties
                .getRoot(), header_block_reader
                    .getSBATStart()), data_blocks, properties.getRoot()
                        .getChildren(), new POIFSDocumentPath());
    }

    /**
     * Register a POIFSReaderListener for all documents
     *
     * @param listener the listener to be registered
     *
     * @exception NullPointerException if listener is null
     * @exception IllegalStateException if read() has already been
     *                                  called
     */

    public void registerListener(final POIFSReaderListener listener)
    {
        if (listener == null)
        {
            throw new NullPointerException();
        }
        if (registryClosed)
        {
            throw new IllegalStateException();
        }
        registry.registerListener(listener);
    }

    /**
     * Register a POIFSReaderListener for a document in the root
     * directory
     *
     * @param listener the listener to be registered
     * @param name the document name
     *
     * @exception NullPointerException if listener is null or name is
     *                                 null or empty
     * @exception IllegalStateException if read() has already been
     *                                  called
     */

    public void registerListener(final POIFSReaderListener listener,
                                 final String name)
    {
        registerListener(listener, null, name);
    }

    /**
     * Register a POIFSReaderListener for a document in the specified
     * directory
     *
     * @param listener the listener to be registered
     * @param path the document path; if null, the root directory is
     *             assumed
     * @param name the document name
     *
     * @exception NullPointerException if listener is null or name is
     *                                 null or empty
     * @exception IllegalStateException if read() has already been
     *                                  called
     */

    public void registerListener(final POIFSReaderListener listener,
                                 final POIFSDocumentPath path,
                                 final String name)
    {
        if ((listener == null) || (name == null) || (name.length() == 0))
        {
            throw new NullPointerException();
        }
        if (registryClosed)
        {
            throw new IllegalStateException();
        }
        registry.registerListener(listener,
                                  (path == null) ? new POIFSDocumentPath()
                                                 : path, name);
    }

    /**
     * read in files
     *
     * @param args names of the files
     *
     * @exception IOException
     */

    public static void main(String args[])
        throws IOException
    {
        if (args.length == 0)
        {
            System.err
                .println("at least one argument required: input filename(s)");
            System.exit(1);
        }

        for (int j = 0; j < args.length; j++)
        {
            POIFSReader         reader   = new POIFSReader();
            POIFSReaderListener listener = new SampleListener();

            reader.registerListener(listener);
            System.out.println("reading " + args[ j ]);
            FileInputStream istream = new FileInputStream(args[ j ]);

            reader.read(istream);
            istream.close();
        }
    }

    private void processProperties(final BlockList small_blocks,
                                   final BlockList big_blocks,
                                   final Iterator properties,
                                   final POIFSDocumentPath path)
        throws IOException
    {
        while (properties.hasNext())
        {
            Property property = ( Property ) properties.next();
            String   name     = property.getName();

            if (property.isDirectory())
            {
                POIFSDocumentPath new_path = new POIFSDocumentPath(path,
                                                 new String[]
                {
                    name
                });

                processProperties(
                    small_blocks, big_blocks,
                    (( DirectoryProperty ) property).getChildren(), new_path);
            }
            else
            {
                int      startBlock = property.getStartBlock();
                Iterator listeners  = registry.getListeners(path, name);

                if (listeners.hasNext())
                {
                    int           size     = property.getSize();
                    POIFSDocument document = null;

                    if (property.shouldUseSmallBlocks())
                    {
                        document =
                            new POIFSDocument(name, small_blocks
                                .fetchBlocks(startBlock), size);
                    }
                    else
                    {
                        document =
                            new POIFSDocument(name, big_blocks
                                .fetchBlocks(startBlock), size);
                    }
                    while (listeners.hasNext())
                    {
                        POIFSReaderListener listener =
                            ( POIFSReaderListener ) listeners.next();

                        listener.processPOIFSReaderEvent(
                            new POIFSReaderEvent(
                                new DocumentInputStream(document), path,
                                name));
                    }
                }
                else
                {

                    if (property.shouldUseSmallBlocks())
                    {
                        small_blocks.fetchBlocks(startBlock);
                    }
                    else
                    {
                        big_blocks.fetchBlocks(startBlock);
                    }
                }
            }
        }
    }

    private static class SampleListener
        implements POIFSReaderListener
    {

        /**
         * Constructor SampleListener
         */

        SampleListener()
        {
        }

        /**
         * Method processPOIFSReaderEvent
         *
         * @param event
         */

        public void processPOIFSReaderEvent(final POIFSReaderEvent event)
        {
            DocumentInputStream istream = event.getStream();
            POIFSDocumentPath   path    = event.getPath();
            String              name    = event.getName();

            try
            {
                byte[] data = new byte[ istream.available() ];

                istream.read(data);
                int pathLength = path.length();

                for (int k = 0; k < pathLength; k++)
                {
                    System.out.print("/" + path.getComponent(k));
                }
                System.out.println("/" + name + ": " + data.length
                                   + " bytes read");
            }
            catch (IOException ignored)
            {
            }
        }

