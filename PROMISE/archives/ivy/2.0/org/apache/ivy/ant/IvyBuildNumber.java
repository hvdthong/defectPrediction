package org.apache.ivy.ant;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.tools.ant.BuildException;

/**
 * Look for the latest module in the repository matching the given criteria, and sets a set of
 * properties according to what was found.
 */
public class IvyBuildNumber extends IvyTask {
    private String organisation;

    private String module;

    private String branch;

    private String revision;

    private String revSep = ".";

    private String prefix = "ivy.";

    private String defaultValue = "0";

    private String defaultBuildNumber = "0";

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDefault() {
        return defaultValue;
    }

    public void setDefault(String default1) {
        defaultValue = default1;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void doExecute() throws BuildException {
        if (organisation == null) {
            throw new BuildException("no organisation provided for ivy findmodules");
        }
        if (module == null) {
            throw new BuildException("no module name provided for ivy findmodules");
        }
        if (prefix == null) {
            throw new BuildException("null prefix not allowed");
        }

        Ivy ivy = getIvyInstance();
        IvySettings settings = ivy.getSettings();
        if (branch == null) {
            settings.getDefaultBranch(new ModuleId(organisation, module));
        }
        if (revision == null || revision.length() == 0) {
            revision = "latest.integration";
        } else if (!revision.endsWith("+")) {
            revision = revision + "+";
        }
        if (!prefix.endsWith(".") && prefix.length() > 0) {
            prefix = prefix + ".";
        }
        ResolvedModuleRevision rmr = ivy.findModule(ModuleRevisionId.newInstance(organisation,
            module, branch, revision));
        String revision = rmr == null ? null : rmr.getId().getRevision();
        NewRevision newRevision = computeNewRevision(revision);
        setProperty("revision", newRevision.getRevision());
        setProperty("new.revision", newRevision.getNewRevision());
        setProperty("build.number", newRevision.getBuildNumber());
        setProperty("new.build.number", newRevision.getNewBuildNumber());
    }

    private void setProperty(String propertyName, String value) {
        if (value != null) {
            getProject().setProperty(prefix + propertyName, value);
        }
    }

    private NewRevision computeNewRevision(String revision) {
        String revPrefix = "latest.integration".equals(this.revision) ? "" 
                : this.revision.substring(0, this.revision.length() - 1);
        if (revision != null && !revision.startsWith(revPrefix)) {
            throw new BuildException("invalid exception found in repository: '" + revision
                    + "' for '" + revPrefix + "'");
        }
        if (revision == null) {
            if (revPrefix.length() > 0) {
                return new NewRevision(revision, revPrefix
                        + (revPrefix.endsWith(revSep) ? defaultBuildNumber : revSep
                                + defaultBuildNumber), null, defaultBuildNumber);
            } else {
                Range r = findLastNumber(defaultValue);
                    return new NewRevision(revision, defaultValue, null, null);
                } else {
                    long n = Long.parseLong(
                        defaultValue.substring(r.getStartIndex(), r.getEndIndex()));
                    return new NewRevision(revision, defaultValue, null, String.valueOf(n));
                }
            }
        }
        Range r;
        if (revPrefix.length() == 0) {
            r = findLastNumber(revision);
            if (r == null) {
                return new NewRevision(revision, revision
                        + (revision.endsWith(revSep) ? "1" : revSep + "1"), null, "1");
            }
        } else {
            r = findFirstNumber(revision, revPrefix.length());
            if (r == null) {
                return new NewRevision(revision, revPrefix
                        + (revPrefix.endsWith(revSep) ? "1" : revSep + "1"), null, "1");
            }
        }
        long n = Long.parseLong(revision.substring(r.getStartIndex(), r.getEndIndex())) + 1;
        return new NewRevision(revision, revision.substring(0, r.getStartIndex()) + n, String
                .valueOf(n - 1), String.valueOf(n));
    }

    private Range findFirstNumber(String str, int startIndex) {
        int startNumberIndex = startIndex;
        while (startNumberIndex < str.length() 
                && !Character.isDigit(str.charAt(startNumberIndex))) {
            startNumberIndex++;
        }
        if (startNumberIndex == str.length()) {
            return null;
        }
        int endNumberIndex = startNumberIndex + 1;
        while (endNumberIndex < str.length() && Character.isDigit(str.charAt(endNumberIndex))) {
            endNumberIndex++;
        }
        return new Range(startNumberIndex, endNumberIndex);
    }

    private Range findLastNumber(String str) {
        int endNumberIndex = str.length() - 1;
        while (endNumberIndex >= 0 && !Character.isDigit(str.charAt(endNumberIndex))) {
            endNumberIndex--;
        }
        int startNumberIndex = endNumberIndex == -1 ? -1 : endNumberIndex - 1;
        while (startNumberIndex >= 0 && Character.isDigit(str.charAt(startNumberIndex))) {
            startNumberIndex--;
        }
        endNumberIndex++;
        startNumberIndex++;
            return null;
        } else {
            return new Range(startNumberIndex, endNumberIndex);
        }
    }

    private static class Range {
        private int startIndex;

        private int endIndex;

        public Range(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }

    private static class NewRevision {
        private String revision;

        private String newRevision;

        private String buildNumber;

        private String newBuildNumber;

        public NewRevision(String revision, String newRevision, String buildNumber,
                String newBuildNumber) {
            this.revision = revision;
            this.newRevision = newRevision;
            this.buildNumber = buildNumber;
            this.newBuildNumber = newBuildNumber;
        }

        public String getRevision() {
            return revision;
        }

        public String getNewRevision() {
            return newRevision;
        }

        public String getBuildNumber() {
            return buildNumber;
        }

        public String getNewBuildNumber() {
            return newBuildNumber;
        }
    }

    public String getRevSep() {
        return revSep;
    }

    public void setRevSep(String revSep) {
        this.revSep = revSep;
    }

    public String getDefaultBuildNumber() {
        return defaultBuildNumber;
    }

    public void setDefaultBuildNumber(String defaultBuildNumber) {
        this.defaultBuildNumber = defaultBuildNumber;
    }
}
