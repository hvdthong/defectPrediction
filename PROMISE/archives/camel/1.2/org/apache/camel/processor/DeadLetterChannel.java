package org.apache.camel.processor;

import java.util.concurrent.RejectedExecutionException;

import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.converter.AsyncProcessorTypeConverter;
import org.apache.camel.model.ExceptionType;
import org.apache.camel.util.AsyncProcessorHelper;
import org.apache.camel.util.ServiceHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements a <a
 * Channel</a> after attempting to redeliver the message using the
 * {@link RedeliveryPolicy}
 * 
 * @version $Revision: 582309 $
 */
public class DeadLetterChannel extends ErrorHandlerSupport implements AsyncProcessor {
    public static final String REDELIVERY_COUNTER = "org.apache.camel.RedeliveryCounter";
    public static final String REDELIVERED = "org.apache.camel.Redelivered";

    private class RedeliveryData {
        int redeliveryCounter;
        long redeliveryDelay;
        boolean sync = true;

        RedeliveryPolicy currentRedeliveryPolicy = redeliveryPolicy;
        Processor failureProcessor = deadLetter;
    }

    private static final transient Log LOG = LogFactory.getLog(DeadLetterChannel.class);
    private static final String FAILURE_HANDLED_PROPERTY = DeadLetterChannel.class.getName()+".FAILURE_HANDLED";
    private Processor output;
    private Processor deadLetter;
    private AsyncProcessor outputAsync;
    private RedeliveryPolicy redeliveryPolicy;
    private Logger logger;

    public DeadLetterChannel(Processor output, Processor deadLetter) {
        this(output, deadLetter, new RedeliveryPolicy(), DeadLetterChannel.createDefaultLogger());
    }

    public DeadLetterChannel(Processor output, Processor deadLetter, RedeliveryPolicy redeliveryPolicy, Logger logger) {
        this.deadLetter = deadLetter;
        this.output = output;        
        this.outputAsync = AsyncProcessorTypeConverter.convert(output);
        
        this.redeliveryPolicy = redeliveryPolicy;
        this.logger = logger;
    }

    public static <E extends Exchange> Logger createDefaultLogger() {
        return new Logger(LOG, LoggingLevel.ERROR);
    }

    @Override
    public String toString() {
        return "DeadLetterChannel[" + output + ", " + deadLetter + ", " + redeliveryPolicy + "]";
    }

    public boolean process(Exchange exchange, final AsyncCallback callback) {
        return process(exchange, callback, new RedeliveryData());
    }

    public boolean process(final Exchange exchange, final AsyncCallback callback, final RedeliveryData data) {

        while (true) {
            
            if (!isRunAllowed()) {
                if (exchange.getException() == null) {
                    exchange.setException(new RejectedExecutionException());
                }
                callback.done(data.sync);
                return data.sync;
            }
            
            if (exchange.getException() != null) {
                Throwable e = exchange.getException();
                
                logger.log("On delivery attempt: " + data.redeliveryCounter + " caught: " + e, e);
                data.redeliveryCounter = incrementRedeliveryCounter(exchange, e);

                ExceptionType exceptionPolicy = getExceptionPolicy(exchange, e);
                if (exceptionPolicy != null) {
                    data.currentRedeliveryPolicy = exceptionPolicy.createRedeliveryPolicy(data.currentRedeliveryPolicy);
                    Processor processor = exceptionPolicy.getErrorHandler();
                    if (processor != null) {
                        data.failureProcessor = processor;
                    }
                }
            }

            if (!data.currentRedeliveryPolicy.shouldRedeliver(data.redeliveryCounter)) {
                setFailureHandled(exchange, true);
                AsyncProcessor afp = AsyncProcessorTypeConverter.convert(data.failureProcessor);
                return afp.process(exchange, new AsyncCallback() {
                    public void done(boolean sync) {
                        callback.done(data.sync);
                    }
                });
            }

            if (data.redeliveryCounter > 0) {
                data.redeliveryDelay = data.currentRedeliveryPolicy.getRedeliveryDelay(data.redeliveryDelay);
                sleep(data.redeliveryDelay);
            }
            
            exchange.setException(null);
            boolean sync = outputAsync.process(exchange, new AsyncCallback() {
                public void done(boolean sync) {
                    if (sync) {
                        return;
                    }
                    data.sync = false;
                    if (exchange.getException() != null) {
                        process(exchange, callback, data);
                    } else {
                        callback.done(sync);
                    }
                }
            });
            if (!sync) {
                return false;
            }
            if (exchange.getException() == null || isFailureHandled(exchange)) {
                callback.done(true);
                return true;
            }
        }

    }
    
    public static boolean isFailureHandled(Exchange exchange) {
        Boolean rc = exchange.getProperty(FAILURE_HANDLED_PROPERTY, Boolean.class);
        return rc == null ? false : rc;
    }

    public static void setFailureHandled(Exchange exchange, boolean b) {
        exchange.setProperty(FAILURE_HANDLED_PROPERTY, b ? Boolean.TRUE : Boolean.FALSE );
    }

    public void process(Exchange exchange) throws Exception {
        AsyncProcessorHelper.process(this, exchange);
    }


    /**
     * Returns the output processor
     */
    public Processor getOutput() {
        return output;
    }

    /**
     * Returns the dead letter that message exchanges will be sent to if the
     * redelivery attempts fail
     */
    public Processor getDeadLetter() {
        return deadLetter;
    }

    public RedeliveryPolicy getRedeliveryPolicy() {
        return redeliveryPolicy;
    }

    /**
     * Sets the redelivery policy
     */
    public void setRedeliveryPolicy(RedeliveryPolicy redeliveryPolicy) {
        this.redeliveryPolicy = redeliveryPolicy;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Sets the logger strategy; which {@link Log} to use and which
     * {@link LoggingLevel} to use
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    /**
     * Increments the redelivery counter and adds the redelivered flag if the
     * message has been redelivered
     */
    protected int incrementRedeliveryCounter(Exchange exchange, Throwable e) {
        Message in = exchange.getIn();
        Integer counter = in.getHeader(REDELIVERY_COUNTER, Integer.class);
        int next = 1;
        if (counter != null) {
            next = counter + 1;
        }
        in.setHeader(REDELIVERY_COUNTER, next);
        in.setHeader(REDELIVERED, true);
        exchange.setException(e);
        return next;
    }

    protected void sleep(long redeliveryDelay) {
        if (redeliveryDelay > 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sleeping for: " + redeliveryDelay + " until attempting redelivery");
            }
            try {
                Thread.sleep(redeliveryDelay);
            } catch (InterruptedException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Thread interupted: " + e, e);
                }
            }
        }
    }

    protected void doStart() throws Exception {
        ServiceHelper.startServices(output, deadLetter);
    }

    protected void doStop() throws Exception {
        ServiceHelper.stopServices(deadLetter, output);
    }

}
