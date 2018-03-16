package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordInputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Glen Stampoultzis
 * @version $Id: ColumnInfoRecordsAggregate.java 496526 2007-01-15 22:46:35Z markt $
 */
public class ColumnInfoRecordsAggregate
    extends Record
{
    List records = null;

    public ColumnInfoRecordsAggregate()
    {
        records = new ArrayList();
    }

    /** You never fill an aggregate */
    protected void fillFields(RecordInputStream in)
    {
    }

    /** Not required by an aggregate */
    protected void validateSid(short id)
    {
    }

    /** It's an aggregate... just made something up */
    public short getSid()
    {
        return -1012;
    }

    public int getRecordSize()
    {
        int size = 0;
        for ( Iterator iterator = records.iterator(); iterator.hasNext(); )
            size += ( (ColumnInfoRecord) iterator.next() ).getRecordSize();
        return size;
    }

    public Iterator getIterator()
    {
        return records.iterator();
    }

    /**
     * Performs a deep clone of the record
     */
    public Object clone()
    {
        ColumnInfoRecordsAggregate rec = new ColumnInfoRecordsAggregate();
        for (int k = 0; k < records.size(); k++)
        {
            ColumnInfoRecord ci = ( ColumnInfoRecord ) records.get(k);
            ci=(ColumnInfoRecord) ci.clone();
            rec.insertColumn( ci );
        }
        return rec;
    }

    /**
     * Inserts a column into the aggregate (at the end of the list).
     */
    public void insertColumn( ColumnInfoRecord col )
    {
        records.add( col );
    }

    /**
     * Inserts a column into the aggregate (at the position specified
     * by <code>idx</code>.
     */
    public void insertColumn( int idx, ColumnInfoRecord col )
    {
        records.add( idx, col );
    }

    public int getNumColumns( )
    {
        return records.size();
    }

    /**
     * called by the class that is responsible for writing this sucker.
     * Subclasses should implement this so that their data is passed back in a
     * byte array.
     *
     * @param offset    offset to begin writing at
     * @param data      byte array containing instance data
     * @return          number of bytes written
     */
    public int serialize(int offset, byte [] data)
    {
        Iterator itr = records.iterator();
        int      pos = offset;

        while (itr.hasNext())
        {
            pos += (( Record ) itr.next()).serialize(pos, data);
        }
        return pos - offset;
    }

    public int findStartOfColumnOutlineGroup(int idx)
    {
        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get( idx );
        int level = columnInfo.getOutlineLevel();
        while (idx != 0)
        {
            ColumnInfoRecord prevColumnInfo = (ColumnInfoRecord) records.get( idx - 1 );
            if (columnInfo.getFirstColumn() - 1 == prevColumnInfo.getLastColumn())
            {
                if (prevColumnInfo.getOutlineLevel() < level)
                {
                    break;
                }
                idx--;
                columnInfo = prevColumnInfo;
            }
            else
            {
                break;
            }
        }

        return idx;
    }

    public int findEndOfColumnOutlineGroup(int idx)
    {
        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get( idx );
        int level = columnInfo.getOutlineLevel();
        while (idx < records.size() - 1)
        {
            ColumnInfoRecord nextColumnInfo = (ColumnInfoRecord) records.get( idx + 1 );
            if (columnInfo.getLastColumn() + 1 == nextColumnInfo.getFirstColumn())
            {
                if (nextColumnInfo.getOutlineLevel() < level)
                {
                    break;
                }
                idx++;
                columnInfo = nextColumnInfo;
            }
            else
            {
                break;
            }
        }

        return idx;
    }

    public ColumnInfoRecord getColInfo(int idx)
    {
        return (ColumnInfoRecord) records.get( idx );
    }

    public ColumnInfoRecord writeHidden( ColumnInfoRecord columnInfo, int idx, boolean hidden )
    {
        int level = columnInfo.getOutlineLevel();
        while (idx < records.size())
        {
            columnInfo.setHidden( hidden );
            if (idx + 1 < records.size())
            {
                ColumnInfoRecord nextColumnInfo = (ColumnInfoRecord) records.get( idx + 1 );
                if (columnInfo.getLastColumn() + 1 == nextColumnInfo.getFirstColumn())
                {
                    if (nextColumnInfo.getOutlineLevel() < level)
                        break;
                    columnInfo = nextColumnInfo;
                }
                else
                {
                    break;
                }
            }
            idx++;
        }
        return columnInfo;
    }

    public boolean isColumnGroupCollapsed( int idx )
    {
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup( idx );
        if (endOfOutlineGroupIdx >= records.size())
            return false;
        if (getColInfo(endOfOutlineGroupIdx).getLastColumn() + 1 != getColInfo(endOfOutlineGroupIdx + 1).getFirstColumn())
            return false;
        else
            return getColInfo(endOfOutlineGroupIdx+1).getCollapsed();
    }


    public boolean isColumnGroupHiddenByParent( int idx )
    {
        int endLevel;
        boolean endHidden;
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup( idx );
        if (endOfOutlineGroupIdx >= records.size())
        {
            endLevel = 0;
            endHidden = false;
        }
        else if (getColInfo(endOfOutlineGroupIdx).getLastColumn() + 1 != getColInfo(endOfOutlineGroupIdx + 1).getFirstColumn())
        {
            endLevel = 0;
            endHidden = false;
        }
        else
        {
            endLevel = getColInfo( endOfOutlineGroupIdx + 1).getOutlineLevel();
            endHidden = getColInfo( endOfOutlineGroupIdx + 1).getHidden();
        }

        int startLevel;
        boolean startHidden;
        int startOfOutlineGroupIdx = findStartOfColumnOutlineGroup( idx );
        if (startOfOutlineGroupIdx <= 0)
        {
            startLevel = 0;
            startHidden = false;
        }
        else if (getColInfo(startOfOutlineGroupIdx).getFirstColumn() - 1 != getColInfo(startOfOutlineGroupIdx - 1).getLastColumn())
        {
            startLevel = 0;
            startHidden = false;
        }
        else
        {
            startLevel = getColInfo( startOfOutlineGroupIdx - 1).getOutlineLevel();
            startHidden = getColInfo( startOfOutlineGroupIdx - 1 ).getHidden();
        }

        if (endLevel > startLevel)
        {
            return endHidden;
        }
        else
        {
            return startHidden;
        }
    }

    public void collapseColumn( short columnNumber )
    {
        int idx = findColumnIdx( columnNumber, 0 );
        if (idx == -1)
            return;

        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get( findStartOfColumnOutlineGroup( idx ) );

        columnInfo = writeHidden( columnInfo, idx, true );

        setColumn( (short) ( columnInfo.getLastColumn() + 1 ), null, null, null, null, Boolean.TRUE);
    }

    public void expandColumn( short columnNumber )
    {
        int idx = findColumnIdx( columnNumber, 0 );
        if (idx == -1)
            return;

        if (!isColumnGroupCollapsed(idx))
            return;

        int startIdx = findStartOfColumnOutlineGroup( idx );
        ColumnInfoRecord columnInfo = getColInfo( startIdx );

        int endIdx = findEndOfColumnOutlineGroup( idx );
        ColumnInfoRecord endColumnInfo = getColInfo( endIdx );

        if (!isColumnGroupHiddenByParent( idx ))
        {
            for (int i = startIdx; i <= endIdx; i++)
            {
                if (columnInfo.getOutlineLevel() == getColInfo(i).getOutlineLevel())
                    getColInfo(i).setHidden( false );
            }
        }

        setColumn( (short) ( columnInfo.getLastColumn() + 1 ), null, null, null, null, Boolean.FALSE);
    }

    /**
     * creates the ColumnInfo Record and sets it to a default column/width
     * @see org.apache.poi.hssf.record.ColumnInfoRecord
     * @return record containing a ColumnInfoRecord
     */
    public static Record createColInfo()
    {
        ColumnInfoRecord retval = new ColumnInfoRecord();

        retval.setColumnWidth(( short ) 2275);
        retval.setOptions(( short ) 2);
        retval.setXFIndex(( short ) 0x0f);
        return retval;
    }


    public void setColumn(short column, Short xfIndex, Short width, Integer level, Boolean hidden, Boolean collapsed)
    {
        ColumnInfoRecord ci = null;
        int              k  = 0;

        for (k = 0; k < records.size(); k++)
        {
            ci = ( ColumnInfoRecord ) records.get(k);
            if ((ci.getFirstColumn() <= column)
                    && (column <= ci.getLastColumn()))
            {
                break;
            }
            ci = null;
        }

        if (ci != null)
        {
	    boolean styleChanged = xfIndex != null && ci.getXFIndex() != xfIndex.shortValue();
            boolean widthChanged = width != null && ci.getColumnWidth() != width.shortValue();
            boolean levelChanged = level != null && ci.getOutlineLevel() != level.intValue();
            boolean hiddenChanged = hidden != null && ci.getHidden() != hidden.booleanValue();
            boolean collapsedChanged = collapsed != null && ci.getCollapsed() != collapsed.booleanValue();
            boolean columnChanged = styleChanged || widthChanged || levelChanged || hiddenChanged || collapsedChanged;
            if (!columnChanged)
            {
            }
            else if ((ci.getFirstColumn() == column)
                     && (ci.getLastColumn() == column))
                setColumnInfoFields( ci, xfIndex, width, level, hidden, collapsed );
            }
            else if ((ci.getFirstColumn() == column)
                     || (ci.getLastColumn() == column))
            {
                if (ci.getFirstColumn() == column)
                {
                    ci.setFirstColumn(( short ) (column + 1));
                }
                else
                {
                    ci.setLastColumn(( short ) (column - 1));
                }
                ColumnInfoRecord nci = ( ColumnInfoRecord ) createColInfo();

                nci.setFirstColumn(column);
                nci.setLastColumn(column);
                nci.setOptions(ci.getOptions());
                nci.setXFIndex(ci.getXFIndex());
                setColumnInfoFields( nci, xfIndex, width, level, hidden, collapsed );

                insertColumn(k, nci);
            }
            else
            {
                short lastcolumn = ci.getLastColumn();
                ci.setLastColumn(( short ) (column - 1));

                ColumnInfoRecord nci = ( ColumnInfoRecord ) createColInfo();
                nci.setFirstColumn(column);
                nci.setLastColumn(column);
                nci.setOptions(ci.getOptions());
                nci.setXFIndex(ci.getXFIndex());
                setColumnInfoFields( nci, xfIndex, width, level, hidden, collapsed );
                insertColumn(++k, nci);

                nci = ( ColumnInfoRecord ) createColInfo();
                nci.setFirstColumn((short)(column+1));
                nci.setLastColumn(lastcolumn);
                nci.setOptions(ci.getOptions());
                nci.setXFIndex(ci.getXFIndex());
                nci.setColumnWidth(ci.getColumnWidth());
                insertColumn(++k, nci);
            }
        }
        else
        {

            ColumnInfoRecord nci = ( ColumnInfoRecord ) createColInfo();

            nci.setFirstColumn(column);
            nci.setLastColumn(column);
            setColumnInfoFields( nci, xfIndex, width, level, hidden, collapsed );
            insertColumn(k, nci);
        }
    }

    /**
     * Sets all non null fields into the <code>ci</code> parameter.
     */
    private void setColumnInfoFields( ColumnInfoRecord ci, Short xfStyle, Short width, Integer level, Boolean hidden, Boolean collapsed )
    {
	if (xfStyle != null)
	    ci.setXFIndex(xfStyle.shortValue());
        if (width != null)
            ci.setColumnWidth(width.shortValue());
        if (level != null)
            ci.setOutlineLevel( level.shortValue() );
        if (hidden != null)
            ci.setHidden( hidden.booleanValue() );
        if (collapsed != null)
            ci.setCollapsed( collapsed.booleanValue() );
    }

    public int findColumnIdx(int column, int fromIdx)
    {
        if (column < 0)
            throw new IllegalArgumentException( "column parameter out of range: " + column );
        if (fromIdx < 0)
            throw new IllegalArgumentException( "fromIdx parameter out of range: " + fromIdx );

        ColumnInfoRecord ci;
        for (int k = fromIdx; k < records.size(); k++)
        {
            ci = ( ColumnInfoRecord ) records.get(k);
            if ((ci.getFirstColumn() <= column)
                    && (column <= ci.getLastColumn()))
            {
                return k;
            }
            ci = null;
        }
        return -1;
    }

    public void collapseColInfoRecords( int columnIdx )
    {
        if (columnIdx == 0)
            return;
        ColumnInfoRecord previousCol = (ColumnInfoRecord) records.get( columnIdx - 1);
        ColumnInfoRecord currentCol = (ColumnInfoRecord) records.get( columnIdx );
        boolean adjacentColumns = previousCol.getLastColumn() == currentCol.getFirstColumn() - 1;
        if (!adjacentColumns)
            return;

        boolean columnsMatch =
                previousCol.getXFIndex() == currentCol.getXFIndex() &&
                previousCol.getOptions() == currentCol.getOptions() &&
                previousCol.getColumnWidth() == currentCol.getColumnWidth();

        if (columnsMatch)
        {
            previousCol.setLastColumn( currentCol.getLastColumn() );
            records.remove( columnIdx );
        }
    }

    /**
     * Creates an outline group for the specified columns.
     * @param fromColumn    group from this column (inclusive)
     * @param toColumn      group to this column (inclusive)
     * @param indent        if true the group will be indented by one level,
     *                      if false indenting will be removed by one level.
     */
    public void groupColumnRange(short fromColumn, short toColumn, boolean indent)
    {

        int fromIdx = 0;
        for (int i = fromColumn; i <= toColumn; i++)
        {
            int level = 1;
            int columnIdx = findColumnIdx( i, Math.max(0,fromIdx) );
            if (columnIdx != -1)
            {
                level = ((ColumnInfoRecord)records.get( columnIdx )).getOutlineLevel();
                if (indent) level++; else level--;
                level = Math.max(0, level);
                level = Math.min(7, level);
            }
            setColumn((short)i, null, null, new Integer(level), null, null);
            columnIdx = findColumnIdx( i, Math.max(0, fromIdx ) );
            collapseColInfoRecords( columnIdx );
        }

    }


}
