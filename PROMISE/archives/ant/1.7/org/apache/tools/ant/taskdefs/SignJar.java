package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.IsSigned;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.FileNameMapper;

/**
 * Signs JAR or ZIP files with the javasign command line tool. The tool detailed
 * dependency checking: files are only signed if they are not signed. The
 * <tt>signjar</tt> attribute can point to the file to generate; if this file
 * exists then its modification date is used as a cue as to whether to resign
 * any JAR file.
 *
 * Timestamp driven signing is based on the unstable and inadequately documented
 * information in the Java1.5 docs
 * beta documentation</a>
 * @ant.task category="java"
 * @since Ant 1.1
 */
public class SignJar extends AbstractJarSignerTask {

    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    /**
     * name to a signature file
     */
    protected String sigfile;

    /**
     * name of a single jar
     */
    protected File signedjar;

    /**
     * flag for internal sf signing
     */
    protected boolean internalsf;

    /**
     * sign sections only?
     */
    protected boolean sectionsonly;

    /**
     * flag to preserve timestamp on modified files
     */
    private boolean preserveLastModified;

    /**
     * Whether to assume a jar which has an appropriate .SF file in is already
     * signed.
     */
    protected boolean lazy;

    /**
     * the output directory when using paths.
     */
    protected File destDir;

    /**
     * mapper for todir work
     */
    private FileNameMapper mapper;

    /**
     * URL for a tsa; null implies no tsa support
     */
    protected String tsaurl;

    /**
     * alias for the TSA in the keystore
     */
    protected String tsacert;

    /**
     * error string for unit test verification: {@value}
     */
    public static final String ERROR_TODIR_AND_SIGNEDJAR
            = "'destdir' and 'signedjar' cannot both be set";
    /**
     * error string for unit test verification: {@value}
     */
    public static final String ERROR_TOO_MANY_MAPPERS = "Too many mappers";
    /**
     * error string for unit test verification {@value}
     */
    public static final String ERROR_SIGNEDJAR_AND_PATHS
        = "You cannot specify the signed JAR when using paths or filesets";
    /**
     * error string for unit test verification: {@value}
     */
    public static final String ERROR_BAD_MAP = "Cannot map source file to anything sensible: ";
    /**
     * error string for unit test verification: {@value}
     */
    public static final String ERROR_MAPPER_WITHOUT_DEST
        = "The destDir attribute is required if a mapper is set";
    /**
     * error string for unit test verification: {@value}
     */
    public static final String ERROR_NO_ALIAS = "alias attribute must be set";
    /**
     * error string for unit test verification: {@value}
     */
    public static final String ERROR_NO_STOREPASS = "storepass attribute must be set";

    /**
     * name of .SF/.DSA file; optional
     *
     * @param sigfile the name of the .SF/.DSA file
     */
    public void setSigfile(final String sigfile) {
        this.sigfile = sigfile;
    }

    /**
     * name of signed JAR file; optional
     *
     * @param signedjar the name of the signed jar file
     */
    public void setSignedjar(final File signedjar) {
        this.signedjar = signedjar;
    }

    /**
     * Flag to include the .SF file inside the signature; optional; default
     * false
     *
     * @param internalsf if true include the .SF file inside the signature
     */
    public void setInternalsf(final boolean internalsf) {
        this.internalsf = internalsf;
    }

    /**
     * flag to compute hash of entire manifest; optional, default false
     *
     * @param sectionsonly flag to compute hash of entire manifest
     */
    public void setSectionsonly(final boolean sectionsonly) {
        this.sectionsonly = sectionsonly;
    }

    /**
     * flag to control whether the presence of a signature file means a JAR is
     * signed; optional, default false
     *
     * @param lazy flag to control whether the presence of a signature
     */
    public void setLazy(final boolean lazy) {
        this.lazy = lazy;
    }

