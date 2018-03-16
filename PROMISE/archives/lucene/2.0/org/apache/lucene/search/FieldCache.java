import org.apache.lucene.index.IndexReader;
import java.io.IOException;

/**
 * Expert: Maintains caches of term values.
 *
 * <p>Created: May 19, 2004 11:13:14 AM
 *
 * @author  Tim Jones (Nacimiento Software)
 * @since   lucene 1.4
 * @version $Id: FieldCache.java 179605 2005-06-02 16:48:40Z cutting $
 */
public interface FieldCache {

  /** Indicator for StringIndex values in the cache. */
  public static final int STRING_INDEX = -1;


  /** Expert: Stores term text values and document ordering data. */
  public static class StringIndex {

    /** All the term values, in natural order. */
    public final String[] lookup;

    /** For each document, an index into the lookup array. */
    public final int[] order;

    /** Creates one of these objects */
    public StringIndex (int[] values, String[] lookup) {
      this.order = values;
      this.lookup = lookup;
    }
  }

  /** Interface to parse ints from document fields.
   * @see #getInts(IndexReader, String, IntParser)
   */
  public interface IntParser {
    /** Return an integer representation of this field's value. */
    public int parseInt(String string);
  }


  /** Interface to parse floats from document fields.
   * @see #getFloats(IndexReader, String, FloatParser)
   */
  public interface FloatParser {
    /** Return an float representation of this field's value. */
    public float parseFloat(String string);
  }

  /** Expert: The cache used internally by sorting and range query classes. */
  public static FieldCache DEFAULT = new FieldCacheImpl();


  /** Checks the internal cache for an appropriate entry, and if none is
   * found, reads the terms in <code>field</code> as integers and returns an array
   * of size <code>reader.maxDoc()</code> of the value each document
   * has in the given field.
   * @param reader  Used to get field values.
   * @param field   Which field contains the integers.
   * @return The values in the given field for each document.
   * @throws IOException  If any error occurs.
   */
  public int[] getInts (IndexReader reader, String field)
  throws IOException;

  /** Checks the internal cache for an appropriate entry, and if none is found,
   * reads the terms in <code>field</code> as integers and returns an array of
   * size <code>reader.maxDoc()</code> of the value each document has in the
   * given field.
   * @param reader  Used to get field values.
   * @param field   Which field contains the integers.
   * @param parser  Computes integer for string values.
   * @return The values in the given field for each document.
   * @throws IOException  If any error occurs.
   */
  public int[] getInts (IndexReader reader, String field, IntParser parser)
  throws IOException;

  /** Checks the internal cache for an appropriate entry, and if
   * none is found, reads the terms in <code>field</code> as floats and returns an array
   * of size <code>reader.maxDoc()</code> of the value each document
   * has in the given field.
   * @param reader  Used to get field values.
   * @param field   Which field contains the floats.
   * @return The values in the given field for each document.
   * @throws IOException  If any error occurs.
   */
  public float[] getFloats (IndexReader reader, String field)
  throws IOException;

  /** Checks the internal cache for an appropriate entry, and if
   * none is found, reads the terms in <code>field</code> as floats and returns an array
   * of size <code>reader.maxDoc()</code> of the value each document
   * has in the given field.
   * @param reader  Used to get field values.
   * @param field   Which field contains the floats.
   * @param parser  Computes float for string values.
   * @return The values in the given field for each document.
   * @throws IOException  If any error occurs.
   */
  public float[] getFloats (IndexReader reader, String field,
                            FloatParser parser) throws IOException;

  /** Checks the internal cache for an appropriate entry, and if none
   * is found, reads the term values in <code>field</code> and returns an array
   * of size <code>reader.maxDoc()</code> containing the value each document
   * has in the given field.
   * @param reader  Used to get field values.
   * @param field   Which field contains the strings.
   * @return The values in the given field for each document.
   * @throws IOException  If any error occurs.
   */
  public String[] getStrings (IndexReader reader, String field)
  throws IOException;

  /** Checks the internal cache for an appropriate entry, and if none
   * is found reads the term values in <code>field</code> and returns
   * an array of them in natural order, along with an array telling
   * which element in the term array each document uses.
   * @param reader  Used to get field values.
   * @param field   Which field contains the strings.
   * @return Array of terms and index into the array for each document.
   * @throws IOException  If any error occurs.
   */
  public StringIndex getStringIndex (IndexReader reader, String field)
  throws IOException;

  /** Checks the internal cache for an appropriate entry, and if
   * none is found reads <code>field</code> to see if it contains integers, floats
   * or strings, and then calls one of the other methods in this class to get the
   * values.  For string values, a StringIndex is returned.  After
   * calling this method, there is an entry in the cache for both
   * type <code>AUTO</code> and the actual found type.
   * @param reader  Used to get field values.
   * @param field   Which field contains the values.
   * @return int[], float[] or StringIndex.
   * @throws IOException  If any error occurs.
   */
  public Object getAuto (IndexReader reader, String field)
  throws IOException;

  /** Checks the internal cache for an appropriate entry, and if none
   * is found reads the terms out of <code>field</code> and calls the given SortComparator
   * to get the sort values.  A hit in the cache will happen if <code>reader</code>,
   * <code>field</code>, and <code>comparator</code> are the same (using <code>equals()</code>)
   * as a previous call to this method.
   * @param reader  Used to get field values.
   * @param field   Which field contains the values.
   * @param comparator Used to convert terms into something to sort by.
   * @return Array of sort objects, one for each document.
   * @throws IOException  If any error occurs.
   */
  public Comparable[] getCustom (IndexReader reader, String field, SortComparator comparator)
  throws IOException;
}
