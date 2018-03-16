package org.apache.xalan.xsltc.trax;

import java.util.Properties;

/**
 * @author Morten Jorgensen
 */
public final class OutputSettings {
    
    private String _cdata_section_elements = null;
    private String _doctype_public = null;
    private String _encoding = null;
    private String _indent = null;
    private String _media_type = null;
    private String _method = null;
    private String _omit_xml_declaration = null;
    private String _standalone = null;
    private String _version = null;

    public Properties getProperties() {
	Properties properties = new Properties();
	return(properties);
    }

    
}
