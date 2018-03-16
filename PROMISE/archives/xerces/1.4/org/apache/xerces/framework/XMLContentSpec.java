package org.apache.xerces.framework;

import org.apache.xerces.utils.StringPool;

/**
 * ContentSpec really exists to aid the parser classes in implementing
 * access to the grammar.
 * <p>
 * This class is used by the DTD scanner and the validator classes,
 * allowing them to be used separately or together.  This "struct"
 * class is used to build content models for validation, where it
 * is more efficient to fetch all of the information for each of
 * these content model "fragments" than to fetch each field one at
 * a time.  Since configurations are allowed to have validators
 * without a DTD scanner (i.e. a schema validator) and a DTD scanner
 * without a validator (non-validating processor), this class can be
 * used by each without requiring the presence of the other.
 * <p>
 * When processing element declarations, the DTD scanner will build
 * up a representation of the content model using the node types that
 * are defined here.  Since a non-validating processor only needs to
 * remember the type of content model declared (i.e. ANY, EMPTY, MIXED,
 * or CHILDREN), it is free to discard the specific details of the
 * MIXED and CHILDREN content models described using this class.
 * <p>
 * In the typical case of a validating processor reading the grammar
 * of the document from a DTD, the information about the content model
 * declared will be preserved and later "compiled" into an efficient
 * form for use during element validation.  Each content spec node
 * that is saved is assigned a unique index that is used as a handle
 * for the "value" or "otherValue" fields of other content spec nodes.
 * A leaf node has a "value" that is either an index in the string
 * pool of the element type of that leaf, or a value of -1 to indicate
 * the special "#PCDATA" leaf type used in a mixed content model.
 * <p>
 * For a mixed content model, the content spec will be made up of
 * leaf and choice content spec nodes, with an optional "zero or more"
 * node.  For example, the mixed content declaration "(#PCDATA)" would
 * contain a single leaf node with a node value of -1.  A mixed content
 * declaration of "(#PCDATA|foo)*" would have a content spec consisting
 * of two leaf nodes, for the "#PCDATA" and "foo" choices, a choice node
 * with the "value" set to the index of the "#PCDATA" leaf node and the
 * "otherValue" set to the index of the "foo" leaf node, and a "zero or
 * more" node with the "value" set to the index of the choice node.  If
 * the content model has more choices, for example "(#PCDATA|a|b)*", then
 * there will be more corresponding choice and leaf nodes, the choice
 * nodes will be chained together through the "value" field with each
 * leaf node referenced by the "otherValue" field.
 * <p>
 * For element content models, there are sequence nodes and also "zero or
 * one" and "one or more" nodes.  The leaf nodes would always have a valid
 * string pool index, as the "#PCDATA" leaf is not used in the declarations
 * for element content models.
 *
 * @version $Id: XMLContentSpec.java 317197 2001-05-31 15:10:09Z neilg $
 */
public class XMLContentSpec {


    /** 
     * Name or #PCDATA. Leaf nodes that represent parsed character
     * data (#PCDATA) have values of -1.
     */
    public static final int CONTENTSPECNODE_LEAF = 0;

    /** Represents a zero or one occurence count, '?'. */
    public static final int CONTENTSPECNODE_ZERO_OR_ONE = 1;

    /** Represents a zero or more occurence count, '*'. */
    public static final int CONTENTSPECNODE_ZERO_OR_MORE = 2;
    
    /** Represents a one or more occurence count, '+'. */
    public static final int CONTENTSPECNODE_ONE_OR_MORE = 3;
    
    /** Represents choice, '|'. */
    public static final int CONTENTSPECNODE_CHOICE = 4;
    
    /** Represents sequence, ','. */
    public static final int CONTENTSPECNODE_SEQ = 5;

    /** 
     * Represents any namespace specified namespace. When the element
     * found in the document must belong to a specific namespace, 
     * <code>otherValue</code> will contain the name of the namespace.
     * If <code>otherValue</code> is <code>-1</code> then the element
     * can be from any namespace.
     * <p>
     * Lists of valid namespaces are created from choice content spec
     * nodes that have any content spec nodes as children.
     */
    public static final int CONTENTSPECNODE_ANY = 6;

