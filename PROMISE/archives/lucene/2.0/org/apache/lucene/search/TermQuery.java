import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.ToStringUtils;

/** A Query that matches documents containing a term.
  This may be combined with other terms with a {@link BooleanQuery}.
  */
public class TermQuery extends Query {
  private Term term;

  private class TermWeight implements Weight {
    private Similarity similarity;
    private float value;
    private float idf;
    private float queryNorm;
    private float queryWeight;

    public TermWeight(Searcher searcher)
      throws IOException {
      this.similarity = getSimilarity(searcher);
    }

    public String toString() { return "weight(" + TermQuery.this + ")"; }

    public Query getQuery() { return TermQuery.this; }
    public float getValue() { return value; }

    public float sumOfSquaredWeights() {
    }

    public void normalize(float queryNorm) {
      this.queryNorm = queryNorm;
    }

    public Scorer scorer(IndexReader reader) throws IOException {
      TermDocs termDocs = reader.termDocs(term);

      if (termDocs == null)
        return null;

      return new TermScorer(this, termDocs, similarity,
                            reader.norms(term.field()));
    }

    public Explanation explain(IndexReader reader, int doc)
      throws IOException {

      Explanation result = new Explanation();
      result.setDescription("weight("+getQuery()+" in "+doc+"), product of:");

      Explanation idfExpl =
        new Explanation(idf, "idf(docFreq=" + reader.docFreq(term) + ")");

      Explanation queryExpl = new Explanation();
      queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");

      Explanation boostExpl = new Explanation(getBoost(), "boost");
      if (getBoost() != 1.0f)
        queryExpl.addDetail(boostExpl);
      queryExpl.addDetail(idfExpl);

      Explanation queryNormExpl = new Explanation(queryNorm,"queryNorm");
      queryExpl.addDetail(queryNormExpl);

      queryExpl.setValue(boostExpl.getValue() *
                         idfExpl.getValue() *
                         queryNormExpl.getValue());

      result.addDetail(queryExpl);

      String field = term.field();
      Explanation fieldExpl = new Explanation();
      fieldExpl.setDescription("fieldWeight("+term+" in "+doc+
                               "), product of:");

      Explanation tfExpl = scorer(reader).explain(doc);
      fieldExpl.addDetail(tfExpl);
      fieldExpl.addDetail(idfExpl);

      Explanation fieldNormExpl = new Explanation();
      byte[] fieldNorms = reader.norms(field);
      float fieldNorm =
        fieldNorms!=null ? Similarity.decodeNorm(fieldNorms[doc]) : 0.0f;
      fieldNormExpl.setValue(fieldNorm);
      fieldNormExpl.setDescription("fieldNorm(field="+field+", doc="+doc+")");
      fieldExpl.addDetail(fieldNormExpl);

      fieldExpl.setValue(tfExpl.getValue() *
                         idfExpl.getValue() *
                         fieldNormExpl.getValue());

      result.addDetail(fieldExpl);

      result.setValue(queryExpl.getValue() * fieldExpl.getValue());

      if (queryExpl.getValue() == 1.0f)
        return fieldExpl;

      return result;
    }
  }

  /** Constructs a query for the term <code>t</code>. */
  public TermQuery(Term t) {
    term = t;
  }

  /** Returns the term of this query. */
  public Term getTerm() { return term; }

  protected Weight createWeight(Searcher searcher) throws IOException {
    return new TermWeight(searcher);
  }

  public void extractTerms(Set terms) {
    terms.add(getTerm());
  }

  /** Prints a user-readable version of this query. */
  public String toString(String field) {
    StringBuffer buffer = new StringBuffer();
    if (!term.field().equals(field)) {
      buffer.append(term.field());
      buffer.append(":");
    }
    buffer.append(term.text());
    buffer.append(ToStringUtils.boost(getBoost()));
    return buffer.toString();
  }

  /** Returns true iff <code>o</code> is equal to this. */
  public boolean equals(Object o) {
    if (!(o instanceof TermQuery))
      return false;
    TermQuery other = (TermQuery)o;
    return (this.getBoost() == other.getBoost())
      && this.term.equals(other.term);
  }

  /** Returns a hash code value for this object.*/
  public int hashCode() {
    return Float.floatToIntBits(getBoost()) ^ term.hashCode();
  }

}
