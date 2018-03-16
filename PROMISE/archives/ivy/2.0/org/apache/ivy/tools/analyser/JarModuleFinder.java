package org.apache.ivy.tools.analyser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.ivy.core.IvyPatternHelper;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.plugins.resolver.util.FileURLLister;
import org.apache.ivy.plugins.resolver.util.ResolverHelper;
import org.apache.ivy.plugins.resolver.util.URLLister;

public class JarModuleFinder {
    private String pattern;

    private String filePattern;

    public JarModuleFinder(String pattern) {
        this.filePattern = pattern;
    }

    public JarModule[] findJarModules() {
        List ret = new ArrayList();
        URLLister lister = new FileURLLister();
        try {
            String[] orgs = ResolverHelper.listTokenValues(lister, pattern, "organisation");
            for (int i = 0; i < orgs.length; i++) {
                String orgPattern = IvyPatternHelper.substituteToken(pattern,
                    IvyPatternHelper.ORGANISATION_KEY, orgs[i]);
                String[] modules = ResolverHelper.listTokenValues(lister, orgPattern, "module");
                for (int j = 0; j < modules.length; j++) {
                    String modPattern = IvyPatternHelper.substituteToken(orgPattern,
                        IvyPatternHelper.MODULE_KEY, modules[j]);
                    String[] revs = ResolverHelper.listTokenValues(lister, modPattern, "revision");
                    for (int k = 0; k < revs.length; k++) {
                        File jar = new File(IvyPatternHelper.substitute(filePattern, orgs[i],
                            modules[j], revs[k], modules[j], "jar", "jar"));
                        if (jar.exists()) {
                            ret.add(new JarModule(ModuleRevisionId.newInstance(orgs[i], modules[j],
                                revs[k]), jar));
                        }
                    }
                }
            }

        } catch (Exception e) {
        }
        return (JarModule[]) ret.toArray(new JarModule[ret.size()]);
    }

    public static void main(String[] args) {
        JarModule[] mods = new JarModuleFinder(
                "D:/temp/test2/ivyrep/[organisation]/[module]/[revision]/[artifact].[ext]")
                .findJarModules();
        for (int i = 0; i < mods.length; i++) {
            System.out.println(mods[i]);
        }
    }
}
