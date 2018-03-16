package org.apache.camel.spring.spi;

import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.Exchange;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.camel.spi.Policy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Wraps the processor in a Spring transaction
 *
 * @version $Revision: 1.1 $
 */
public class SpringTransactionPolicy<E> implements Policy<E> {
    private static final transient Log log = LogFactory.getLog(SpringTransactionPolicy.class);

    private TransactionTemplate template;

    public SpringTransactionPolicy() {
    }

    public SpringTransactionPolicy(TransactionTemplate template) {
        this.template = template;
    }

    public Processor wrap(Processor processor) {
        final TransactionTemplate transactionTemplate = getTemplate();
        if (transactionTemplate == null) {
            log.warn("No TransactionTemplate available so transactions will not be enabled!");
            return processor;
        }

        return new DelegateProcessor(processor) {

            public void process(final Exchange exchange) {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        try {
							processNext(exchange);
						} catch (Exception e) {
							throw new RuntimeCamelException(e);
						}
                    }
                });
            }

            @Override
            public String toString() {
                return "SpringTransactionPolicy:"+propagationBehaviorToString(transactionTemplate.getPropagationBehavior())+"[" + getNext() + "]";
            }

			private String propagationBehaviorToString(int propagationBehavior) {
				switch( propagationBehavior ) {
				case TransactionDefinition.PROPAGATION_MANDATORY:
					return "PROPAGATION_MANDATORY";
				case TransactionDefinition.PROPAGATION_NESTED:
					return "PROPAGATION_NESTED";
				case TransactionDefinition.PROPAGATION_NEVER:
					return "PROPAGATION_NEVER";
				case TransactionDefinition.PROPAGATION_NOT_SUPPORTED:
					return "PROPAGATION_NOT_SUPPORTED";
				case TransactionDefinition.PROPAGATION_REQUIRED:
					return "PROPAGATION_REQUIRED";
				case TransactionDefinition.PROPAGATION_REQUIRES_NEW:
					return "PROPAGATION_REQUIRES_NEW";
				case TransactionDefinition.PROPAGATION_SUPPORTS:
					return "PROPAGATION_SUPPORTS";
				}
				return "UNKOWN";
			}
        };
    }

    public TransactionTemplate getTemplate() {
        return template;
    }

    public void setTemplate(TransactionTemplate template) {
        this.template = template;
    }
}
