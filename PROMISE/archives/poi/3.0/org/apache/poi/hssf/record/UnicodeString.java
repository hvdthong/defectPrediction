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

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.HexDump;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Title: Unicode String<P>
 * Description:  Unicode String record.  We implement these as a record, although
 *               they are really just standard fields that are in several records.
 *               It is considered more desirable then repeating it in all of them.<P>
 * REFERENCE:  PG 264 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author  Andrew C. Oliver
 * @author Marc Johnson (mjohnson at apache dot org)
 * @author Glen Stampoultzis (glens at apache.org)
 * @version 2.0-pre
 */

public class UnicodeString
    implements Comparable
{
    public final static short sid = 0xFFF;
    private List field_4_format_runs;
    private byte[] field_5_ext_rst;
    private  static final BitField   highByte  = BitFieldFactory.getInstance(0x1);
    private  static final BitField   extBit    = BitFieldFactory.getInstance(0x4);
    private  static final BitField   richText  = BitFieldFactory.getInstance(0x8);

    public static class FormatRun implements Comparable {
      private short character;
      private short fontIndex;

      public FormatRun(short character, short fontIndex) {
        this.character = character;
        this.fontIndex = fontIndex;
      }

      public short getCharacterPos() {
        return character;
      }

      public short getFontIndex() {
        return fontIndex;
      }

      public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != this.getClass()))
    {
            return false;
        }
        FormatRun other = ( FormatRun ) o;

        return ((character == other.character) && (fontIndex == other.fontIndex));
      }

      public int compareTo(Object obj) {
        FormatRun r = (FormatRun)obj;
        if ((character == r.character) && (fontIndex == r.fontIndex))
          return 0;
        if (character == r.character)
          return fontIndex - r.fontIndex;
        else return character - r.character;
      }

      public String toString() {
        return "character="+character+",fontIndex="+fontIndex;
      }
    }

    private UnicodeString() {
    }

    public UnicodeString(String str)
    {
      setString(str);
    }

    /**
     * construct a unicode string record and fill its fields, ID is ignored
     * @param id - ignored
     * @param size - size of the data
     * @param data - the bytes of the string/fields
     */

    public UnicodeString(RecordInputStream in)
    {
      validateSid(in.getSid());
      fillFields(in);
    }


    public int hashCode()
    {
        int stringHash = 0;
        if (field_3_string != null)
            stringHash = field_3_string.hashCode();
        return field_1_charCount + stringHash;
    }

    /**
     * Our handling of equals is inconsistent with compareTo.  The trouble is because we don't truely understand
     * rich text fields yet it's difficult to make a sound comparison.
     *
     * @param o     The object to compare.
     * @return      true if the object is actually equal.
     */
    public boolean equals(Object o)
    {
        if ((o == null) || (o.getClass() != this.getClass()))
        {
            return false;
        }
        UnicodeString other = ( UnicodeString ) o;

        boolean eq = ((field_1_charCount == other.field_1_charCount)
                && (field_2_optionflags == other.field_2_optionflags)
                && field_3_string.equals(other.field_3_string));
        if (!eq) return false;

        if ((field_4_format_runs == null) && (other.field_4_format_runs == null))
          return true;
        if (((field_4_format_runs == null) && (other.field_4_format_runs != null)) ||
             (field_4_format_runs != null) && (other.field_4_format_runs == null))
           return false;

        int size = field_4_format_runs.size();
        if (size != other.field_4_format_runs.size())
          return false;

        for (int i=0;i<size;i++) {
          FormatRun run1 = (FormatRun)field_4_format_runs.get(i);
          FormatRun run2 = (FormatRun)other.field_4_format_runs.get(i);

          if (!run1.equals(run2))
            return false;
    }

        if ((field_5_ext_rst == null) && (other.field_5_ext_rst == null))
          return true;
        if (((field_5_ext_rst == null) && (other.field_5_ext_rst != null)) ||
            ((field_5_ext_rst != null) && (other.field_5_ext_rst == null)))
          return false;
        size = field_5_ext_rst.length;
        if (size != field_5_ext_rst.length)
          return false;

        for (int i=0;i<size;i++) {
          if (field_5_ext_rst[i] != other.field_5_ext_rst[i])
            return false;
        }
        return true;
    }

    /**
     * NO OP
     */

    protected void validateSid(short id)
    {

    }

    /**
     * called by the constructor, should set class level fields.  Should throw
     * runtime exception for bad/icomplete data.
     *
     * @param data raw data
     * @param size size of data
     * @param offset of the records data (provided a big array of the file)
     */
    protected void fillFields(RecordInputStream in)
        {
        field_1_charCount   = in.readShort();
        field_2_optionflags = in.readByte();

        int runCount = 0;
        int extensionLength = 0;
        if ( isRichText() )
        {
            runCount = in.readShort();
        }
        if ( isExtendedText() )
        {
            extensionLength = in.readInt();
        }

        in.setAutoContinue(false);
        StringBuffer tmpString = new StringBuffer(field_1_charCount);
        int stringCharCount = field_1_charCount;
        boolean isCompressed = ((field_2_optionflags & 1) == 0);
        while (stringCharCount != 0) {
          if (in.remaining() == 0) {
            if (in.isContinueNext()) {
              in.nextRecord();
              byte optionflags = in.readByte();
              isCompressed = ((optionflags & 1) == 0);
            } else
              throw new RecordFormatException("Expected continue record.");
          }
          if (isCompressed) {
            char ch = (char)( (short)0xff & (short)in.readByte() );
            tmpString.append(ch);
          } else {
            char ch = (char) in.readShort();
            tmpString.append(ch);
          }
          stringCharCount --;
        }
        field_3_string = tmpString.toString();
        in.setAutoContinue(true);


        if (isRichText() && (runCount > 0)) {
          field_4_format_runs = new ArrayList(runCount);
          for (int i=0;i<runCount;i++) {
            field_4_format_runs.add(new FormatRun(in.readShort(), in.readShort()));
            }
        }

        if (isExtendedText() && (extensionLength > 0)) {
          field_5_ext_rst = new byte[extensionLength];
          for (int i=0;i<extensionLength;i++) {
            field_5_ext_rst[i] = in.readByte();
            }
        }
    }



    /**
     * get the number of characters in the string
     *
     *
     * @return number of characters
     *
     */

    public short getCharCount()
    {
        return field_1_charCount;
    }

    /**
     * set the number of characters in the string
     * @param cc - number of characters
     */

    public void setCharCount(short cc)
    {
        field_1_charCount = cc;
    }

    /**
     * get the option flags which among other things return if this is a 16-bit or
     * 8 bit string
     *
     * @return optionflags bitmask
     *
     */

    public byte getOptionFlags()
    {
        return field_2_optionflags;
    }

    /**
     * set the option flags which among other things return if this is a 16-bit or
     * 8 bit string
     *
     * @param of  optionflags bitmask
     *
     */

    public void setOptionFlags(byte of)
    {
        field_2_optionflags = of;
    }

    /**
     * get the actual string this contains as a java String object
     *
     *
     * @return String
     *
     */

    public String getString()
    {
        return field_3_string;
    }

    /**
     * set the actual string this contains
     * @param string  the text
     */

    public void setString(String string)
    {
        field_3_string = string;
        setCharCount((short)field_3_string.length());
        boolean useUTF16 = false;
        int strlen = string.length();

        for ( int j = 0; j < strlen; j++ )
        {
            if ( string.charAt( j ) > 255 )
        {
                useUTF16 = true;
                break;
            }
        }
        if (useUTF16)
          field_2_optionflags = highByte.setByte(field_2_optionflags);
        else field_2_optionflags = highByte.clearByte(field_2_optionflags);
    }

    public int getFormatRunCount() {
      if (field_4_format_runs == null)
        return 0;
      return field_4_format_runs.size();
    }

    public FormatRun getFormatRun(int index) {
      if (field_4_format_runs == null)
        return null;
      if ((index < 0) || (index >= field_4_format_runs.size()))
        return null;
      return (FormatRun)field_4_format_runs.get(index);
    }

    private int findFormatRunAt(int characterPos) {
      int size = field_4_format_runs.size();
      for (int i=0;i<size;i++) {
        FormatRun r = (FormatRun)field_4_format_runs.get(i);
        if (r.character == characterPos)
          return i;
        else if (r.character > characterPos)
          return -1;
      }
      return -1;
    }

    /** Adds a font run to the formatted string.
     *
     *  If a font run exists at the current charcter location, then it is
     *  replaced with the font run to be added.
     */
    public void addFormatRun(FormatRun r) {
      if (field_4_format_runs == null)
        field_4_format_runs = new ArrayList();

      int index = findFormatRunAt(r.character);
      if (index != -1)
         field_4_format_runs.remove(index);

      field_4_format_runs.add(r);
      Collections.sort(field_4_format_runs);

      field_2_optionflags = richText.setByte(field_2_optionflags);
        }

    public Iterator formatIterator() {
      if (field_4_format_runs != null)
        return field_4_format_runs.iterator();
      return null;
    }

    public void removeFormatRun(FormatRun r) {
      field_4_format_runs.remove(r);
      if (field_4_format_runs.size() == 0) {
        field_4_format_runs = null;
        field_2_optionflags = richText.clearByte(field_2_optionflags);
      }
    }

    public void clearFormatting() {
      field_4_format_runs = null;
      field_2_optionflags = richText.clearByte(field_2_optionflags);
    }

    public byte[] getExtendedRst() {
       return this.field_5_ext_rst;
    }

    public void setExtendedRst(byte[] ext_rst) {
      if (ext_rst != null)
        field_2_optionflags = extBit.setByte(field_2_optionflags);
      else field_2_optionflags = extBit.clearByte(field_2_optionflags);
      this.field_5_ext_rst = ext_rst;
    }

    /**
     * unlike the real records we return the same as "getString()" rather than debug info
     * @see #getDebugInfo()
     * @return String value of the record
     */

    public String toString()
    {
        return getString();
    }

    /**
     * return a character representation of the fields of this record
     *
     *
     * @return String of output for biffviewer etc.
     *
     */

    public String getDebugInfo()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[UNICODESTRING]\n");
        buffer.append("    .charcount       = ")
            .append(Integer.toHexString(getCharCount())).append("\n");
        buffer.append("    .optionflags     = ")
            .append(Integer.toHexString(getOptionFlags())).append("\n");
        buffer.append("    .string          = ").append(getString()).append("\n");
        if (field_4_format_runs != null) {
          for (int i = 0; i < field_4_format_runs.size();i++) {
            FormatRun r = (FormatRun)field_4_format_runs.get(i);
            buffer.append("      .format_run"+i+"          = ").append(r.toString()).append("\n");
          }
        }
        if (field_5_ext_rst != null) {
          buffer.append("    .field_5_ext_rst          = ").append("\n").append(HexDump.toHex(field_5_ext_rst)).append("\n");
        }
        buffer.append("[/UNICODESTRING]\n");
        return buffer.toString();
    }

    private int writeContinueIfRequired(UnicodeRecordStats stats, final int requiredSize, int offset, byte[] data) {
      if (stats.remainingSize < requiredSize) {
        if (stats.lastLengthPos != -1) {
          short lastRecordLength = (short)(offset - stats.lastLengthPos - 2);
          if (lastRecordLength > 8224)
            throw new InternalError();
          LittleEndian.putShort(data, stats.lastLengthPos, lastRecordLength);
        }

        LittleEndian.putShort(data, offset, ContinueRecord.sid);
        offset+=2;
        stats.lastLengthPos = offset;
        offset += 2;

        stats.recordSize += 4;
        stats.remainingSize = SSTRecord.MAX_RECORD_SIZE-4;
      }
      return offset;
        }

    public int serialize(UnicodeRecordStats stats, final int offset, byte [] data)
    {
      int pos = offset;

      pos = writeContinueIfRequired(stats, 3, pos, data);
      LittleEndian.putShort(data, pos, getCharCount());
      pos += 2;
      data[ pos ] = getOptionFlags();
      pos += 1;
      stats.recordSize += 3;
      stats.remainingSize-= 3;

      if (isRichText()) {
        if (field_4_format_runs != null) {
          pos = writeContinueIfRequired(stats, 2, pos, data);

          LittleEndian.putShort(data, pos, (short) field_4_format_runs.size());
          pos += 2;
          stats.recordSize += 2;
          stats.remainingSize -= 2;
        }
      }
      if ( isExtendedText() )
      {
        if (this.field_5_ext_rst != null) {
          pos = writeContinueIfRequired(stats, 4, pos, data);

          LittleEndian.putInt(data, pos, field_5_ext_rst.length);
          pos += 4;
          stats.recordSize += 4;
          stats.remainingSize -= 4;
        }
      }

      int charsize = isUncompressedUnicode() ? 2 : 1;
      int strSize = (getString().length() * charsize);

      byte[] strBytes = null;
        try {
            String unicodeString = getString();
              if (!isUncompressedUnicode())
            {
                strBytes = unicodeString.getBytes("ISO-8859-1");
            }
            else
            {
                  strBytes = unicodeString.getBytes("UTF-16LE");
            }
        }
        catch (Exception e) {
              throw new InternalError();
        }
          if (strSize != strBytes.length)
            throw new InternalError("That shouldnt have happened!");

      if (strSize > stats.remainingSize) {

        int ammountThatCantFit = strSize;
        int strPos = 0;
        while (ammountThatCantFit > 0) {
          int ammountWritten = Math.min(stats.remainingSize, ammountThatCantFit);
          if (isUncompressedUnicode()) {
            if ( ( (ammountWritten ) % 2) == 1)
              ammountWritten--;
          }
          System.arraycopy(strBytes, strPos, data, pos, ammountWritten);
          pos += ammountWritten;
          strPos += ammountWritten;
          stats.recordSize += ammountWritten;
          stats.remainingSize -= ammountWritten;

          ammountThatCantFit -= ammountWritten;

          if (ammountThatCantFit > 0) {
            pos = writeContinueIfRequired(stats, ammountThatCantFit, pos, data);

            data[pos] = (byte) (isUncompressedUnicode() ? 0x1 : 0x0);
            pos++;
            stats.recordSize++;
            stats.remainingSize --;
          }
        }
      } else {
        if (strSize > (data.length-pos))
          System.out.println("Hmm shouldnt happen");
        System.arraycopy(strBytes, 0, data, pos, strSize);
        pos += strSize;
        stats.recordSize += strSize;
        stats.remainingSize -= strSize;
      }


      if (isRichText() && (field_4_format_runs != null)) {
        int count = field_4_format_runs.size();

        for (int i=0;i<count;i++) {
          pos = writeContinueIfRequired(stats, 4, pos, data);
          FormatRun r = (FormatRun)field_4_format_runs.get(i);
          LittleEndian.putShort(data, pos, r.character);
          pos += 2;
          LittleEndian.putShort(data, pos, r.fontIndex);
          pos += 2;

          stats.recordSize += 4;
          stats.remainingSize -=4;
        }
      }

      if (isExtendedText() && (field_5_ext_rst != null)) {
        int ammountThatCantFit = field_5_ext_rst.length - stats.remainingSize;
        int extPos = 0;
        if (ammountThatCantFit > 0) {
          while (ammountThatCantFit > 0) {
            int ammountWritten = Math.min(stats.remainingSize, ammountThatCantFit);
            System.arraycopy(field_5_ext_rst, extPos, data, pos, ammountWritten);
            pos += ammountWritten;
            extPos += ammountWritten;
            stats.recordSize += ammountWritten;
            stats.remainingSize -= ammountWritten;

            ammountThatCantFit -= ammountWritten;
            if (ammountThatCantFit > 0) {
              pos = writeContinueIfRequired(stats, 1, pos, data);
            }
          }
        } else {
          System.arraycopy(field_5_ext_rst, 0, data, pos, field_5_ext_rst.length);
          pos +=  field_5_ext_rst.length;
          stats.remainingSize -= field_5_ext_rst.length;
          stats.recordSize += field_5_ext_rst.length;
        }
      }

        return pos - offset;
    }


    public void setCompressedUnicode() {
      field_2_optionflags = highByte.setByte(field_2_optionflags);
    }

    public void setUncompressedUnicode() {
      field_2_optionflags = highByte.clearByte(field_2_optionflags);
    }

    private boolean isUncompressedUnicode()
    {
        return highByte.isSet(getOptionFlags());
    }

    /** Returns the size of this record, given the ammount of record space
     * remaining, it will also include the size of writing a continue record.
     */

    public static class UnicodeRecordStats {
      public int recordSize;
      public int remainingSize = SSTRecord.MAX_RECORD_SIZE;
      public int lastLengthPos = -1;
    }
    public void getRecordSize(UnicodeRecordStats stats) {
      if (stats.remainingSize < 3) {
         stats.recordSize += 4;
         stats.remainingSize = SSTRecord.MAX_RECORD_SIZE-4;
      }
      stats.recordSize += 3;
      stats.remainingSize-= 3;

      if ( isRichText() )
    {
          if (stats.remainingSize < 2) {
            stats.remainingSize = SSTRecord.MAX_RECORD_SIZE-4;
            stats.recordSize+=4;
    }

          stats.recordSize += 2;
          stats.remainingSize -=2;
      }
      if ( isExtendedText() )
    {
          if (stats.remainingSize < 4) {
            stats.remainingSize = SSTRecord.MAX_RECORD_SIZE-4;
            stats.recordSize+=4;
          }

          stats.recordSize += 4;
          stats.remainingSize -=4;
      }

      int charsize = isUncompressedUnicode() ? 2 : 1;
      int strSize = (getString().length() * charsize);
      if (strSize > stats.remainingSize) {

        int ammountThatCantFit = strSize;
        while (ammountThatCantFit > 0) {
          int ammountWritten = Math.min(stats.remainingSize, ammountThatCantFit);
          if (isUncompressedUnicode()) {
            if ( ( (ammountWritten) % 2) == 1)
              ammountWritten--;
          }
          stats.recordSize += ammountWritten;
          stats.remainingSize -= ammountWritten;

          ammountThatCantFit -= ammountWritten;

          if (ammountThatCantFit > 0) {
            stats.remainingSize = SSTRecord.MAX_RECORD_SIZE-4;
            stats.recordSize+=4;

            stats.recordSize++;
            stats.remainingSize --;
          }
        }
      } else {
        stats.recordSize += strSize;
        stats.remainingSize -= strSize;
      }

      if (isRichText() && (field_4_format_runs != null)) {
        int count = field_4_format_runs.size();

        for (int i=0;i<count;i++) {
          if (stats.remainingSize < 4) {
            stats.remainingSize = SSTRecord.MAX_RECORD_SIZE-4;
            stats.recordSize+=4;
          }

          stats.recordSize += 4;
          stats.remainingSize -=4;
        }
      }

      if (isExtendedText() && (field_5_ext_rst != null)) {
        int ammountThatCantFit = field_5_ext_rst.length - stats.remainingSize;
        if (ammountThatCantFit > 0) {
          while (ammountThatCantFit > 0) {
            int ammountWritten = Math.min(stats.remainingSize, ammountThatCantFit);
            stats.recordSize += ammountWritten;
            stats.remainingSize -= ammountWritten;

            ammountThatCantFit -= ammountWritten;
            if (ammountThatCantFit > 0) {

              stats.remainingSize = SSTRecord.MAX_RECORD_SIZE-4;
              stats.recordSize += 4;
            }
          }
        } else {
          stats.remainingSize -= field_5_ext_rst.length;
          stats.recordSize += field_5_ext_rst.length;
        }
      }
    }



    public short getSid()
    {
        return sid;
    }

    public int compareTo(Object obj)
    {
        UnicodeString str = ( UnicodeString ) obj;

        int result = getString().compareTo(str.getString());

        if (result != 0)
          return result;

        if ((field_4_format_runs == null) && (str.field_4_format_runs == null))
          return 0;

        if ((field_4_format_runs == null) && (str.field_4_format_runs != null))
          return 1;
        if ((field_4_format_runs != null) && (str.field_4_format_runs == null))
          return -1;

        int size = field_4_format_runs.size();
        if (size != str.field_4_format_runs.size())
          return size - str.field_4_format_runs.size();

        for (int i=0;i<size;i++) {
          FormatRun run1 = (FormatRun)field_4_format_runs.get(i);
          FormatRun run2 = (FormatRun)str.field_4_format_runs.get(i);

          result = run1.compareTo(run2);
          if (result != 0)
            return result;
        }

        if ((field_5_ext_rst == null) && (str.field_5_ext_rst == null))
          return 0;
        if ((field_5_ext_rst == null) && (str.field_5_ext_rst != null))
         return 1;
        if ((field_5_ext_rst != null) && (str.field_5_ext_rst == null))
          return -1;

        size = field_5_ext_rst.length;
        if (size != field_5_ext_rst.length)
          return size - field_5_ext_rst.length;

        for (int i=0;i<size;i++) {
          if (field_5_ext_rst[i] != str.field_5_ext_rst[i])
            return field_5_ext_rst[i] - str.field_5_ext_rst[i];
        }
        return 0;
    }

    public boolean isRichText()
    {
      return richText.isSet(getOptionFlags());
    }

    public boolean isExtendedText()
        {
        return extBit.isSet(getOptionFlags());
    }

    public Object clone() {
        UnicodeString str = new UnicodeString();
        str.field_1_charCount = field_1_charCount;
        str.field_2_optionflags = field_2_optionflags;
        str.field_3_string = field_3_string;
        if (field_4_format_runs != null) {
          str.field_4_format_runs = new ArrayList();
          int size = field_4_format_runs.size();
          for (int i = 0; i < size; i++) {
            FormatRun r = (FormatRun) field_4_format_runs.get(i);
            str.field_4_format_runs.add(new FormatRun(r.character, r.fontIndex));
            }
        }
        if (field_5_ext_rst != null) {
          str.field_5_ext_rst = new byte[field_5_ext_rst.length];
          System.arraycopy(field_5_ext_rst, 0, str.field_5_ext_rst, 0,
                           field_5_ext_rst.length);
    }

        return str;
    }


}