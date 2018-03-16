package org.apache.tools.ant.types.mappers;

import java.io.StringReader;
import java.io.Reader;

import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.UnsupportedAttributeException;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

/**
 * This is a FileNameMapper based on a FilterChain.
 */
public class FilterMapper extends FilterChain implements FileNameMapper {

    private static final int BUFFER_SIZE = 8192;

    /**
     * From attribute not supported.
     * @param from a string
     * @throws BuildException always
     */
    public void setFrom(String from) {
        throw new UnsupportedAttributeException(
            "filtermapper doesn't support the \"from\" attribute.", "from");
    }

    /**
     * From attribute not supported.
     * @param to a string
     * @throws BuildException always
     */
    public void setTo(String to) {
        throw new UnsupportedAttributeException(
            "filtermapper doesn't support the \"to\" attribute.", "to");
    }

    /**
     * Return the result of the filters on the sourcefilename.
     * @param sourceFileName the filename to map
     * @return  a one-element array of converted filenames, or null if
     *          the filterchain returns an empty string.
     */
    public String[] mapFileName(String sourceFileName) {
        try {
            Reader stringReader = new StringReader(sourceFileName);
            ChainReaderHelper helper = new ChainReaderHelper();
            helper.setBufferSize(BUFFER_SIZE);
            helper.setPrimaryReader(stringReader);
            helper.setProject(getProject());
            Vector filterChains = new Vector();
            filterChains.add(this);
            helper.setFilterChains(filterChains);
            String result = FileUtils.safeReadFully(helper.getAssembledReader());
            if (result.length() == 0) {
                return null;
            } else {
                return new String[] {result};
            }
        } catch (BuildException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BuildException(ex);
        }
    }
}