    /**
     * Optionally sets the output directory to be used.
     *
     * @param destDir the directory in which to place signed jars
     * @since Ant 1.7
     */
    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }


    /**
     * add a mapper to determine file naming policy. Only used with toDir
     * processing.
     *
     * @param newMapper the mapper to add.
     * @since Ant 1.7
     */
    public void add(FileNameMapper newMapper) {
        if (mapper != null) {
            throw new BuildException(ERROR_TOO_MANY_MAPPERS);
        }
        mapper = newMapper;
    }

    /**
     * get the active mapper; may be null
     * @return mapper or null
     * @since Ant 1.7
     */
    public FileNameMapper getMapper() {
        return mapper;
    }

    /**
     * get the -tsaurl url
     * @return url or null
     * @since Ant 1.7
     */
    public String getTsaurl() {
        return tsaurl;
    }

    /**
     *
     * @param tsaurl the tsa url.
     * @since Ant 1.7
     */
    public void setTsaurl(String tsaurl) {
        this.tsaurl = tsaurl;
    }

    /**
     * get the -tsacert option
     * @since Ant 1.7
     * @return a certificate alias or null
     */
    public String getTsacert() {
        return tsacert;
    }

    /**
     * set the alias in the keystore of the TSA to use;
     * @param tsacert the cert alias.
     */
    public void setTsacert(String tsacert) {
        this.tsacert = tsacert;
    }

    /**
     * sign the jar(s)
     *
     * @throws BuildException on errors
     */
    public void execute() throws BuildException {
        final boolean hasJar = jar != null;
        final boolean hasSignedJar = signedjar != null;
        final boolean hasDestDir = destDir != null;
        final boolean hasMapper = mapper != null;

        if (!hasJar && !hasResources()) {
            throw new BuildException(ERROR_NO_SOURCE);
        }
        if (null == alias) {
            throw new BuildException(ERROR_NO_ALIAS);
        }

        if (null == storepass) {
            throw new BuildException(ERROR_NO_STOREPASS);
        }

        if (hasDestDir && hasSignedJar) {
            throw new BuildException(ERROR_TODIR_AND_SIGNEDJAR);
        }


        if (hasResources() && hasSignedJar) {
            throw new BuildException(ERROR_SIGNEDJAR_AND_PATHS);
        }

        if (!hasDestDir && hasMapper) {
            throw new BuildException(ERROR_MAPPER_WITHOUT_DEST);
        }

        beginExecution();


        try {
            if (hasJar && hasSignedJar) {
                signOneJar(jar, signedjar);
                return;
            }


            Path sources = createUnifiedSourcePath();
            FileNameMapper destMapper;
            if (hasMapper) {
                destMapper = mapper;
            } else {
                destMapper = new IdentityMapper();
            }


            Iterator iter = sources.iterator();
            while (iter.hasNext()) {
                FileResource fr = (FileResource) iter.next();

                File toDir = hasDestDir ? destDir : fr.getBaseDir();

                String[] destFilenames = destMapper.mapFileName(fr.getName());
                if (destFilenames == null || destFilenames.length != 1) {
                    throw new BuildException(ERROR_BAD_MAP + fr.getFile());
                }
                File destFile = new File(toDir, destFilenames[0]);
                signOneJar(fr.getFile(), destFile);
            }
        } finally {
            endExecution();
        }
    }

    /**
     * Sign one jar.
     * <p/>
     * The signing only takes place if {@link #isUpToDate(File, File)} indicates
     * that it is needed.
     *
     * @param jarSource source to sign
     * @param jarTarget target; may be null
     * @throws BuildException
     */
    private void signOneJar(File jarSource, File jarTarget)
            throws BuildException {


        File targetFile = jarTarget;
        if (targetFile == null) {
            targetFile = jarSource;
        }
        if (isUpToDate(jarSource, targetFile)) {
            return;
        }

        long lastModified = jarSource.lastModified();
        final ExecTask cmd = createJarSigner();

        setCommonOptions(cmd);

        bindToKeystore(cmd);
        if (null != sigfile) {
            addValue(cmd, "-sigfile");
            String value = this.sigfile;
            addValue(cmd, value);
        }

        if (!jarSource.equals(targetFile)) {
            addValue(cmd, "-signedjar");
            addValue(cmd, targetFile.getPath());
        }

        if (internalsf) {
            addValue(cmd, "-internalsf");
        }

        if (sectionsonly) {
            addValue(cmd, "-sectionsonly");
        }

        addTimestampAuthorityCommands(cmd);

        addValue(cmd, jarSource.getPath());

        addValue(cmd, alias);

        log("Signing JAR: "
            + jarSource.getAbsolutePath()
            + " to "
            + targetFile.getAbsolutePath()
            + " as " + alias);

        cmd.execute();

        if (preserveLastModified) {
            targetFile.setLastModified(lastModified);
        }
    }

    /**
     * If the tsa parameters are set, this passes them to the command.
     * There is no validation of java version, as third party JDKs
     * may implement this on earlier/later jarsigner implementations.
     * @param cmd the exec task.
     */
    private void addTimestampAuthorityCommands(final ExecTask cmd) {
        if (tsaurl != null) {
            addValue(cmd, "-tsa");
            addValue(cmd, tsaurl);
        }
        if (tsacert != null) {
            addValue(cmd, "-tsacert");
            addValue(cmd, tsacert);
        }
    }

    /**
     * Compare a jar file with its corresponding signed jar. The logic for this
     * is complex, and best explained in the source itself. Essentially if
     * either file doesnt exist, or the destfile has an out of date timestamp,
     * then the return value is false.
     * <p/>
     * If we are signing ourself, the check {@link #isSigned(File)} is used to
     * trigger the process.
     *
     * @param jarFile       the unsigned jar file
     * @param signedjarFile the result signed jar file
     * @return true if the signedjarFile is considered up to date
     */
    protected boolean isUpToDate(File jarFile, File signedjarFile) {
        if (null == jarFile || !jarFile.exists()) {
            return false;
        }

        File destFile = signedjarFile;
        if (destFile == null) {
            destFile = jarFile;
        }

        if (jarFile.equals(destFile)) {
            if (lazy) {
                return isSigned(jarFile);
            }
            return false;
        }

        return FILE_UTILS.isUpToDate(jarFile, destFile);
    }

    /**
     * test for a file being signed, by looking for a signature in the META-INF
     * directory with our alias.
     *
     * @param file the file to be checked
     * @return true if the file is signed
     * @see IsSigned#isSigned(File, String)
     */
    protected boolean isSigned(File file) {
        try {
            return IsSigned.isSigned(file, alias);
        } catch (IOException e) {
            log(e.toString(), Project.MSG_VERBOSE);
            return false;
        }
    }

    /**
     * true to indicate that the signed jar modification date remains the same
     * as the original. Defaults to false
     *
     * @param preserveLastModified if true preserve the last modified time
     */
    public void setPreserveLastModified(boolean preserveLastModified) {
        this.preserveLastModified = preserveLastModified;
    }
}
