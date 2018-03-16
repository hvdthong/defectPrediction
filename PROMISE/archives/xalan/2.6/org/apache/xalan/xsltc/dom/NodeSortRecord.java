package org.apache.xalan.xsltc.dom;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Locale;

import org.apache.xalan.xsltc.CollatorFactory;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xml.utils.StringComparable;

/**
 * Base class for sort records containing application specific sort keys 
 */
public abstract class NodeSortRecord {
    public static final int COMPARE_STRING     = 0;
    public static final int COMPARE_NUMERIC    = 1;

    public static final int COMPARE_ASCENDING  = 0;
    public static final int COMPARE_DESCENDING = 1;

    /**
     * A reference to a collator. May be updated by subclass if the stylesheet
     * specifies a different language (will be updated iff _locale is updated).
     * @deprecated This field continues to exist for binary compatibility.
     *             New code should not refer to it.
     */
    private static final Collator DEFAULT_COLLATOR = Collator.getInstance();

    /**
     * A reference to the first Collator
     * @deprecated This field continues to exist for binary compatibility.
     *             New code should not refer to it.
     */
    protected Collator _collator = DEFAULT_COLLATOR;
    protected Collator[] _collators;

    /**
     * A locale field that might be set by an instance of a subclass.
     * @deprecated This field continues to exist for binary compatibility.
     *             New code should not refer to it.
     */
    protected Locale _locale;

    protected CollatorFactory _collatorFactory;

    protected SortSettings _settings;

    private DOM    _dom = null;


    /**
     * This constructor is run by a call to ClassLoader in the
     * makeNodeSortRecord method in the NodeSortRecordFactory class. Since we
     * cannot pass any parameters to the constructor in that case we just set
     * the default values here and wait for new values through initialize().
     */ 
    public NodeSortRecord(int node) {
	_node = node;
    }

    public NodeSortRecord() {
        this(0);
    }

    /**
     * This method allows the caller to set the values that could not be passed
     * to the default constructor.
     */
    public final void initialize(int node, int last, DOM dom,
         SortSettings settings)
        throws TransletException
    {
	_dom = dom;
	_node = node;
	_last = last;
        _settings = settings;

        int levels = settings.getSortOrders().length;
	_values = new Object[levels];
  
        String colFactClassname = 
	    System.getProperty("org.apache.xalan.xsltc.COLLATOR_FACTORY");

        if (colFactClassname != null) {
            try {
                Object candObj = ObjectFactory.findProviderClass(
                    colFactClassname, ObjectFactory.findClassLoader(), true);
                _collatorFactory = (CollatorFactory)candObj;
            } catch (ClassNotFoundException e) {
                throw new TransletException(e);
            }
            Locale[] locales = settings.getLocales();
            _collators = new Collator[levels];
            for (int i = 0; i < levels; i++){
                _collators[i] = _collatorFactory.getCollator(locales[i]);
            }
            _collator = _collators[0];
        } else {
    	    _collators = settings.getCollators();
            _collator = _collators[0];
        }
    }

    /**
     * Returns the node for this sort object
     */
    public final int getNode() {
	return _node;
    }

    /**
     *
     */
    public final int compareDocOrder(NodeSortRecord other) {
	return _node - other._node;
    }

    /**
     * Get the string or numeric value of a specific level key for this sort
     * element. The value is extracted from the DOM if it is not already in
     * our sort key vector.
     */
    private final Comparable stringValue(int level) {
    	if (_scanned <= level) {
            AbstractTranslet translet = _settings.getTranslet();
            Locale[] locales = _settings.getLocales();
            String[] caseOrder = _settings.getCaseOrders();

    	    final String str = extractValueFromDOM(_dom, _node, level,
    						   translet, _last);
    	    final Comparable key =
                StringComparable.getComparator(str, locales[level],
                                               _collators[level],
                                               caseOrder[level]);
    	    _values[_scanned++] = key;
    	    return(key);
    	}
    	return((Comparable)_values[level]);
  }
    
    private final Double numericValue(int level) {
	if (_scanned <= level) {
            AbstractTranslet translet = _settings.getTranslet();

	    final String str = extractValueFromDOM(_dom, _node, level,
						   translet, _last);
	    Double num;
	    try {
		num = new Double(str);
	    }
	    catch (NumberFormatException e) {
		num = new Double(Double.NEGATIVE_INFINITY);
	    }
	    _values[_scanned++] = num;
	    return(num);
	}
	return((Double)_values[level]);
    }

    /**
     * Compare this sort element to another. The first level is checked first,
     * and we proceed to the next level only if the first level keys are
     * identical (and so the key values may not even be extracted from the DOM)
     *
     * !!!!MUST OPTIMISE - THIS IS REALLY, REALLY SLOW!!!!
     */
    public int compareTo(NodeSortRecord other) {
	int cmp, level;
        int[] sortOrder = _settings.getSortOrders();
        int levels = _settings.getSortOrders().length;
        int[] compareTypes = _settings.getTypes();

	for (level = 0; level < levels; level++) {
	    if (compareTypes[level] == COMPARE_NUMERIC) {
		final Double our = numericValue(level);
		final Double their = other.numericValue(level);
		cmp = our.compareTo(their);
	    }
	    else {
		final Comparable our = stringValue(level);
		final Comparable their = other.stringValue(level);
		cmp = our.compareTo(their);
	    }
	    
	    if (cmp != 0) {
		return sortOrder[level] == COMPARE_DESCENDING ? 0 - cmp : cmp;
	    }
	}
	return(_node - other._node);
    }

    /**
     * Returns the array of Collators used for text comparisons in this object.
     * May be overridden by inheriting classes
     */
    public Collator[] getCollator() {
	return _collators;
    }

    /**
     * Extract the sort value for a level of this key.
     */
    public abstract String extractValueFromDOM(DOM dom, int current, int level,
					       AbstractTranslet translet,
					       int last);

}
