package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.util.*;

import java.io.*;
import java.util.*;

/**
 * A consolidated copy task.  Copies a file or directory to a new file 
 * or directory.  Files are only copied if the source file is newer
 * than the destination file, or when the destination file does not 
 * exist.  It is possible to explicitly overwrite existing files.</p>
 *
 * <p>This implementation is based on Arnout Kuiper's initial design
 * document, the following mailing list discussions, and the 
 * copyfile/copydir tasks.</p>
 *
 * @author Glenn McAllister <a href="mailto:glennm@ca.ibm.com">glennm@ca.ibm.com</a>
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @author <A href="gholam@xtra.co.nz">Michael McCallum</A>
 */
public class Copy extends Task {
    protected Vector filesets = new Vector();

    protected boolean filtering = false;
    protected boolean preserveLastModified = false;
    protected boolean forceOverwrite = false;
    protected boolean flatten = false;
    protected int verbosity = Project.MSG_VERBOSE;
    protected boolean includeEmpty = true;

    protected Hashtable fileCopyMap = new Hashtable();
    protected Hashtable dirCopyMap = new Hashtable();

    protected Mapper mapperElement = null;
    private Vector filterSets = new Vector();
    private FileUtils fileUtils;
    
    public Copy() {
        fileUtils = FileUtils.newFileUtils();
    }

    protected FileUtils getFileUtils() {return fileUtils;}

    /**
     * Sets a single source file to copy.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Sets the destination file.
     */
    public void setTofile(File destFile) {
        this.destFile = destFile;
    }

    /**
     * Sets the destination directory.
     */
    public void setTodir(File destDir) {
        this.destDir = destDir;
    }

    /**
     * Create a nested filterset
     */
    public FilterSet createFilterSet() {
        FilterSet filterSet = new FilterSet();
        filterSets.addElement(filterSet);
        return filterSet;
    }
    
    /**
     * Give the copied files the same last modified time as the original files.
     */
    public void setPreserveLastModified(String preserve) {
        preserveLastModified = Project.toBoolean(preserve);
    }

    /**
     * Get the filtersets being applied to this operation.
     *
     * @return a vector of FilterSet objects
     */
    protected Vector getFilterSets() {
        return filterSets;
    }
    
