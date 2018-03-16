package org.apache.camel.component.cxf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.CamelException;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.cxf.feature.MessageDataFormatFeature;
import org.apache.camel.component.cxf.feature.PayLoadDataFormatFeature;
import org.apache.camel.component.cxf.invoker.CxfClient;
import org.apache.camel.component.cxf.invoker.CxfClientFactoryBean;
import org.apache.camel.component.cxf.invoker.InvokingContext;
import org.apache.camel.component.cxf.invoker.InvokingContextFactory;
import org.apache.camel.component.cxf.spring.CxfEndpointBean;
import org.apache.camel.component.cxf.util.CxfEndpointUtils;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.BindingOperationInfo;

/**
 * Sends messages from Camel into the CXF endpoint
 *
 * @version $Revision: 670454 $
 */
public class CxfProducer extends DefaultProducer<CxfExchange> {
    private CxfEndpoint endpoint;
    private Client client;
    private DataFormat dataFormat;

    public CxfProducer(CxfEndpoint endpoint) throws CamelException {
        super(endpoint);
        this.endpoint = endpoint;
        dataFormat = CxfEndpointUtils.getDataFormat(endpoint);
        if (dataFormat.equals(DataFormat.POJO)) {
            client = createClientFromClientFactoryBean(null);
        } else {
            client = createClientForStreamMessage();
        }
    }

    private Client createClientForStreamMessage() throws CamelException {
        CxfClientFactoryBean cfb = new CxfClientFactoryBean();
        Class serviceClass = null;
        if (endpoint.isSpringContextEndpoint()) {
            CxfEndpointBean cxfEndpointBean = endpoint.getCxfEndpointBean();
            serviceClass = cxfEndpointBean.getServiceClass();
        } else {
            if (endpoint.getServiceClass() == null) {
                throw new CamelException("serviceClass setting missing from CXF endpoint configuration");
            }
            try {
                serviceClass = ClassLoaderUtils.loadClass(endpoint.getServiceClass(), this.getClass());
            } catch (ClassNotFoundException e) {
                throw new CamelException(e);
            }
        }

        boolean jsr181Enabled = CxfEndpointUtils.hasWebServiceAnnotation(serviceClass);
        cfb.setJSR181Enabled(jsr181Enabled);

        dataFormat = CxfEndpointUtils.getDataFormat(endpoint);
        List<AbstractFeature> features = new ArrayList<AbstractFeature>();
        if (dataFormat.equals(DataFormat.MESSAGE)) {
            features.add(new MessageDataFormatFeature());
        } else if (dataFormat.equals(DataFormat.PAYLOAD)) {
            features.add(new PayLoadDataFormatFeature());
        }
        cfb.setFeatures(features);

        return createClientFromClientFactoryBean(cfb);
    }

    private Client createClientFromClientFactoryBean(ClientFactoryBean cfb) throws CamelException {
        Bus bus = null;
        if (endpoint.getApplicationContext() != null) {
            SpringBusFactory bf = new SpringBusFactory(endpoint.getApplicationContext());
            bus = bf.createBus();
            if (CxfEndpointUtils.getSetDefaultBus(endpoint)) {
                BusFactory.setDefaultBus(bus);
            }
        } else {
            bus = BusFactory.getDefaultBus();
        }
        if (endpoint.isSpringContextEndpoint()) {
            CxfEndpointBean cxfEndpointBean = endpoint.getCxfEndpointBean();
            if (cfb == null) {
                cfb = CxfEndpointUtils.getClientFactoryBean(cxfEndpointBean.getServiceClass());
            }
            endpoint.configure(cfb);

            if (null != endpoint.getServiceClass()) {
                try {
                    Class serviceClass = ClassLoaderUtils.loadClass(endpoint.getServiceClass(), this
                        .getClass());
                    if (cfb == null) {
                        cfb = CxfEndpointUtils.getClientFactoryBean(serviceClass);
                    }
                    cfb.setAddress(endpoint.getAddress());
                    if (null != endpoint.getServiceClass()) {
                        cfb.setServiceClass(ObjectHelper.loadClass(endpoint.getServiceClass()));
                    }
                    if (null != endpoint.getWsdlURL()) {
                        cfb.setWsdlURL(endpoint.getWsdlURL());
                    }
                } catch (ClassNotFoundException e) {
                    throw new CamelException(e);
                }
                if (cfb == null) {
                    cfb = new ClientFactoryBean();
                }
                if (null != endpoint.getWsdlURL()) {
                    cfb.setWsdlURL(endpoint.getWsdlURL());
                } else {
                    throw new CamelException("Not enough information to create a CXF endpoint. (Provide WSDL url or service class name.)");
                }
            }
            if (endpoint.getServiceName() != null) {
                cfb.setServiceName(CxfEndpointUtils.getServiceName(endpoint));
            }
            if (endpoint.getPortName() != null) {
                cfb.setEndpointName(CxfEndpointUtils.getPortName(endpoint));

            }
            if (endpoint.getWsdlURL() != null) {
                cfb.setWsdlURL(endpoint.getWsdlURL());
            }
        }
        cfb.setBus(bus);
        return cfb.create();
    }

