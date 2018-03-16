package org.apache.xalan.res;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a String constant. And
 *  you need to enter key , value pair as part of contents
 * Array. You also need to update MAX_CODE for error strings
 * and MAX_WARNING for warnings ( Needed for only information
 * purpose )
 */
public class XSLTErrorResources_tr extends ListResourceBundle
{

/*
 * This file contains error and warning messages related to Xalan Error
 * Handling.
 *
 *  General notes to translators:
 *
 *  1) Xalan (or more properly, Xalan-interpretive) and XSLTC are names of
 *     components.
 *     XSLT is an acronym for "XML Stylesheet Language: Transformations".
 *     XSLTC is an acronym for XSLT Compiler.
 *
 *  2) A stylesheet is a description of how to transform an input XML document
 *     into a resultant XML document (or HTML document or text).  The
 *     stylesheet itself is described in the form of an XML document.
 *
 *  3) A template is a component of a stylesheet that is used to match a
 *     particular portion of an input document and specifies the form of the
 *     corresponding portion of the output document.
 *
 *  4) An element is a mark-up tag in an XML document; an attribute is a
 *     modifier on the tag.  For example, in <elem attr='val' attr2='val2'>
 *     "elem" is an element name, "attr" and "attr2" are attribute names with
 *     the values "val" and "val2", respectively.
 *
 *  5) A namespace declaration is a special attribute that is used to associate
 *     a prefix with a URI (the namespace).  The meanings of element names and
 *     attribute names that use that prefix are defined with respect to that
 *     namespace.
 *
 *  6) "Translet" is an invented term that describes the class file that
 *     results from compiling an XML stylesheet into a Java class.
 *
 *  7) XPath is a specification that describes a notation for identifying
 *     nodes in a tree-structured representation of an XML document.  An
 *     instance of that notation is referred to as an XPath expression.
 *
 */

  /** Maximum error messages, this is needed to keep track of the number of messages.    */
  public static final int MAX_CODE = 201;

  /** Maximum warnings, this is needed to keep track of the number of warnings.          */
  public static final int MAX_WARNING = 29;

  /** Maximum misc strings.   */
  public static final int MAX_OTHERS = 55;

