package org.apache.tools.ant.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PathTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.resources.FileResource;

/**
 * This class also encapsulates methods which allow Files to be
 * referred to using abstract path names which are translated to native
 * system file paths at runtime as well as copying files or setting
 * their last modification time.
 *
 */
public class FileUtils {
    private static final int EXPAND_SPACE = 50;
    private static final FileUtils PRIMARY_INSTANCE = new FileUtils();

    private static Random rand = new Random(System.currentTimeMillis()
            + Runtime.getRuntime().freeMemory());

    private static final boolean ON_NETWARE = Os.isFamily("netware");
    private static final boolean ON_DOS = Os.isFamily("dos");
    private static final boolean ON_WIN9X = Os.isFamily("win9x");
    private static final boolean ON_WINDOWS = Os.isFamily("windows");

    static final int BUF_SIZE = 8192;


    /**
     * The granularity of timestamps under FAT.
     */
    public static final long FAT_FILE_TIMESTAMP_GRANULARITY = 2000;

    /**
     * The granularity of timestamps under Unix.
     */
    public static final long UNIX_FILE_TIMESTAMP_GRANULARITY = 1000;

    /**
     * The granularity of timestamps under the NT File System.
     * NTFS has a granularity of 100 nanoseconds, which is less
     * than 1 millisecond, so we round this up to 1 millisecond.
     */
    public static final long NTFS_FILE_TIMESTAMP_GRANULARITY = 1;

    /**
     * A one item cache for fromUri.
     * fromUri is called for each element when parseing ant build
     * files. It is a costly operation. This just caches the result
     * of the last call.
     */
    private Object cacheFromUriLock = new Object();
    private String cacheFromUriRequest = null;
    private String cacheFromUriResponse = null;

    /**
     * Factory method.
     *
     * @return a new instance of FileUtils.
     * @deprecated since 1.7.
     *             Use getFileUtils instead,
     * FileUtils do not have state.
     */
    public static FileUtils newFileUtils() {
        return new FileUtils();
    }

    /**
     * Method to retrieve The FileUtils, which is shared by all users of this
     * method.
     * @return an instance of FileUtils.
     * @since Ant 1.6.3
     */
    public static FileUtils getFileUtils() {
        return PRIMARY_INSTANCE;
    }

    /**
     * Empty constructor.
     */
    protected FileUtils() {
    }

    /**
     * Get the URL for a file taking into account # characters.
     *
     * @param file the file whose URL representation is required.
     * @return The FileURL value.
     * @throws MalformedURLException if the URL representation cannot be
     *      formed.
     */
    public URL getFileURL(File file) throws MalformedURLException {
        return new URL(toURI(file.getAbsolutePath()));
    }

    /**
     * Convenience method to copy a file from a source to a destination.
     * No filtering is performed.
     *
     * @param sourceFile Name of file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile Name of file to copy to.
     *                 Must not be <code>null</code>.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(String sourceFile, String destFile) throws IOException {
        copyFile(new File(sourceFile), new File(destFile), null, false, false);
    }

    /**
     * Convenience method to copy a file from a source to a destination
     * specifying if token filtering must be used.
     *
     * @param sourceFile Name of file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile Name of file to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters)
            throws IOException {
        copyFile(new File(sourceFile), new File(destFile), filters, false, false);
    }

    /**
     * Convenience method to copy a file from a source to a destination specifying if token
     * filtering must be used and if source files may overwrite newer destination files.
     *
     * @param sourceFile Name of file to copy from. Must not be <code>null</code>.
     * @param destFile Name of file to copy to. Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param overwrite Whether or not the destination file should be overwritten if it already
     *            exists.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(String sourceFile, String destFile, FilterSetCollection filters,
                         boolean overwrite) throws IOException {
        copyFile(new File(sourceFile), new File(destFile), filters, overwrite, false);
    }

    /**
     * Convenience method to copy a file from a source to a destination
     * specifying if token
     * filtering must be used, if source files may overwrite newer destination
     * files and the last
     * modified time of <code>destFile</code> file should be made equal to
     * the last modified time
     * of <code>sourceFile</code>.
     *
     * @param sourceFile Name of file to copy from. Must not be <code>null</code>.
     * @param destFile Name of file to copy to. Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param overwrite Whether or not the destination file should be
     *            overwritten if it already exists.
     * @param preserveLastModified Whether or not the last modified time of
     *            the resulting file
     *            should be set to that of the source file.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(String sourceFile, String destFile,
                         FilterSetCollection filters,
                         boolean overwrite, boolean preserveLastModified)
        throws IOException {
        copyFile(new File(sourceFile), new File(destFile), filters, overwrite,
                 preserveLastModified);
    }

    /**
     * Convenience method to copy a file from a source to a destination specifying if token
     * filtering must be used, if source files may overwrite newer destination files and the last
     * modified time of <code>destFile</code> file should be made equal to the last modified time
     * of <code>sourceFile</code>.
     *
     * @param sourceFile Name of file to copy from. Must not be <code>null</code>.
     * @param destFile Name of file to copy to. Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param overwrite Whether or not the destination file should be overwritten if it already
     *            exists.
     * @param preserveLastModified Whether or not the last modified time of the resulting file
     *            should be set to that of the source file.
     * @param encoding the encoding used to read and write the files.
     *
     * @throws IOException if the copying fails.
     *
     * @since Ant 1.5
     */
    public void copyFile(String sourceFile, String destFile,
                         FilterSetCollection filters, boolean overwrite,
                         boolean preserveLastModified, String encoding) throws IOException {
        copyFile(new File(sourceFile), new File(destFile), filters,
                 overwrite, preserveLastModified, encoding);
    }

