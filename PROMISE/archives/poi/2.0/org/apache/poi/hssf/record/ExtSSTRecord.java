package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

import java.util.ArrayList;

/**
 * Title:        Extended Static String Table<P>
 * Description: This record is used for a quick lookup into the SST record. This
 *              record breaks the SST table into a set of buckets. The offsets
 *              to these buckets within the SST record are kept as well as the
 *              position relative to the start of the SST record.
 * REFERENCE:  PG 313 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at apache dot org)
 * @version 2.0-pre
 * @see org.apache.poi.hssf.record.ExtSSTInfoSubRecord
 */

public class ExtSSTRecord
    extends Record
{
    public static final int DEFAULT_BUCKET_SIZE = 8;
    public static final int MAX_BUCKETS = 128;
    public final static short sid = 0xff;
    private short             field_1_strings_per_bucket = DEFAULT_BUCKET_SIZE;
    private ArrayList         field_2_sst_info;


    public ExtSSTRecord()
    {
        field_2_sst_info = new ArrayList();
    }

    /**
     * Constructs a EOFRecord record and sets its fields appropriately.
     *
     * @param id     id must be 0xff or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     */

    public ExtSSTRecord(short id, short size, byte [] data)
    {
        super(id, size, data);
    }

    /**
     * Constructs a EOFRecord record and sets its fields appropriately.
     *
     * @param id     id must be 0xff or an exception will be throw upon validation
     * @param size  the size of the data area of the record
     * @param data  data of the record (should not contain sid/len)
     * @param offset of the record's data
     */

    public ExtSSTRecord(short id, short size, byte [] data, int offset)
    {
        super(id, size, data, offset);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT An EXTSST RECORD");
        }
    }

    protected void fillFields(byte [] data, short size, int offset)
    {
        field_2_sst_info           = new ArrayList();
        field_1_strings_per_bucket = LittleEndian.getShort(data, 0 + offset);
        for (int k = 2; k < (size-offset); k += 8)
        {
            byte[] tempdata = new byte[ 8 + offset ];

            System.arraycopy(data, k, tempdata, 0, 8);
            ExtSSTInfoSubRecord rec = new ExtSSTInfoSubRecord(( short ) 0,
                                          ( short ) 8, tempdata);

            field_2_sst_info.add(rec);
        }
    }

    public void setNumStringsPerBucket(short numStrings)
    {
        field_1_strings_per_bucket = numStrings;
    }

    public void addInfoRecord(ExtSSTInfoSubRecord rec)
    {
        field_2_sst_info.add(rec);
    }

    public short getNumStringsPerBucket()
    {
        return field_1_strings_per_bucket;
    }

    public int getNumInfoRecords()
    {
        return field_2_sst_info.size();
    }

    public ExtSSTInfoSubRecord getInfoRecordAt(int elem)
    {
        return ( ExtSSTInfoSubRecord ) field_2_sst_info.get(elem);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[EXTSST]\n");
        buffer.append("    .dsst           = ")
            .append(Integer.toHexString(getNumStringsPerBucket()))
            .append("\n");
        buffer.append("    .numInfoRecords = ").append(getNumInfoRecords())
            .append("\n");
        for (int k = 0; k < getNumInfoRecords(); k++)
        {
            buffer.append("    .inforecord     = ").append(k).append("\n");
            buffer.append("    .streampos      = ")
                .append(Integer
                .toHexString(getInfoRecordAt(k).getStreamPos())).append("\n");
            buffer.append("    .sstoffset      = ")
                .append(Integer
                .toHexString(getInfoRecordAt(k).getBucketSSTOffset()))
                    .append("\n");
        }
        buffer.append("[/EXTSST]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));
        LittleEndian.putShort(data, 4 + offset, field_1_strings_per_bucket);
        int pos = 6;

        for (int k = 0; k < getNumInfoRecords(); k++)
        {
            ExtSSTInfoSubRecord rec = getInfoRecordAt(k);
            pos += rec.serialize(pos + offset, data);
        }
        return pos;
    }

    /** Returns the size of this record */
    public int getRecordSize()
    {
        return 6 + 8*getNumInfoRecords();
    }

    public static final int getNumberOfInfoRecsForStrings(int numStrings) {
      int infoRecs = (numStrings / DEFAULT_BUCKET_SIZE);
      if ((numStrings % DEFAULT_BUCKET_SIZE) != 0)
        infoRecs ++;
      if (infoRecs > MAX_BUCKETS)
        infoRecs = MAX_BUCKETS;
      return infoRecs;
    }

    /** Given a number of strings (in the sst), returns the size of the extsst record*/
    public static final int getRecordSizeForStrings(int numStrings) {
      return 4 + 2 + (getNumberOfInfoRecsForStrings(numStrings) * 8);
    }

    public short getSid()
    {
        return sid;
    }

    public void setBucketOffsets( int[] bucketAbsoluteOffsets, int[] bucketRelativeOffsets )
    {
        this.field_2_sst_info = new ArrayList(bucketAbsoluteOffsets.length);
        for ( int i = 0; i < bucketAbsoluteOffsets.length; i++ )
        {
            ExtSSTInfoSubRecord r = new ExtSSTInfoSubRecord();
            r.setBucketRecordOffset((short)bucketRelativeOffsets[i]);
            r.setStreamPos(bucketAbsoluteOffsets[i]);
            field_2_sst_info.add(r);
        }
    }

}