    /** 
     * Represents any other namespace (XML Schema: ##other). 
     * <p>
     * When the content spec node type is set to CONTENTSPECNODE_ANY_OTHER, 
     * <code>value</code> will contain the namespace that <em>cannot</em>
     * occur.
     */
    public static final int CONTENTSPECNODE_ANY_OTHER = 7;

    /** Represents any namespace element (including "##local"). */
    public static final int CONTENTSPECNODE_ANY_NS = 8;

    /** Represents <ALL> */
    public static final int CONTENTSPECNODE_ALL = 9;

    /** prcessContent is 'lax' **/
    public static final int CONTENTSPECNODE_ANY_LAX = 22;

    public static final int CONTENTSPECNODE_ANY_OTHER_LAX = 23;

    public static final int CONTENTSPECNODE_ANY_NS_LAX = 24;

    /** processContent is 'skip' **/
    
    public static final int CONTENTSPECNODE_ANY_SKIP = 38;

    public static final int CONTENTSPECNODE_ANY_OTHER_SKIP = 39;

    public static final int CONTENTSPECNODE_ANY_NS_SKIP = 40;

    /** 
     * The content spec node type. 
     *
     * @see CONTENTSPECNODE_LEAF
     * @see CONTENTSPECNODE_ZERO_OR_ONE
     * @see CONTENTSPECNODE_ZERO_OR_MORE
     * @see CONTENTSPECNODE_ONE_OR_MORE
     * @see CONTENTSPECNODE_CHOICE
     * @see CONTENTSPECNODE_SEQ
     * @see CONTENTSPECNODE_ALL
     */
    public int type;

    /**
     * The "left hand" value of the content spec node.
     */
    public int value;

    /**
     * The "right hand" value of the content spec node.
     */
    public int otherValue;


    /** Default constructor. */
    public XMLContentSpec() {
        clear();
    }

    /** Constructs a content spec with the specified values. */
    public XMLContentSpec(int type, int value, int otherValue) {
        setValues(type, value, otherValue);
    }

    /** 
     * Constructs a content spec from the values in the specified content spec.
     */
    public XMLContentSpec(XMLContentSpec contentSpec) {
        setValues(contentSpec);
    }

    /**
     * Constructs a content spec from the values specified by the given
     * content spec provider and identifier.
     */
    public XMLContentSpec(XMLContentSpec.Provider provider,
                          int contentSpecIndex) {
        setValues(provider, contentSpecIndex);
    }


    /** Clears the values. */
    public void clear() {
        type = -1;
        value = -1;
        otherValue = -1;
    }

    /** Sets the values. */
    public void setValues(int type, int value, int otherValue) {
        this.type = type;
        this.value = value;
        this.otherValue = otherValue;
    }
    
    /** Sets the values of the specified content spec. */
    public void setValues(XMLContentSpec contentSpec) {
        type = contentSpec.type;
        value = contentSpec.value;
        otherValue = contentSpec.otherValue;
    }

    /**
     * Sets the values from the values specified by the given content spec
     * provider and identifier. If the specified content spec cannot be
     * provided, the values of this content spec are cleared.
     */
    public void setValues(XMLContentSpec.Provider provider,
                          int contentSpecIndex) {
        if (!provider.getContentSpec(contentSpecIndex, this)) {
            clear();
        }
    }