    public void process(Exchange exchange) {
        CxfExchange cxfExchange = endpoint.createExchange(exchange);
        process(cxfExchange);
        exchange.copyFrom(cxfExchange);

    }

    public void process(CxfExchange exchange) {
        Message inMessage = CxfBinding.createCxfMessage(exchange);
        exchange.setProperty(CxfExchange.DATA_FORMAT, dataFormat);
        try {
            if (dataFormat.equals(DataFormat.POJO)) {
                List parameters = inMessage.getContent(List.class);
                if (parameters == null) {
                    parameters = new ArrayList();
                }
                String operationName = (String)inMessage.get(CxfConstants.OPERATION_NAME);
                String operationNameSpace = (String)inMessage.get(CxfConstants.OPERATION_NAMESPACE);
                Map<String, Object> context = new HashMap<String, Object>();
                Map<String, Object> responseContext = CxfBinding.propogateContext(inMessage, context);
                Message response = new MessageImpl();
                if (operationName != null) {
                    try {
                        Object[] result = null;
                        result = invokeClient(operationNameSpace, operationName, parameters, context);
                        response.setContent(Object[].class, result);
                        CxfBinding.storeCXfResponseContext(response, responseContext);
                        CxfBinding.storeCxfResponse(exchange, response);
                    } catch (Exception ex) {
                        response.setContent(Exception.class, ex);
                        CxfBinding.storeCxfFault(exchange, response);
                    }
                } else {
                    throw new RuntimeCamelException("Can't find the operation name in the message!");
                }
            } else {
                org.apache.cxf.message.Exchange ex = exchange.getExchange();
                if (ex == null) {
                    ex = (org.apache.cxf.message.Exchange)exchange.getProperty(CxfConstants.CXF_EXCHANGE);
                    exchange.setExchange(ex);
                }
                if (ex == null) {
                    ex = new ExchangeImpl();
                    exchange.setExchange(ex);
                }
                assert ex != null;
                InvokingContext invokingContext = ex.get(InvokingContext.class);
                if (invokingContext == null) {
                    invokingContext = InvokingContextFactory.createContext(dataFormat);
                    ex.put(InvokingContext.class, invokingContext);
                }
                Map<Class, Object> params = invokingContext.getRequestContent(inMessage);
                CxfClient cxfClient = (CxfClient)client;
                BindingOperationInfo boi = ex.get(BindingOperationInfo.class);
                Message response = null;
                if (boi == null) {
                    response = new MessageImpl();
                } else {
                    Endpoint ep = ex.get(Endpoint.class);
                    response = ep.getBinding().createMessage();
                }
                response.setExchange(ex);
                Map<String, Object> context = new HashMap<String, Object>();
                Map<String, Object> responseContext = CxfBinding.propogateContext(inMessage, context);
                try {
                    Object result = cxfClient.dispatch(params, context, ex);
                    ex.setOutMessage(response);
                    invokingContext.setResponseContent(response, result);
                    CxfBinding.storeCXfResponseContext(response, responseContext);
                    CxfBinding.storeCxfResponse(exchange, response);
                } catch (Exception e) {
                    response.setContent(Exception.class, e);
                    CxfBinding.storeCxfFault(exchange, response);
                }
            }
        } catch (Exception e) {
            throw new RuntimeCamelException(e);
        }

    }


    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

    private Object[] invokeClient(String operationNameSpace, String operationName, List parameters, Map<String, Object> context) throws Exception {

        QName operationQName = null;
        if (operationNameSpace == null) {
            operationQName = new QName(client.getEndpoint().getService().getName().getNamespaceURI(), operationName);
        } else {
            operationQName = new QName(operationNameSpace, operationName);
        }
        BindingOperationInfo op = client.getEndpoint().getEndpointInfo().getBinding().getOperation(operationQName);
        if (op == null) {
            throw new RuntimeCamelException("No operation found in the CXF client, the operation is " + operationQName);
        }
        if (!endpoint.isWrapped()) {
            if (op.isUnwrappedCapable()) {
                op = op.getUnwrappedOperation();
            }
        }
        Object[] result = client.invoke(op, parameters.toArray(), context);

        return result;
    }

}
