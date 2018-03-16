package org.apache.xerces.validators.datatype;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.text.Collator;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import org.apache.xerces.validators.schema.SchemaSymbols;
import org.apache.xerces.utils.regex.RegularExpression;

/**
 * StringValidator validates that XML content is a W3C string type.
 * @author Ted Leung
 * @author Kito D. Mann, Virtua Communications Corp.
 * @author Jeffrey Rodriguez
 * @version $Id: StringDatatypeValidator.java 315962 2000-07-31 17:56:52Z jeffreyr $
 */
public class StringDatatypeValidator extends AbstractDatatypeValidator{
    private Locale     fLocale          = null;

    private int        fLength           = 0;
    private int        fMaxLength        = Integer.MAX_VALUE;
    private int        fMinLength        = 0;
    private String     fPattern          = null;
    private Vector     fEnumeration      = null;
    private String     fMaxInclusive     = null;
    private String     fMaxExclusive     = null;
    private String     fMinInclusive     = null;
    private String     fMinExclusive     = null;
    private int        fFacetsDefined    = 0;

    private boolean    isMaxExclusiveDefined = false;
    private boolean    isMaxInclusiveDefined = false;
    private boolean    isMinExclusiveDefined = false;
    private boolean    isMinInclusiveDefined = false;
    private RegularExpression fRegex         = null;




    public  StringDatatypeValidator () throws InvalidDatatypeFacetException{

    }

