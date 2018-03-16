package org.apache.camel.util.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * An alternative to a {@link CountDownLatch} -- this implementation also supports incrementing
 * the latch count while counting down.  It can also be used to count up to 0 from a negative integer.
 */
public class CountingLatch {

    @SuppressWarnings("serial")
    private final class Sync extends AbstractQueuedSynchronizer {

        private Sync() {
            super();
        }

        int getCount() {
            return getState();
        }

        public int tryAcquireShared(int acquires) {
            return getState() == 0 ? 1 : -1;
        }

        public boolean tryReleaseShared(int delta) {
            for (;;) {
                int c = getState();
                int nextc = c + delta;
                if (compareAndSetState(c, nextc)) {
                    return nextc == 0;
                }
            }
        }
    }

    private final Sync sync;

    /**
     * Create a new counting latch (starting count is 0)
     */
    public CountingLatch() {
        super();
        this.sync = new Sync();
    }

    /**
     * Get the current count
     */
    public int getCount() {
        return sync.getCount();
    }

    /**
     * Increment the count with 1
     */
    public void increment() {
        sync.releaseShared(+1);
    }

    /**
     * Decrement the count with 1
     */
    public void decrement() {
        sync.releaseShared(-1);
    }

    /**
     * Await the latch reaching the count of 0
     *
     * @throws InterruptedException if the threads gets interrupted while waiting
     */
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * Wait for a given timeout while checking if the latch reached the count of 0
     *
     * @param timeout the value of the timeout
     * @param unit the unit in which the timeout is expressed
     * @return <code>true</code> if the latch has reached the count of 0 in the given time
     * @throws InterruptedException if the thread gets interrupted while waiting
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

}