    /**
     * Convenience method to copy a file from a source to a
     * destination specifying if token filtering must be used, if
     * filter chains must be used, if source files may overwrite
     * newer destination files and the last modified time of
     * <code>destFile</code> file should be made equal
     * to the last modified time of <code>sourceFile</code>.
     *
     * @param sourceFile Name of file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile Name of file to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param filterChains filterChains to apply during the copy.
     * @param overwrite Whether or not the destination file should be
     *                  overwritten if it already exists.
     * @param preserveLastModified Whether or not the last modified time of
     *                             the resulting file should be set to that
     *                             of the source file.
     * @param encoding the encoding used to read and write the files.
     * @param project the project instance.
     *
     * @throws IOException if the copying fails.
     *
     * @since Ant 1.5
     */
    public void copyFile(String sourceFile, String destFile,
                         FilterSetCollection filters, Vector filterChains,
                         boolean overwrite, boolean preserveLastModified,
                         String encoding, Project project) throws IOException {
        copyFile(new File(sourceFile), new File(destFile), filters, filterChains, overwrite,
                preserveLastModified, encoding, project);
    }

    /**
     * Convenience method to copy a file from a source to a destination specifying if token
     * filtering must be used, if filter chains must be used, if source files may overwrite newer
     * destination files and the last modified time of <code>destFile</code> file should be made
     * equal to the last modified time of <code>sourceFile</code>.
     *
     * @param sourceFile Name of file to copy from. Must not be <code>null</code>.
     * @param destFile Name of file to copy to. Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param filterChains filterChains to apply during the copy.
     * @param overwrite Whether or not the destination file should be overwritten if it already
     *            exists.
     * @param preserveLastModified Whether or not the last modified time of the resulting file
     *            should be set to that of the source file.
     * @param inputEncoding the encoding used to read the files.
     * @param outputEncoding the encoding used to write the files.
     * @param project the project instance.
     *
     * @throws IOException if the copying fails.
     *
     * @since Ant 1.6
     */
    public void copyFile(String sourceFile, String destFile,
                         FilterSetCollection filters, Vector filterChains,
                         boolean overwrite, boolean preserveLastModified,
                         String inputEncoding, String outputEncoding,
                         Project project) throws IOException {
        copyFile(new File(sourceFile), new File(destFile), filters, filterChains, overwrite,
                preserveLastModified, inputEncoding, outputEncoding, project);
    }

    /**
     * Convenience method to copy a file from a source to a destination. No filtering is performed.
     *
     * @param sourceFile the file to copy from. Must not be <code>null</code>.
     * @param destFile the file to copy to. Must not be <code>null</code>.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(File sourceFile, File destFile) throws IOException {
        copyFile(sourceFile, destFile, null, false, false);
    }

    /**
     * Convenience method to copy a file from a source to a destination
     * specifying if token filtering must be used.
     *
     * @param sourceFile the file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile the file to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters)
            throws IOException {
        copyFile(sourceFile, destFile, filters, false, false);
    }

    /**
     * Convenience method to copy a file from a source to a
     * destination specifying if token filtering must be used and if
     * source files may overwrite newer destination files.
     *
     * @param sourceFile the file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile the file to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param overwrite Whether or not the destination file should be
     *                  overwritten if it already exists.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters,
                         boolean overwrite) throws IOException {
        copyFile(sourceFile, destFile, filters, overwrite, false);
    }

    /**
     * Convenience method to copy a file from a source to a
     * destination specifying if token filtering must be used, if
     * source files may overwrite newer destination files and the
     * last modified time of <code>destFile</code> file should be made equal
     * to the last modified time of <code>sourceFile</code>.
     *
     * @param sourceFile the file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile the file to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param overwrite Whether or not the destination file should be
     *                  overwritten if it already exists.
     * @param preserveLastModified Whether or not the last modified time of
     *                             the resulting file should be set to that
     *                             of the source file.
     *
     * @throws IOException if the copying fails.
     */
    public void copyFile(File sourceFile, File destFile, FilterSetCollection filters,
                         boolean overwrite, boolean preserveLastModified) throws IOException {
        copyFile(sourceFile, destFile, filters, overwrite, preserveLastModified, null);
    }

    /**
     * Convenience method to copy a file from a source to a destination specifying if token
     * filtering must be used, if source files may overwrite newer destination files, the last
     * modified time of <code>destFile</code> file should be made equal to the last modified time
     * of <code>sourceFile</code> and which character encoding to assume.
     *
     * @param sourceFile the file to copy from. Must not be <code>null</code>.
     * @param destFile the file to copy to. Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param overwrite Whether or not the destination file should be overwritten if it already
     *            exists.
     * @param preserveLastModified Whether or not the last modified time of the resulting file
     *            should be set to that of the source file.
     * @param encoding the encoding used to read and write the files.
     *
     * @throws IOException if the copying fails.
     *
     * @since Ant 1.5
     */
    public void copyFile(File sourceFile, File destFile,
                         FilterSetCollection filters, boolean overwrite,
                         boolean preserveLastModified, String encoding) throws IOException {
        copyFile(sourceFile, destFile, filters, null, overwrite,
                 preserveLastModified, encoding, null);
    }