    /**
     * Sets filtering.
     */
    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
    }

    /**
     * Overwrite any existing destination file(s).
     */
    public void setOverwrite(boolean overwrite) {
        this.forceOverwrite = overwrite;
    }

    /**
     * When copying directory trees, the files can be "flattened"
     * into a single directory.  If there are multiple files with
     * the same name in the source directory tree, only the first
     * file will be copied into the "flattened" directory, unless
     * the forceoverwrite attribute is true.
     */
    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    /**
     * Used to force listing of all names of copied files.
     */
    public void setVerbose(boolean verbose) {
        if (verbose) {
            this.verbosity = Project.MSG_INFO;
        } else {
            this.verbosity = Project.MSG_VERBOSE;
        } 
    } 

    /**
     * Used to copy empty directories.
     */
    public void setIncludeEmptyDirs(boolean includeEmpty) {
        this.includeEmpty = includeEmpty;
    }

    /**
     * Adds a set of files (nested fileset attribute).
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    /**
     * Defines the FileNameMapper to use (nested mapper element).
     */
    public Mapper createMapper() throws BuildException {
        if (mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper",
                                     location);
        }
        mapperElement = new Mapper(project);
        return mapperElement;
    }

    /**
     * Performs the copy operation.
     */
    public void execute() throws BuildException {
        validateAttributes();   

        if (file != null) {
            if (file.exists()) {
                if (destFile == null) {
                    destFile = new File(destDir, file.getName());
                }
                
                if (forceOverwrite || 
                    (file.lastModified() > destFile.lastModified())) {
                    fileCopyMap.put(file.getAbsolutePath(), destFile.getAbsolutePath());
                } else {
                    log(file + " omitted as " + destFile + " is up to date.",
                        Project.MSG_VERBOSE);
                }
            } else {
                String message = "Could not find file " 
                                 + file.getAbsolutePath() + " to copy.";
                log(message);
                throw new BuildException(message);
            }
        }

        for (int i=0; i<filesets.size(); i++) {
            FileSet fs = (FileSet) filesets.elementAt(i);
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            File fromDir = fs.getDir(project);

            String[] srcFiles = ds.getIncludedFiles();
            String[] srcDirs = ds.getIncludedDirectories();

            scan(fromDir, destDir, srcFiles, srcDirs);
        }

        doFileOperations();

        if (destFile != null) {
            destDir = null;
        }
    }


    /**
     * Ensure we have a consistent and legal set of attributes, and set
     * any internal flags necessary based on different combinations 
     * of attributes.
     */
    protected void validateAttributes() throws BuildException {
        if (file == null && filesets.size() == 0) {
            throw new BuildException("Specify at least one source - a file or a fileset.");
        }

        if (destFile != null && destDir != null) {
            throw new BuildException("Only one of destfile and destdir may be set.");
        }

        if (destFile == null && destDir == null) {
            throw new BuildException("One of destfile or destdir must be set.");
        }

        if (file != null && file.exists() && file.isDirectory()) {
            throw new BuildException("Use a fileset to copy directories.");
        }
           
        if (destFile != null && filesets.size() > 0) {
            throw new BuildException("Cannot concatenate multple files into a single file.");
        }

        if (destFile != null) {
        }

    }

    /**
     * Compares source files to destination files to see if they should be
     * copied.
     */
    protected void scan(File fromDir, File toDir, String[] files, String[] dirs) {
        FileNameMapper mapper = null;
        if (mapperElement != null) {
            mapper = mapperElement.getImplementation();
        } else if (flatten) {
            mapper = new FlatFileNameMapper();
        } else {
            mapper = new IdentityMapper();
        }

        buildMap(fromDir, toDir, files, mapper, fileCopyMap);

        if (includeEmpty) {
            buildMap(fromDir, toDir, dirs, mapper, dirCopyMap);
        }
    }

    protected void buildMap(File fromDir, File toDir, String[] names,
                            FileNameMapper mapper, Hashtable map) {

        String[] toCopy = null;
        if (forceOverwrite) {
            Vector v = new Vector();
            for (int i=0; i<names.length; i++) {
                if (mapper.mapFileName(names[i]) != null) {
                    v.addElement(names[i]);
                }
            }
            toCopy = new String[v.size()];
            v.copyInto(toCopy);
        } else {
            SourceFileScanner ds = new SourceFileScanner(this);
            toCopy = ds.restrict(names, fromDir, toDir, mapper);
        }
        
        for (int i = 0; i < toCopy.length; i++) {
            File src = new File(fromDir, toCopy[i]);
            File dest = new File(toDir, mapper.mapFileName(toCopy[i])[0]);
            map.put( src.getAbsolutePath(), dest.getAbsolutePath() );
        }
    }

    /**
     * Actually does the file (and possibly empty directory) copies.
     * This is a good method for subclasses to override.
     */
    protected void doFileOperations() {
        if (fileCopyMap.size() > 0) {
            log("Copying " + fileCopyMap.size() + 
                " file" + (fileCopyMap.size() == 1 ? "" : "s") + 
                " to " + destDir.getAbsolutePath() );

            Enumeration e = fileCopyMap.keys();
            while (e.hasMoreElements()) {
                String fromFile = (String) e.nextElement();
                String toFile = (String) fileCopyMap.get(fromFile);

                if( fromFile.equals( toFile ) ) {
                    log("Skipping self-copy of " + fromFile, verbosity);
                    continue;
                }

                try {
                    log("Copying " + fromFile + " to " + toFile, verbosity);
                    
                    FilterSetCollection executionFilters = new FilterSetCollection();
                    if (filtering) {
                        executionFilters.addFilterSet(project.getGlobalFilterSet());
                    }
                    for (Enumeration filterEnum = filterSets.elements(); filterEnum.hasMoreElements();) {
                        executionFilters.addFilterSet((FilterSet)filterEnum.nextElement());
                    }
                    fileUtils.copyFile(fromFile, toFile, executionFilters,
                                       forceOverwrite, preserveLastModified);
                } catch (IOException ioe) {
                    String msg = "Failed to copy " + fromFile + " to " + toFile
                        + " due to " + ioe.getMessage();
                    throw new BuildException(msg, ioe, location);
                }
            }
        }

        if (includeEmpty) {
            Enumeration e = dirCopyMap.elements();
            int count = 0;
            while (e.hasMoreElements()) {
                File d = new File((String)e.nextElement());
                if (!d.exists()) {
                    if (!d.mkdirs()) {
                        log("Unable to create directory " + d.getAbsolutePath(), Project.MSG_ERR);
                    } else {
                        count++;
                    }
                }
            }

            if (count > 0) {
                log("Copied " + count + 
                    " empty director" + 
                    (count==1?"y":"ies") + 
                    " to " + destDir.getAbsolutePath());
            }
        }
    }

}
