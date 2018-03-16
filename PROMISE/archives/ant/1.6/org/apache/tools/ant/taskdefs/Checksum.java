package org.apache.tools.ant.taskdefs;

import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;
import java.util.Arrays;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.FileSet;

/**
 * Used to create or verify file checksums.
 *
 *
 * @since Ant 1.5
 *
 * @ant.task category="control"
 */
public class Checksum extends MatchingTask implements Condition {

    /**
     * File for which checksum is to be calculated.
     */
    private File file = null;

    /**
     * Root directory in which the checksum files will be written.
     * If not specified, the checksum files will be written
     * in the same directory as each file.
     */
    private File todir;

    /**
     * MessageDigest algorithm to be used.
     */
    private String algorithm = "MD5";
    /**
     * MessageDigest Algorithm provider
     */
    private String provider = null;
    /**
     * File Extension that is be to used to create or identify
     * destination file
     */
    private String fileext;
    /**
     * Holds generated checksum and gets set as a Project Property.
     */
    private String property;
    /**
     * Holds checksums for all files (both calculated and cached on disk).
     * Key:   java.util.File (source file)
     * Value: java.lang.String (digest)
     */
    private Map allDigests = new HashMap();
    /**
     * Holds relative file names for all files (always with a forward slash).
     * This is used to calculate the total hash.
     * Key:   java.util.File (source file)
     * Value: java.lang.String (relative file name)
     */
    private Map relativeFilePaths = new HashMap();
    /**
     * Property where totalChecksum gets set.
     */
    private String totalproperty;
    /**
     * Whether or not to create a new file.
     * Defaults to <code>false</code>.
     */
    private boolean forceOverwrite;
    /**
     * Contains the result of a checksum verification. ("true" or "false")
     */
    private String verifyProperty;
    /**
     * Vector to hold source file sets.
     */
    private Vector filesets = new Vector();
    /**
     * Stores SourceFile, DestFile pairs and SourceFile, Property String pairs.
     */
    private Hashtable includeFileMap = new Hashtable();
    /**
     * Message Digest instance
     */
    private MessageDigest messageDigest;
    /**
     * is this task being used as a nested condition element?
     */
    private boolean isCondition;
    /**
     * Size of the read buffer to use.
     */
    private int readBufferSize = 8 * 1024;

    /**
     * Sets the file for which the checksum is to be calculated.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Sets the root directory where checksum files will be
     * written/read
     *
     * @since Ant 1.6
     */
    public void setTodir(File todir) {
        this.todir = todir;
    }

    /**
     * Specifies the algorithm to be used to compute the checksum.
     * Defaults to "MD5". Other popular algorithms like "SHA" may be used as well.
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Sets the MessageDigest algorithm provider to be used
     * to calculate the checksum.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * Sets the file extension that is be to used to
     * create or identify destination file.
     */
    public void setFileext(String fileext) {
        this.fileext = fileext;
    }

    /**
     * Sets the property to hold the generated checksum.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Sets the property to hold the generated total checksum
     * for all files.
     *
     * @since Ant 1.6
     */
    public void setTotalproperty(String totalproperty) {
        this.totalproperty = totalproperty;
    }

    /**
     * Sets the verify property.  This project property holds
     * the result of a checksum verification - "true" or "false"
     */
    public void setVerifyproperty(String verifyProperty) {
        this.verifyProperty = verifyProperty;
    }

    /**
     * Whether or not to overwrite existing file irrespective of
     * whether it is newer than
     * the source file.  Defaults to false.
     */
    public void setForceOverwrite(boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }

    /**
     * The size of the read buffer to use.
     */
    public void setReadBufferSize(int size) {
        this.readBufferSize = size;
    }

    /**
     * Files to generate checksums for.
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    /**
     * Calculate the checksum(s).
     */
    public void execute() throws BuildException {
        isCondition = false;
        boolean value = validateAndExecute();
        if (verifyProperty != null) {
            getProject().setNewProperty(verifyProperty,
                                new Boolean(value).toString());
        }
    }

    /**
     * Calculate the checksum(s)
     *
     * @return Returns true if the checksum verification test passed,
     * false otherwise.
     */
    public boolean eval() throws BuildException {
        isCondition = true;
        return validateAndExecute();
    }

