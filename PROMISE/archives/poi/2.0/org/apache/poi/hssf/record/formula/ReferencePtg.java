package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.BitField;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.model.Workbook;

/**
 * ReferencePtg - handles references (such as A1, A2, IA4)
 * @author  Andrew C. Oliver (acoliver@apache.org)
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class ReferencePtg extends Ptg
{
    private final static int SIZE = 5;
    public final static byte sid  = 0x24;
    private short            field_1_row;
    private short            field_2_col;
    private BitField         rowRelative = new BitField(0x8000);
    private BitField         colRelative = new BitField(0x4000);

    private ReferencePtg() {
    }
    
    /**
     * Takes in a String represnetation of a cell reference and fills out the 
     * numeric fields.
     */
    public ReferencePtg(String cellref) {
        CellReference c= new CellReference(cellref);
        setRow((short) c.getRow());
        setColumn((short) c.getCol());
        setColRelative(!c.isColAbsolute());
        setRowRelative(!c.isRowAbsolute());
    }

    /** Creates new ValueReferencePtg */

    public ReferencePtg(byte[] data, int offset)
    {
        field_1_row = LittleEndian.getShort(data, offset + 0);
        field_2_col = LittleEndian.getShort(data, offset + 2);

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("[ValueReferencePtg]\n");

        buffer.append("row = ").append(getRow()).append("\n");
        buffer.append("col = ").append(getColumnRaw()).append("\n");
        buffer.append("rowrelative = ").append(isRowRelative()).append("\n");
        buffer.append("colrelative = ").append(isColRelative()).append("\n");
        return buffer.toString();
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[offset] = (byte) (sid + ptgClass);
        LittleEndian.putShort(array,offset+1,field_1_row);
        LittleEndian.putShort(array,offset+3,field_2_col);
    }

    public void setRow(short row)
    {
        field_1_row = row;
    }

    public short getRow()
    {
        return field_1_row;
    }

    public boolean isRowRelative()
    {
        return rowRelative.isSet(field_2_col);
    }
    
    public void setRowRelative(boolean rel) {
        field_2_col=rowRelative.setShortBoolean(field_2_col,rel);
    }
    
    public boolean isColRelative()
    {
        return colRelative.isSet(field_2_col);
    }
    
    public void setColRelative(boolean rel) {
        field_2_col=colRelative.setShortBoolean(field_2_col,rel);
    }

    public void setColumnRaw(short col)
    {
        field_2_col = col;
    }

    public short getColumnRaw()
    {
        return field_2_col;
    }

    public void setColumn(short col)
    {
    }

    public short getColumn()
    {
        return rowRelative.setShortBoolean(colRelative.setShortBoolean(field_2_col,false),false);
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        return (new CellReference(getRow(),getColumn(),!isRowRelative(),!isColRelative())).toString();
    }
    
    public byte getDefaultOperandClass() {
        return Ptg.CLASS_REF;
    }
    
    public Object clone() {
      ReferencePtg ptg = new ReferencePtg();
      ptg.field_1_row = field_1_row;
      ptg.field_2_col = field_2_col;
      ptg.setClass(ptgClass);
      return ptg;
    }
}