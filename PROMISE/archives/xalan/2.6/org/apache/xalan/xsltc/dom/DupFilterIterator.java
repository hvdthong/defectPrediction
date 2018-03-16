package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;
import org.apache.xml.dtm.ref.DTMDefaultBase;

/**
 * Removes duplicates and sorts a source iterator. The nodes from the 
 * source are collected in an array upon calling setStartNode(). This
 * array is later sorted and duplicates are ignored in next().
 * @author G. Todd Miller 
 */
public final class DupFilterIterator extends DTMAxisIteratorBase {

    /**
     * Reference to source iterator.
     */
    private DTMAxisIterator _source;

    /**
     * Array to cache all nodes from source.
     */
    private IntegerArray _nodes = new IntegerArray();

    /**
     * Index in _nodes array to current node.
     */
    private int _current = 0;

    /**
     * Cardinality of _nodes array.
     */
    private int _nodesSize = 0; 

    /**
     * Last value returned by next().
     */
    private int _lastNext = END;

    /**
     * Temporary variable to store _lastNext.
     */
    private int _markedLastNext = END;

    public DupFilterIterator(DTMAxisIterator source) {
	_source = source;

	if (source instanceof KeyIndex) {
	    setStartNode(DTMDefaultBase.ROOTNODE);
	}
    }
    
    /**
     * Set the start node for this iterator
     * @param node The start node
     * @return A reference to this node iterator
     */
    public DTMAxisIterator setStartNode(int node) {
	if (_isRestartable) {
	    if (_source instanceof KeyIndex
                    && _startNode == DTMDefaultBase.ROOTNODE) {
		return this;
	    }

	    if (node != _startNode) {
		_source.setStartNode(_startNode = node);

		_nodes.clear();
		while ((node = _source.next()) != END) {
		    _nodes.add(node);
		}
		_nodes.sort();
		_nodesSize = _nodes.cardinality();
		_current = 0;
		_lastNext = END;
		resetPosition();
	    }
	}
	return this;
    }

    public int next() {
	while (_current < _nodesSize) {
	    final int next = _nodes.at(_current++);
	    if (next != _lastNext) {
		return returnNode(_lastNext = next);
	    }
	}
	return END;
    }

    public DTMAxisIterator cloneIterator() {
	try {
	    final DupFilterIterator clone =
		(DupFilterIterator) super.clone();
	    clone._nodes = (IntegerArray) _nodes.clone();
	    clone._source = _source.cloneIterator();
	    clone._isRestartable = false;
	    return clone.reset();
	}
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }

    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
	_source.setRestartable(isRestartable);
    }
   
    public void setMark() {
	_markedNode = _current;
    }

    public void gotoMark() {
	_current = _markedNode;
    }

    public DTMAxisIterator reset() {
	_current = 0;
	_lastNext = END;
	return resetPosition();
    }
}
