   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at


   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hssf.record;

import org.apache.poi.util.*;

/**
 * Record for the top margin.
 * NOTE: This source was automatically generated.
 *
 * @author Shawn Laubach (slaubach at apache dot org)
 */
public class TopMarginRecord extends Record implements Margin
{
    public final static short sid = 0x28;
    private double field_1_margin;

    public TopMarginRecord()    {    }

    /**
     * Constructs a TopMargin record and sets its fields appropriately.
     *
     * @param id    id must be 0x28 or an exception
     *               will be throw upon validation
     * @param size  size the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     */
    public TopMarginRecord( RecordInputStream in )
    {        super( in );    }

    /**
     * Checks the sid matches the expected side for this record
     *
     * @param id   the expected sid.
     */
    protected void validateSid( short id )
    {
        if ( id != sid )
        {
            throw new RecordFormatException( "Not a TopMargin record" );
        }
    }

    protected void fillFields( RecordInputStream in )
    {
        field_1_margin = in.readDouble();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[TopMargin]\n" );
        buffer.append( "    .margin               = " ).append( " (" ).append( getMargin() ).append( " )\n" );
        buffer.append( "[/TopMargin]\n" );
        return buffer.toString();
    }

    public int serialize( int offset, byte[] data )
    {
        LittleEndian.putShort( data, 0 + offset, sid );
        LittleEndian.putShort( data, 2 + offset, (short) ( getRecordSize() - 4 ) );
        LittleEndian.putDouble( data, 4 + offset, field_1_margin );
        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()    {        return 4 + 8;    }

    public short getSid()    {        return sid;    }

    /**
     * Get the margin field for the TopMargin record.
     */
    public double getMargin()    {        return field_1_margin;    }

    /**
     * Set the margin field for the TopMargin record.
     */
    public void setMargin( double field_1_margin )
    {        this.field_1_margin = field_1_margin;    }

    public Object clone()
    {
        TopMarginRecord rec = new TopMarginRecord();
        rec.field_1_margin = this.field_1_margin;
        return rec;
    }