  /** Maximum total warnings and error messages.          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;


  /*
   * Static variables
   */
  public static final String ER_NO_CURLYBRACE = "ER_NO_CURLYBRACE";;
  public static final String ER_ILLEGAL_ATTRIBUTE = "ER_ILLEGAL_ATTRIBUTE";
  public static final String ER_NULL_SOURCENODE_APPLYIMPORTS = "ER_NULL_SOURCENODE_APPLYIMPORTS";
  public static final String ER_CANNOT_ADD = "ER_CANNOT_ADD";
  public static final String ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES="ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES";
  public static final String ER_NO_NAME_ATTRIB = "ER_NO_NAME_ATTRIB";
  public static final String ER_TEMPLATE_NOT_FOUND = "ER_TEMPLATE_NOT_FOUND";
  public static final String ER_CANT_RESOLVE_NAME_AVT = "ER_CANT_RESOLVE_NAME_AVT";
  public static final String ER_REQUIRES_ATTRIB = "ER_REQUIRES_ATTRIB";
  public static final String ER_MUST_HAVE_TEST_ATTRIB = "ER_MUST_HAVE_TEST_ATTRIB";
  public static final String ER_BAD_VAL_ON_LEVEL_ATTRIB =
         "ER_BAD_VAL_ON_LEVEL_ATTRIB";
  public static final String ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML =
         "ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML";
  public static final String ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME =
         "ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME";
  public static final String ER_NEED_MATCH_ATTRIB = "ER_NEED_MATCH_ATTRIB";
  public static final String ER_NEED_NAME_OR_MATCH_ATTRIB =
         "ER_NEED_NAME_OR_MATCH_ATTRIB";
  public static final String ER_CANT_RESOLVE_NSPREFIX =
         "ER_CANT_RESOLVE_NSPREFIX";
  public static final String ER_ILLEGAL_VALUE = "ER_ILLEGAL_VALUE";
  public static final String ER_NO_OWNERDOC = "ER_NO_OWNERDOC";
  public static final String ER_ELEMTEMPLATEELEM_ERR ="ER_ELEMTEMPLATEELEM_ERR";
  public static final String ER_NULL_CHILD = "ER_NULL_CHILD";
  public static final String ER_NEED_SELECT_ATTRIB = "ER_NEED_SELECT_ATTRIB";
  public static final String ER_NEED_TEST_ATTRIB = "ER_NEED_TEST_ATTRIB";
  public static final String ER_NEED_NAME_ATTRIB = "ER_NEED_NAME_ATTRIB";
  public static final String ER_NO_CONTEXT_OWNERDOC = "ER_NO_CONTEXT_OWNERDOC";
  public static final String ER_COULD_NOT_CREATE_XML_PROC_LIAISON =
         "ER_COULD_NOT_CREATE_XML_PROC_LIAISON";
  public static final String ER_PROCESS_NOT_SUCCESSFUL =
         "ER_PROCESS_NOT_SUCCESSFUL";
  public static final String ER_NOT_SUCCESSFUL = "ER_NOT_SUCCESSFUL";
  public static final String ER_ENCODING_NOT_SUPPORTED =
         "ER_ENCODING_NOT_SUPPORTED";
  public static final String ER_COULD_NOT_CREATE_TRACELISTENER =
         "ER_COULD_NOT_CREATE_TRACELISTENER";
  public static final String ER_KEY_REQUIRES_NAME_ATTRIB =
         "ER_KEY_REQUIRES_NAME_ATTRIB";
  public static final String ER_KEY_REQUIRES_MATCH_ATTRIB =
         "ER_KEY_REQUIRES_MATCH_ATTRIB";
  public static final String ER_KEY_REQUIRES_USE_ATTRIB =
         "ER_KEY_REQUIRES_USE_ATTRIB";
  public static final String ER_REQUIRES_ELEMENTS_ATTRIB =
         "ER_REQUIRES_ELEMENTS_ATTRIB";
  public static final String ER_MISSING_PREFIX_ATTRIB =
         "ER_MISSING_PREFIX_ATTRIB";
  public static final String ER_BAD_STYLESHEET_URL = "ER_BAD_STYLESHEET_URL";
  public static final String ER_FILE_NOT_FOUND = "ER_FILE_NOT_FOUND";
  public static final String ER_IOEXCEPTION = "ER_IOEXCEPTION";
  public static final String ER_NO_HREF_ATTRIB = "ER_NO_HREF_ATTRIB";
  public static final String ER_STYLESHEET_INCLUDES_ITSELF =
         "ER_STYLESHEET_INCLUDES_ITSELF";
  public static final String ER_PROCESSINCLUDE_ERROR ="ER_PROCESSINCLUDE_ERROR";
  public static final String ER_MISSING_LANG_ATTRIB = "ER_MISSING_LANG_ATTRIB";
  public static final String ER_MISSING_CONTAINER_ELEMENT_COMPONENT =
         "ER_MISSING_CONTAINER_ELEMENT_COMPONENT";
  public static final String ER_CAN_ONLY_OUTPUT_TO_ELEMENT =
         "ER_CAN_ONLY_OUTPUT_TO_ELEMENT";
  public static final String ER_PROCESS_ERROR = "ER_PROCESS_ERROR";
  public static final String ER_UNIMPLNODE_ERROR = "ER_UNIMPLNODE_ERROR";
  public static final String ER_NO_SELECT_EXPRESSION ="ER_NO_SELECT_EXPRESSION";
  public static final String ER_CANNOT_SERIALIZE_XSLPROCESSOR =
         "ER_CANNOT_SERIALIZE_XSLPROCESSOR";
  public static final String ER_NO_INPUT_STYLESHEET = "ER_NO_INPUT_STYLESHEET";
  public static final String ER_FAILED_PROCESS_STYLESHEET =
         "ER_FAILED_PROCESS_STYLESHEET";
  public static final String ER_COULDNT_PARSE_DOC = "ER_COULDNT_PARSE_DOC";
  public static final String ER_COULDNT_FIND_FRAGMENT =
         "ER_COULDNT_FIND_FRAGMENT";
  public static final String ER_NODE_NOT_ELEMENT = "ER_NODE_NOT_ELEMENT";
  public static final String ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB =
         "ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB";
  public static final String ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB =
         "ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB";
  public static final String ER_NO_CLONE_OF_DOCUMENT_FRAG =
         "ER_NO_CLONE_OF_DOCUMENT_FRAG";
  public static final String ER_CANT_CREATE_ITEM = "ER_CANT_CREATE_ITEM";
  public static final String ER_XMLSPACE_ILLEGAL_VALUE =
         "ER_XMLSPACE_ILLEGAL_VALUE";
  public static final String ER_NO_XSLKEY_DECLARATION =
         "ER_NO_XSLKEY_DECLARATION";
  public static final String ER_CANT_CREATE_URL = "ER_CANT_CREATE_URL";
  public static final String ER_XSLFUNCTIONS_UNSUPPORTED =
         "ER_XSLFUNCTIONS_UNSUPPORTED";
  public static final String ER_PROCESSOR_ERROR = "ER_PROCESSOR_ERROR";
  public static final String ER_NOT_ALLOWED_INSIDE_STYLESHEET =
         "ER_NOT_ALLOWED_INSIDE_STYLESHEET";
  public static final String ER_RESULTNS_NOT_SUPPORTED =
         "ER_RESULTNS_NOT_SUPPORTED";
  public static final String ER_DEFAULTSPACE_NOT_SUPPORTED =
         "ER_DEFAULTSPACE_NOT_SUPPORTED";
  public static final String ER_INDENTRESULT_NOT_SUPPORTED =
         "ER_INDENTRESULT_NOT_SUPPORTED";
  public static final String ER_ILLEGAL_ATTRIB = "ER_ILLEGAL_ATTRIB";
  public static final String ER_UNKNOWN_XSL_ELEM = "ER_UNKNOWN_XSL_ELEM";
  public static final String ER_BAD_XSLSORT_USE = "ER_BAD_XSLSORT_USE";
  public static final String ER_MISPLACED_XSLWHEN = "ER_MISPLACED_XSLWHEN";
  public static final String ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE =
         "ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE";
  public static final String ER_MISPLACED_XSLOTHERWISE =
         "ER_MISPLACED_XSLOTHERWISE";
  public static final String ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE =
         "ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE";
  public static final String ER_NOT_ALLOWED_INSIDE_TEMPLATE =
         "ER_NOT_ALLOWED_INSIDE_TEMPLATE";
  public static final String ER_UNKNOWN_EXT_NS_PREFIX =
         "ER_UNKNOWN_EXT_NS_PREFIX";
  public static final String ER_IMPORTS_AS_FIRST_ELEM =
         "ER_IMPORTS_AS_FIRST_ELEM";
  public static final String ER_IMPORTING_ITSELF = "ER_IMPORTING_ITSELF";
  public static final String ER_XMLSPACE_ILLEGAL_VAL ="ER_XMLSPACE_ILLEGAL_VAL";
  public static final String ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL =
         "ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL";
  public static final String ER_SAX_EXCEPTION = "ER_SAX_EXCEPTION";
  public static final String ER_XSLT_ERROR = "ER_XSLT_ERROR";
  public static final String ER_CURRENCY_SIGN_ILLEGAL=
         "ER_CURRENCY_SIGN_ILLEGAL";
  public static final String ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM =
         "ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM";
  public static final String ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER =
         "ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER";
  public static final String ER_REDIRECT_COULDNT_GET_FILENAME =
         "ER_REDIRECT_COULDNT_GET_FILENAME";
  public static final String ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT =
         "ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT";
  public static final String ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX =
         "ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX";
  public static final String ER_MISSING_NS_URI = "ER_MISSING_NS_URI";
  public static final String ER_MISSING_ARG_FOR_OPTION =
         "ER_MISSING_ARG_FOR_OPTION";
  public static final String ER_INVALID_OPTION = "ER_INVALID_OPTION";
  public static final String ER_MALFORMED_FORMAT_STRING =
         "ER_MALFORMED_FORMAT_STRING";
  public static final String ER_STYLESHEET_REQUIRES_VERSION_ATTRIB =
         "ER_STYLESHEET_REQUIRES_VERSION_ATTRIB";
  public static final String ER_ILLEGAL_ATTRIBUTE_VALUE =
         "ER_ILLEGAL_ATTRIBUTE_VALUE";
  public static final String ER_CHOOSE_REQUIRES_WHEN ="ER_CHOOSE_REQUIRES_WHEN";
  public static final String ER_NO_APPLY_IMPORT_IN_FOR_EACH =
         "ER_NO_APPLY_IMPORT_IN_FOR_EACH";
  public static final String ER_CANT_USE_DTM_FOR_OUTPUT =
         "ER_CANT_USE_DTM_FOR_OUTPUT";
  public static final String ER_CANT_USE_DTM_FOR_INPUT =
         "ER_CANT_USE_DTM_FOR_INPUT";
  public static final String ER_CALL_TO_EXT_FAILED = "ER_CALL_TO_EXT_FAILED";
  public static final String ER_PREFIX_MUST_RESOLVE = "ER_PREFIX_MUST_RESOLVE";
  public static final String ER_INVALID_UTF16_SURROGATE =
         "ER_INVALID_UTF16_SURROGATE";
  public static final String ER_XSLATTRSET_USED_ITSELF =
         "ER_XSLATTRSET_USED_ITSELF";
  public static final String ER_CANNOT_MIX_XERCESDOM ="ER_CANNOT_MIX_XERCESDOM";
  public static final String ER_TOO_MANY_LISTENERS = "ER_TOO_MANY_LISTENERS";
  public static final String ER_IN_ELEMTEMPLATEELEM_READOBJECT =
         "ER_IN_ELEMTEMPLATEELEM_READOBJECT";
  public static final String ER_DUPLICATE_NAMED_TEMPLATE =
         "ER_DUPLICATE_NAMED_TEMPLATE";
  public static final String ER_INVALID_KEY_CALL = "ER_INVALID_KEY_CALL";
  public static final String ER_REFERENCING_ITSELF = "ER_REFERENCING_ITSELF";
  public static final String ER_ILLEGAL_DOMSOURCE_INPUT =
         "ER_ILLEGAL_DOMSOURCE_INPUT";
  public static final String ER_CLASS_NOT_FOUND_FOR_OPTION =
         "ER_CLASS_NOT_FOUND_FOR_OPTION";
  public static final String ER_REQUIRED_ELEM_NOT_FOUND =
         "ER_REQUIRED_ELEM_NOT_FOUND";
  public static final String ER_INPUT_CANNOT_BE_NULL ="ER_INPUT_CANNOT_BE_NULL";
  public static final String ER_URI_CANNOT_BE_NULL = "ER_URI_CANNOT_BE_NULL";
  public static final String ER_FILE_CANNOT_BE_NULL = "ER_FILE_CANNOT_BE_NULL";
  public static final String ER_SOURCE_CANNOT_BE_NULL =
         "ER_SOURCE_CANNOT_BE_NULL";
  public static final String ER_CANNOT_INIT_BSFMGR = "ER_CANNOT_INIT_BSFMGR";
  public static final String ER_CANNOT_CMPL_EXTENSN = "ER_CANNOT_CMPL_EXTENSN";
  public static final String ER_CANNOT_CREATE_EXTENSN =
         "ER_CANNOT_CREATE_EXTENSN";
  public static final String ER_INSTANCE_MTHD_CALL_REQUIRES =
         "ER_INSTANCE_MTHD_CALL_REQUIRES";
  public static final String ER_INVALID_ELEMENT_NAME ="ER_INVALID_ELEMENT_NAME";
  public static final String ER_ELEMENT_NAME_METHOD_STATIC =
         "ER_ELEMENT_NAME_METHOD_STATIC";
  public static final String ER_EXTENSION_FUNC_UNKNOWN =
         "ER_EXTENSION_FUNC_UNKNOWN";
  public static final String ER_MORE_MATCH_CONSTRUCTOR =
         "ER_MORE_MATCH_CONSTRUCTOR";
  public static final String ER_MORE_MATCH_METHOD = "ER_MORE_MATCH_METHOD";
  public static final String ER_MORE_MATCH_ELEMENT = "ER_MORE_MATCH_ELEMENT";
  public static final String ER_INVALID_CONTEXT_PASSED =
         "ER_INVALID_CONTEXT_PASSED";
  public static final String ER_POOL_EXISTS = "ER_POOL_EXISTS";
  public static final String ER_NO_DRIVER_NAME = "ER_NO_DRIVER_NAME";
  public static final String ER_NO_URL = "ER_NO_URL";
  public static final String ER_POOL_SIZE_LESSTHAN_ONE =
         "ER_POOL_SIZE_LESSTHAN_ONE";
  public static final String ER_INVALID_DRIVER = "ER_INVALID_DRIVER";
  public static final String ER_NO_STYLESHEETROOT = "ER_NO_STYLESHEETROOT";
  public static final String ER_ILLEGAL_XMLSPACE_VALUE =
         "ER_ILLEGAL_XMLSPACE_VALUE";
  public static final String ER_PROCESSFROMNODE_FAILED =
         "ER_PROCESSFROMNODE_FAILED";
  public static final String ER_RESOURCE_COULD_NOT_LOAD =
         "ER_RESOURCE_COULD_NOT_LOAD";
  public static final String ER_BUFFER_SIZE_LESSTHAN_ZERO =
         "ER_BUFFER_SIZE_LESSTHAN_ZERO";
  public static final String ER_UNKNOWN_ERROR_CALLING_EXTENSION =
         "ER_UNKNOWN_ERROR_CALLING_EXTENSION";
  public static final String ER_NO_NAMESPACE_DECL = "ER_NO_NAMESPACE_DECL";
  public static final String ER_ELEM_CONTENT_NOT_ALLOWED =
         "ER_ELEM_CONTENT_NOT_ALLOWED";
  public static final String ER_STYLESHEET_DIRECTED_TERMINATION =
         "ER_STYLESHEET_DIRECTED_TERMINATION";
  public static final String ER_ONE_OR_TWO = "ER_ONE_OR_TWO";
  public static final String ER_TWO_OR_THREE = "ER_TWO_OR_THREE";
  public static final String ER_COULD_NOT_LOAD_RESOURCE =
         "ER_COULD_NOT_LOAD_RESOURCE";
  public static final String ER_CANNOT_INIT_DEFAULT_TEMPLATES =
         "ER_CANNOT_INIT_DEFAULT_TEMPLATES";
  public static final String ER_RESULT_NULL = "ER_RESULT_NULL";
  public static final String ER_RESULT_COULD_NOT_BE_SET =
         "ER_RESULT_COULD_NOT_BE_SET";
  public static final String ER_NO_OUTPUT_SPECIFIED = "ER_NO_OUTPUT_SPECIFIED";
  public static final String ER_CANNOT_TRANSFORM_TO_RESULT_TYPE =
         "ER_CANNOT_TRANSFORM_TO_RESULT_TYPE";
  public static final String ER_CANNOT_TRANSFORM_SOURCE_TYPE =
         "ER_CANNOT_TRANSFORM_SOURCE_TYPE";
  public static final String ER_NULL_CONTENT_HANDLER ="ER_NULL_CONTENT_HANDLER";
  public static final String ER_NULL_ERROR_HANDLER = "ER_NULL_ERROR_HANDLER";
  public static final String ER_CANNOT_CALL_PARSE = "ER_CANNOT_CALL_PARSE";
  public static final String ER_NO_PARENT_FOR_FILTER ="ER_NO_PARENT_FOR_FILTER";
  public static final String ER_NO_STYLESHEET_IN_MEDIA =
         "ER_NO_STYLESHEET_IN_MEDIA";
  public static final String ER_NO_STYLESHEET_PI = "ER_NO_STYLESHEET_PI";
  public static final String ER_NOT_SUPPORTED = "ER_NOT_SUPPORTED";
  public static final String ER_PROPERTY_VALUE_BOOLEAN =
         "ER_PROPERTY_VALUE_BOOLEAN";
  public static final String ER_COULD_NOT_FIND_EXTERN_SCRIPT =
         "ER_COULD_NOT_FIND_EXTERN_SCRIPT";
  public static final String ER_RESOURCE_COULD_NOT_FIND =
         "ER_RESOURCE_COULD_NOT_FIND";
  public static final String ER_OUTPUT_PROPERTY_NOT_RECOGNIZED =
         "ER_OUTPUT_PROPERTY_NOT_RECOGNIZED";
  public static final String ER_FAILED_CREATING_ELEMLITRSLT =
         "ER_FAILED_CREATING_ELEMLITRSLT";
  public static final String ER_VALUE_SHOULD_BE_NUMBER =
         "ER_VALUE_SHOULD_BE_NUMBER";
  public static final String ER_VALUE_SHOULD_EQUAL = "ER_VALUE_SHOULD_EQUAL";
  public static final String ER_FAILED_CALLING_METHOD =
         "ER_FAILED_CALLING_METHOD";
  public static final String ER_FAILED_CREATING_ELEMTMPL =
         "ER_FAILED_CREATING_ELEMTMPL";
  public static final String ER_CHARS_NOT_ALLOWED = "ER_CHARS_NOT_ALLOWED";
  public static final String ER_ATTR_NOT_ALLOWED = "ER_ATTR_NOT_ALLOWED";
  public static final String ER_BAD_VALUE = "ER_BAD_VALUE";
  public static final String ER_ATTRIB_VALUE_NOT_FOUND =
         "ER_ATTRIB_VALUE_NOT_FOUND";
  public static final String ER_ATTRIB_VALUE_NOT_RECOGNIZED =
         "ER_ATTRIB_VALUE_NOT_RECOGNIZED";
  public static final String ER_NULL_URI_NAMESPACE = "ER_NULL_URI_NAMESPACE";
  public static final String ER_NUMBER_TOO_BIG = "ER_NUMBER_TOO_BIG";
  public static final String  ER_CANNOT_FIND_SAX1_DRIVER =
         "ER_CANNOT_FIND_SAX1_DRIVER";
  public static final String  ER_SAX1_DRIVER_NOT_LOADED =
         "ER_SAX1_DRIVER_NOT_LOADED";
  public static final String  ER_SAX1_DRIVER_NOT_INSTANTIATED =
         "ER_SAX1_DRIVER_NOT_INSTANTIATED" ;
  public static final String ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER =
         "ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER";
  public static final String  ER_PARSER_PROPERTY_NOT_SPECIFIED =
         "ER_PARSER_PROPERTY_NOT_SPECIFIED";
  public static final String  ER_PARSER_ARG_CANNOT_BE_NULL =
         "ER_PARSER_ARG_CANNOT_BE_NULL" ;
  public static final String  ER_FEATURE = "ER_FEATURE";
  public static final String ER_PROPERTY = "ER_PROPERTY" ;
  public static final String ER_NULL_ENTITY_RESOLVER ="ER_NULL_ENTITY_RESOLVER";
  public static final String  ER_NULL_DTD_HANDLER = "ER_NULL_DTD_HANDLER" ;
  public static final String ER_NO_DRIVER_NAME_SPECIFIED =
         "ER_NO_DRIVER_NAME_SPECIFIED";
  public static final String ER_NO_URL_SPECIFIED = "ER_NO_URL_SPECIFIED";
  public static final String ER_POOLSIZE_LESS_THAN_ONE =
         "ER_POOLSIZE_LESS_THAN_ONE";
  public static final String ER_INVALID_DRIVER_NAME = "ER_INVALID_DRIVER_NAME";
  public static final String ER_ERRORLISTENER = "ER_ERRORLISTENER";
  public static final String ER_ASSERT_NO_TEMPLATE_PARENT =
         "ER_ASSERT_NO_TEMPLATE_PARENT";
  public static final String ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR =
         "ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR";
  public static final String ER_NOT_ALLOWED_IN_POSITION =
         "ER_NOT_ALLOWED_IN_POSITION";
  public static final String ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION =
         "ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION";
  public static final String INVALID_TCHAR = "INVALID_TCHAR";
  public static final String INVALID_QNAME = "INVALID_QNAME";
  public static final String INVALID_ENUM = "INVALID_ENUM";
  public static final String INVALID_NMTOKEN = "INVALID_NMTOKEN";
  public static final String INVALID_NCNAME = "INVALID_NCNAME";
  public static final String INVALID_BOOLEAN = "INVALID_BOOLEAN";
  public static final String INVALID_NUMBER = "INVALID_NUMBER";
  public static final String ER_ARG_LITERAL = "ER_ARG_LITERAL";
  public static final String ER_DUPLICATE_GLOBAL_VAR ="ER_DUPLICATE_GLOBAL_VAR";
  public static final String ER_DUPLICATE_VAR = "ER_DUPLICATE_VAR";
  public static final String ER_TEMPLATE_NAME_MATCH = "ER_TEMPLATE_NAME_MATCH";
  public static final String ER_INVALID_PREFIX = "ER_INVALID_PREFIX";
  public static final String ER_NO_ATTRIB_SET = "ER_NO_ATTRIB_SET";

