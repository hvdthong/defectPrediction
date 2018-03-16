package org.apache.ivy.plugins.repository;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.event.EventListenerList;

import org.apache.ivy.core.module.descriptor.Artifact;

public abstract class AbstractRepository implements Repository {
    private EventListenerList listeners = new EventListenerList();

    private String name;

    private TransferEvent evt;

    public void addTransferListener(TransferListener listener) {
        listeners.add(TransferListener.class, listener);
    }

    public void removeTransferListener(TransferListener listener) {
        listeners.remove(TransferListener.class, listener);
    }

    public boolean hasTransferListener(TransferListener listener) {
        return Arrays.asList(listeners.getListeners(TransferListener.class)).contains(listener);
    }

    protected void fireTransferInitiated(Resource res, int requestType) {
        evt = new TransferEvent(this, res, TransferEvent.TRANSFER_INITIATED, requestType);
        fireTransferEvent(evt);
    }

    protected void fireTransferStarted() {
        evt.setEventType(TransferEvent.TRANSFER_STARTED);
        fireTransferEvent(evt);
    }

    protected void fireTransferStarted(long totalLength) {
        evt.setEventType(TransferEvent.TRANSFER_STARTED);
        evt.setTotalLength(totalLength);
        evt.setTotalLengthSet(true);
        fireTransferEvent(evt);
    }

    protected void fireTransferProgress(long length) {
        evt.setEventType(TransferEvent.TRANSFER_PROGRESS);
        evt.setLength(length);
        if (!evt.isTotalLengthSet()) {
            evt.setTotalLength(evt.getTotalLength() + length);
        }
        fireTransferEvent(evt);
    }

    protected void fireTransferCompleted() {
        evt.setEventType(TransferEvent.TRANSFER_COMPLETED);
        if (evt.getTotalLength() > 0 && !evt.isTotalLengthSet()) {
            evt.setTotalLengthSet(true);
        }
        fireTransferEvent(evt);
    }

    protected void fireTransferCompleted(long totalLength) {
        evt.setEventType(TransferEvent.TRANSFER_COMPLETED);
        evt.setTotalLength(totalLength);
        evt.setTotalLengthSet(true);
        fireTransferEvent(evt);
    }

    protected void fireTransferError() {
        evt.setEventType(TransferEvent.TRANSFER_ERROR);
        fireTransferEvent(evt);
    }

    protected void fireTransferError(Exception ex) {
        evt.setEventType(TransferEvent.TRANSFER_ERROR);
        evt.setException(ex);
        fireTransferEvent(evt);
    }

    protected void fireTransferEvent(TransferEvent evt) {
        Object[] listeners = this.listeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TransferListener.class) {
                ((TransferListener) listeners[i + 1]).transferProgress(evt);
            }
        }
    }

    public String getFileSeparator() {
        return "/";
    }

    public String standardize(String source) {
        return source.replace('\\', '/');
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return getName();
    }

    public void put(Artifact artifact, File source, String destination, boolean overwrite)
            throws IOException {
        put(source, destination, overwrite);
    }

    protected void put(File source, String destination, boolean overwrite) throws IOException {
        throw new UnsupportedOperationException("put in not supported by " + getName());
    }
}
