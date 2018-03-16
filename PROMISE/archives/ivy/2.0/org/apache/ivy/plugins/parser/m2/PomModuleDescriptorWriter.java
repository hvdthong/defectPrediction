package org.apache.ivy.plugins.parser.m2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.util.StringUtils;

public final class PomModuleDescriptorWriter {
    private static boolean addIvyVersion = true;
    static void setAddIvyVersion(boolean addIvyVersion) {
        PomModuleDescriptorWriter.addIvyVersion = addIvyVersion;
    }

    private PomModuleDescriptorWriter() {
    }
    
    public static void write(ModuleDescriptor md, 
            ConfigurationScopeMapping mapping, File output) throws IOException {
        write(md, null, mapping, output);
    }

    public static void write(ModuleDescriptor md, 
            String licenseHeader, ConfigurationScopeMapping mapping, File output)
            throws IOException {
        if (output.getParentFile() != null) {
            output.getParentFile().mkdirs();
        }
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(output),
                "UTF-8"));
        try {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            if (licenseHeader != null) {
                out.print(licenseHeader);
            }
            out.println("<!--"); 
            out.println("   Apache Maven 2 POM generated by Apache Ivy"); 
            out.println("   " + Ivy.getIvyHomeURL());
            if (addIvyVersion) {
                out.println("   Apache Ivy version: " + Ivy.getIvyVersion() 
                    + " " + Ivy.getIvyDate());
            }
            out.println("-->"); 
            out.println("  <modelVersion>4.0.0</modelVersion>");
            printModuleId(md, out);
            printDependencies(md, mapping, out);
            out.println("</project>");
        } finally {
            out.close();
        }
    }

    private static void printModuleId(ModuleDescriptor md, PrintWriter out) {
        ModuleRevisionId mrid = md.getModuleRevisionId();
        out.println("  <groupId>" + mrid.getOrganisation() + "</groupId>");
        out.println("  <artifactId>" + mrid.getName() + "</artifactId>");
        out.println("  <packaging>jar</packaging>");
        if (mrid.getRevision() != null) {
            out.println("  <version>" + mrid.getRevision() + "</version>");
        }
        if (md.getHomePage() != null) {
            out.println("  <url>" + md.getHomePage() + "</url>");
        }
    }

    private static void printDependencies(
            ModuleDescriptor md, ConfigurationScopeMapping mapping, PrintWriter out) {
        DependencyDescriptor[] dds = md.getDependencies();
        if (dds.length > 0) {
            out.println("  <dependencies>");
            for (int i = 0; i < dds.length; i++) {
                ModuleRevisionId mrid = dds[i].getDependencyRevisionId();
                out.println("    <dependency>");
                out.println("      <groupId>" + mrid.getOrganisation() + "</groupId>");
                out.println("      <artifactId>" + mrid.getName() + "</artifactId>");
                out.println("      <version>" + mrid.getRevision() + "</version>");
                String scope = mapping.getScope(dds[i].getModuleConfigurations());
                if (scope != null) {
                    out.println("      <scope>" + scope + "</scope>");
                }
                if (mapping.isOptional(dds[i].getModuleConfigurations())) {
                    out.println("      <optional>true</optional>");
                }
                out.println("    </dependency>");
            }
            out.println("  </dependencies>");
        }
    }
    
    public static final ConfigurationScopeMapping DEFAULT_MAPPING 
        = new ConfigurationScopeMapping(new HashMap() {
            {
                put("compile, runtime", "compile");
                put("runtime", "runtime");
                put("provided", "provided");
                put("test", "test");
                put("system", "system");
            }
        });

    public static class ConfigurationScopeMapping {
        private Map/*<String,String>*/ scopes;
        
        public ConfigurationScopeMapping(Map/*<String,String>*/ scopesMapping) {
            this.scopes = new HashMap(scopesMapping);
        }

        /**
         * Returns the scope mapped to the given configuration array.
         * 
         * @param confs the configurations for which the scope should be returned
         * @return the scope to which the conf is mapped
         */
        public String getScope(String[] confs) {
            return (String) scopes.get(StringUtils.join(confs, ", "));
        }
        public boolean isOptional(String[] confs) {
            return getScope(confs) == null;
        }
    }
}