    /**
     * Validate attributes and get down to business.
     */
    private boolean validateAndExecute() throws BuildException {
        String savedFileExt = fileext;

        if (file == null && filesets.size() == 0) {
            throw new BuildException(
                "Specify at least one source - a file or a fileset.");
        }

        if (file != null && file.exists() && file.isDirectory()) {
            throw new BuildException(
                "Checksum cannot be generated for directories");
        }

        if (file != null && totalproperty != null) {
            throw new BuildException(
                "File and Totalproperty cannot co-exist.");
        }

        if (property != null && fileext != null) {
            throw new BuildException(
                "Property and FileExt cannot co-exist.");
        }

        if (property != null) {
            if (forceOverwrite) {
                throw new BuildException(
                    "ForceOverwrite cannot be used when Property is specified");
            }

            if (file != null) {
                if (filesets.size() > 0) {
                    throw new BuildException("Multiple files cannot be used "
                        + "when Property is specified");
                }
            } else {
                if (filesets.size() > 1) {
                    throw new BuildException("Multiple files cannot be used "
                        + "when Property is specified");
                }
            }
        }

        if (verifyProperty != null) {
            isCondition = true;
        }

        if (verifyProperty != null && forceOverwrite) {
            throw new BuildException(
                "VerifyProperty and ForceOverwrite cannot co-exist.");
        }

        if (isCondition && forceOverwrite) {
            throw new BuildException("ForceOverwrite cannot be used when "
                + "conditions are being used.");
        }

        messageDigest = null;
        if (provider != null) {
            try {
                messageDigest = MessageDigest.getInstance(algorithm, provider);
            } catch (NoSuchAlgorithmException noalgo) {
                throw new BuildException(noalgo, getLocation());
            } catch (NoSuchProviderException noprovider) {
                throw new BuildException(noprovider, getLocation());
            }
        } else {
            try {
                messageDigest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException noalgo) {
                throw new BuildException(noalgo, getLocation());
            }
        }

        if (messageDigest == null) {
            throw new BuildException("Unable to create Message Digest",
                                     getLocation());
        }

        if (fileext == null) {
            fileext = "." + algorithm;
        } else if (fileext.trim().length() == 0) {
            throw new BuildException(
                "File extension when specified must not be an empty string");
        }

        try {
            int sizeofFileSet = filesets.size();
            for (int i = 0; i < sizeofFileSet; i++) {
                FileSet fs = (FileSet) filesets.elementAt(i);
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                String[] srcFiles = ds.getIncludedFiles();
                for (int j = 0; j < srcFiles.length; j++) {
                    File src = new File(fs.getDir(getProject()), srcFiles[j]);
                    if (totalproperty != null || todir != null) {
                        String relativePath = srcFiles[j].replace(File.separatorChar, '/');
                        relativeFilePaths.put(src, relativePath);
                    }
                    addToIncludeFileMap(src);
                }
            }

            addToIncludeFileMap(file);

            return generateChecksums();
        } finally {
            fileext = savedFileExt;
            includeFileMap.clear();
        }
    }

    /**
     * Add key-value pair to the hashtable upon which
     * to later operate upon.
     */
    private void addToIncludeFileMap(File file) throws BuildException {
        if (file != null) {
            if (file.exists()) {
                if (property == null) {
                    File checksumFile = getChecksumFile(file);
                    if (forceOverwrite || isCondition
                        || (file.lastModified() > checksumFile.lastModified())) {
                        includeFileMap.put(file, checksumFile);
                    } else {
                        log(file + " omitted as " + checksumFile + " is up to date.",
                            Project.MSG_VERBOSE);
                        if (totalproperty != null) {
                            String checksum = null;
                            try {
                                BufferedReader diskChecksumReader
                                    = new BufferedReader(new FileReader(checksumFile));
                                checksum = diskChecksumReader.readLine();
                            } catch (IOException e) {
                                throw new BuildException("Couldn't read checksum file "
                                    + checksumFile, e);
                            }
                            byte[] digest = decodeHex(checksum.toCharArray());
                            allDigests.put(file, digest);
                        }
                    }
                } else {
                    includeFileMap.put(file, property);
                }
            } else {
                String message = "Could not find file "
                                 + file.getAbsolutePath()
                                 + " to generate checksum for.";
                log(message);
                throw new BuildException(message, getLocation());
            }
        }
    }

