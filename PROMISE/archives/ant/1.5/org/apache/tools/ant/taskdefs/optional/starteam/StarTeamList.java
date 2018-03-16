package org.apache.tools.ant.taskdefs.optional.starteam;

import com.starbase.starteam.File;
import com.starbase.starteam.Folder;
import com.starbase.starteam.Item;
import com.starbase.starteam.Status;
import com.starbase.starteam.View;
import com.starbase.starteam.ViewConfiguration;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Produces a listing of the contents of the StarTeam repository
 * at the specified view and StarTeamFolder.
 *
 * Created: Tue Dec 25 06:51:14 2001
 *
 * @author <a href="mailto:stevec@ignitesports.com">Steve Cohen</a>
 * @version 1.0
 *
 * @ant.task name="stlist" category="scm"
 */

public class StarTeamList extends TreeBasedTask {
    private boolean listUncontrolled = true;
    /**
     * List files, dates, and statuses as of this label; optional.
     * The label must exist in starteam or an exception will be thrown.  
     * If not specified, the most recent version of each file will be listed.
     *
     * @param label the label to be listed
     */
    public void setLabel(String label) {
        _setLabel(label);
    }

    /**
     * Override of base-class abstract function creates an
     * appropriately configured view for checkoutlists - either
     * the current view or a view from this.label.   
     *
     * @param raw the unconfigured <code>View</code>
     * @return the snapshot <code>View</code> appropriately configured.
     */
    protected View createSnapshotView(View raw) {

        int labelID = getLabelID(raw);

        if (labelID >= 0) {
            return new View(raw, ViewConfiguration.createFromLabel(labelID));
        } else {
            return new View(raw, ViewConfiguration.createTip());
        }
    }

    /**
     * Required base-class abstract function implementation is a no-op here.
     *
     * @exception BuildException not thrown in this implementation
     */
    protected void testPreconditions() throws BuildException {
    }

    /**
     * extenders should emit to the log an entry describing the parameters
     * that will be used by this operation.
     *
     * @param starteamrootFolder
     *               root folder in StarTeam for the operation
     * @param targetrootFolder
     *               root local folder for the operation (whether specified by the user or not.
     */
    protected void logOperationDescription(Folder starteamrootFolder, java.io.File targetrootFolder) {
        log((this.isRecursive() ? "Recursive" : "Non-recursive") + 
            " Listing of: " + starteamrootFolder.getFolderHierarchy());

        log("Listing against local folder" 
            + (null == getRootLocalFolder() ? " (default): " : ": ") 
            + targetrootFolder.getAbsolutePath(),
                    Project.MSG_INFO);
        logLabel();
        logIncludes();
        logExcludes();


    }
    /**
     * Implements base-class abstract function to perform the checkout
     * operation on the files in each folder of the tree.
     *
     * @param starteamFolder the StarTeam folder from which files to be
     *                       checked out
     * @param targetFolder the local mapping of rootStarteamFolder
     */
    protected void visit(Folder starteamFolder, java.io.File targetFolder)
            throws BuildException {
        try {
            if (null != getRootLocalFolder()) {
                starteamFolder.setAlternatePathFragment(
                    targetFolder.getAbsolutePath());

            }
            Folder[] subFolders = starteamFolder.getSubFolders();
            Item[] files = starteamFolder.getItems(getTypeNames().FILE);
            
            UnmatchedFileMap ufm = 
                new UnmatchedListingMap().init(
                    targetFolder.getAbsoluteFile(), starteamFolder);

            log("");
            log("Listing StarTeam folder " + 
                starteamFolder.getFolderHierarchy()); 
            log(" against local folder " + 
                targetFolder.getAbsolutePath());



            for (int i = 0; i < files.length; i++) {
                File eachFile = (File) files[i];
                String filename = eachFile.getName();
                java.io.File localFile =
                        new java.io.File(targetFolder, filename);

                ufm.removeControlledItem(localFile);

                if (!shouldProcess(filename)) {
                    continue;
                }

                list(eachFile, localFile);
            }


            for (int i = 0; i < subFolders.length; i++) {
                java.io.File targetSubfolder =
                        new java.io.File(targetFolder, subFolders[i].getName());
                ufm.removeControlledItem(targetSubfolder);
                if (isRecursive()) {
                    visit(subFolders[i], targetSubfolder);
                }
            }
            if (this.listUncontrolled) {
                ufm.processUncontrolledItems();
            }

        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private static final SimpleDateFormat SDF = 
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss zzz");

    protected void list(File reposFile, java.io.File localFile)
            throws IOException {
        StringBuffer b = new StringBuffer();
        int status = reposFile.getStatus();
        java.util.Date displayDate = null;
        if (status==Status.NEW) {
            displayDate = new java.util.Date(localFile.lastModified());
        } else {
            displayDate = reposFile.getModifiedTime().createDate();
        }
        b.append(pad(Status.name(status), 12)).append(' ');
        b.append(pad(getUserName(reposFile.getLocker()), 20))
                .append(' ')
                .append(SDF.format(displayDate))
                .append(rpad(String.valueOf(reposFile.getSize()), 9))
                .append(' ')
                .append(reposFile.getName());

        log(b.toString());
    }

    private static final String blankstr = blanks(30);

    private static String blanks(int len) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < len; i++) {
            b.append(' ');
        }
        return b.toString();
    }

    protected static String pad(String s, int padlen) {
        return (s + blankstr).substring(0, padlen);
    }

    protected static String rpad(String s, int padlen) {
        s = blankstr + s;
        return s.substring(s.length() - padlen);
    }

    /**
     * handles the list of uncontrolled items
     */
    private class UnmatchedListingMap extends UnmatchedFileMap {

        protected boolean isActive() {
            return StarTeamList.this.listUncontrolled;
        }
    
        /**
         * lists uncontrolled items from the local tree.  It is assumed
         * that this method will not be called until all the items in the
         * corresponding folder have been processed, and that the internal map
         * will contain only uncontrolled items.
         */
        void processUncontrolledItems() throws BuildException{
            if (this.isActive()) {
                Enumeration e = this.keys();
                
                while (e.hasMoreElements()) {
                    java.io.File local = (java.io.File) e.nextElement();
                    Item remoteItem = (Item) this.get(local);

                    if (local.isFile()) {
                        com.starbase.starteam.File remoteFile =
                            (com.starbase.starteam.File) remoteItem;
                        try {
                            list(remoteFile, local);
                        } catch (IOException ie) {
                            throw new BuildException("IOError in stlist",ie);
                        }
                    }
                }
                e = this.keys();
                while (e.hasMoreElements()) {
                    java.io.File local = (java.io.File) e.nextElement();
                    Item remoteItem = (Item) this.get(local);

                    if (local.isDirectory()) {
                        Folder folder = (Folder) remoteItem;
                        if (isRecursive()) {
                            log("Listing uncontrolled folder " 
                                + folder.getFolderHierarchy()
                                + " from " + local.getAbsoluteFile());
                            UnmatchedFileMap submap = 
                                new UnmatchedListingMap().init(local, folder);
                            submap.processUncontrolledItems();
                        }
                    }
                }
            }
        }


    }




