package org.apache.lucene.search.function;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;

import java.io.IOException;

/**
 * Expert: obtains the ordinal of the field value from the default Lucene 
 * {@link org.apache.lucene.search.FieldCache Fieldcache} using getStringIndex().
 * <p>
 * The native lucene index order is used to assign an ordinal value for each field value.
 * <p
 * Field values (terms) are lexicographically ordered by unicode value, and numbered starting at 1.
 * <p>
 * Example:
 * <br>If there were only three field values: "apple","banana","pear"
 * <br>then ord("apple")=1, ord("banana")=2, ord("pear")=3
 * <p>
 * WARNING: 
 * ord() depends on the position in an index and can thus change 
 * when other documents are inserted or deleted,
 * or if a MultiSearcher is used. 
 *
 * <p><font color="#FF0000">
 * WARNING: The status of the <b>search.function</b> package is experimental. 
 * The APIs introduced here might change in the future and will not be 
 * supported anymore in such a case.</font>
 *
 */

public class OrdFieldSource extends ValueSource {
  protected String field;

  /** 
   * Contructor for a certain field.
   * @param field field whose values order is used.  
   */
  public OrdFieldSource(String field) {
    this.field = field;
  }

  /*(non-Javadoc) @see org.apache.lucene.search.function.ValueSource#description() */
  public String description() {
    return "ord(" + field + ')';
  }

  /*(non-Javadoc) @see org.apache.lucene.search.function.ValueSource#getValues(org.apache.lucene.index.IndexReader) */
  public DocValues getValues(IndexReader reader) throws IOException {
    final int[] arr = FieldCache.DEFAULT.getStringIndex(reader, field).order;
    return new DocValues() {
      /*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#floatVal(int) */
      public float floatVal(int doc) {
        return (float)arr[doc];
      }
      /*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#strVal(int) */
      public String strVal(int doc) {
        return Integer.toString(arr[doc]);
      }
      /*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#toString(int) */
      public String toString(int doc) {
        return description() + '=' + intVal(doc);
      }
      /*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#getInnerArray() */
      Object getInnerArray() {
        return arr;
      }
    };
  }

  /*(non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
  public boolean equals(Object o) {
    if (o.getClass() !=  OrdFieldSource.class) return false;
    OrdFieldSource other = (OrdFieldSource)o;
    return this.field.equals(other.field);
  }

  private static final int hcode = OrdFieldSource.class.hashCode();
  
  /*(non-Javadoc) @see java.lang.Object#hashCode() */
  public int hashCode() {
    return hcode + field.hashCode();
  }
}
