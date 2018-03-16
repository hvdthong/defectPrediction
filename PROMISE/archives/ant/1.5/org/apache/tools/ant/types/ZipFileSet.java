package org.apache.tools.ant.types;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.zip.UnixStat;
import java.util.Stack;

/**
 * A ZipFileSet is a FileSet with extra attributes useful in the context of
 * Zip/Jar tasks.
 *
 * A ZipFileSet extends FileSets with the ability to extract a subset of the
 * entries of a Zip file for inclusion in another Zip file.  It also includes
 * a prefix attribute which is prepended to each entry in the output Zip file.
 *
 * At present, ZipFileSets are not surfaced in the public API.  FileSets
 * nested in a Zip task are instantiated as ZipFileSets, and their attributes
 * are only recognized in the context of the the Zip task.
 * It is not possible to define a ZipFileSet outside of the Zip task and
 * refer to it via a refid.  However a standard FileSet may be included by
 * reference in the Zip task, and attributes in the refering ZipFileSet
 * can augment FileSet definition.
 *
 * @author Don Ferguson <a href="mailto:don@bea.com">don@bea.com</a>
 */
public class ZipFileSet extends FileSet {

    /**
     * Default value for the dirmode attribute.
     *
     * @since Ant 1.5.2
     */
    public static final int DEFAULT_DIR_MODE =
        UnixStat.DIR_FLAG  | UnixStat.DEFAULT_DIR_PERM;

    /**
     * Default value for the filemode attribute.
     *
     * @since Ant 1.5.2
     */
    public static final int DEFAULT_FILE_MODE =
        UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

    private File srcFile          = null;
    private String prefix         = "";
    private String fullpath       = "";
    private boolean hasDir        = false;
    private int fileMode          = DEFAULT_FILE_MODE;
    private int dirMode           = DEFAULT_DIR_MODE;

    public ZipFileSet() {
        super();
    }

    protected ZipFileSet(FileSet fileset) {
        super(fileset);
    }

    protected ZipFileSet(ZipFileSet fileset) {
        super(fileset);
        srcFile = fileset.srcFile;
        prefix = fileset.prefix;
        fullpath = fileset.fullpath;
        hasDir = fileset.hasDir;
        fileMode = fileset.fileMode;
        dirMode = fileset.dirMode;
    }

    /**
     * Set the directory for the fileset.  Prevents both "dir" and "src"
     * from being specified.
     */
    public void setDir(File dir) throws BuildException {
        if (srcFile != null) {
            throw new BuildException("Cannot set both dir and src attributes");
        } else {
            super.setDir(dir);
            hasDir = true;
        }
    }

    /**
     * Set the source Zip file for the zipfileset.  Prevents both
     * "dir" and "src" from being specified.
     *
     * @param srcFile The zip file from which to extract entries.
     */
    public void setSrc(File srcFile) {
        if (hasDir) {
            throw new BuildException("Cannot set both dir and src attributes");
        }
        this.srcFile = srcFile;
    }

    /**
     * Get the zip file from which entries will be extracted.
     * References are not followed, since it is not possible
     * to have a reference to a ZipFileSet, only to a FileSet.
     */
    public File getSrc() {
        return srcFile;
    }

    /**
     * Prepend this prefix to the path for each zip entry.
     * Does not perform reference test; the referenced file set
     * can be augmented with a prefix.
     *
     * @param prefix The prefix to prepend to entries in the zip file.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Return the prefix prepended to entries in the zip file.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the full pathname of the single entry in this fileset.
     *
     * @param fullpath the full pathname of the single entry in this fileset.
     */
    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    /**
     * Return the full pathname of the single entry in this fileset.
     */
    public String getFullpath() {
        return fullpath;
    }

    /**
     * Return the DirectoryScanner associated with this FileSet.
     * If the ZipFileSet defines a source Zip file, then a ZipScanner
     * is returned instead.
     */
    public DirectoryScanner getDirectoryScanner(Project p) {
        if (isReference()) {
            return getRef(p).getDirectoryScanner(p);
        }
        if (srcFile != null) {
            ZipScanner zs = new ZipScanner();
            zs.setSrc(srcFile);
            super.setDir(p.getBaseDir());
            setupDirectoryScanner(zs, p);
            zs.init();
            return zs;
        } else {
            return super.getDirectoryScanner(p);
        }
    }

    /**
     * A 3 digit octal string, specify the user, group and 
     * other modes in the standard Unix fashion; 
     * optional, default=0644
     *
     * @since Ant 1.5.2
     */
    public void setFileMode(String octalString) {
        this.fileMode = 
            UnixStat.FILE_FLAG | Integer.parseInt(octalString, 8);
    }
    
    /**
     * @since Ant 1.5.2
     */
    public int getFileMode() {
        return fileMode;
    }
    
    /**
     * A 3 digit octal string, specify the user, group and 
     * other modes in the standard Unix fashion; 
     * optional, default=0755
     *
     * @since Ant 1.6
     */
    public void setDirMode(String octalString) {
        this.dirMode = 
            UnixStat.DIR_FLAG | Integer.parseInt(octalString, 8);
    }
    
    /**
     * @since Ant 1.6
     */
    public int getDirMode() {
        return dirMode;
    }

    /**
     * A ZipFileset can accept any fileset as a reference as it just uses the
     * standard directory scanner.
     */
    protected AbstractFileSet getRef(Project p) {
        if (!checked) {
            Stack stk = new Stack();
            stk.push(this);
            dieOnCircularReference(stk, p);
        }

        Object o = ref.getReferencedObject(p);
        if (!(o instanceof FileSet)) {
            String msg = ref.getRefId() + " doesn\'t denote a fileset";
            throw new BuildException(msg);
        } else {
            return (AbstractFileSet) o;
        }
    }
}
