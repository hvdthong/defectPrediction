package org.w3c.dom;

/**
 *  CDATA sections are used to escape blocks of text containing  characters 
 * that would otherwise be regarded as markup. The only  delimiter that is 
 * recognized in a CDATA section is the "]]&gt;" string  that ends the CDATA 
 * section. CDATA sections cannot be  nested. Their primary purpose is for 
 * including material such as XML fragments, without needing to escape all 
 * the delimiters.
 * <p> The <code>DOMString</code> attribute of the <code>Text</code> node 
 * holds the text that is contained by the CDATA section. Note that this  may 
 * contain characters that need to be escaped outside of CDATA sections and 
 * that, depending on the character encoding ("charset") chosen for 
 * serialization, it may be impossible to write out some characters as part 
 * of a CDATA section. 
 * <p> The <code>CDATASection</code> interface inherits from the 
 * <code>CharacterData</code> interface through the <code>Text</code> 
 * interface. Adjacent <code>CDATASections</code> nodes are not merged by use 
 * of the <code>normalize</code> method of the <code>Element</code> interface.
 *  Because no markup is recognized within a <code>CDATASection</code> , 
 * character numeric references cannot be used as an escape mechanism when 
 * serializing. Therefore, action needs to be taken when serializing a 
 * <code>CDATASection</code> with a character encoding where some of the 
 * contained characters cannot be represented. Failure to do so would not 
 * produce well-formed XML. One potential solution in the serialization 
 * process is to end the CDATA section before the character, output the 
 * character using a  character reference or entity reference, and open a new 
 * CDATA section  for any further characters in the text node. Note, however, 
 * that some  code conversion libraries at the time of writing do not return 
 * an error or exception when a character is missing from the encoding,  
 * making the task of ensuring that data is not corrupted on serialization 
 * more difficult.
 */
public interface CDATASection extends Text {
}

