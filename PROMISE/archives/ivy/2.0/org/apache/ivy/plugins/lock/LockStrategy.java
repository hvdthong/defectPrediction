package org.apache.ivy.plugins.lock;

import java.io.File;

import org.apache.ivy.core.module.descriptor.Artifact;

/**
 * A lock strategy determines when and how lock should be performed when downloading data to a
 * cache.
 * <p>
 * Note that some implementations may actually choose to NOT perform locking, when no lock is
 * necessary (cache not shared). Some other implementations may choose to lock the cache for the
 * download of a whole module (not possible yet), or at the artifact level.
 * <p>
 * </p>
 * The lock methods should return true when the lock is either actually acquired or not performed by
 * the strategy.
 * </p>
 * <p>
 * Locking used in the locking strategy must support reentrant lock. Reentrant locking should be
 * performed for the whole strategy.
 * </p>
 */
public interface LockStrategy {

    /**
     * Returns the name of the strategy
     * @return the name of the strategy
     */
    String getName();

    /**
     * Performs a lock before downloading the given {@link Artifact} to the given file.
     * 
     * @param artifact
     *            the artifact about to be downloaded
     * @param artifactFileToDownload
     *            the file where the artifact will be downloaded
     * @return true if the artifact is locked, false otherwise
     * @throws InterruptedException
     *             if the thread is interrupted while waiting to acquire the lock
     */
    boolean lockArtifact(Artifact artifact, File artifactFileToDownload) 
        throws InterruptedException;

    /**
     * Release the lock acquired for an artifact download.
     * 
     * @param artifact
     *            the artifact for which the lock was acquired
     * @param artifactFileToDownload
     *            the file where the artifact is supposed to have been downloaded
     */
    void unlockArtifact(Artifact artifact, File artifactFileToDownload);

}
