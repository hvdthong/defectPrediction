package org.apache.xml.utils;

/**
 * <meta name="usage" content="internal"/>
 * A very simple table that stores a list of StringToStringTables, optimized
 * for small lists.
 */
public class StringToStringTableVector
{

  /** Size of blocks to allocate         */
  private int m_blocksize;

  /** Array of StringToStringTable objects          */
  private StringToStringTable m_map[];

  /** Number of StringToStringTable objects in this array          */
  private int m_firstFree = 0;

  /** Size of this array          */
  private int m_mapSize;

  /**
   * Default constructor.  Note that the default
   * block size is very small, for small lists.
   */
  public StringToStringTableVector()
  {

    m_blocksize = 8;
    m_mapSize = m_blocksize;
    m_map = new StringToStringTable[m_blocksize];
  }

  /**
   * Construct a StringToStringTableVector, using the given block size.
   *
   * @param blocksize Size of blocks to allocate 
   */
  public StringToStringTableVector(int blocksize)
  {

    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new StringToStringTable[blocksize];
  }

  /**
   * Get the length of the list.
   *
   * @return Number of StringToStringTable objects in the list
   */
  public final int getLength()
  {
    return m_firstFree;
  }

  /**
   * Get the length of the list.
   *
   * @return Number of StringToStringTable objects in the list
   */
  public final int size()
  {
    return m_firstFree;
  }

  /**
   * Append a StringToStringTable object onto the vector.
   *
   * @param value StringToStringTable object to add
   */
  public final void addElement(StringToStringTable value)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      StringToStringTable newMap[] = new StringToStringTable[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Given a string, find the last added occurance value
   * that matches the key.
   *
   * @param key String to look up
   *
   * @return the last added occurance value that matches the key
   * or null if not found.
   */
  public final String get(String key)
  {

    for (int i = m_firstFree - 1; i >= 0; --i)
    {
      String nsuri = m_map[i].get(key);

      if (nsuri != null)
        return nsuri;
    }

    return null;
  }

  /**
   * Given a string, find out if there is a value in this table
   * that matches the key.
   *
   * @param key String to look for  
   *
   * @return True if the string was found in table, null if not
   */
  public final boolean containsKey(String key)
  {

    for (int i = m_firstFree - 1; i >= 0; --i)
    {
      if (m_map[i].get(key) != null)
        return true;
    }

    return false;
  }

  /**
   * Remove the last element.
   */
  public final void removeLastElem()
  {

    if (m_firstFree > 0)
    {
      m_map[m_firstFree] = null;

      m_firstFree--;
    }
  }

  /**
   * Get the nth element.
   *
   * @param i Index of element to find
   *
   * @return The StringToStringTable object at the given index
   */
  public final StringToStringTable elementAt(int i)
  {
    return m_map[i];
  }

  /**
   * Tell if the table contains the given StringToStringTable.
   *
   * @param s The StringToStringTable to find
   *
   * @return True if the StringToStringTable is found
   */
  public final boolean contains(StringToStringTable s)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i].equals(s))
        return true;
    }

    return false;
  }
}