  public static final String WG_FOUND_CURLYBRACE = "WG_FOUND_CURLYBRACE";
  public static final String WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR =
         "WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR";
  public static final String WG_EXPR_ATTRIB_CHANGED_TO_SELECT =
         "WG_EXPR_ATTRIB_CHANGED_TO_SELECT";
  public static final String WG_NO_LOCALE_IN_FORMATNUMBER =
         "WG_NO_LOCALE_IN_FORMATNUMBER";
  public static final String WG_LOCALE_NOT_FOUND = "WG_LOCALE_NOT_FOUND";
  public static final String WG_CANNOT_MAKE_URL_FROM ="WG_CANNOT_MAKE_URL_FROM";
  public static final String WG_CANNOT_LOAD_REQUESTED_DOC =
         "WG_CANNOT_LOAD_REQUESTED_DOC";
  public static final String WG_CANNOT_FIND_COLLATOR ="WG_CANNOT_FIND_COLLATOR";
  public static final String WG_FUNCTIONS_SHOULD_USE_URL =
         "WG_FUNCTIONS_SHOULD_USE_URL";
  public static final String WG_ENCODING_NOT_SUPPORTED_USING_UTF8 =
         "WG_ENCODING_NOT_SUPPORTED_USING_UTF8";
  public static final String WG_ENCODING_NOT_SUPPORTED_USING_JAVA =
         "WG_ENCODING_NOT_SUPPORTED_USING_JAVA";
  public static final String WG_SPECIFICITY_CONFLICTS =
         "WG_SPECIFICITY_CONFLICTS";
  public static final String WG_PARSING_AND_PREPARING =
         "WG_PARSING_AND_PREPARING";
  public static final String WG_ATTR_TEMPLATE = "WG_ATTR_TEMPLATE";
  public static final String WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = "WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESP";
  public static final String WG_ATTRIB_NOT_HANDLED = "WG_ATTRIB_NOT_HANDLED";
  public static final String WG_NO_DECIMALFORMAT_DECLARATION =
         "WG_NO_DECIMALFORMAT_DECLARATION";
  public static final String WG_OLD_XSLT_NS = "WG_OLD_XSLT_NS";
  public static final String WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED =
         "WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED";
  public static final String WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE =
         "WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE";
  public static final String WG_ILLEGAL_ATTRIBUTE = "WG_ILLEGAL_ATTRIBUTE";
  public static final String WG_COULD_NOT_RESOLVE_PREFIX =
         "WG_COULD_NOT_RESOLVE_PREFIX";
  public static final String WG_STYLESHEET_REQUIRES_VERSION_ATTRIB =
         "WG_STYLESHEET_REQUIRES_VERSION_ATTRIB";
  public static final String WG_ILLEGAL_ATTRIBUTE_NAME =
         "WG_ILLEGAL_ATTRIBUTE_NAME";
  public static final String WG_ILLEGAL_ATTRIBUTE_VALUE =
         "WG_ILLEGAL_ATTRIBUTE_VALUE";
  public static final String WG_EMPTY_SECOND_ARG = "WG_EMPTY_SECOND_ARG";
  public static final String WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML =
         "WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML";
  public static final String WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME =
         "WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME";
  public static final String WG_ILLEGAL_ATTRIBUTE_POSITION =
         "WG_ILLEGAL_ATTRIBUTE_POSITION";


  /*
   * Now fill in the message text.
   * Then fill in the message text for that message code in the
   * array. Use the new error code as the index into the array.
   */


