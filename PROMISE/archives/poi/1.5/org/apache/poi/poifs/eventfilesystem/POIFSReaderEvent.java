package org.apache.poi.poifs.eventfilesystem;

import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;

/**
 * Class POIFSReaderEvent
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 * @version %I%, %G%
 */

public class POIFSReaderEvent
{
    private DocumentInputStream stream;
    private POIFSDocumentPath   path;
    private String              documentName;

    /**
     * package scoped constructor
     *
     * @param stream the DocumentInputStream, freshly opened
     * @param path the path of the document
     * @param documentName the name of the document
     */

    POIFSReaderEvent(final DocumentInputStream stream,
                     final POIFSDocumentPath path, final String documentName)
    {
        this.stream       = stream;
        this.path         = path;
        this.documentName = documentName;
    }

    /**
     * @return the DocumentInputStream, freshly opened
     */

    public DocumentInputStream getStream()
    {
        return stream;
    }

    /**
     * @return the document's path
     */

    public POIFSDocumentPath getPath()
    {
        return path;
    }

    /**
     * @return the document's name
     */

    public String getName()
    {
        return documentName;
    }