    /**
     * Convenience method to copy a file from a source to a
     * destination specifying if token filtering must be used, if
     * filter chains must be used, if source files may overwrite
     * newer destination files and the last modified time of
     * <code>destFile</code> file should be made equal
     * to the last modified time of <code>sourceFile</code>.
     *
     * @param sourceFile the file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile the file to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param filterChains filterChains to apply during the copy.
     * @param overwrite Whether or not the destination file should be
     *                  overwritten if it already exists.
     * @param preserveLastModified Whether or not the last modified time of
     *                             the resulting file should be set to that
     *                             of the source file.
     * @param encoding the encoding used to read and write the files.
     * @param project the project instance.
     *
     * @throws IOException if the copying fails.
     *
     * @since Ant 1.5
     */
    public void copyFile(File sourceFile, File destFile,
                         FilterSetCollection filters, Vector filterChains,
                         boolean overwrite, boolean preserveLastModified,
                         String encoding, Project project) throws IOException {
        copyFile(sourceFile, destFile, filters, filterChains,
                 overwrite, preserveLastModified, encoding, encoding, project);
    }

    /**
     * Convenience method to copy a file from a source to a
     * destination specifying if token filtering must be used, if
     * filter chains must be used, if source files may overwrite
     * newer destination files and the last modified time of
     * <code>destFile</code> file should be made equal
     * to the last modified time of <code>sourceFile</code>.
     *
     * @param sourceFile the file to copy from.
     *                   Must not be <code>null</code>.
     * @param destFile the file to copy to.
     *                 Must not be <code>null</code>.
     * @param filters the collection of filters to apply to this copy.
     * @param filterChains filterChains to apply during the copy.
     * @param overwrite Whether or not the destination file should be
     *                  overwritten if it already exists.
     * @param preserveLastModified Whether or not the last modified time of
     *                             the resulting file should be set to that
     *                             of the source file.
     * @param inputEncoding the encoding used to read the files.
     * @param outputEncoding the encoding used to write the files.
     * @param project the project instance.
     *
     *
     * @throws IOException if the copying fails.
     *
     * @since Ant 1.6
     */
    public void copyFile(File sourceFile, File destFile,
                         FilterSetCollection filters, Vector filterChains,
                         boolean overwrite, boolean preserveLastModified,
                         String inputEncoding, String outputEncoding,
                         Project project) throws IOException {
        ResourceUtils.copyResource(
            new FileResource(sourceFile), new FileResource(destFile),
            filters, filterChains, overwrite, preserveLastModified,
            inputEncoding, outputEncoding, project);
    }


    /**
     * Calls File.setLastModified(long time). Originally written to
     * to dynamically bind to that call on Java1.2+.
     *
     * @param file the file whose modified time is to be set
     * @param time the time to which the last modified time is to be set.
     *             if this is -1, the current time is used.
     */
    public void setFileLastModified(File file, long time) {
        ResourceUtils.setLastModified(new FileResource(file), time);
    }

    /**
     * Interpret the filename as a file relative to the given file
     * unless the filename already represents an absolute filename.
     * Differs from <code>new File(file, filename)</code> in that
     * the resulting File's path will always be a normalized,
     * absolute pathname.  Also, if it is determined that
     * <code>filename</code> is context-relative, <code>file</code>
     * will be discarded and the reference will be resolved using
     * available context/state information about the filesystem.
     *
     * @param file the "reference" file for relative paths. This
     * instance must be an absolute file and must not contain
     * &quot;./&quot; or &quot;../&quot; sequences (same for \ instead
     * of /).  If it is null, this call is equivalent to
     * <code>new java.io.File(filename).getAbsoluteFile()</code>.
     *
     * @param filename a file name.
     *
     * @return an absolute file.
     * @throws java.lang.NullPointerException if filename is null.
     */
    public File resolveFile(File file, String filename) {
        if (!isAbsolutePath(filename)) {
            char sep = File.separatorChar;
            filename = filename.replace('/', sep).replace('\\', sep);
            if (isContextRelativePath(filename)) {
                file = null;
                String udir = System.getProperty("user.dir");
                if (filename.charAt(0) == sep && udir.charAt(0) == sep) {
                    filename = dissect(udir)[0] + filename.substring(1);
                }
            }
            filename = new File(file, filename).getAbsolutePath();
        }
        return normalize(filename);
    }

    /**
     * On DOS and NetWare, the evaluation of certain file
     * specifications is context-dependent.  These are filenames
     * beginning with a single separator (relative to current root directory)
     * and filenames with a drive specification and no intervening separator
     * (relative to current directory of the specified root).
     * @param filename the filename to evaluate.
     * @return true if the filename is relative to system context.
     * @throws java.lang.NullPointerException if filename is null.
     * @since Ant 1.7
     */
    public static boolean isContextRelativePath(String filename) {
        if (!(ON_DOS || ON_NETWARE) || filename.length() == 0) {
            return false;
        }
        char sep = File.separatorChar;
        filename = filename.replace('/', sep).replace('\\', sep);
        char c = filename.charAt(0);
        int len = filename.length();
        return (c == sep && (len == 1 || filename.charAt(1) != sep))
                || (Character.isLetter(c) && len > 1
                && filename.indexOf(':') == 1
                && (len == 2 || filename.charAt(2) != sep));
    }

    /**
     * Verifies that the specified filename represents an absolute path.
     * Differs from new java.io.File("filename").isAbsolute() in that a path
     * beginning with a double file separator--signifying a Windows UNC--must
     * at minimum match "\\a\b" to be considered an absolute path.
     * @param filename the filename to be checked.
     * @return true if the filename represents an absolute path.
     * @throws java.lang.NullPointerException if filename is null.
     * @since Ant 1.6.3
     */
    public static boolean isAbsolutePath(String filename) {
        int len = filename.length();
        if (len == 0) {
            return false;
        }
        char sep = File.separatorChar;
        filename = filename.replace('/', sep).replace('\\', sep);
        char c = filename.charAt(0);
        if (!(ON_DOS || ON_NETWARE)) {
            return (c == sep);
        }
        if (c == sep) {
            if (!(ON_DOS && len > 4 && filename.charAt(1) == sep)) {
                return false;
            }
            int nextsep = filename.indexOf(sep, 2);
            return nextsep > 2 && nextsep + 1 < len;
        }
        int colon = filename.indexOf(':');
        return (Character.isLetter(c) && colon == 1
                && filename.length() > 2 && filename.charAt(2) == sep)
                || (ON_NETWARE && colon > 0);
    }