  /** The lookup table for error messages.   */
  public static final Object[][] contents = {

  /** Error message ID that has a null message, but takes in a single object.    */
  {"ER0000" , "{0}" },


  /** ER_NO_CURLYBRACE          */


    { ER_NO_CURLYBRACE,
      "Hata: \u0130fade i\u00e7inde '{' olamaz"},

  /** ER_ILLEGAL_ATTRIBUTE          */

    { ER_ILLEGAL_ATTRIBUTE ,
     "{0} ge\u00e7ersiz {1} \u00f6zniteli\u011fini i\u00e7eriyor"},

  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */

  {ER_NULL_SOURCENODE_APPLYIMPORTS ,
      "xsl:apply-imports i\u00e7inde sourceNode bo\u015f de\u011ferli!"},

  /** ER_CANNOT_ADD          */

  {ER_CANNOT_ADD,
      "{0}, {1} i\u00e7ine eklenemiyor"},


  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */


    { ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
      "handleApplyTemplatesInstruction i\u00e7inde sourceNode bo\u015f de\u011ferli!"},

  /** ER_NO_NAME_ATTRIB          */


    { ER_NO_NAME_ATTRIB,
     "{0} i\u00e7in \u00f6znitelik belirtilmeli."},

  /** ER_TEMPLATE_NOT_FOUND          */


    {ER_TEMPLATE_NOT_FOUND,
     "Ad\u0131 {0} olan \u015fablon bulunamad\u0131"},

  /** ER_CANT_RESOLVE_NAME_AVT          */

    {ER_CANT_RESOLVE_NAME_AVT,
      "xsl:call-template i\u00e7inde AVT ad\u0131 \u00e7\u00f6z\u00fclemedi."},

  /** ER_REQUIRES_ATTRIB          */


    {ER_REQUIRES_ATTRIB,
     "{0} i\u00e7in {1} \u00f6zniteli\u011fi gerekiyor."},

  /** ER_MUST_HAVE_TEST_ATTRIB          */


    { ER_MUST_HAVE_TEST_ATTRIB,
      "{0} i\u00e7in ''test'' \u00f6zniteli\u011fi gerekiyor."},

  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */


    {ER_BAD_VAL_ON_LEVEL_ATTRIB,
      "{0} d\u00fczey \u00f6zniteli\u011finde hatal\u0131 de\u011fer."},

  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */


    {ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "processing-instruction ad\u0131 'xml' olamaz"},

  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */


    { ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "processing-instruction ad\u0131 ge\u00e7erli bir NCName olmal\u0131d\u0131r: {0}"},

  /** ER_NEED_MATCH_ATTRIB          */


    { ER_NEED_MATCH_ATTRIB,
      "{0} kip i\u00e7eriyorsa match \u00f6zniteli\u011fi olmas\u0131 gerekir."},

  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */


    { ER_NEED_NAME_OR_MATCH_ATTRIB,
      "{0} i\u00e7in name ya da match \u00f6zniteli\u011fi gerekiyor."},

  /** ER_CANT_RESOLVE_NSPREFIX          */


    {ER_CANT_RESOLVE_NSPREFIX,
      "Ad alan\u0131 \u00f6neki {0} \u00e7\u00f6z\u00fclemiyor."},

  /** ER_ILLEGAL_VALUE          */


    { ER_ILLEGAL_VALUE,
     "xml:space ge\u00e7ersiz {0} de\u011ferini i\u00e7eriyor."},

  /** ER_NO_OWNERDOC          */


    { ER_NO_OWNERDOC,
      "Alt d\u00fc\u011f\u00fcm\u00fcn iye belgesi yok!"},

  /** ER_ELEMTEMPLATEELEM_ERR          */


    { ER_ELEMTEMPLATEELEM_ERR,
     "ElemTemplateElement hatas\u0131: {0}"},

  /** ER_NULL_CHILD          */


    { ER_NULL_CHILD,
     "Bo\u015f de\u011ferli (null) alt \u00f6\u011fe ekleme giri\u015fimi!"},

  /** ER_NEED_SELECT_ATTRIB          */


    { ER_NEED_SELECT_ATTRIB,
     "{0} i\u00e7in select \u00f6zniteli\u011fi gerekiyor."},

  /** ER_NEED_TEST_ATTRIB          */


    { ER_NEED_TEST_ATTRIB ,
      "xsl:when i\u00e7in 'test' \u00f6zniteli\u011fi gereklidir."},

  /** ER_NEED_NAME_ATTRIB          */


    { ER_NEED_NAME_ATTRIB,
      "xsl:with-param i\u00e7in 'name' \u00f6zniteli\u011fi gereklidir."},

  /** ER_NO_CONTEXT_OWNERDOC          */


    { ER_NO_CONTEXT_OWNERDOC,
      "Ba\u011flam\u0131n iye belgesi yok!"},

  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */


    {ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
      "XML TransformerFactory ili\u015fkisi {0} yarat\u0131lamad\u0131"},

  /** ER_PROCESS_NOT_SUCCESSFUL          */


    {ER_PROCESS_NOT_SUCCESSFUL,
      "Xalan: Process ba\u015far\u0131l\u0131 olmad\u0131."},

  /** ER_NOT_SUCCESSFUL          */


    { ER_NOT_SUCCESSFUL,
     "Xalan: ba\u015far\u0131l\u0131 olmad\u0131."},

  /** ER_ENCODING_NOT_SUPPORTED          */


    { ER_ENCODING_NOT_SUPPORTED,
     "{0} kodlamas\u0131 desteklenmiyor."},

  /** ER_COULD_NOT_CREATE_TRACELISTENER          */


    {ER_COULD_NOT_CREATE_TRACELISTENER,
      "TraceListener {0} yarat\u0131lamad\u0131."},

  /** ER_KEY_REQUIRES_NAME_ATTRIB          */


    {ER_KEY_REQUIRES_NAME_ATTRIB,
      "xsl:key i\u00e7in 'name' \u00f6zniteli\u011fi gerekiyor!"},

  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */


    { ER_KEY_REQUIRES_MATCH_ATTRIB,
      "xsl:key i\u00e7in 'match' \u00f6zniteli\u011fi gerekiyor!"},

  /** ER_KEY_REQUIRES_USE_ATTRIB          */


    { ER_KEY_REQUIRES_USE_ATTRIB,
      "xsl:key i\u00e7in 'use' \u00f6zniteli\u011fi gerekiyor!"},

  /** ER_REQUIRES_ELEMENTS_ATTRIB          */


    { ER_REQUIRES_ELEMENTS_ATTRIB,
      "(StylesheetHandler) {0} i\u00e7in ''elements'' \u00f6zniteli\u011fi gerekiyor!"},

  /** ER_MISSING_PREFIX_ATTRIB          */


    { ER_MISSING_PREFIX_ATTRIB,
      "(StylesheetHandler) {0} \u00f6zniteli\u011fi ''prefix'' eksik"},

  /** ER_BAD_STYLESHEET_URL          */


    { ER_BAD_STYLESHEET_URL,
     "Bi\u00e7em yapra\u011f\u0131 URL adresi {0} ge\u00e7ersiz"},

  /** ER_FILE_NOT_FOUND          */


    { ER_FILE_NOT_FOUND,
     "Bi\u00e7em yapra\u011f\u0131 dosyas\u0131 bulunamad\u0131: {0}"},

  /** ER_IOEXCEPTION          */


    { ER_IOEXCEPTION,
      "Bi\u00e7em yapra\u011f\u0131 dosyas\u0131 {0} ile G\u00c7 kural d\u0131\u015f\u0131 durumu olu\u015ftu"},

  /** ER_NO_HREF_ATTRIB          */


    { ER_NO_HREF_ATTRIB,
      "(StylesheetHandler) {0} i\u00e7in href \u00f6zniteli\u011fi bulunamad\u0131"},

  /** ER_STYLESHEET_INCLUDES_ITSELF          */


    { ER_STYLESHEET_INCLUDES_ITSELF,
      "(StylesheetHandler) {0} do\u011frudan ya da dolayl\u0131 olarak kendisini i\u00e7eriyor!"},

  /** ER_PROCESSINCLUDE_ERROR          */


    { ER_PROCESSINCLUDE_ERROR,
      "StylesheetHandler.processInclude hatas\u0131, {0}"},

  /** ER_MISSING_LANG_ATTRIB          */


    { ER_MISSING_LANG_ATTRIB,
      "(StylesheetHandler) {0} \u00f6zniteli\u011fi ''lang'' eksik"},

  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */

    { ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
      "(StylesheetHandler) {0} \u00f6\u011fesinin yeri yanl\u0131\u015f? ta\u015f\u0131y\u0131c\u0131 \u00f6\u011fesi ''component'' eksik"},

  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */

    { ER_CAN_ONLY_OUTPUT_TO_ELEMENT,
      "\u00c7\u0131k\u0131\u015f yaln\u0131zca \u015funlara y\u00f6neltilebilir: Element, DocumentFragment, Document ya da PrintWriter."},

  /** ER_PROCESS_ERROR          */

    { ER_PROCESS_ERROR,
     "StylesheetRoot.process hatas\u0131"},

  /** ER_UNIMPLNODE_ERROR          */

    { ER_UNIMPLNODE_ERROR,
     "UnImplNode hatas\u0131: {0}"},

  /** ER_NO_SELECT_EXPRESSION          */

    { ER_NO_SELECT_EXPRESSION,
      "Hata! xpath select ifadesi (-select) bulunamad\u0131."},

  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */

    { ER_CANNOT_SERIALIZE_XSLPROCESSOR,
      "XSLProcessor diziselle\u015ftirilemez!"},

  /** ER_NO_INPUT_STYLESHEET          */

    { ER_NO_INPUT_STYLESHEET,
      "Bi\u00e7em yapra\u011f\u0131 giri\u015fi belirtilmedi!"},

  /** ER_FAILED_PROCESS_STYLESHEET          */

    { ER_FAILED_PROCESS_STYLESHEET,
      "Bi\u00e7em yapra\u011f\u0131 i\u015flenemedi!"},

  /** ER_COULDNT_PARSE_DOC          */

    { ER_COULDNT_PARSE_DOC,
     "{0} belgesi ayr\u0131\u015ft\u0131r\u0131lamad\u0131!"},

  /** ER_COULDNT_FIND_FRAGMENT          */

    { ER_COULDNT_FIND_FRAGMENT,
     "Par\u00e7a bulunamad\u0131: {0}"},

  /** ER_NODE_NOT_ELEMENT          */

    { ER_NODE_NOT_ELEMENT,
      "Par\u00e7a tan\u0131t\u0131c\u0131s\u0131n\u0131n g\u00f6sterdi\u011fi d\u00fc\u011f\u00fcm bir \u00f6\u011fe de\u011fildi: {0}"},

  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */

    { ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
      "for-each i\u00e7in e\u015fle\u015fme ya da ad \u00f6zniteli\u011fi gerekir"},

  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */

    { ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
      "templates i\u00e7in e\u015fle\u015fme ya da ad \u00f6zniteli\u011fi gerekir"},

  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */

    { ER_NO_CLONE_OF_DOCUMENT_FRAG,
      "Belge par\u00e7as\u0131n\u0131n e\u015fkopyas\u0131 de\u011fil!"},

  /** ER_CANT_CREATE_ITEM          */

    { ER_CANT_CREATE_ITEM,
      "Sonu\u00e7 a\u011fac\u0131nda \u00f6\u011fe yarat\u0131lam\u0131yor: {0}"},

  /** ER_XMLSPACE_ILLEGAL_VALUE          */

    { ER_XMLSPACE_ILLEGAL_VALUE,
      "Kaynak XML i\u00e7inde xml:space ge\u00e7ersiz de\u011fer i\u00e7eriyor: {0}"},

  /** ER_NO_XSLKEY_DECLARATION          */

    { ER_NO_XSLKEY_DECLARATION,
      "{0} i\u00e7in xsl:key bildirimi yok!"},

  /** ER_CANT_CREATE_URL          */

    { ER_CANT_CREATE_URL,
     "Hata! \u0130lgili url yarat\u0131lam\u0131yor: {0}"},

  /** ER_XSLFUNCTIONS_UNSUPPORTED          */

    { ER_XSLFUNCTIONS_UNSUPPORTED,
     "xsl:functions desteklenmiyor"},

  /** ER_PROCESSOR_ERROR          */

    { ER_PROCESSOR_ERROR,
     "XSLT TransformerFactory Hatas\u0131"},

  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */

    { ER_NOT_ALLOWED_INSIDE_STYLESHEET,
      "(StylesheetHandler) {0} bi\u00e7em yapra\u011f\u0131 i\u00e7inde olamaz!"},

  /** ER_RESULTNS_NOT_SUPPORTED          */

    { ER_RESULTNS_NOT_SUPPORTED,
      "result-ns art\u0131k desteklenmiyor! Yerine xsl:output kullan\u0131n."},

  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */

    { ER_DEFAULTSPACE_NOT_SUPPORTED,
      "default-space art\u0131k desteklenmiyor!  Yerine xsl:strip-space ya da xsl:preserve-space kullan\u0131n."},

  /** ER_INDENTRESULT_NOT_SUPPORTED          */

    { ER_INDENTRESULT_NOT_SUPPORTED,
      "indent-result art\u0131k desteklenmiyor!  Yerine xsl:output kullan\u0131n."},

  /** ER_ILLEGAL_ATTRIB          */

    { ER_ILLEGAL_ATTRIB,
      "(StylesheetHandler) {0} ge\u00e7ersiz {1} \u00f6zniteli\u011fini i\u00e7eriyor"},

  /** ER_UNKNOWN_XSL_ELEM          */

    { ER_UNKNOWN_XSL_ELEM,
     "Bilinmeyen XSL \u00f6\u011fesi: {0}"},

  /** ER_BAD_XSLSORT_USE          */

    { ER_BAD_XSLSORT_USE,
      "(StylesheetHandler) xsl:sort yaln\u0131zca xsl:apply-templates ya da xsl:for-each ile kullan\u0131labilir."},

  /** ER_MISPLACED_XSLWHEN          */

    { ER_MISPLACED_XSLWHEN,
      "(StylesheetHandler) xsl:when yeri yanl\u0131\u015f!"},

  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */

    { ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:when \u00f6\u011fesinin \u00fcst \u00f6\u011fesi xsl:choose de\u011fil!"},

  /** ER_MISPLACED_XSLOTHERWISE          */

    { ER_MISPLACED_XSLOTHERWISE,
      "(StylesheetHandler) xsl:otherwise yeri yanl\u0131\u015f!"},

  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */

    { ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:otherwise \u00f6\u011fesinin \u00fcst \u00f6\u011fesi xsl:choose de\u011fil!"},

  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */

    { ER_NOT_ALLOWED_INSIDE_TEMPLATE,
      "(StylesheetHandler) {0} \u015fablon i\u00e7inde kullan\u0131lamaz!"},

  /** ER_UNKNOWN_EXT_NS_PREFIX          */

    { ER_UNKNOWN_EXT_NS_PREFIX,
      "(StylesheetHandler) {0} eklenti ad alan\u0131 \u00f6neki {1} bilinmiyor"},

  /** ER_IMPORTS_AS_FIRST_ELEM          */

    { ER_IMPORTS_AS_FIRST_ELEM,
      "(StylesheetHandler) Import \u00f6\u011feleri, bi\u00e7em yapra\u011f\u0131n\u0131n ilk \u00f6\u011feleri olarak ge\u00e7ebilir!"},

  /** ER_IMPORTING_ITSELF          */

    { ER_IMPORTING_ITSELF,
      "(StylesheetHandler) {0} do\u011frudan ya da dolayl\u0131 olarak kendisini i\u00e7e aktar\u0131yor!"},

  /** ER_XMLSPACE_ILLEGAL_VAL          */

    { ER_XMLSPACE_ILLEGAL_VAL,
      "(StylesheetHandler) xml:space ge\u00e7ersiz {0} de\u011ferini i\u00e7eriyor"},

  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */

    { ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
      "processStylesheet ba\u015far\u0131s\u0131z oldu!"},

  /** ER_SAX_EXCEPTION          */

    { ER_SAX_EXCEPTION,
     "SAX kural d\u0131\u015f\u0131 durumu"},

  /** ER_FUNCTION_NOT_SUPPORTED          */


  /** ER_XSLT_ERROR          */

    { ER_XSLT_ERROR,
     "XSLT hatas\u0131"},

  /** ER_CURRENCY_SIGN_ILLEGAL          */

    { ER_CURRENCY_SIGN_ILLEGAL,
      "Bi\u00e7im \u00f6r\u00fcnt\u00fcs\u00fc dizgisinde para birimi simgesi olamaz"},

  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */

    { ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
      "Stylesheet DOM belge i\u015flevini desteklemiyor!"},

  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */

    { ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
      "\u00d6nek d\u0131\u015f\u0131 \u00e7\u00f6z\u00fcc\u00fcn\u00fcn \u00f6neki \u00e7\u00f6z\u00fclemez."},

  /** ER_REDIRECT_COULDNT_GET_FILENAME          */

    { ER_REDIRECT_COULDNT_GET_FILENAME,
      "Yeniden y\u00f6nlendirme eklentisi: Dosya ad\u0131 al\u0131namad\u0131 - file ya da select \u00f6zniteli\u011fi ge\u00e7erli bir dizgi d\u00f6nd\u00fcrmelidir."},

  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */

    { ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
      "Yeniden y\u00f6nlendirme eklentisinde FormatterListener olu\u015fturulamad\u0131!"},

  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */

    { ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
      "exclude-result-prefixes i\u00e7indeki \u00f6nek ge\u00e7erli de\u011fil: {0}"},

  /** ER_MISSING_NS_URI          */

    { ER_MISSING_NS_URI,
      "Belirtilen \u00f6nek i\u00e7in ad alan\u0131 URI eksik"},

  /** ER_MISSING_ARG_FOR_OPTION          */

    { ER_MISSING_ARG_FOR_OPTION,
      "{0} se\u00e7ene\u011fi i\u00e7in ba\u011f\u0131ms\u0131z de\u011fi\u015fken eksik"},

  /** ER_INVALID_OPTION          */

    { ER_INVALID_OPTION,
     "Ge\u00e7ersiz se\u00e7enek: {0}"},

  /** ER_MALFORMED_FORMAT_STRING          */

    { ER_MALFORMED_FORMAT_STRING,
     "Bozuk bi\u00e7imli bi\u00e7im dizgisi: {0}"},

  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */

    { ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet i\u00e7in 'version' \u00f6zniteli\u011fi gerekiyor!"},

  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */

    { ER_ILLEGAL_ATTRIBUTE_VALUE,
      "{0} \u00f6zniteli\u011fi ge\u00e7ersiz {1} de\u011ferini i\u00e7eriyor"},

  /** ER_CHOOSE_REQUIRES_WHEN          */

    { ER_CHOOSE_REQUIRES_WHEN,
     "xsl:choose i\u00e7in xsl:when gerekiyor"},

  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */

    { ER_NO_APPLY_IMPORT_IN_FOR_EACH,
      "xsl:apply-imports, xsl:for-each i\u00e7inde kullan\u0131lamaz"},

  /** ER_CANT_USE_DTM_FOR_OUTPUT          */

    { ER_CANT_USE_DTM_FOR_OUTPUT,
      "\u00c7\u0131k\u0131\u015f DOM d\u00fc\u011f\u00fcm\u00fc i\u00e7in DTMLiaison kullan\u0131lamaz... onun yerine org.apache.xpath.DOM2Helper aktar\u0131n!"},

  /** ER_CANT_USE_DTM_FOR_INPUT          */

    { ER_CANT_USE_DTM_FOR_INPUT,
      "Giri\u015f DOM d\u00fc\u011f\u00fcm\u00fc i\u00e7in DTMLiaison kullan\u0131lamaz... onun yerine org.apache.xpath.DOM2Helper aktar\u0131n!"},

  /** ER_CALL_TO_EXT_FAILED          */

    { ER_CALL_TO_EXT_FAILED,
      "Eklenti \u00f6\u011fesine \u00e7a\u011fr\u0131 ba\u015far\u0131s\u0131z oldu: {0}"},

  /** ER_PREFIX_MUST_RESOLVE          */

    { ER_PREFIX_MUST_RESOLVE,
      "\u00d6nek bir ad alan\u0131na \u00e7\u00f6z\u00fclmelidir: {0}"},

  /** ER_INVALID_UTF16_SURROGATE          */

    { ER_INVALID_UTF16_SURROGATE,
      "UTF-16 yerine kullan\u0131lan de\u011fer ge\u00e7ersiz: {0} ?"},

  /** ER_XSLATTRSET_USED_ITSELF          */

    { ER_XSLATTRSET_USED_ITSELF,
      "xsl:attribute-set {0} kendisini kulland\u0131, sonsuz d\u00f6ng\u00fc olu\u015facak."},

  /** ER_CANNOT_MIX_XERCESDOM          */

    { ER_CANNOT_MIX_XERCESDOM,
      "Xerces-DOM d\u0131\u015f\u0131 giri\u015f Xerces-DOM \u00e7\u0131k\u0131\u015fla birle\u015ftirilemez!"},

  /** ER_TOO_MANY_LISTENERS          */

    { ER_TOO_MANY_LISTENERS,
      "addTraceListenersToStylesheet - TooManyListenersException"},

  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */

    { ER_IN_ELEMTEMPLATEELEM_READOBJECT,
      "ElemTemplateElement.readObject i\u00e7inde: {0}"},

  /** ER_DUPLICATE_NAMED_TEMPLATE          */

    { ER_DUPLICATE_NAMED_TEMPLATE,
      "Bu ad\u0131 ta\u015f\u0131yan birden \u00e7ok \u015fablon saptand\u0131: {0}"},

  /** ER_INVALID_KEY_CALL          */

    { ER_INVALID_KEY_CALL,
      "Ge\u00e7ersiz i\u015flev \u00e7a\u011fr\u0131s\u0131: \u00d6zyineli key() \u00e7a\u011fr\u0131lar\u0131na izin verilmez"},

  /** Variable is referencing itself          */

    { ER_REFERENCING_ITSELF,
      "{0} de\u011fi\u015fkeni do\u011frudan ya da dolayl\u0131 olarak kendisine ba\u015fvuruda bulunuyor!"},

  /** Illegal DOMSource input          */

    { ER_ILLEGAL_DOMSOURCE_INPUT,
      "newTemplates ile ilgili DOMSource i\u00e7in giri\u015f d\u00fc\u011f\u00fcm\u00fc bo\u015f de\u011ferli olamaz!"},

        /** Class not found for option         */

    { ER_CLASS_NOT_FOUND_FOR_OPTION,
        "{0} se\u00e7ene\u011fi i\u00e7in s\u0131n\u0131f dosyas\u0131 bulunamad\u0131"},

        /** Required Element not found         */

    { ER_REQUIRED_ELEM_NOT_FOUND,
        "Gerekli \u00f6\u011fe bulunamad\u0131: {0}"},

  /** InputStream cannot be null         */

    { ER_INPUT_CANNOT_BE_NULL,
        "InputStream bo\u015f de\u011ferli olamaz"},

  /** URI cannot be null         */

    { ER_URI_CANNOT_BE_NULL,
        "URI bo\u015f de\u011ferli olamaz"},

  /** File cannot be null         */

    { ER_FILE_CANNOT_BE_NULL,
        "Dosya bo\u015f de\u011ferli olamaz"},

   /** InputSource cannot be null         */

    { ER_SOURCE_CANNOT_BE_NULL,
                "InputSource bo\u015f de\u011ferli olamaz"},

  /** Can't overwrite cause         */


  /** Could not initialize BSF Manager        */

    { ER_CANNOT_INIT_BSFMGR,
                "BSF Manager kullan\u0131ma haz\u0131rlanamad\u0131"},

  /** Could not compile extension       */

    { ER_CANNOT_CMPL_EXTENSN,
                "Eklenti derlenemedi"},

  /** Could not create extension       */

    { ER_CANNOT_CREATE_EXTENSN,
      "Eklenti yarat\u0131lamad\u0131: {0} nedeni: {1}"},

  /** Instance method call to method {0} requires an Object instance as first argument       */

    { ER_INSTANCE_MTHD_CALL_REQUIRES,
      "{0} y\u00f6ntemine y\u00f6nelik Instance y\u00f6ntemi, birincil ba\u011f\u0131ms\u0131z de\u011fi\u015fkenin somutla\u015fan nesne \u00f6rne\u011fi olmas\u0131n\u0131 gerektirir"},

  /** Invalid element name specified       */

    { ER_INVALID_ELEMENT_NAME,
      "Belirtilen \u00f6\u011fe ad\u0131 ge\u00e7ersiz {0}"},

   /** Element name method must be static      */

    { ER_ELEMENT_NAME_METHOD_STATIC,
      "\u00d6\u011fe ad\u0131 y\u00f6ntemi dura\u011fan {0} olmal\u0131"},

   /** Extension function {0} : {1} is unknown      */

    { ER_EXTENSION_FUNC_UNKNOWN,
             "Eklenti i\u015flevi {0} : {1} bilinmiyor"},

   /** More than one best match for constructor for       */

    { ER_MORE_MATCH_CONSTRUCTOR,
             "{0} ile ilgili olu\u015fturucu i\u00e7in en iyi e\u015fle\u015fme say\u0131s\u0131 birden \u00e7ok"},

   /** More than one best match for method      */

    { ER_MORE_MATCH_METHOD,
             "{0} y\u00f6ntemi i\u00e7in en iyi e\u015fle\u015fme say\u0131s\u0131 birden \u00e7ok"},

   /** More than one best match for element method      */

    { ER_MORE_MATCH_ELEMENT,
             "{0} \u00f6\u011fe y\u00f6ntemi i\u00e7in en iyi e\u015fle\u015fme say\u0131s\u0131 birden \u00e7ok"},

   /** Invalid context passed to evaluate       */

    { ER_INVALID_CONTEXT_PASSED,
             "{0} de\u011ferlendirmesi i\u00e7in ge\u00e7ersiz ba\u011flam aktar\u0131ld\u0131"},

   /** Pool already exists       */

    { ER_POOL_EXISTS,
             "Havuz zaten var"},

   /** No driver Name specified      */

    { ER_NO_DRIVER_NAME,
             "S\u00fcr\u00fcc\u00fc ad\u0131 belirtilmedi"},

   /** No URL specified     */

    { ER_NO_URL,
             "URL belirtilmedi"},

   /** Pool size is less than one    */

    { ER_POOL_SIZE_LESSTHAN_ONE,
             "Havuz b\u00fcy\u00fckl\u00fc\u011f\u00fc birden az!"},

   /** Invalid driver name specified    */

    { ER_INVALID_DRIVER,
             "Belirtilen s\u00fcr\u00fcc\u00fc ad\u0131 ge\u00e7ersiz!"},

   /** Did not find the stylesheet root    */

    { ER_NO_STYLESHEETROOT,
             "Bi\u00e7em yapra\u011f\u0131 k\u00f6k\u00fc bulunamad\u0131!"},

   /** Illegal value for xml:space     */

    { ER_ILLEGAL_XMLSPACE_VALUE,
         "xml:space i\u00e7in ge\u00e7ersiz de\u011fer"},

   /** processFromNode failed     */

    { ER_PROCESSFROMNODE_FAILED,
         "processFromNode ba\u015far\u0131s\u0131z oldu"},

   /** The resource [] could not load:     */

    { ER_RESOURCE_COULD_NOT_LOAD,
        "Kaynak [ {0} ] y\u00fckleyemedi: {1} \n {2} \t {3}"},


   /** Buffer size <=0     */

    { ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Arabellek b\u00fcy\u00fckl\u00fc\u011f\u00fc <=0"},

   /** Unknown error when calling extension    */

    { ER_UNKNOWN_ERROR_CALLING_EXTENSION,
        "Eklenti \u00e7a\u011fr\u0131l\u0131rken bilinmeyen hata"},

   /** Prefix {0} does not have a corresponding namespace declaration    */

    { ER_NO_NAMESPACE_DECL,
        "{0} \u00f6nekinin ili\u015fkili bir ad alan\u0131 bildirimi yok"},

   /** Element content not allowed for lang=javaclass   */

    { ER_ELEM_CONTENT_NOT_ALLOWED,
        "lang=javaclass {0} i\u00e7in \u00f6\u011fe i\u00e7eri\u011fine izin verilmiyor"},

   /** Stylesheet directed termination   */

    { ER_STYLESHEET_DIRECTED_TERMINATION,
        "Bi\u00e7em yapra\u011f\u0131 y\u00f6nlendirmeli sonland\u0131rma"},

   /** 1 or 2   */

    { ER_ONE_OR_TWO,
        "1 ya da 2"},

   /** 2 or 3   */

    { ER_TWO_OR_THREE,
        "2 ya da 3"},

   /** Could not load {0} (check CLASSPATH), now using just the defaults   */

    { ER_COULD_NOT_LOAD_RESOURCE,
        "{0} y\u00fcklenemedi (CLASSPATH de\u011fi\u015fkenini inceleyin), yaln\u0131zca varsay\u0131lanlar kullan\u0131l\u0131yor"},

   /** Cannot initialize default templates   */

    { ER_CANNOT_INIT_DEFAULT_TEMPLATES,
        "Varsay\u0131lan \u015fablonlar kullan\u0131ma haz\u0131rlanam\u0131yor"},

   /** Result should not be null   */

    { ER_RESULT_NULL,
        "Sonu\u00e7 bo\u015f de\u011ferli olmamal\u0131"},

   /** Result could not be set   */

    { ER_RESULT_COULD_NOT_BE_SET,
        "Sonu\u00e7 tan\u0131mlanamad\u0131"},

   /** No output specified   */

    { ER_NO_OUTPUT_SPECIFIED,
        "\u00c7\u0131k\u0131\u015f belirtilmedi"},

   /** Can't transform to a Result of type   */

    { ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
        "{0} tipi sonuca d\u00f6n\u00fc\u015ft\u00fcr\u00fclemiyor"},

   /** Can't transform to a Source of type   */

    { ER_CANNOT_TRANSFORM_SOURCE_TYPE,
        "{0} tipi kaynak d\u00f6n\u00fc\u015ft\u00fcr\u00fclemiyor"},

   /** Null content handler  */

    { ER_NULL_CONTENT_HANDLER,
        "Bo\u015f de\u011ferli i\u00e7erik i\u015fleyici"},

   /** Null error handler  */
    { ER_NULL_ERROR_HANDLER,
        "Bo\u015f de\u011ferli hata i\u015fleyici"},

   /** parse can not be called if the ContentHandler has not been set */

    { ER_CANNOT_CALL_PARSE,
        "ContentHandler tan\u0131mlanmad\u0131ysa parse \u00e7a\u011fr\u0131lamaz"},

   /**  No parent for filter */

    { ER_NO_PARENT_FOR_FILTER,
        "S\u00fczgecin \u00fcst \u00f6\u011fesi yok"},


   /**  No stylesheet found in: {0}, media */

    { ER_NO_STYLESHEET_IN_MEDIA,
         "Bi\u00e7em yapra\u011f\u0131 burada bulunamad\u0131: {0}, ortam= {1}"},

   /**  No xml-stylesheet PI found in */

    { ER_NO_STYLESHEET_PI,
         "xml-stylesheet PI burada bulunamad\u0131: {0}"},

   /**  No default implementation found */


   /**  ChunkedIntArray({0}) not currently supported */


   /**  Offset bigger than slot */


   /**  Coroutine not available, id= */


   /**  CoroutineManager recieved co_exit() request */


   /**  co_joinCoroutineSet() failed */


   /**  Coroutine parameter error () */


   /**  UNEXPECTED: Parser doTerminate answers  */


   /**  parse may not be called while parsing */


   /**  Error: typed iterator for axis  {0} not implemented  */


   /**  Error: iterator for axis {0} not implemented  */


   /**  Iterator clone not supported  */


   /**  Unknown axis traversal type  */


   /**  Axis traverser not supported  */


   /**  No more DTM IDs are available  */


   /**  Not supported  */

    { ER_NOT_SUPPORTED,
       "Desteklenmiyor: {0}"},

   /**  node must be non-null for getDTMHandleFromNode  */


   /**  Could not resolve the node to a handle  */


   /**  startParse may not be called while parsing */


   /**  startParse needs a non-null SAXParser  */


   /**  could not initialize parser with */

   /**  Value for property {0} should be a Boolean instance  */

    { ER_PROPERTY_VALUE_BOOLEAN,
       "{0} \u00f6zelli\u011finin de\u011feri Boole somut \u00f6rne\u011fi olmal\u0131"},

   /**  exception creating new instance for pool  */


   /**  Path contains invalid escape sequence  */


   /**  Scheme is required!  */


   /**  No scheme found in URI  */


   /**  No scheme found in URI  */


   /**  Path contains invalid character:   */


   /**  Cannot set scheme from null string  */


   /**  The scheme is not conformant. */


   /**  Host is not a well formed address  */


   /**  Port cannot be set when host is null  */


   /**  Invalid port number  */


   /**  Fragment can only be set for a generic URI  */


   /**  Fragment cannot be set when path is null  */


   /**  Fragment contains invalid character  */




   /** Parser is already in use  */


   /** Parser is already in use  */


   /** Self-causation not permitted  */


   /** src attribute not yet supported for  */

    { ER_COULD_NOT_FIND_EXTERN_SCRIPT,
         "{0} i\u00e7inde d\u0131\u015f komut dosyas\u0131na ula\u015f\u0131lamad\u0131"},

  /** The resource [] could not be found     */

    { ER_RESOURCE_COULD_NOT_FIND,
        "Kaynak [ {0} ] bulunamad\u0131.\n{1}"},

   /** output property not recognized:  */

    { ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
        "\u00c7\u0131k\u0131\u015f \u00f6zelli\u011fi tan\u0131nm\u0131yor: {0}"},

   /** Userinfo may not be specified if host is not specified   */


   /** Port may not be specified if host is not specified   */


   /** Query string cannot be specified in path and query string   */


   /** Fragment cannot be specified in both the path and fragment   */


   /** Cannot initialize URI with empty parameters   */


   /** Failed creating ElemLiteralResult instance   */

    { ER_FAILED_CREATING_ELEMLITRSLT,
        "ElemLiteralResult somut \u00f6rne\u011fi yarat\u0131lmas\u0131 ba\u015far\u0131s\u0131z oldu"},


   /** Priority value does not contain a parsable number   */

    { ER_VALUE_SHOULD_BE_NUMBER,
        "{0} de\u011feri ayr\u0131\u015ft\u0131r\u0131labilir bir say\u0131 i\u00e7ermelidir"},

   /**  Value for {0} should equal 'yes' or 'no'   */

    { ER_VALUE_SHOULD_EQUAL,
        "{0} de\u011feri yes ya da no olmal\u0131"},

   /**  Failed calling {0} method   */

    { ER_FAILED_CALLING_METHOD,
        "{0} y\u00f6ntemi \u00e7a\u011fr\u0131s\u0131 ba\u015far\u0131s\u0131z oldu"},

   /** Failed creating ElemLiteralResult instance   */

    { ER_FAILED_CREATING_ELEMTMPL,
        "ElemTemplateElement somut \u00f6rne\u011fi yarat\u0131lmas\u0131 ba\u015far\u0131s\u0131z oldu"},

   /**  Characters are not allowed at this point in the document   */

    { ER_CHARS_NOT_ALLOWED,
        "Belgenin bu noktas\u0131nda karakterlere izin verilmez"},

  /**  attribute is not allowed on the element   */
    { ER_ATTR_NOT_ALLOWED,
        "\"{0}\" \u00f6zniteli\u011fi {1} \u00f6\u011fesinde kullan\u0131lamaz!"},

  /**  Method not yet supported    */


  /**  Bad value    */

    { ER_BAD_VALUE,
     "{0} hatal\u0131 de\u011fer {1}"},

  /**  attribute value not found   */

    { ER_ATTRIB_VALUE_NOT_FOUND,
     "{0} \u00f6znitelik de\u011feri bulunamad\u0131"},

  /**  attribute value not recognized    */

    { ER_ATTRIB_VALUE_NOT_RECOGNIZED,
     "{0} \u00f6znitelik de\u011feri tan\u0131nm\u0131yor"},

  /** IncrementalSAXSource_Filter not currently restartable   */


  /** IncrementalSAXSource_Filter not currently restartable   */


  /** Attempting to generate a namespace prefix with a null URI   */

    { ER_NULL_URI_NAMESPACE,
     "Bo\u015f de\u011ferli URI ile ad alan\u0131 \u00f6neki olu\u015fturma giri\u015fimi"},


  /** Attempting to generate a namespace prefix with a null URI   */

    { ER_NUMBER_TOO_BIG,
     "En b\u00fcy\u00fck uzun tamsay\u0131dan daha b\u00fcy\u00fck bir say\u0131 bi\u00e7imleme giri\u015fimi"},



    { ER_CANNOT_FIND_SAX1_DRIVER,
     "SAX1 s\u00fcr\u00fcc\u00fc s\u0131n\u0131f\u0131 {0} bulunam\u0131yor"},


    { ER_SAX1_DRIVER_NOT_LOADED,
     "SAX1 s\u00fcr\u00fcc\u00fc s\u0131n\u0131f\u0131 {0} bulundu, ancak y\u00fcklenemiyor"},


    { ER_SAX1_DRIVER_NOT_INSTANTIATED,
     "SAX1 s\u00fcr\u00fcc\u00fc s\u0131n\u0131f\u0131 {0} y\u00fcklendi, ancak somutla\u015ft\u0131r\u0131lam\u0131yor"},



    { ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
     "SAX1 s\u00fcr\u00fcc\u00fc s\u0131n\u0131f\u0131 {0} org.xml.sax.Parser \u00f6zelli\u011fini uygulam\u0131yor"},


    { ER_PARSER_PROPERTY_NOT_SPECIFIED,
     "Sistem \u00f6zelli\u011fi org.xml.sax.parser belirtilmedi"},


    { ER_PARSER_ARG_CANNOT_BE_NULL,
     "Ayr\u0131\u015ft\u0131r\u0131c\u0131 (Parser) ba\u011f\u0131ms\u0131z de\u011fi\u015fkeni bo\u015f de\u011ferli olmamal\u0131"},



    { ER_FEATURE,
     "\u00d6zellik: {0}"},



    { ER_PROPERTY,
     "\u00d6zellik: {0}"},


    { ER_NULL_ENTITY_RESOLVER,
     "Bo\u015f de\u011ferli varl\u0131k \u00e7\u00f6z\u00fcc\u00fc"},


    { ER_NULL_DTD_HANDLER,
     "Bo\u015f de\u011ferli DTD i\u015fleyici"},

    { ER_NO_DRIVER_NAME_SPECIFIED,
     "S\u00fcr\u00fcc\u00fc ad\u0131 belirtilmedi!"},


    { ER_NO_URL_SPECIFIED,
     "URL belirtilmedi!"},


    { ER_POOLSIZE_LESS_THAN_ONE,
     "Havuz b\u00fcy\u00fckl\u00fc\u011f\u00fc birden az!"},


    { ER_INVALID_DRIVER_NAME,
     "Belirtilen s\u00fcr\u00fcc\u00fc ad\u0131 ge\u00e7ersiz!"},



    { ER_ERRORLISTENER,
     "ErrorListener"},


    { ER_ASSERT_NO_TEMPLATE_PARENT,
     "Programc\u0131 hatas\u0131! expr i\u00e7in ElemTemplateElement \u00fcst \u00f6\u011fesi yok!"},


    { ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
     "RundundentExprEliminator i\u00e7inde programc\u0131 de\u011ferlendirmesi: {0}"},



    { ER_NOT_ALLOWED_IN_POSITION,
     "{0} bi\u00e7em yapra\u011f\u0131nda bu konumda bulunamaz!"},

    { ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
     "Beyaz alan d\u0131\u015f\u0131 metin bi\u00e7em yapra\u011f\u0131nda bu konumda bulunamaz!"},

    { INVALID_TCHAR,
     "CHAR \u00f6zniteli\u011fi {0} i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131. CHAR tipinde bir \u00f6znitelik yaln\u0131zca 1 karakter olmal\u0131d\u0131r!"},




    { INVALID_QNAME,
     "QNAME \u00f6zniteli\u011fi {0} i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131"},


    { INVALID_ENUM,
     "ENUM \u00f6zniteli\u011fi {0} i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131. Ge\u00e7erli de\u011ferler: {2}."},


    { INVALID_NMTOKEN,
     "NMTOKEN \u00f6zniteli\u011fi {0} i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131"},


    { INVALID_NCNAME,
     "NCNAME \u00f6zniteli\u011fi {0} i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131"},



    { INVALID_BOOLEAN,
     "boolean \u00f6zniteli\u011fi {0} i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131"},


     { INVALID_NUMBER,
     "number \u00f6zniteli\u011fi {0} i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131"},




    { ER_ARG_LITERAL,
     "E\u015fle\u015fme \u00f6r\u00fcnt\u00fcs\u00fcnde {0} i\u015flevine ili\u015fkin ba\u011f\u0131ms\u0131z de\u011fi\u015fken bir haz\u0131r bilgi olmal\u0131d\u0131r."},


    { ER_DUPLICATE_GLOBAL_VAR,
     "Yinelenen genel de\u011fi\u015fken bildirimi."},



    { ER_DUPLICATE_VAR,
     "Yinelenen de\u011fi\u015fken bildirimi."},


    { ER_TEMPLATE_NAME_MATCH,
     "xsl:template bir name ya da match \u00f6zniteli\u011fi (ya da her ikisini) i\u00e7ermelidir"},


    { ER_INVALID_PREFIX,
     "exclude-result-prefixes i\u00e7indeki \u00f6nek ge\u00e7erli de\u011fil: {0}"},


    { ER_NO_ATTRIB_SET,
     "{0} adl\u0131 \u00f6znitelik k\u00fcmesi yok"},






  /** WG_FOUND_CURLYBRACE          */
    { WG_FOUND_CURLYBRACE,
      "'}' bulundu, ancak a\u00e7\u0131k \u00f6znitelik \u015fablonu yok!"},

  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */

    { WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
      "Uyar\u0131: count \u00f6zniteli\u011fi xsl:number i\u00e7indeki bir \u00fcst \u00f6znitelikle e\u015fle\u015fmiyor! Hedef = {0}"},

  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */

    { WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
      "Eski s\u00f6zdizimi: 'expr' \u00f6zniteli\u011finin ad\u0131 'select' olarak de\u011fi\u015ftirildi."},

  /** WG_NO_LOCALE_IN_FORMATNUMBER          */

    { WG_NO_LOCALE_IN_FORMATNUMBER,
      "Xalan hen\u00fcz format-number i\u015flevinde \u00fclke de\u011feri ad\u0131n\u0131 i\u015flemiyor."},

  /** WG_LOCALE_NOT_FOUND          */

    { WG_LOCALE_NOT_FOUND,
      "Uyar\u0131: xml:lang={0} ile ilgili \u00fclke de\u011feri bulunamad\u0131"},

  /** WG_CANNOT_MAKE_URL_FROM          */

    { WG_CANNOT_MAKE_URL_FROM,
      "Dizgiden URL olu\u015fturulamad\u0131: {0}"},

  /** WG_CANNOT_LOAD_REQUESTED_DOC          */

    { WG_CANNOT_LOAD_REQUESTED_DOC,
      "\u0130stenen belge y\u00fcklenemiyor: {0}"},

  /** WG_CANNOT_FIND_COLLATOR          */
    { WG_CANNOT_FIND_COLLATOR,
      "<sort xml:lang={0} i\u00e7in Collator bulunamad\u0131"},

  /** WG_FUNCTIONS_SHOULD_USE_URL          */

    { WG_FUNCTIONS_SHOULD_USE_URL,
      "Eski s\u00f6zdizimi: functions y\u00f6nergesi {0} url adresini kullanmal\u0131d\u0131r"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */

    { WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
      "{0} kodlamas\u0131 desteklenmiyor, UTF-8 kullan\u0131l\u0131yor"},

  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */

    { WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
      "{0} kodlamas\u0131 desteklenmiyor, Java {1} kullan\u0131l\u0131yor"},

  /** WG_SPECIFICITY_CONFLICTS          */

    { WG_SPECIFICITY_CONFLICTS,
      "Belirtim \u00e7at\u0131\u015fmalar\u0131 saptand\u0131: {0} Bi\u00e7em yapra\u011f\u0131nda son bulunan kullan\u0131lacak."},

  /** WG_PARSING_AND_PREPARING          */

    { WG_PARSING_AND_PREPARING,
      "========= {0} ayr\u0131\u015ft\u0131r\u0131l\u0131yor ve haz\u0131rlan\u0131yor =========="},

  /** WG_ATTR_TEMPLATE          */

    { WG_ATTR_TEMPLATE,
     "\u00d6znitelik \u015fablonu, {0}"},

  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */

    { WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
      "xsl:strip-space ile xsl:preserve-space aras\u0131nda e\u015fle\u015fme \u00e7at\u0131\u015fmas\u0131"},

  /** WG_ATTRIB_NOT_HANDLED          */

    { WG_ATTRIB_NOT_HANDLED,
      "Xalan hen\u00fcz {0} \u00f6zniteli\u011fini i\u015flemiyor!"},

  /** WG_NO_DECIMALFORMAT_DECLARATION          */

    { WG_NO_DECIMALFORMAT_DECLARATION,
      "Onlu bi\u00e7imi i\u00e7in bildirim bulunamad\u0131: {0}"},

  /** WG_OLD_XSLT_NS          */

    { WG_OLD_XSLT_NS,
     "Eksik ya da yanl\u0131\u015f XSLT ad alan\u0131."},

  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */

    { WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
      "Varsay\u0131lan tek bir xsl:decimal-format bildirimine izin verilir."},

  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */

    { WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
      "xsl:decimal-format adlar\u0131 benzersiz olmal\u0131d\u0131r. \"{0}\" ad\u0131 yineleniyor."},

  /** WG_ILLEGAL_ATTRIBUTE          */

    { WG_ILLEGAL_ATTRIBUTE,
      "{0} ge\u00e7ersiz {1} \u00f6zniteli\u011fini i\u00e7eriyor"},

  /** WG_COULD_NOT_RESOLVE_PREFIX          */

    { WG_COULD_NOT_RESOLVE_PREFIX,
      "Ad alan\u0131 \u00f6neki {0} \u00e7\u00f6z\u00fclemedi. D\u00fc\u011f\u00fcm yoksay\u0131lacak."},

  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
    { WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet i\u00e7in 'version' \u00f6zniteli\u011fi gerekiyor!"},

  /** WG_ILLEGAL_ATTRIBUTE_NAME          */

    { WG_ILLEGAL_ATTRIBUTE_NAME,
      "Ge\u00e7ersiz \u00f6znitelik ad\u0131: {0}"},

  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
    { WG_ILLEGAL_ATTRIBUTE_VALUE,
      "{0} \u00f6zniteli\u011fi i\u00e7in ge\u00e7ersiz {1} de\u011feri kullan\u0131ld\u0131"},

  /** WG_EMPTY_SECOND_ARG          */

    { WG_EMPTY_SECOND_ARG,
      "Belge i\u015flevinin ikinci ba\u011f\u0131ms\u0131z de\u011fi\u015fkeninden sonu\u00e7lanan d\u00fc\u011f\u00fcm k\u00fcmesi (nodeset) bo\u015f. Bo\u015f d\u00fc\u011f\u00fcm k\u00fcmesi d\u00f6nd\u00fcr\u00fcr."},




  /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
    { WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "xsl:processing-instruction ad\u0131n\u0131n 'name' \u00f6zniteli\u011fi de\u011feri 'xml' olmamal\u0131d\u0131r"},


  /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
    { WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "xsl:processing-instruction ile ilgili 'name' \u00f6zniteli\u011fi de\u011feri ge\u00e7erli bir NCName olmal\u0131d\u0131r: {0}"},


  /** WG_ILLEGAL_ATTRIBUTE_POSITION         */
    { WG_ILLEGAL_ATTRIBUTE_POSITION,
      "Alt d\u00fc\u011f\u00fcmlerden sonra ya da bir \u00f6\u011fe \u00fcretilmeden \u00f6nce {0} \u00f6zniteli\u011fi eklenemez. \u00d6znitelik yoksay\u0131lacak."},


  { "ui_language", "tr"},
  {  "help_language",  "tr" },
  {  "language",  "tr" },
  { "BAD_CODE", "createMessage i\u00e7in kullan\u0131lan de\u011fi\u015ftirge s\u0131n\u0131rlar\u0131n d\u0131\u015f\u0131nda"},
  {  "FORMAT_FAILED", "messageFormat \u00e7a\u011fr\u0131s\u0131 s\u0131ras\u0131nda kural d\u0131\u015f\u0131 durum yay\u0131nland\u0131"},
  {  "version", ">>>>>>> Xalan S\u00fcr\u00fcm "},
  {  "version2",  "<<<<<<<"},
  {  "yes", "yes"},
  { "line", "Sat\u0131r #"},
  { "column","Kolon #"},
  { "xsldone", "XSLProcessor: bitti"},


  { "xslProc_option", "Xalan-J komut sat\u0131r\u0131 Process s\u0131n\u0131f\u0131 se\u00e7enekleri: "},
  { "xslProc_option", "Xalan-J komut sat\u0131r\u0131 i\u015flem s\u0131n\u0131f\u0131 se\u00e7enekleri\u003a"},
  { "xslProc_invalid_xsltc_option", "{0} se\u00e7ene\u011fi XSLTC kipinde desteklenmez."},
  { "xslProc_invalid_xalan_option", "{0} se\u00e7ene\u011fi yaln\u0131zca -XSLTC ile kullan\u0131labilir."},
  { "xslProc_no_input", "Hata: Bi\u00e7em yapra\u011f\u0131 ya da giri\u015f xml belirtilmedi. Kullan\u0131m y\u00f6nergeleri i\u00e7in, bu komutu se\u00e7enek belirtmeden \u00e7al\u0131\u015ft\u0131r\u0131n."},
  { "xslProc_common_options", "-Ortak Se\u00e7enekler-"},
  { "xslProc_xalan_options", "-Xalan Se\u00e7enekleri-"},
  { "xslProc_xsltc_options", "-XSLTC Se\u00e7enekleri-"},
  { "xslProc_return_to_continue", "(devam etmek i\u00e7in <Enter> tu\u015funa bas\u0131n)"},

  { "optionXSLTC", "[-XSLTC (XSLTC d\u00f6n\u00fc\u015ft\u00fcrmede kullan\u0131l\u0131r)]"},
  { "optionIN", "[-IN inputXMLURL]"},
  { "optionXSL", "[-XSL XSLTransformationURL]"},
  { "optionOUT",  "[-OUT outputFileName]"},
  { "optionLXCIN", "[-LXCIN compiledStylesheetFileNameIn]"},
  { "optionLXCOUT", "[-LXCOUT compiledStylesheetFileNameOutOut]"},
  { "optionPARSER", "[-PARSER ayr\u0131\u015ft\u0131r\u0131c\u0131 ili\u015fkisinin tam olarak nitelenmi\u015f s\u0131n\u0131f ad\u0131]"},
  {  "optionE", "[-E (Varl\u0131k ba\u015fvurular\u0131 geni\u015fletilmez)]"},
  {  "optionV",  "[-E (Varl\u0131k ba\u015fvurular\u0131 geni\u015fletilmez)]"},
  {  "optionQC", "[-QC (Sessiz \u00f6r\u00fcnt\u00fc \u00e7at\u0131\u015fmalar\u0131 uyar\u0131s\u0131)]"},
  {  "optionQ", "[-Q  (Sessiz kip)]"},
  {  "optionLF", "   [-LF (sat\u0131r besleme yaln\u0131zca \u00e7\u0131k\u0131\u015fta kullan\u0131l\u0131r {varsay\u0131lan CR/LF})]"},
  {  "optionCR", "   [-CR (Sat\u0131rba\u015f\u0131 yaln\u0131zca \u00e7\u0131k\u0131\u015fta kullan\u0131l\u0131r {varsay\u0131lan CR/LF})]"},
  { "optionESCAPE", "   [-ESCAPE (Ka\u00e7\u0131\u015f karakterleri {varsay\u0131lan <>&\"\'\\r\\n}]"},
  { "optionINDENT", "[-INDENT (Girintili yazarken kullan\u0131lacak bo\u015fluk say\u0131s\u0131 {varsay\u0131lan 0})]"},
  { "optionTT", "[-TT (\u015eablonlar \u00e7a\u011fr\u0131l\u0131rken izlenir.)]"},
  { "optionTG", "[-TG (Her olu\u015fturma olay\u0131 izlenir.)]"},
  { "optionTS", "[-TS (Her se\u00e7im olay\u0131 izlenir.)]"},
  {  "optionTTC", "[-TTC (\u015eablon alt \u00f6\u011feleri i\u015flenirken izlenir.)]"},
  { "optionTCLASS", "[-TCLASS (\u0130zleme eklentileri i\u00e7in TraceListener s\u0131n\u0131f\u0131.)]"},
  { "optionVALIDATE", "[-VALIDATE (Ge\u00e7erlilik denetimi yap\u0131l\u0131p yap\u0131lmayaca\u011f\u0131n\u0131 belirler. Varsay\u0131lan olarak, ge\u00e7erlilik denetimi kapal\u0131d\u0131r.)]"},
  { "optionEDUMP", "[-EDUMP {iste\u011fe ba\u011fl\u0131 dosya ad\u0131} (Hata durumunda y\u0131\u011f\u0131n d\u00f6k\u00fcm\u00fc ger\u00e7ekle\u015ftirilir.)]"},
  {  "optionXML", "[-XML (XML bi\u00e7imleyici kullan\u0131l\u0131r ve XML \u00fcstbilgisi eklenir.)]"},
  {  "optionTEXT", "[-TEXT (Yal\u0131n metin bi\u00e7imleyici kullan\u0131l\u0131r.)]"},
  {  "optionHTML", "[-HTML (HTML bi\u00e7imleyici kullan\u0131l\u0131r.)]"},
  {  "optionPARAM", "[-PARAM ad ifadesi (Bi\u00e7em yapra\u011f\u0131 de\u011fi\u015ftirgesi belirlenir.)]"},
  {  "noParsermsg1", "XSL i\u015flemi ba\u015far\u0131s\u0131z oldu."},
  {  "noParsermsg2", "** Ayr\u0131\u015ft\u0131r\u0131c\u0131 bulunamad\u0131 **"},
  { "noParsermsg3",  "L\u00fctfen classpath de\u011fi\u015fkeninizi inceleyin."},
  { "noParsermsg4", "Sisteminizde IBM XML Parser for Java arac\u0131 yoksa, \u015fu adresten y\u00fckleyebilirsiniz:"},
  { "optionURIRESOLVER", "[-URIRESOLVER tam s\u0131n\u0131f ad\u0131 (URI \u00e7\u00f6zmekte kullan\u0131lacak URIResolver)]"},
  { "optionENTITYRESOLVER",  "[-ENTITYRESOLVER tam s\u0131n\u0131f ad\u0131 (Varl\u0131klar\u0131 \u00e7\u00f6zmekte kullan\u0131lacak EntityResolver)]"},
  { "optionCONTENTHANDLER",  "[-CONTENTHANDLER tam s\u0131n\u0131f ad\u0131 (\u00c7\u0131k\u0131\u015f\u0131 diziselle\u015ftirmekte kullan\u0131lacak ContentHandler)]"},
  {  "optionLINENUMBERS",  "[-L kaynak belge i\u00e7in sat\u0131r numaralar\u0131 kullan\u0131l\u0131r]"},



  {  "optionMEDIA",  "   [-MEDIA mediaType (Ortam \u00f6zniteli\u011fi, bir belgeyle ili\u015fkili bi\u00e7em yapra\u011f\u0131n\u0131 bulmak i\u00e7in kullan\u0131l\u0131r.)]"},
  { "optionDIAG", "   [-DIAG (D\u00f6n\u00fc\u015ft\u00fcrmenin ka\u00e7 milisaniye s\u00fcrd\u00fc\u011f\u00fcn\u00fc yazd\u0131r\u0131r.)]"},
  { "optionRL",  "   [-RL recursionlimit (Bi\u00e7em yapra\u011f\u0131 \u00f6zyineleme derinli\u011fine say\u0131sal s\u0131n\u0131r koyar.)]"},
  {   "optionXO",  "[-XO [derleme sonucu s\u0131n\u0131f dosyas\u0131 ad\u0131] (Olu\u015fturulan s\u0131n\u0131f dosyas\u0131na bu ad\u0131 atar.)]"},
  {  "optionXD", "[-XD destinationDirectory (Derleme sonucu s\u0131n\u0131f dosyas\u0131 i\u00e7in hedef dizin belirtir.)]"},
  {  "optionXJ",  "[-XJ jardsy (Derleme sonucu \u00fcretilen s\u0131n\u0131flar\u0131 <jardsy> adl\u0131 jar dosyas\u0131nda paketler.)]"},
  {   "optionXP",  "[-XP paket (Derleme sonucunda \u00fcretilen t\u00fcm s\u0131n\u0131flar i\u00e7in bir paket ad\u0131 \u00f6neki belirtir.)]"},

  { "optionXN",  "[-XN (\u015eablona do\u011frudan yerle\u015ftirmeyi a\u00e7ar.)]" },
  { "optionXX",  "[-XX (Ek hata ay\u0131klama iletisi \u00e7\u0131k\u0131\u015f\u0131n\u0131 a\u00e7ar.)]"},
  { "optionXT" , "[-XT (Yap\u0131labiliyorsa, d\u00f6n\u00fc\u015ft\u00fcrme i\u00e7in derleme sonucu s\u0131n\u0131f dosyas\u0131n\u0131 kullan\u0131r.)]"},
  { "diagTiming","--------- {1} ile {0} d\u00f6n\u00fc\u015ft\u00fcrme i\u015flemi {2} ms s\u00fcrd\u00fc" },
  { "recursionTooDeep","\u015eablon i\u00e7i\u00e7e kullan\u0131m derinli\u011fi \u00e7ok fazla. \u0130\u00e7i\u00e7e kullan\u0131m = {0}, \u015fablon {1} {2}" },
  { "nameIs", "ad\u0131" },
  { "matchPatternIs", "e\u015fle\u015fme \u00f6r\u00fcnt\u00fcs\u00fc" }

  };


  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "HATALI_KOD";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "B\u0130\u00c7\u0130MLEME_BA\u015eARISIZ";

  /** General error string.   */
  public static final String ERROR_STRING = "#hata";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "Hata: ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "Uyar\u0131: ";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "\u00d6R\u00dcNT\u00dc ";

  /**
   * Get the lookup table.
   *
   * @return The int to message lookup table.
   */
  public Object[][] getContents()
  {
    return contents;
  }

  /**
   *   Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   *   of ResourceBundle.getBundle().
   *
   *   @param className the name of the class that implements the resource bundle.
   *   @return the ResourceBundle
   *   @throws MissingResourceException
   */
  public static final XSLTErrorResources loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);

    try
    {

      return (XSLTErrorResources) ResourceBundle.getBundle(className
              + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      {

        return (XSLTErrorResources) ResourceBundle.getBundle(className,
                new Locale("tr", "TR"));
      }
      catch (MissingResourceException e2)
      {

        throw new MissingResourceException(
          "Could not load any resource bundles.", className, "");
      }
    }
  }

  /**
   * Return the resource file suffic for the indicated locale
   * For most locales, this will be based the language code.  However
   * for Chinese, we do distinguish between Taiwan and PRC
   *
   * @param locale the locale
   * @return an String suffix which canbe appended to a resource name
   */
  private static final String getResourceSuffix(Locale locale)
  {

    String suffix = "_" + locale.getLanguage();
    String country = locale.getCountry();

    if (country.equals("TW"))
      suffix += "_" + country;

    return suffix;
  }


}
