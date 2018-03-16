import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;

import java.io.IOException;
import java.util.Collections;
import java.util.Collection;

/**
 * Expert:
 * Public for extension only
 */
public class TermSpans implements PayloadSpans {
  protected TermPositions positions;
  protected Term term;
  protected int doc;
  protected int freq;
  protected int count;
  protected int position;


  public TermSpans(TermPositions positions, Term term) throws IOException {

    this.positions = positions;
    this.term = term;
    doc = -1;
  }

  public boolean next() throws IOException {
    if (count == freq) {
      if (!positions.next()) {
        doc = Integer.MAX_VALUE;
        return false;
      }
      doc = positions.doc();
      freq = positions.freq();
      count = 0;
    }
    position = positions.nextPosition();
    count++;
    return true;
  }

  public boolean skipTo(int target) throws IOException {
    if (doc >= target) {
      return true;
    }

    if (!positions.skipTo(target)) {
      doc = Integer.MAX_VALUE;
      return false;
    }

    doc = positions.doc();
    freq = positions.freq();
    count = 0;

    position = positions.nextPosition();
    count++;

    return true;
  }

  public int doc() {
    return doc;
  }

  public int start() {
    return position;
  }

  public int end() {
    return position + 1;
  }

  public Collection/*<byte[]>*/ getPayload() throws IOException {
    byte [] bytes = new byte[positions.getPayloadLength()]; 
    bytes = positions.getPayload(bytes, 0);
    return Collections.singletonList(bytes);
  }

 public boolean isPayloadAvailable() {
    return positions.isPayloadAvailable();
  }

  public String toString() {
    return "spans(" + term.toString() + ")@" +
            (doc == -1 ? "START" : (doc == Integer.MAX_VALUE) ? "END" : doc + "-" + position);
  }


  public TermPositions getPositions() {
    return positions;
  }
}