    public StringDatatypeValidator ( DatatypeValidator base, Hashtable facets, 
                                     boolean derivedByList ) throws InvalidDatatypeFacetException {


        fDerivedByList = derivedByList;

        if ( facets != null  ){
            if ( fDerivedByList == false) {

                for (Enumeration e = facets.keys(); e.hasMoreElements();) {
                    String key = (String) e.nextElement();

                    if ( key.equals(SchemaSymbols.ELT_LENGTH) ) {
                        fFacetsDefined += DatatypeValidator.FACET_LENGTH;
                        String lengthValue = (String)facets.get(key);
                        try {
                            fLength     = Integer.parseInt( lengthValue );
                        } catch (NumberFormatException nfe) {
                            throw new InvalidDatatypeFacetException("Length value '"+lengthValue+"' is invalid.");
                        }
                        if ( fLength < 0 )
                            throw new InvalidDatatypeFacetException("Length value '"+lengthValue+"'  must be a nonNegativeInteger.");

                    } else if (key.equals(SchemaSymbols.ELT_MINLENGTH) ) {
                        fFacetsDefined += DatatypeValidator.FACET_MINLENGTH;
                        String minLengthValue = (String)facets.get(key);
                        try {
                            fMinLength     = Integer.parseInt( minLengthValue );
                        } catch (NumberFormatException nfe) {
                            throw new InvalidDatatypeFacetException("minLength value '"+minLengthValue+"' is invalid.");
                        }
                    } else if (key.equals(SchemaSymbols.ELT_MAXLENGTH) ) {
                        fFacetsDefined += DatatypeValidator.FACET_MAXLENGTH;
                        String maxLengthValue = (String)facets.get(key);
                        try {
                            fMaxLength     = Integer.parseInt( maxLengthValue );
                        } catch (NumberFormatException nfe) {
                            throw new InvalidDatatypeFacetException("maxLength value '"+maxLengthValue+"' is invalid.");
                        }
                    } else if (key.equals(SchemaSymbols.ELT_PATTERN)) {
                        fFacetsDefined += DatatypeValidator.FACET_PATTERN;
                        fPattern = (String)facets.get(key);
                        fRegex   = new RegularExpression(fPattern, "X");
                    } else if (key.equals(SchemaSymbols.ELT_ENUMERATION)) {
                        fFacetsDefined += DatatypeValidator.FACET_ENUMERATION;
                        fEnumeration = (Vector)facets.get(key);
                    } else if (key.equals(SchemaSymbols.ELT_MAXINCLUSIVE)) {
                        fFacetsDefined += DatatypeValidator.FACET_MAXINCLUSIVE;
                        fMaxInclusive = (String)facets.get(key);
                    } else if (key.equals(SchemaSymbols.ELT_MAXEXCLUSIVE)) {
                        fFacetsDefined += DatatypeValidator.FACET_MAXEXCLUSIVE;
                        fMaxExclusive = (String)facets.get(key);
                    } else if (key.equals(SchemaSymbols.ELT_MININCLUSIVE)) {
                        fFacetsDefined += DatatypeValidator.FACET_MININCLUSIVE;
                        fMinInclusive = (String)facets.get(key);
                    } else if (key.equals(SchemaSymbols.ELT_MINEXCLUSIVE)) {
                        fFacetsDefined += DatatypeValidator.FACET_MINEXCLUSIVE;
                        fMinExclusive = (String)facets.get(key);
                    } else {
                        throw new InvalidDatatypeFacetException("invalid facet tag : " + key);
                    }
                }

                if (((fFacetsDefined & DatatypeValidator.FACET_LENGTH ) != 0 ) ) {
                    if (((fFacetsDefined & DatatypeValidator.FACET_MAXLENGTH ) != 0 ) ) {
                        throw new InvalidDatatypeFacetException(
                                                               "It is an error for both length and maxLength to be members of facets." );  
                    } else if (((fFacetsDefined & DatatypeValidator.FACET_MINLENGTH ) != 0 ) ) {
                        throw new InvalidDatatypeFacetException(
                                                               "It is an error for both length and minLength to be members of facets." );
                    }
                }

                if ( ( (fFacetsDefined & ( DatatypeValidator.FACET_MINLENGTH |
                                           DatatypeValidator.FACET_MAXLENGTH) ) != 0 ) ) {
                    if ( fMinLength > fMaxLength ) {
                        throw new InvalidDatatypeFacetException( "Value of minLength = '" + fMinLength +
                                                                 "'must be less than the value of maxLength = '" + fMaxLength + "'.");
                    }
                }

                isMaxExclusiveDefined = ((fFacetsDefined & 
                                          DatatypeValidator.FACET_MAXEXCLUSIVE ) != 0 )?true:false;
                isMaxInclusiveDefined = ((fFacetsDefined & 
                                          DatatypeValidator.FACET_MAXINCLUSIVE ) != 0 )?true:false;
                isMinExclusiveDefined = ((fFacetsDefined &
                                          DatatypeValidator.FACET_MINEXCLUSIVE ) != 0 )?true:false;
                isMinInclusiveDefined = ((fFacetsDefined &
                                          DatatypeValidator.FACET_MININCLUSIVE ) != 0 )?true:false;

                if ( isMaxExclusiveDefined && isMaxInclusiveDefined ) {
                    throw new InvalidDatatypeFacetException(
                                                           "It is an error for both maxInclusive and maxExclusive to be specified for the same datatype." ); 
                }
                if ( isMinExclusiveDefined && isMinInclusiveDefined ) {
                    throw new InvalidDatatypeFacetException(
                                                           "It is an error for both minInclusive and minExclusive to be specified for the same datatype." ); 
                }
                for (Enumeration e = facets.keys(); e.hasMoreElements();) {
                    String key = (String) e.nextElement();
                    if ( key.equals(SchemaSymbols.ELT_LENGTH) ) {
                        fFacetsDefined += DatatypeValidator.FACET_LENGTH;
                        String lengthValue = (String)facets.get(key);
                        try {
                            fLength     = Integer.parseInt( lengthValue );
                        } catch (NumberFormatException nfe) {
                            throw new InvalidDatatypeFacetException("Length value '"+lengthValue+"' is invalid.");
                        }
                        if ( fLength < 0 )
                            throw new InvalidDatatypeFacetException("Length value '"+lengthValue+"'  must be a nonNegativeInteger.");

                    } else if (key.equals(SchemaSymbols.ELT_MINLENGTH) ) {
                        fFacetsDefined += DatatypeValidator.FACET_MINLENGTH;
                        String minLengthValue = (String)facets.get(key);
                        try {
                            fMinLength     = Integer.parseInt( minLengthValue );
                        } catch (NumberFormatException nfe) {
                            throw new InvalidDatatypeFacetException("maxLength value '"+minLengthValue+"' is invalid.");
                        }
                    } else if (key.equals(SchemaSymbols.ELT_MAXLENGTH) ) {
                        fFacetsDefined += DatatypeValidator.FACET_MAXLENGTH;
                        String maxLengthValue = (String)facets.get(key);
                        try {
                            fMaxLength     = Integer.parseInt( maxLengthValue );
                        } catch (NumberFormatException nfe) {
                            throw new InvalidDatatypeFacetException("maxLength value '"+maxLengthValue+"' is invalid.");
                        }
                    } else if (key.equals(SchemaSymbols.ELT_ENUMERATION)) {
                        fFacetsDefined += DatatypeValidator.FACET_ENUMERATION;
                        fEnumeration    = (Vector)facets.get(key);
                    } else {
                        throw new InvalidDatatypeFacetException("invalid facet tag : " + key);
                    }
                }
                if (((fFacetsDefined & DatatypeValidator.FACET_LENGTH ) != 0 ) ) {
                    if (((fFacetsDefined & DatatypeValidator.FACET_MAXLENGTH ) != 0 ) ) {
                        throw new InvalidDatatypeFacetException(
                                                               "It is an error for both length and maxLength to be members of facets." );  
                    } else if (((fFacetsDefined & DatatypeValidator.FACET_MINLENGTH ) != 0 ) ) {
                        throw new InvalidDatatypeFacetException(
                                                               "It is an error for both length and minLength to be members of facets." );
                    }
                }

                if ( ( (fFacetsDefined & ( DatatypeValidator.FACET_MINLENGTH |
                                           DatatypeValidator.FACET_MAXLENGTH) ) != 0 ) ) {
                    if ( fMinLength > fMaxLength ) {
                        throw new InvalidDatatypeFacetException( "Value of minLength = " + fMinLength +
                                                                 "must be greater that the value of maxLength" + fMaxLength );
                    }
                }
            }
    }




    /**
     * validate that a string is a W3C string type
     * 
     * @param content A string containing the content to be validated
     * @param list
     * @exception throws InvalidDatatypeException if the content is
     *                   not a W3C string type
     * @exception InvalidDatatypeValueException
     */
    public Object validate(String content, Object state)  throws InvalidDatatypeValueException
    {
        if ( fDerivedByList == false  ) {
                checkContent( content, state );
            checkContentList( content, state );
        }
        return null;
    }


    /**
     * set the locate to be used for error messages
     */
    public void setLocale(Locale locale) {
        fLocale = locale;
    }


    /**
     * 
     * @return                          A Hashtable containing the facets
     *         for this datatype.
     */
    public Hashtable getFacets(){
        return null;
    }

    private void checkContent( String content, Object state )throws InvalidDatatypeValueException
    {

            this.fBaseValidator.validate( content, state );
        }

        if ( (fFacetsDefined & DatatypeValidator.FACET_MAXLENGTH) != 0 ) {
            if ( content.length() > fMaxLength ) {
                throw new InvalidDatatypeValueException("Value '"+content+
                                                        "' with length '"+content.length()+
                                                        "' exceeds maximum length facet of '"+fMaxLength+"'.");
            }
        }
        if ( (fFacetsDefined & DatatypeValidator.FACET_MINLENGTH) != 0 ) {
            if ( content.length() < fMinLength ) {
                throw new InvalidDatatypeValueException("Value '"+content+
                                                        "' with length '"+content.length()+
                                                        "' is less than minimum length facet of '"+fMinLength+"'." );
            }
        }

        if ( (fFacetsDefined & DatatypeValidator.FACET_LENGTH) != 0 ) {
            if ( content.length() != fLength ) {
                throw new InvalidDatatypeValueException("Value '"+content+
                                                        "' with length '"+content.length()+
                                                        "' is not equal to length facet '"+fLength+"'.");
            }
        }



        if ( (fFacetsDefined & DatatypeValidator.FACET_ENUMERATION) != 0 ) {
            if ( fEnumeration.contains( content ) == false )
                throw new InvalidDatatypeValueException("Value '"+content+"' must be one of "+fEnumeration);
        }

        if ( isMaxExclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult  = compare( content, fMaxExclusive );
            if ( comparisonResult >= 0 ) {
                throw new InvalidDatatypeValueException( "MaxExclusive:Value '"+content+ "'  must be " +
                                                         "lexicographically less than" + fMaxExclusive );

            }

        }
        if ( isMaxInclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult  = compare( content, fMaxInclusive );
            if ( comparisonResult > 0 )
                throw new InvalidDatatypeValueException( "MaxInclusive:Value '"+content+ "' must be " +
                                                         "lexicographically less or equal than" + fMaxInclusive );
        }

        if ( isMinExclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult  = compare( content, fMinExclusive );


            if ( comparisonResult <= 0 )
                throw new InvalidDatatypeValueException( "MinExclusive:Value '"+content+ "' must be " +
                                                         "lexicographically greater than" + fMinExclusive );
        }
        if ( isMinInclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult = compare( content, fMinInclusive );
            if ( comparisonResult < 0 )
                throw new InvalidDatatypeValueException( "MinInclusive:Value '"+content+ "' must be " +
                                                         "lexicographically greater or equal than '" + fMinInclusive  + "'." );
        }


        if ( (fFacetsDefined & DatatypeValidator.FACET_PATTERN ) != 0 ) {
            if ( fRegex == null || fRegex.matches( content) == false )
                throw new InvalidDatatypeValueException("Value '"+content+
                                                        "' does not match regular expression facet '" + fPattern + "'." );
        }

    }
    public int compare( String content, String facetValue ){
        Locale    loc       = Locale.getDefault();
        Collator  collator  = Collator.getInstance( loc );
        return collator.compare( content, facetValue );
    }

