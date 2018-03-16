package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A String Constant Pool Entry. The String info contains an index into the
 * constant pool where a UTF8 string is stored.
 *
 */
public class StringCPInfo extends ConstantCPInfo {

    /** Constructor.  */
    public StringCPInfo() {
        super(CONSTANT_STRING, 1);
    }

    /**
     * read a constant pool entry from a class stream.
     *
     * @param cpStream the DataInputStream which contains the constant pool
     *      entry to be read.
     * @exception IOException if there is a problem reading the entry from
     *      the stream.
     */
    public void read(DataInputStream cpStream) throws IOException {
        index = cpStream.readUnsignedShort();

        setValue("unresolved");
    }

    /**
     * Print a readable version of the constant pool entry.
     *
     * @return the string representation of this constant pool entry.
     */
    public String toString() {
        return "String Constant Pool Entry for "
            + getValue() + "[" + index + "]";
    }

    /**
     * Resolve this constant pool entry with respect to its dependents in
     * the constant pool.
     *
     * @param constantPool the constant pool of which this entry is a member
     *      and against which this entry is to be resolved.
     */
    public void resolve(ConstantPool constantPool) {
        setValue(((Utf8CPInfo) constantPool.getEntry(index)).getValue());
        super.resolve(constantPool);
    }

    /** the index into the constant pool containing the string's content */
    private int index;
}