    /** 
     * Returns a string representation of the specified content spec 
     * identifier in the form of a DTD element content model.
     * <p>
     * <strong>Note:</strong> This method is not namespace aware.
     */
    public static String toString(XMLContentSpec.Provider provider, 
                                  StringPool stringPool, 
                                  int contentSpecIndex) {

        XMLContentSpec contentSpec = new XMLContentSpec();
       
        if (provider.getContentSpec(contentSpecIndex, contentSpec)) {

            StringBuffer str = new StringBuffer();
            int    parentContentSpecType = contentSpec.type & 0x0f;
            int    nextContentSpec;
            switch (parentContentSpecType) {
                case XMLContentSpec.CONTENTSPECNODE_LEAF: {
                    str.append('(');
                    if (contentSpec.value == -1 && contentSpec.otherValue == -1) {
                        str.append("#PCDATA");
                    }
                    else {
                        str.append(stringPool.toString(contentSpec.value));
                    }
                    str.append(')');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE: {
                    provider.getContentSpec(contentSpec.value, contentSpec);
                    nextContentSpec = contentSpec.type;

                    if (nextContentSpec == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                        str.append('(');
                        str.append(stringPool.toString(contentSpec.value));
                        str.append(')');
                    } else if( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                            nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                            nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                        str.append('(' );
                        appendContentSpec(provider, stringPool, contentSpec, str, 
                                                                 true, parentContentSpecType );
                        str.append(')');

                    } else {
                        appendContentSpec(provider, stringPool, contentSpec, str, 
                                                                 true, parentContentSpecType );
                    }
                    str.append('?');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE: {
                    provider.getContentSpec(contentSpec.value, contentSpec);
                    nextContentSpec = contentSpec.type;

                    if ( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                        str.append('(');
                        if (contentSpec.value == -1 && contentSpec.otherValue == -1) {
                            str.append("#PCDATA");
                        }
                        else if (contentSpec.otherValue != -1) {
                            str.append("##any:uri="+stringPool.toString(contentSpec.otherValue));
                        }
                        else if (contentSpec.value == -1) {
                            str.append("##any");
                        }
                        else {
                             appendContentSpec(provider, stringPool, contentSpec, str, 
                                                    true, parentContentSpecType );
                        }
                        str.append(')');

                    } else if( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                        str.append('(' );
                        appendContentSpec(provider, stringPool, contentSpec, str, 
                                                            true, parentContentSpecType );
                        str.append(')');
                    } else {
                        appendContentSpec(provider, stringPool, contentSpec, str, 
                                                                true, parentContentSpecType );

                    }
                    str.append('*');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE: {
                    provider.getContentSpec(contentSpec.value, contentSpec);
                    nextContentSpec = contentSpec.type;

                    if ( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                        str.append('(');
                        if (contentSpec.value == -1 && contentSpec.otherValue == -1) {
                            str.append("#PCDATA");
                        }
                        else if (contentSpec.otherValue != -1) {
                            str.append("##any:uri="+stringPool.toString(contentSpec.otherValue));
                        }
                        else if (contentSpec.value == -1) {
                            str.append("##any");
                        }
                        else {
                            str.append(stringPool.toString(contentSpec.value));
                        }
                        str.append(')');
                    } else if( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                        str.append('(' );
                        appendContentSpec(provider, stringPool, contentSpec, str, 
                                                      true, parentContentSpecType );
                        str.append(')');
                    } else {
                        appendContentSpec(provider, stringPool, contentSpec, str,
                                                            true, parentContentSpecType);
                    }
                    str.append('+');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ALL:
                case XMLContentSpec.CONTENTSPECNODE_CHOICE:
                case XMLContentSpec.CONTENTSPECNODE_SEQ: {
                    appendContentSpec(provider, stringPool,
                                      contentSpec, str, true,
                                      parentContentSpecType );
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ANY: {
                    str.append("##any");
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ANY_OTHER: {
                    str.append("##other:uri=");
                    str.append(stringPool.toString(contentSpec.otherValue));
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ANY_NS: {
                    str.append("namespace:uri=");
                    str.append(stringPool.toString(contentSpec.otherValue));
                    break;
                }
                default: {
                    str.append("???");
                }


            return str.toString();
        }

        return null;



    /** Returns a hash code for this node. */
    public int hashCode() {
        return type << 16 | 
               value << 8 |
               otherValue;
    }

    /** Returns true if the two objects are equal. */
    public boolean equals(Object object) {
        if (object != null && object instanceof XMLContentSpec) {
            XMLContentSpec contentSpec = (XMLContentSpec)object;
            return type == contentSpec.type &&
                   value == contentSpec.value &&
                   otherValue == contentSpec.otherValue;
        }
        return false;
    }


    /**
     * Appends more information to the current string buffer.
     * <p>
     * <strong>Note:</strong> This method does <em>not</em> preserve the
     * contents of the content spec node.
     */
    private static void appendContentSpec(XMLContentSpec.Provider provider,
                                          StringPool stringPool,
                                          XMLContentSpec contentSpec, 
                                          StringBuffer str, 
                                          boolean parens,
                                          int     parentContentSpecType ) {

        int thisContentSpec = contentSpec.type & 0x0f;
        switch (thisContentSpec) {
            case XMLContentSpec.CONTENTSPECNODE_LEAF: {
                if (contentSpec.value == -1 && contentSpec.otherValue == -1) {
                    str.append("#PCDATA");
                }
                else if (contentSpec.value == -1 && contentSpec.otherValue != -1) {
                    str.append("##any:uri="+stringPool.toString(contentSpec.otherValue));
                }
                else if (contentSpec.value == -1) {
                    str.append("##any");
                }
                else {
                    str.append(stringPool.toString(contentSpec.value));
                }
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE: {
                if( parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                    provider.getContentSpec(contentSpec.value, contentSpec);
                    str.append('(');
                    appendContentSpec(provider, stringPool, 
                                      contentSpec, str, true, thisContentSpec );
                    str.append(')');

                } 
                else {
                    provider.getContentSpec(contentSpec.value, contentSpec);
                    appendContentSpec(provider, stringPool, 
                                      contentSpec, str, true, thisContentSpec );
                }
                str.append('?');
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE: {
                if( parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE ||
                   parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE ||
                   parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                   provider.getContentSpec(contentSpec.value, contentSpec);
                   str.append('(');
                   appendContentSpec(provider, stringPool,
                                      contentSpec, str, true, thisContentSpec);
                   str.append(')' );
                } else{
                    provider.getContentSpec(contentSpec.value, contentSpec);
                    appendContentSpec(provider, stringPool,
                                     contentSpec, str, true, thisContentSpec);
                }
                str.append('*');
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE: {
                if( parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE   ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {

                  str.append('(');
                  provider.getContentSpec(contentSpec.value, contentSpec);
                  appendContentSpec(provider, stringPool,
                                    contentSpec, str, true, thisContentSpec);
                  str.append(')' );
                } else {
                    provider.getContentSpec(contentSpec.value, contentSpec);
                    appendContentSpec(provider, stringPool,
                                   contentSpec, str, true, thisContentSpec);
                }
                str.append('+');
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_CHOICE:
            case XMLContentSpec.CONTENTSPECNODE_SEQ:
            case XMLContentSpec.CONTENTSPECNODE_ALL: {
                int type = contentSpec.type;
                if (parens) {
                    if (type == XMLContentSpec.CONTENTSPECNODE_ALL)
                        str.append("all(");
                    else
                        str.append('(');
                }
                int otherValue = contentSpec.otherValue;
                provider.getContentSpec(contentSpec.value, contentSpec);
                appendContentSpec(provider, stringPool,
                           contentSpec, str, contentSpec.type != type, thisContentSpec);
                if (otherValue != -2) {
                if (type == XMLContentSpec.CONTENTSPECNODE_CHOICE) {
                    str.append('|');
                }
                else {
                    str.append(',');
                }
                /***
                if (++index == CHUNK_SIZE) {
                    chunk++;
                    index = 0;
                }
                /***/
                provider.getContentSpec(otherValue, contentSpec);
                appendContentSpec(provider, stringPool,
                                  contentSpec, str, true, thisContentSpec);
                }
                if (parens) {
                    str.append(')');
                }
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ANY: {
                str.append("##any");
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ANY_OTHER: {
                str.append("##other:uri=");
                str.append(stringPool.toString(contentSpec.otherValue));
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ANY_NS: {
                str.append("namespace:uri=");
                str.append(stringPool.toString(contentSpec.otherValue));
                break;
            }
            default: {
                str.append("???");
                break;
            }




    /**
     * Provides a means for walking the structure built out of 
     * content spec "nodes". The user of this provider interface is
     * responsible for knowing what the content spec node values
     * "mean". If those values refer to content spec identifiers,
     * then the user can call back into the provider to get the
     * next content spec node in the structure.
     */
    public interface Provider {


        /**
         * Fills in the provided content spec structure with content spec
         * information for a unique identifier.
         *
         * @param contentSpecIndex The content spec identifier. All content
         *                         spec "nodes" have a unique identifier.
         * @param contentSpec      The content spec struct to fill in with
         *                         the information.
         *
         * @return Returns true if the contentSpecIndex was found.
         */
        public boolean getContentSpec(int contentSpecIndex, XMLContentSpec contentSpec);

