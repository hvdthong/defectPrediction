package org.apache.ivy.plugins.parser.xml;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.ivy.plugins.namespace.Namespace;
import org.apache.ivy.plugins.parser.ParserSettings;

public class UpdateOptions {
    /**
     * Settings to use for update, may be <code>null</code>.
     */
    private ParserSettings settings = null;
    /**
     * Namespace in which the module to update is, may be <code>null</code>.
     */
    private Namespace namespace = null;
    /**
     * Map from ModuleId of dependencies to new revision (as String)
     */
    private Map resolvedRevisions = Collections.EMPTY_MAP;
    /**
     * the new status, <code>null</code> to keep the old one
     */
    private String status = null;
    /**
     * the new revision, <code>null</code> to keep the old one
     */
    private String revision = null;
    /**
     * the new publication date, <code>null</code> to keep the old one
     */
    private Date pubdate = null;
    /**
     * Should included information be replaced
     */
    private boolean replaceInclude = true;
    /**
     * Configurations to exclude during update, or <code>null</code> to keep all confs.
     */
    private String[] confsToExclude = null;
    /**
     * True to set branch information on dependencies to default branch when omitted, false to keep 
     * it as is.
     */
    private boolean updateBranch = true;
    private String branch;
    
    public ParserSettings getSettings() {
        return settings;
    }
    public UpdateOptions setSettings(ParserSettings settings) {
        this.settings = settings;
        return this;
    }
    public Namespace getNamespace() {
        return namespace;
    }
    public UpdateOptions setNamespace(Namespace ns) {
        this.namespace = ns;
        return this;
    }
    public Map getResolvedRevisions() {
        return resolvedRevisions;
    }
    public UpdateOptions setResolvedRevisions(Map resolvedRevisions) {
        this.resolvedRevisions = resolvedRevisions;
        return this;
    }
    public String getStatus() {
        return status;
    }
    public UpdateOptions setStatus(String status) {
        this.status = status;
        return this;
    }
    public String getRevision() {
        return revision;
    }
    public UpdateOptions setRevision(String revision) {
        this.revision = revision;
        return this;
    }
    public Date getPubdate() {
        return pubdate;
    }
    public UpdateOptions setPubdate(Date pubdate) {
        this.pubdate = pubdate;
        return this;
    }
    public boolean isReplaceInclude() {
        return replaceInclude;
    }
    public UpdateOptions setReplaceInclude(boolean replaceInclude) {
        this.replaceInclude = replaceInclude;
        return this;
    }
    public String[] getConfsToExclude() {
        return confsToExclude;
    }
    public UpdateOptions setConfsToExclude(String[] confsToExclude) {
        this.confsToExclude = confsToExclude;
        return this;
    }
    public boolean isUpdateBranch() {
        return updateBranch;
    }
    public UpdateOptions setUpdateBranch(boolean updateBranch) {
        this.updateBranch = updateBranch;
        return this;
    }
    public String getBranch() {
        return branch;
    }
    public UpdateOptions setBranch(String pubBranch) {
        this.branch = pubBranch;
        return this;
    }
}
