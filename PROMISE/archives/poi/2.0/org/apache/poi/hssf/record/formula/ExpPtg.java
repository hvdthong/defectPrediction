package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;

/**
 *
 * @author  andy
 * @author Jason Height (jheight at chariot dot net dot au)
 * @author dmui (save existing implementation)
 */

public class ExpPtg
    extends Ptg
{
    private final static int  SIZE = 5;
    public final static short sid  = 0x1;
	 private	byte[] existing = null;

    /** Creates new ExpPtg */

    public ExpPtg()
    {
    }

    /** Creates new ExpPtg */

    public ExpPtg(byte [] array, int offset)
    {
    	existing = new byte[this.getSize()];
    	System.arraycopy(array, offset, existing, 0, this.getSize());
    }

    public void writeBytes(byte [] array, int offset)
    {
    	if (existing != null) {
			System.arraycopy(existing, 0, array, offset, existing.length);
    	}
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        return "NO IDEA SHARED FORMULA EXP PTG";
    }
    
    public byte getDefaultOperandClass() {return Ptg.CLASS_VALUE;}
    
    public Object clone() {
		if (this.existing == null) throw new RuntimeException("NO IDEA SHARED FORMULA EXP PTG"); 
		
    	return new ExpPtg(this.existing, 0);
    }

}