    /**
   * Returns a copy of this object.
   */
    public Object clone() throws CloneNotSupportedException  {
        StringDatatypeValidator newObj = null;
        try {
            newObj = new StringDatatypeValidator();

            newObj.fLocale           =  this.fLocale;
            newObj.fBaseValidator    =  this.fBaseValidator;
            newObj.fLength           =  this.fLength;
            newObj.fMaxLength        =  this.fMaxLength;
            newObj.fMinLength        =  this.fMinLength;
            newObj.fPattern          =  this.fPattern;
            newObj.fEnumeration      =  this.fEnumeration;
            newObj.fMaxInclusive     =  this.fMaxInclusive;
            newObj.fMaxExclusive     =  this.fMaxExclusive;
            newObj.fMinInclusive     =  this.fMinInclusive;
            newObj.fMinExclusive     =  this.fMinExclusive;
            newObj.fFacetsDefined    =  this.fFacetsDefined;
            newObj.fDerivedByList    =  this.fDerivedByList;
            newObj.isMaxExclusiveDefined = this.isMaxExclusiveDefined;
            newObj.isMaxInclusiveDefined = this.isMaxInclusiveDefined;
            newObj.isMinExclusiveDefined = this.isMinExclusiveDefined;
            newObj.isMinInclusiveDefined = this.isMinInclusiveDefined;
        } catch ( InvalidDatatypeFacetException ex) {
            ex.printStackTrace();
        }
        return newObj;
    }

