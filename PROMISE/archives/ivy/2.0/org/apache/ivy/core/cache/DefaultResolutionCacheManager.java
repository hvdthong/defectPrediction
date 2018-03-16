package org.apache.ivy.core.cache;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.ivy.core.IvyPatternHelper;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.util.FileUtil;

public class DefaultResolutionCacheManager implements ResolutionCacheManager {
    private static final String DEFAULT_CACHE_RESOLVED_IVY_PATTERN = 
        "resolved-[organisation]-[module]-[revision].xml";

    private static final String DEFAULT_CACHE_RESOLVED_IVY_PROPERTIES_PATTERN = 
        "resolved-[organisation]-[module]-[revision].properties";


    private String resolvedIvyPattern = DEFAULT_CACHE_RESOLVED_IVY_PATTERN;

    private String resolvedIvyPropertiesPattern = 
        DEFAULT_CACHE_RESOLVED_IVY_PROPERTIES_PATTERN;
    
    private File basedir;

    private String name = "resolution-cache"; 

    public DefaultResolutionCacheManager() {
    }
    
    public DefaultResolutionCacheManager(File basedir) {
        setBasedir(basedir);
    }

    public File getResolutionCacheRoot() {
        return basedir;
    }
    
    public File getBasedir() {
        return basedir;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    public String getResolvedIvyPattern() {
        return resolvedIvyPattern;
    }

    public void setResolvedIvyPattern(String cacheResolvedIvyPattern) {
        this.resolvedIvyPattern = cacheResolvedIvyPattern;
    }

    public String getResolvedIvyPropertiesPattern() {
        return resolvedIvyPropertiesPattern;
    }

    public void setResolvedIvyPropertiesPattern(String cacheResolvedIvyPropertiesPattern) {
        this.resolvedIvyPropertiesPattern = cacheResolvedIvyPropertiesPattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public File getResolvedIvyFileInCache(ModuleRevisionId mrid) {
        String file = IvyPatternHelper.substitute(getResolvedIvyPattern(), mrid
                .getOrganisation(), mrid.getName(), mrid.getRevision(), "ivy", "ivy", "xml");
        return new File(getResolutionCacheRoot(), file);
    }

    public File getResolvedIvyPropertiesInCache(ModuleRevisionId mrid) {
        String file = IvyPatternHelper.substitute(getResolvedIvyPropertiesPattern(),
            mrid.getOrganisation(), mrid.getName(), mrid.getRevision(), "ivy", "ivy", "xml");
        return new File(getResolutionCacheRoot(), file);
    }

    public File getConfigurationResolveReportInCache(String resolveId, String conf) {
        return new File(getResolutionCacheRoot(), resolveId + "-" + conf + ".xml");
    }

    public File[] getConfigurationResolveReportsInCache(final String resolveId) {
        final String prefix = resolveId + "-";
        final String suffix = ".xml";
        return getResolutionCacheRoot().listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(prefix) && name.endsWith(suffix));
            }
        });
    }

    public String toString() {
        return name;
    }

    public void clean() {
        FileUtil.forceDelete(getBasedir());
    }

}