    private File getChecksumFile(File file) {
        File directory;
        if (todir != null) {
            String path = (String) relativeFilePaths.get(file);
            directory = new File(todir, path).getParentFile();
            directory.mkdirs();
        } else {
            directory = file.getParentFile();
        }
        File checksumFile = new File(directory, file.getName() + fileext);
        return checksumFile;
    }

    /**
     * Generate checksum(s) using the message digest created earlier.
     */
    private boolean generateChecksums() throws BuildException {
        boolean checksumMatches = true;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte[] buf = new byte[readBufferSize];
        try {
            for (Enumeration e = includeFileMap.keys(); e.hasMoreElements();) {
                messageDigest.reset();
                File src = (File) e.nextElement();
                if (!isCondition) {
                    log("Calculating " + algorithm + " checksum for " + src, Project.MSG_VERBOSE);
                }
                fis = new FileInputStream(src);
                DigestInputStream dis = new DigestInputStream(fis,
                                                              messageDigest);
                while (dis.read(buf, 0, readBufferSize) != -1) {
                    ;
                }
                dis.close();
                fis.close();
                fis = null;
                byte[] fileDigest = messageDigest.digest ();
                if (totalproperty != null) {
                    allDigests.put(src, fileDigest);
                }
                String checksum = createDigestString(fileDigest);
                Object destination = includeFileMap.get(src);
                if (destination instanceof java.lang.String) {
                    String prop = (String) destination;
                    if (isCondition) {
                        checksumMatches
                            = checksumMatches && checksum.equals(property);
                    } else {
                        getProject().setNewProperty(prop, checksum);
                    }
                } else if (destination instanceof java.io.File) {
                    if (isCondition) {
                        File existingFile = (File) destination;
                        if (existingFile.exists()) {
                            fis = new FileInputStream(existingFile);
                            InputStreamReader isr = new InputStreamReader(fis);
                            BufferedReader br = new BufferedReader(isr);
                            String suppliedChecksum = br.readLine();
                            fis.close();
                            fis = null;
                            br.close();
                            isr.close();
                            checksumMatches = checksumMatches
                                && checksum.equals(suppliedChecksum);
                        } else {
                            checksumMatches = false;
                        }
                    } else {
                        File dest = (File) destination;
                        fos = new FileOutputStream(dest);
                        fos.write(checksum.getBytes());
                        fos.close();
                        fos = null;
                    }
                }
            }
            if (totalproperty != null) {
                Set keys = allDigests.keySet();
                Object[] keyArray = keys.toArray();
                Arrays.sort(keyArray);
                messageDigest.reset();
                for (int i = 0; i < keyArray.length; i++) {
                    File src = (File) keyArray[i];

                    byte[] digest = (byte[]) allDigests.get(src);
                    messageDigest.update(digest);

                    String fileName = (String) relativeFilePaths.get(src);
                    messageDigest.update(fileName.getBytes());
                }
                String totalChecksum = createDigestString(messageDigest.digest());
                getProject().setNewProperty(totalproperty, totalChecksum);
            }
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return checksumMatches;
    }

    private String createDigestString(byte[] fileDigest) {
        StringBuffer checksumSb = new StringBuffer();
        for (int i = 0; i < fileDigest.length; i++) {
            String hexStr = Integer.toHexString(0x00ff & fileDigest[i]);
            if (hexStr.length() < 2) {
                checksumSb.append("0");
            }
            checksumSb.append(hexStr);
        }
        return checksumSb.toString();
    }

    /**
     * Converts an array of characters representing hexadecimal values into an
     * array of bytes of those same values. The returned array will be half the
     * length of the passed array, as it takes two characters to represent any
     * given byte. An exception is thrown if the passed char array has an odd
     * number of elements.
     *
     * NOTE: This code is copied from jakarta-commons codec.
     */
    public static byte[] decodeHex(char[] data) throws BuildException {
        int l = data.length;

        if ((l & 0x01) != 0) {
            throw new BuildException("odd number of characters.");
        }

        byte[] out = new byte[l >> 1];

        for (int i = 0, j = 0; j < l; i++) {
            int f = Character.digit(data[j++], 16) << 4;
            f = f | Character.digit(data[j++], 16);
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }
}