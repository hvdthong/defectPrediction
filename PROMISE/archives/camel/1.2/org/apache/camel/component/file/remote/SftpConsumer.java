package org.apache.camel.component.file.remote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import com.jcraft.jsch.ChannelSftp;

import org.apache.camel.Processor;
import org.apache.camel.component.file.FileComponent;

public class SftpConsumer extends RemoteFileConsumer<RemoteFileExchange> {
    private boolean recursive = true;
    private String regexPattern = "";
    private long lastPollTime;
    private final SftpEndpoint endpoint;
    private ChannelSftp channel;
    private boolean setNames = false;

    public SftpConsumer(SftpEndpoint endpoint, Processor processor, ChannelSftp channel) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.channel = channel;
    }

    public SftpConsumer(SftpEndpoint endpoint, Processor processor, ChannelSftp channel, ScheduledExecutorService executor) {
        super(endpoint, processor, executor);
        this.endpoint = endpoint;
        this.channel = channel;
    }

    protected void poll() throws Exception {
        final String fileName = endpoint.getConfiguration().getFile();
        if (endpoint.getConfiguration().isDirectory()) {
            pollDirectory(fileName);
        } else {
            channel.cd(fileName.substring(0, fileName.lastIndexOf('/')));
            final ChannelSftp.LsEntry file = (ChannelSftp.LsEntry)channel.ls(fileName.substring(fileName.lastIndexOf('/') + 1)).get(0);
            pollFile(file);
        }
        lastPollTime = System.currentTimeMillis();
    }

    protected void pollDirectory(String dir) throws Exception {
        channel.cd(dir);
        for (ChannelSftp.LsEntry sftpFile : (ChannelSftp.LsEntry[])channel.ls(".").toArray(new ChannelSftp.LsEntry[] {})) {
            if (sftpFile.getFilename().startsWith(".")) {
            } else if (sftpFile.getAttrs().isDir()) {
                if (isRecursive()) {
                    pollDirectory(getFullFileName(sftpFile));
                }
            } else {
                pollFile(sftpFile);
            }
        }
    }

    protected String getFullFileName(ChannelSftp.LsEntry sftpFile) throws IOException {
        return channel.pwd() + "/" + sftpFile.getFilename();
    }

    private void pollFile(ChannelSftp.LsEntry sftpFile) throws Exception {
            if (isMatched(sftpFile)) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                channel.get(sftpFile.getFilename(), byteArrayOutputStream);
                RemoteFileExchange exchange = endpoint.createExchange(getFullFileName(sftpFile), byteArrayOutputStream);

                if (isSetNames()) {
                    String relativePath = getFullFileName(sftpFile).substring(endpoint.getConfiguration().getFile().length());
                    if (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    exchange.getIn().setHeader(FileComponent.HEADER_FILE_NAME, relativePath);
                }

                getProcessor().process(exchange);
            }
        }
    }

    protected boolean isMatched(ChannelSftp.LsEntry sftpFile) {
        boolean result = true;
        if (regexPattern != null && regexPattern.length() > 0) {
            result = sftpFile.getFilename().matches(getRegexPattern());
        }
        return result;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public long getLastPollTime() {
        return lastPollTime;
    }

    public void setLastPollTime(long lastPollTime) {
        this.lastPollTime = lastPollTime;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public boolean isSetNames() {
        return setNames;
    }

    public void setSetNames(boolean setNames) {
        this.setNames = setNames;
    }
}
