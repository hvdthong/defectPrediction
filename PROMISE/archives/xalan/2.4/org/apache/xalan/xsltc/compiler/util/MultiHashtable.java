package org.apache.xalan.xsltc.compiler.util;

import java.util.Vector;
import java.util.Hashtable;

public final class MultiHashtable extends Hashtable {
    public Object put(Object key, Object value) {
	Vector vector = (Vector)get(key);
	if (vector == null)
	    super.put(key, vector = new Vector());
	vector.add(value);
	return vector;
    }
	
    public Object maps(Object from, Object to) {
	if (from == null) return null;
	final Vector vector = (Vector) get(from);
	if (vector != null) {
	    final int n = vector.size();
	    for (int i = 0; i < n; i++) {
                final Object item = vector.elementAt(i);
		if (item.equals(to)) {
		    return item;
		}
	    }
	}
	return null;
    }
}
