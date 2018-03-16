package org.apache.camel.processor;

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;

/**
 * A logger for logging message throughput.
 *  
 * @version $Revision: 659760 $
 */
public class ThroughputLogger extends Logger {
    private int groupSize = 100;
    private long startTime;
    private long groupStartTime;
    private AtomicInteger receivedCounter = new AtomicInteger();
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private String action = "Received";
    private String logMessage;

    public ThroughputLogger() {
    }

    public ThroughputLogger(Log log) {
        super(log);
    }

    public ThroughputLogger(Log log, LoggingLevel level) {
        super(log, level);
    }

    public ThroughputLogger(String logName) {
        super(logName);
    }

    public ThroughputLogger(String logName, LoggingLevel level) {
        super(logName, level);
    }

    public ThroughputLogger(String logName, LoggingLevel level, int groupSize) {
        super(logName, level);
        setGroupSize(groupSize);
    }

    public ThroughputLogger(String logName, int groupSize) {
        super(logName);
        setGroupSize(groupSize);
    }

    public ThroughputLogger(int groupSize) {
        setGroupSize(groupSize);
    }

    @Override
    public void process(Exchange exchange) {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        int receivedCount = receivedCounter.incrementAndGet();
        if (receivedCount % groupSize == 0) {
            logMessage = createLogMessage(exchange, receivedCount);
            super.process(exchange);
        }
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        if (groupSize == 0) {
            throw new IllegalArgumentException("groupSize cannot be zero!");
        }
        this.groupSize = groupSize;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    protected Object logMessage(Exchange exchange) {
        return logMessage;
    }

    protected String createLogMessage(Exchange exchange, int receivedCount) {
        long time = System.currentTimeMillis();
        if (groupStartTime == 0) {
            groupStartTime = startTime;
        }

        double rate = messagesPerSecond(groupSize, groupStartTime, time);
        double average = messagesPerSecond(receivedCount, startTime, time);

        groupStartTime = time;

        return getAction() + ": " + receivedCount + " messages so far. Last group took: " + (time - groupStartTime)
                + " millis which is: " + numberFormat.format(rate)
                + " messages per second. average: " + numberFormat.format(average);
    }

    protected double messagesPerSecond(long messageCount, long startTime, long endTime) {
        double rate = messageCount * 1000.0;
        rate /= endTime - startTime;
        return rate;
    }
}