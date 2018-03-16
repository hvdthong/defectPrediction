package org.apache.camel.component.file.remote;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

public class FtpProducer extends RemoteFileProducer<RemoteFileExchange> {
    FtpEndpoint endpoint;
    private final FTPClient client;

    public FtpProducer(FtpEndpoint endpoint, FTPClient client) {
        super(endpoint);
        this.endpoint = endpoint;
        this.client = client;
    }

    public void process(Exchange exchange) throws Exception {
        process(endpoint.toExchangeType(exchange));
    }

    public void process(RemoteFileExchange exchange) throws Exception {
        final String fileName;
        InputStream payload = exchange.getIn().getBody(InputStream.class);
        final String endpointFile = endpoint.getConfiguration().getFile();
        if (endpointFile == null) {
            throw new NullPointerException("Null Endpoint File");
        }
        else {
            if (endpoint.getConfiguration().isDirectory()) {
                fileName = endpointFile + "/" + exchange.getIn().getMessageId();
            }
            else {
                fileName = endpointFile;
            }
        }
        buildDirectory(client, fileName.substring(0, fileName.lastIndexOf('/')));
        final boolean success = client.storeFile(fileName, payload);
        if (success) {

        }
        else {
            throw new RuntimeCamelException("error sending file");
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        client.disconnect();
        super.doStop();
    }

    protected static boolean buildDirectory(FTPClient ftpClient, String dirName) throws IOException {
        boolean atLeastOneSuccess = false;
        final StringBuilder sb = new StringBuilder(dirName.length());
        final String[] dirs = dirName.split("\\/");
        for (String dir : dirs) {
            sb.append('/').append(dir);
            final boolean success = ftpClient.makeDirectory(sb.toString());
            System.out.println(sb.toString() + " = " + success);
            if (!atLeastOneSuccess && success) {
                atLeastOneSuccess = true;
            }
        }
        return atLeastOneSuccess;
    }
}