    /**
     * Translate a path into its native (platform specific) format.
     * <p>
     * This method uses PathTokenizer to separate the input path
     * into its components. This handles DOS style paths in a relatively
     * sensible way. The file separators are then converted to their platform
     * specific versions.
     *
     * @param toProcess The path to be translated.
     *                  May be <code>null</code>.
     *
     * @return the native version of the specified path or
     *         an empty string if the path is <code>null</code> or empty.
     *
     * @since ant 1.7
     * @see PathTokenizer
     */
    public static String translatePath(String toProcess) {
        if (toProcess == null || toProcess.length() == 0) {
            return "";
        }
        StringBuffer path = new StringBuffer(toProcess.length() + EXPAND_SPACE);
        PathTokenizer tokenizer = new PathTokenizer(toProcess);
        while (tokenizer.hasMoreTokens()) {
            String pathComponent = tokenizer.nextToken();
            pathComponent = pathComponent.replace('/', File.separatorChar);
            pathComponent = pathComponent.replace('\\', File.separatorChar);
            if (path.length() != 0) {
                path.append(File.pathSeparatorChar);
            }
            path.append(pathComponent);
        }
        return path.toString();
    }

    /**
     * &quot;Normalize&quot; the given absolute path.
     *
     * <p>This includes:
     * <ul>
     *   <li>Uppercase the drive letter if there is one.</li>
     *   <li>Remove redundant slashes after the drive spec.</li>
     *   <li>Resolve all ./, .\, ../ and ..\ sequences.</li>
     *   <li>DOS style paths that start with a drive letter will have
     *     \ as the separator.</li>
     * </ul>
     * Unlike {@link File#getCanonicalPath()} this method
     * specifically does not resolve symbolic links.
     *
     * @param path the path to be normalized.
     * @return the normalized version of the path.
     *
     * @throws java.lang.NullPointerException if path is null.
     */
    public File normalize(final String path) {
        Stack s = new Stack();
        String[] dissect = dissect(path);
        s.push(dissect[0]);

        StringTokenizer tok = new StringTokenizer(dissect[1], File.separator);
        while (tok.hasMoreTokens()) {
            String thisToken = tok.nextToken();
            if (".".equals(thisToken)) {
                continue;
            }
            if ("..".equals(thisToken)) {
                if (s.size() < 2) {
                    return new File(path);
                }
                s.pop();
                s.push(thisToken);
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.size(); i++) {
            if (i > 1) {
                sb.append(File.separatorChar);
            }
            sb.append(s.elementAt(i));
        }
        return new File(sb.toString());
    }

    /**
     * Dissect the specified absolute path.
     * @param path the path to dissect.
     * @return String[] {root, remaining path}.
     * @throws java.lang.NullPointerException if path is null.
     * @since Ant 1.7
     */
    public String[] dissect(String path) {
        char sep = File.separatorChar;
        path = path.replace('/', sep).replace('\\', sep);

        if (!isAbsolutePath(path)) {
            throw new BuildException(path + " is not an absolute path");
        }
        String root = null;
        int colon = path.indexOf(':');
        if (colon > 0 && (ON_DOS || ON_NETWARE)) {

            int next = colon + 1;
            root = path.substring(0, next);
            char[] ca = path.toCharArray();
            root += sep;
            next = (ca[next] == sep) ? next + 1 : next;

            StringBuffer sbPath = new StringBuffer();
            for (int i = next; i < ca.length; i++) {
                if (ca[i] != sep || ca[i - 1] != sep) {
                    sbPath.append(ca[i]);
                }
            }
            path = sbPath.toString();
        } else if (path.length() > 1 && path.charAt(1) == sep) {
            int nextsep = path.indexOf(sep, 2);
            nextsep = path.indexOf(sep, nextsep + 1);
            root = (nextsep > 2) ? path.substring(0, nextsep + 1) : path;
            path = path.substring(root.length());
        } else {
            root = File.separator;
            path = path.substring(1);
        }
        return new String[] {root, path};
    }

    /**
     * Returns a VMS String representation of a <code>File</code> object.
     * This is useful since the JVM by default internally converts VMS paths
     * to Unix style.
     * The returned String is always an absolute path.
     *
     * @param f The <code>File</code> to get the VMS path for.
     * @return The absolute VMS path to <code>f</code>.
     */
    public String toVMSPath(File f) {
        String osPath;
        String path = normalize(f.getAbsolutePath()).getPath();
        String name = f.getName();
        boolean isAbsolute = path.charAt(0) == File.separatorChar;
        boolean isDirectory = f.isDirectory()
                && !name.regionMatches(true, name.length() - 4, ".DIR", 0, 4);
        String device = null;
        StringBuffer directory = null;
        String file = null;

        int index = 0;

        if (isAbsolute) {
            index = path.indexOf(File.separatorChar, 1);
            if (index == -1) {
                return path.substring(1) + ":[000000]";
            }
            device = path.substring(1, index++);
        }
        if (isDirectory) {
            directory = new StringBuffer(path.substring(index).replace(File.separatorChar, '.'));
        } else {
            int dirEnd = path.lastIndexOf(File.separatorChar, path.length());
            if (dirEnd == -1 || dirEnd < index) {
                file = path.substring(index);
            } else {
                directory = new StringBuffer(path.substring(index, dirEnd).
                                             replace(File.separatorChar, '.'));
                index = dirEnd + 1;
                if (path.length() > index) {
                    file = path.substring(index);
                }
            }
        }
        if (!isAbsolute && directory != null) {
            directory.insert(0, '.');
        }
        osPath = ((device != null) ? device + ":" : "")
                + ((directory != null) ? "[" + directory + "]" : "")
                + ((file != null) ? file : "");
        return osPath;
    }

    /**
     * Create a File object for a temporary file in a given directory. Without
     * actually creating the file.
     *
     * <p>The file denoted by the returned abstract pathname did not
     * exist before this method was invoked, any subsequent invocation
     * of this method will yield a different file name.</p>
     * <p>
     * The filename is prefixNNNNNsuffix where NNNN is a random number.
     * </p>
     *
     * @param prefix prefix before the random number.
     * @param suffix file extension; include the '.'.
     * @param parentDir Directory to create the temporary file in;
     * java.io.tmpdir used if not specified.
     *
     * @return a File reference to the new, nonexistent temporary file.
     * @deprecated since Ant 1.7.1
     * @since Ant 1.7
     */
    public File createTempFile(String prefix, String suffix, File parentDir) {
        return createTempFile(prefix, suffix, parentDir, false);
    }

    /**
     * Create a temporary file in a given directory.
     *
     * <p>The file denoted by the returned abstract pathname did not
     * exist before this method was invoked, any subsequent invocation
     * of this method will yield a different file name.</p>
     *
     * @param prefix prefix before the random number.
     * @param suffix file extension; include the '.'.
     * @param parentDir Directory to create the temporary file in;
     * java.io.tmpdir used if not specified.
     * @param deleteOnExit whether to set the tempfile for deletion on
     *        normal VM exit.
     * @param createFile true if the file must actually be created. If false
     * chances exist that a file with the same name is created in the time
     * between invoking this method and the moment the file is actually created.
     * If possible set to true.
     * @return a File reference to the new temporary file.
     * @since Ant 1.7.1
     */
    public File createTempFile(String prefix, String suffix, File parentDir,
                               boolean deleteOnExit, boolean createFile) {
        File result = null;
        String parent = (parentDir == null)
                ? System.getProperty("java.io.tmpdir")
                : parentDir.getPath();

        if (createFile) {
            try {
                result = File.createTempFile(prefix, suffix, new File(parent));
            } catch (IOException e) {
                throw new BuildException("Could not create tempfile in "
                        + parent, e);
            }
        } else {
            DecimalFormat fmt = new DecimalFormat("#####");
            synchronized (rand) {
                do {
                    result = new File(parent, prefix
                            + fmt.format(Math.abs(rand.nextInt())) + suffix);
                } while (result.exists());
            }
        }
        if (deleteOnExit) {
            result.deleteOnExit();
        }
        return result;
    }

    /**
     * Create a File object for a temporary file in a given directory. Without
     * actually creating the file.
     *
     * <p>The file denoted by the returned abstract pathname did not
     * exist before this method was invoked, any subsequent invocation
     * of this method will yield a different file name.</p>
     * <p>
     * The filename is prefixNNNNNsuffix where NNNN is a random number.
     * </p>
     *
     * @param prefix prefix before the random number.
     * @param suffix file extension; include the '.'.
     * @param parentDir Directory to create the temporary file in;
     * java.io.tmpdir used if not specified.
     * @param deleteOnExit whether to set the tempfile for deletion on
     *        normal VM exit.
     *
     * @return a File reference to the new, nonexistent temporary file.
     * @deprecated since Ant 1.7.1
     * @since Ant 1.7
     */
    public File createTempFile(String prefix, String suffix,
            File parentDir, boolean deleteOnExit) {
        return createTempFile(prefix, suffix, parentDir, deleteOnExit, false);
    }

    /**
     * Compares the contents of two files.
     *
     * @param f1 the file whose content is to be compared.
     * @param f2 the other file whose content is to be compared.
     *
     * @return true if the content of the files is the same.
     *
     * @throws IOException if the files cannot be read.
     */
    public boolean contentEquals(File f1, File f2) throws IOException {
        return contentEquals(f1, f2, false);
    }

    /**
     * Compares the contents of two files.
     *
     * @param f1 the file whose content is to be compared.
     * @param f2 the other file whose content is to be compared.
     * @param textfile true if the file is to be treated as a text file and
     *        differences in kind of line break are to be ignored.
     *
     * @return true if the content of the files is the same.
     *
     * @throws IOException if the files cannot be read.
     * @since Ant 1.6.3
     */
    public boolean contentEquals(File f1, File f2, boolean textfile) throws IOException {
        return ResourceUtils.contentEquals(new FileResource(f1), new FileResource(f2), textfile);
    }

    /**
     * This was originally an emulation of {@link File#getParentFile} for JDK 1.1, but it is now
     * implemented using that method (Ant 1.6.3 onwards).
     *
     * @param f the file whose parent is required.
     * @return the given file's parent, or null if the file does not have a parent.
     * @since 1.10
     * @deprecated since 1.7. Just use {@link File#getParentFile} directly.
     */
    public File getParentFile(File f) {
        return (f == null) ? null : f.getParentFile();
    }

    /**
     * Read from reader till EOF.
     * @param rdr the reader from which to read.
     * @return the contents read out of the given reader.
     *
     * @throws IOException if the contents could not be read out from the
     *         reader.
     */
    public static String readFully(Reader rdr) throws IOException {
        return readFully(rdr, BUF_SIZE);
    }

    /**
     * Read from reader till EOF.
     *
     * @param rdr the reader from which to read.
     * @param bufferSize the buffer size to use when reading.
     *
     * @return the contents read out of the given reader.
     *
     * @throws IOException if the contents could not be read out from the
     *         reader.
     */
    public static String readFully(Reader rdr, int bufferSize)
        throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater "
                                               + "than 0");
        }
        final char[] buffer = new char[bufferSize];
        int bufferLength = 0;
        StringBuffer textBuffer = null;
        while (bufferLength != -1) {
            bufferLength = rdr.read(buffer);
            if (bufferLength > 0) {
                textBuffer = (textBuffer == null) ? new StringBuffer() : textBuffer;
                textBuffer.append(new String(buffer, 0, bufferLength));
            }
        }
        return (textBuffer == null) ? null : textBuffer.toString();
    }

    /**
     * Safe read fully - do not return a null for an empty reader.
     * @param reader the input to read from.
     * @return the string.
     * @throws IOException if unable to read from reader.
     */
    public static String safeReadFully(Reader reader) throws IOException {
        String ret = readFully(reader);
        return ret == null ? "" : ret;
    }

    /**
     * This was originally an emulation of File.createNewFile for JDK 1.1,
     * but it is now implemented using that method (Ant 1.6.3 onwards).
     *
     * <p>This method has historically <strong>not</strong> guaranteed that the
     * operation was atomic. In its current implementation it is.
     *
     * @param f the file to be created.
     * @return true if the file did not exist already.
     * @throws IOException on error.
     * @since Ant 1.5
     */
    public boolean createNewFile(File f) throws IOException {
        return f.createNewFile();
    }

    /**
     * Create a new file, optionally creating parent directories.
     *
     * @param f the file to be created.
     * @param mkdirs <code>boolean</code> whether to create parent directories.
     * @return true if the file did not exist already.
     * @throws IOException on error.
     * @since Ant 1.6.3
     */
    public boolean createNewFile(File f, boolean mkdirs) throws IOException {
        File parent = f.getParentFile();
        if (mkdirs && !(parent.exists())) {
            parent.mkdirs();
        }
        return f.createNewFile();
    }

    /**
     * Checks whether a given file is a symbolic link.
     *
     * <p>It doesn't really test for symbolic links but whether the
     * canonical and absolute paths of the file are identical--this
     * may lead to false positives on some platforms.</p>
     *
     * @param parent the parent directory of the file to test
     * @param name the name of the file to test.
     *
     * @return true if the file is a symbolic link.
     * @throws IOException on error.
     * @since Ant 1.5
     */
    public boolean isSymbolicLink(File parent, String name)
        throws IOException {
        if (parent == null) {
            File f = new File(name);
            parent = f.getParentFile();
            name = f.getName();
        }
        File toTest = new File(parent.getCanonicalPath(), name);
        return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
    }

    /**
     * Removes a leading path from a second path.
     *
     * @param leading The leading path, must not be null, must be absolute.
     * @param path The path to remove from, must not be null, must be absolute.
     *
     * @return path's normalized absolute if it doesn't start with
     * leading; path's path with leading's path removed otherwise.
     *
     * @since Ant 1.5
     */
    public String removeLeadingPath(File leading, File path) {
        String l = normalize(leading.getAbsolutePath()).getAbsolutePath();
        String p = normalize(path.getAbsolutePath()).getAbsolutePath();
        if (l.equals(p)) {
            return "";
        }
        if (!l.endsWith(File.separator)) {
            l += File.separator;
        }
        return (p.startsWith(l)) ? p.substring(l.length()) : p;
    }

    /**
     * Learn whether one path "leads" another.
     * @param leading The leading path, must not be null, must be absolute.
     * @param path The path to remove from, must not be null, must be absolute.
     * @return true if path starts with leading; false otherwise.
     * @since Ant 1.7
     */
    public boolean isLeadingPath(File leading, File path) {
        String l = normalize(leading.getAbsolutePath()).getAbsolutePath();
        String p = normalize(path.getAbsolutePath()).getAbsolutePath();
        if (l.equals(p)) {
            return true;
        }
        if (!l.endsWith(File.separator)) {
            l += File.separator;
        }
        return p.startsWith(l);
    }

    /**
     * Constructs a <code>file:</code> URI that represents the
     * external form of the given pathname.
     *
     * <p>Will be an absolute URI if the given path is absolute.</p>
     *
     * <p>This code encodes non ASCII characters too.</p>
     *
     * <p>The coding of the output is the same as what File.toURI().toASCIIString() produces</p>
     *
     * which makes some mention of how
     * characters not supported by URI Reference syntax should be escaped.
     *
     * @param path the path in the local file system.
     * @return the URI version of the local path.
     * @since Ant 1.6
     */
    public String toURI(String path) {
        Class uriClazz = null;
        try {
            uriClazz = Class.forName("java.net.URI");
        } catch (ClassNotFoundException e) {
        }
        if (uriClazz != null) {
            try {
                File f = new File(path).getAbsoluteFile();
                java.lang.reflect.Method toURIMethod = File.class.getMethod("toURI", new Class[0]);
                Object uriObj = toURIMethod.invoke(f, new Object[] {});
                java.lang.reflect.Method toASCIIStringMethod
                        = uriClazz.getMethod("toASCIIString", new Class[0]);
                return (String) toASCIIStringMethod.invoke(uriObj, new Object[] {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        boolean isDir = new File(path).isDirectory();

        StringBuffer sb = new StringBuffer("file:");

        path = resolveFile(null, path).getPath();
        if (!path.startsWith(File.separator)) {
            sb.append("/");
        }
        path = path.replace('\\', '/');
        try {
            sb.append(Locator.encodeURI(path));
        } catch (UnsupportedEncodingException exc) {
            throw new BuildException(exc);
        }
        if (isDir && !path.endsWith("/")) {
            sb.append('/');
        }
        return sb.toString();
    }

    /**
     * Constructs a file path from a <code>file:</code> URI.
     *
     * <p>Will be an absolute path if the given URI is absolute.</p>
     *
     * <p>Swallows '%' that are not followed by two characters,
     * doesn't deal with non-ASCII characters.</p>
     *
     * @param uri the URI designating a file in the local filesystem.
     * @return the local file system path for the file.
     * @since Ant 1.6
     */
    public String fromURI(String uri) {
        synchronized (cacheFromUriLock) {
            if (uri.equals(cacheFromUriRequest)) {
                return cacheFromUriResponse;
            }
            String path = Locator.fromURI(uri);
            String ret = isAbsolutePath(path) ? normalize(path).getAbsolutePath() : path;
            cacheFromUriRequest = uri;
            cacheFromUriResponse = ret;
            return ret;
        }
    }

    /**
     * Compares two filenames.
     *
     * <p>Unlike java.io.File#equals this method will try to compare
     * the absolute paths and &quot;normalize&quot; the filenames
     * before comparing them.</p>
     *
     * @param f1 the file whose name is to be compared.
     * @param f2 the other file whose name is to be compared.
     *
     * @return true if the file are for the same file.
     *
     * @since Ant 1.5.3
     */
    public boolean fileNameEquals(File f1, File f2) {
        return normalize(f1.getAbsolutePath()).getAbsolutePath().equals(
                normalize(f2.getAbsolutePath()).getAbsolutePath());
    }

    /**
     * Renames a file, even if that involves crossing file system boundaries.
     *
     * <p>This will remove <code>to</code> (if it exists), ensure that
     * <code>to</code>'s parent directory exists and move
     * <code>from</code>, which involves deleting <code>from</code> as
     * well.</p>
     *
     * @param from the file to move.
     * @param to the new file name.
     *
     * @throws IOException if anything bad happens during this
     * process.  Note that <code>to</code> may have been deleted
     * already when this happens.
     *
     * @since Ant 1.6
     */
    public void rename(File from, File to) throws IOException {
        from = normalize(from.getAbsolutePath()).getCanonicalFile();
        to = normalize(to.getAbsolutePath());
        if (!from.exists()) {
            System.err.println("Cannot rename nonexistent file " + from);
            return;
        }
        if (from.equals(to)) {
            System.err.println("Rename of " + from + " to " + to + " is a no-op.");
            return;
        }
        if (to.exists() && !(from.equals(to.getCanonicalFile()) || to.delete())) {
            throw new IOException("Failed to delete " + to + " while trying to rename " + from);
        }
        File parent = to.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent
                                  + " while trying to rename " + from);
        }
        if (!from.renameTo(to)) {
            copyFile(from, to);
            if (!from.delete()) {
                throw new IOException("Failed to delete " + from + " while trying to rename it.");
            }
        }
    }

    /**
     * Get the granularity of file timestamps. The choice is made based on OS, which is
     * incorrect--it should really be by filesystem. We do not have an easy way to probe for file
     * systems, however, so this heuristic gives us a decent default.
     *
     * @return the difference, in milliseconds, which two file timestamps must have in order for the
     *         two files to be considered to have different timestamps.
     */
    public long getFileTimestampGranularity() {
        if (ON_WIN9X) {
            return FAT_FILE_TIMESTAMP_GRANULARITY;
        }
        if (ON_WINDOWS) {
            return NTFS_FILE_TIMESTAMP_GRANULARITY;
        }
        if (ON_DOS) {
            return FAT_FILE_TIMESTAMP_GRANULARITY;
        }
        return UNIX_FILE_TIMESTAMP_GRANULARITY;
    }

    /**
     * test whether a file or directory exists, with an error in the
     * upper/lower case spelling of the name.
     * Using this method is only interesting on case insensitive file systems
     * (Windows).<br/>
     * It will return true only if 3 conditions are met :
     * <br/>
     * <ul>
     *   <li>operating system is case insensitive</li>
     *   <li>file exists</li>
     *   <li>actual name from directory reading is different from the
     *       supplied argument</li>
     * </ul>
     *  <br/>
     * the purpose is to identify files or directories on case-insensitive
     * filesystems whose case is not what is expected.<br/>
     * Possibly to rename them afterwards to the desired upper/lowercase
     * combination.
     * <br/>
     * @param localFile file to test
     * @return true if the file exists and the case of the actual file
     *              is not the case of the parameter
     * @since Ant 1.7.1
     */
    public boolean hasErrorInCase(File localFile) {
        localFile = normalize(localFile.getAbsolutePath());
        if (!localFile.exists()) {
            return false;
        }
        final String localFileName = localFile.getName();
        FilenameFilter ff = new FilenameFilter () {
            public boolean accept(File dir, String name) {
                return name.equalsIgnoreCase(localFileName) && (!name.equals(localFileName));
            }
        };
        String[] names = localFile.getParentFile().list(ff);
        return names != null && names.length == 1;
    }

    /**
     * Returns true if the source is older than the dest.
     * If the dest file does not exist, then the test returns false; it is
     * implicitly not up do date.
     * @param source source file (should be the older).
     * @param dest dest file (should be the newer).
     * @param granularity an offset added to the source time.
     * @return true if the source is older than the dest after accounting
     *              for granularity.
     * @since Ant 1.6.3
     */
    public boolean isUpToDate(File source, File dest, long granularity) {
        if (!dest.exists()) {
            return false;
        }
        long sourceTime = source.lastModified();
        long destTime = dest.lastModified();
        return isUpToDate(sourceTime, destTime, granularity);
    }

    /**
     * Returns true if the source is older than the dest.
     * @param source source file (should be the older).
     * @param dest dest file (should be the newer).
     * @return true if the source is older than the dest, taking the granularity into account.
     * @since Ant 1.6.3
     */
    public boolean isUpToDate(File source, File dest) {
        return isUpToDate(source, dest, getFileTimestampGranularity());
    }

    /**
     * Compare two timestamps for being up to date using
     * the specified granularity.
     *
     * @param sourceTime timestamp of source file.
     * @param destTime timestamp of dest file.
     * @param granularity os/filesys granularity.
     * @return true if the dest file is considered up to date.
     */
    public boolean isUpToDate(long sourceTime, long destTime, long granularity) {
        return destTime != -1 && destTime >= sourceTime + granularity;
    }

    /**
     * Compare two timestamps for being up to date using the
     * current granularity.
     *
     * @param sourceTime  timestamp of source file.
     * @param destTime    timestamp of dest file.
     * @return true if the dest file is considered up to date.
     */
    public boolean isUpToDate(long sourceTime, long destTime) {
        return isUpToDate(sourceTime, destTime, getFileTimestampGranularity());
    }

    /**
     * Close a Writer without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     * @param device output writer, can be null.
     */
    public static void close(Writer device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Close a Reader without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device Reader, can be null.
     */
    public static void close(Reader device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Close a stream without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device stream, can be null.
     */
    public static void close(OutputStream device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Close a stream without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device stream, can be null.
     */
    public static void close(InputStream device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Delete the file with {@link File#delete()} if the argument is not null.
     * Do nothing on a null argument.
     * @param file file to delete.
     */
    public static void delete(File file) {
        if (file != null) {
            file.delete();
        }
    }

    /**
     * Calculates the relative path between two files.
     * <p>
     * Implementation note:<br/> This function may throw an IOException if an I/O error occurs
     * because its use of the canonical pathname may require filesystem queries.
     * </p>
     *
     * @param fromFile the <code>File</code> to calculate the path from
     * @param toFile the <code>File</code> to calculate the path to
     * @return the relative path between the files
     * @throws Exception for undocumented reasons
     * @see File#getCanonicalPath()
     *
     * @since Ant 1.7
     */
    public static String getRelativePath(File fromFile, File toFile) throws Exception {
        String fromPath = fromFile.getCanonicalPath();
        String toPath = toFile.getCanonicalPath();

        String[] fromPathStack = getPathStack(fromPath);
        String[] toPathStack = getPathStack(toPath);

        if (0 < toPathStack.length && 0 < fromPathStack.length) {
            if (!fromPathStack[0].equals(toPathStack[0])) {

                return getPath(Arrays.asList(toPathStack));
            }
        } else {
            return getPath(Arrays.asList(toPathStack));
        }

        int minLength = Math.min(fromPathStack.length, toPathStack.length);

        for (;
             same < minLength && fromPathStack[same].equals(toPathStack[same]);
             same++) {
        }

        List relativePathStack = new ArrayList();

        for (int i = same; i < fromPathStack.length; i++) {
            relativePathStack.add("..");
        }

        for (int i = same; i < toPathStack.length; i++) {
            relativePathStack.add(toPathStack[i]);
        }

        return getPath(relativePathStack);
    }

    /**
     * Gets all names of the path as an array of <code>String</code>s.
     *
     * @param path to get names from
     * @return <code>String</code>s, never <code>null</code>
     *
     * @since Ant 1.7
     */
    public static String[] getPathStack(String path) {
        String normalizedPath = path.replace(File.separatorChar, '/');

        Object[] tokens = StringUtils.split(normalizedPath, '/').toArray();
        String[] rv = new String[tokens.length];
        System.arraycopy(tokens, 0, rv, 0, tokens.length);

        return rv;
    }

    /**
     * Gets path from a <code>List</code> of <code>String</code>s.
     *
     * @param pathStack <code>List</code> of <code>String</code>s to be concatenated as a path.
     * @return <code>String</code>, never <code>null</code>
     *
     * @since Ant 1.7
     */
    public static String getPath(List pathStack) {
        return getPath(pathStack, '/');
    }

    /**
     * Gets path from a <code>List</code> of <code>String</code>s.
     *
     * @param pathStack <code>List</code> of <code>String</code>s to be concated as a path.
     * @param separatorChar <code>char</code> to be used as separator between names in path
     * @return <code>String</code>, never <code>null</code>
     *
     * @since Ant 1.7
     */
    public static String getPath(final List pathStack, final char separatorChar) {
        final StringBuffer buffer = new StringBuffer();

        final Iterator iter = pathStack.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
        }
        while (iter.hasNext()) {
            buffer.append(separatorChar);
            buffer.append(iter.next());
        }
        return buffer.toString();
    }

    /**
     * Get the default encoding.
     * This is done by opening an InputStreamReader on
     * a dummy InputStream and getting the encoding.
     * Could use System.getProperty("file.encoding"), but cannot
     * see where this is documented.
     * @return the default file encoding.
     */
    public String getDefaultEncoding() {
        InputStreamReader is = new InputStreamReader(
            new InputStream() {
                public int read() {
                    return -1;
                }
            });
        try {
            return is.getEncoding();
        } finally {
            close(is);
        }
    }
}
