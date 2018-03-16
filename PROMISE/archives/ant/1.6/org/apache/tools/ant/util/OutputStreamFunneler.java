package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Manages a set of <CODE>OutputStream</CODE>s to
 * write to a single underlying stream, which is
 * closed only when the last &quot;funnel&quot;
 * has been closed.
 */
public class OutputStreamFunneler {

    /**
     * Default timeout.
     * @see #setTimeout(long)
     */
    public static final long DEFAULT_TIMEOUT_MILLIS = 1000;

    private final class Funnel extends OutputStream {
        private boolean closed = false;

        private Funnel() {
            synchronized (OutputStreamFunneler.this) {
                ++count;
            }
        }

        public void flush() throws IOException {
            synchronized (OutputStreamFunneler.this) {
                dieIfClosed();
                out.flush();
            }
        }

        public void write(int b) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                dieIfClosed();
                out.write(b);
            }
        }

        public void write(byte[] b) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                dieIfClosed();
                out.write(b);
            }
        }

        public void write(byte[] b, int off, int len) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                dieIfClosed();
                out.write(b, off, len);
            }
        }

        public void close() throws IOException {
            release(this);
        }
    }

    private OutputStream out;
    private int count = 0;
    private boolean closed;
    private long timeoutMillis;

    /**
     * Create a new <CODE>OutputStreamFunneler</CODE> for
     * the specified <CODE>OutputStream</CODE>.
     * @param out   <CODE>OutputStream</CODE>.
     */
    public OutputStreamFunneler(OutputStream out) {
        this(out, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Create a new <CODE>OutputStreamFunneler</CODE> for
     * the specified <CODE>OutputStream</CODE>, with the
     * specified timeout value.
     * @param out             <CODE>OutputStream</CODE>.
     * @param timeoutMillis   <CODE>long</CODE>.
     * @see #setTimeout(long)
     */
    public OutputStreamFunneler(OutputStream out, long timeoutMillis) {
        if (out == null) {
            throw new IllegalArgumentException(
                "OutputStreamFunneler.<init>:  out == null");
        }
        this.out = out;
        setTimeout(timeoutMillis);
    }

    /**
     * Set the timeout for this <CODE>OutputStreamFunneler</CODE>.
     * This is the maximum time that may elapse between the closure
     * of the last &quot;funnel&quot; and the next call to
     * <CODE>getOutputStream()</CODE> without closing the
     * underlying stream.
     * @param timeoutMillis   <CODE>long</CODE> timeout value.
     */
    public synchronized void setTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * Get a &quot;funnel&quot; <CODE>OutputStream</CODE> instance to
     * write to this <CODE>OutputStreamFunneler</CODE>'s underlying
     * <CODE>OutputStream</CODE>.
     * @return <code>OutputStream</code>.
     * @throws IOException if unable to create the funnel.
     */
    public synchronized OutputStream getFunnelInstance()
        throws IOException {
        dieIfClosed();
        try {
            return new Funnel();
        } finally {
            notifyAll();
        }
    }

    private synchronized void release(Funnel funnel) throws IOException {
        if (!funnel.closed) {
            try {
                if (timeoutMillis > 0) {
                    try {
                        wait(timeoutMillis);
                    } catch (InterruptedException eyeEx) {
                    }
                }
                if (--count == 0) {
                    close();
                }
            } finally {
                funnel.closed = true;
            }
        }
   }

    private synchronized void close() throws IOException {
        try {
            dieIfClosed();
            out.close();
        } finally {
            closed = true;
        }
    }

    private synchronized void dieIfClosed() throws IOException {
        if (closed) {
            throw new IOException("The funneled OutputStream has been closed.");
        }
    }

}