    private void checkContentList( String content,  Object state )throws InvalidDatatypeValueException
    {
        StringTokenizer parsedList = new StringTokenizer( content );
        try {
            int numberOfTokens =  parsedList.countTokens();
            if ( (fFacetsDefined & DatatypeValidator.FACET_MAXLENGTH) != 0 ) {
                if ( numberOfTokens > fMaxLength ) {
                    throw new InvalidDatatypeValueException("Value '"+content+
                                                            "' with length ='"+  numberOfTokens + "'tokens"+
                                                            "' exceeds maximum length facet with  '"+fMaxLength+"' tokens.");
                }
            }
            if ( (fFacetsDefined & DatatypeValidator.FACET_MINLENGTH) != 0 ) {
                if ( numberOfTokens < fMinLength ) {
                    throw new InvalidDatatypeValueException("Value '"+content+
                                                            "' with length ='"+ numberOfTokens+ "'tokens" +
                                                            "' is less than minimum length facet with '"+fMinLength+"' tokens." );
                }
            }

            if ( (fFacetsDefined & DatatypeValidator.FACET_LENGTH) != 0 ) {
                if ( numberOfTokens != fLength ) {
                    throw new InvalidDatatypeValueException("Value '"+content+
                                                            "' with length ='"+ numberOfTokens+ "'tokens" +
                                                            "' is not equal to length facet with '"+fLength+"'. tokens");
                }
            }

            if ( (fFacetsDefined & DatatypeValidator.FACET_ENUMERATION) != 0 ) {
                if ( fEnumeration.contains( content ) == false )
                    throw new InvalidDatatypeValueException("Value '"+
                                                            content+"' must be one of "+fEnumeration);
            }
                    this.fBaseValidator.validate( parsedList.nextToken(), state );
                }
            }
        } catch ( NoSuchElementException e ) {
            e.printStackTrace();
        }
    }

    private void setBasetype( DatatypeValidator base) {
        fBaseValidator = base;
    }

}

