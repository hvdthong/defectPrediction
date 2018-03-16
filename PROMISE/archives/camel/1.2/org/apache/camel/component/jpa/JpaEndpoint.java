package org.apache.camel.component.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.ScheduledPollEndpoint;
import org.apache.camel.util.IntrospectionSupport;

import org.springframework.orm.jpa.JpaTemplate;

/**
 * @version $Revision: 572625 $
 */
public class JpaEndpoint extends ScheduledPollEndpoint<Exchange> {
    private EntityManagerFactory entityManagerFactory;
    private String persistenceUnit = "camel";
    private JpaTemplate template;
    private Expression<Exchange> producerExpression;
    private int maximumResults = -1;
    private Class<?> entityType;
    private Map entityManagerProperties;
    private boolean consumeDelete = true;
    private boolean consumeLockEntity = true;

    public JpaEndpoint(String uri, JpaComponent component) {
        super(uri, component);
        entityManagerFactory = component.getEntityManagerFactory();
    }

    public Producer<Exchange> createProducer() throws Exception {
        return new JpaProducer(this, getProducerExpression());
    }

    public Consumer<Exchange> createConsumer(Processor processor) throws Exception {
        JpaConsumer consumer = new JpaConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    @Override
    public void configureProperties(Map options) {
        super.configureProperties(options);
        Map emProperties = IntrospectionSupport.extractProperties(options, "emf.");
        if (emProperties != null) {
            setEntityManagerProperties(emProperties);
        }
    }

    public boolean isSingleton() {
        return false;
    }

    public JpaTemplate getTemplate() {
        if (template == null) {
            template = createTemplate();
        }
        return template;
    }

    public void setTemplate(JpaTemplate template) {
        this.template = template;
    }

    public Expression<Exchange> getProducerExpression() {
        if (producerExpression == null) {
            producerExpression = createProducerExpression();
        }
        return producerExpression;
    }

    public void setProducerExpression(Expression<Exchange> producerExpression) {
        this.producerExpression = producerExpression;
    }

    public int getMaximumResults() {
        return maximumResults;
    }

    public void setMaximumResults(int maximumResults) {
        this.maximumResults = maximumResults;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = createEntityManagerFactory();
        }
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Map getEntityManagerProperties() {
        if (entityManagerProperties == null) {
            entityManagerProperties = System.getProperties();
        }
        return entityManagerProperties;
    }

    public void setEntityManagerProperties(Map entityManagerProperties) {
        this.entityManagerProperties = entityManagerProperties;
    }

    public String getPersistenceUnit() {
        return persistenceUnit;
    }

    public void setPersistenceUnit(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    public boolean isConsumeDelete() {
        return consumeDelete;
    }

    public void setConsumeDelete(boolean consumeDelete) {
        this.consumeDelete = consumeDelete;
    }

    public boolean isConsumeLockEntity() {
        return consumeLockEntity;
    }

    public void setConsumeLockEntity(boolean consumeLockEntity) {
        this.consumeLockEntity = consumeLockEntity;
    }

    protected JpaTemplate createTemplate() {
        return new JpaTemplate(getEntityManagerFactory());
    }

    protected EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(persistenceUnit, getEntityManagerProperties());
    }

    protected EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    protected TransactionStrategy createTransactionStrategy() {
        EntityManagerFactory emf = getEntityManagerFactory();
        return JpaTemplateTransactionStrategy.newInstance(emf, getTemplate());
    }

    protected Expression<Exchange> createProducerExpression() {
        final Class<?> type = getEntityType();
        if (type == null) {
            return ExpressionBuilder.bodyExpression();
        } else {
            return new Expression<Exchange>() {
                public Object evaluate(Exchange exchange) {
                    Object answer = exchange.getIn().getBody(type);
                    if (answer == null) {
                        Object defaultValue = exchange.getIn().getBody();
                        if (defaultValue != null) {
                            throw new NoTypeConversionAvailableException(defaultValue, type);
                        }

                        answer = exchange.getContext().getInjector().newInstance(type);
                    }
                    return answer;
                }
            };
        }
    }
}
