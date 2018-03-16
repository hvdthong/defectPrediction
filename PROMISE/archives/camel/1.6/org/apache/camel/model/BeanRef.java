package org.apache.camel.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.camel.Processor;
import org.apache.camel.component.bean.BeanProcessor;
import org.apache.camel.component.bean.RegistryBean;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.util.CamelContextHelper;
import org.apache.camel.util.ObjectHelper;

/**
 * Represents an XML &lt;bean/&gt; element
 *
 * @version $Revision: 671918 $
 */
@XmlRootElement(name = "bean")
@XmlAccessorType(XmlAccessType.FIELD)
public class BeanRef extends OutputType<ProcessorType> {
    @XmlAttribute(required = false)
    private String ref;
    @XmlAttribute(required = false)
    private String method;
    @XmlAttribute(required = false)
    private Class beanType;
    @XmlTransient
    private Object bean;

    public BeanRef() {
    }

    public BeanRef(String ref) {
        this.ref = ref;
    }

    public BeanRef(String ref, String method) {
        this.ref = ref;
        this.method = method;
    }

    @Override
    public String toString() {
        return "Bean[" + getLabel() + "]";
    }

    @Override
    public String getShortName() {
        return "bean";
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class getBeanType() {
        return beanType;
    }

    public void setBeanType(Class beanType) {
        this.beanType = beanType;
    }

    @Override
    public Processor createProcessor(RouteContext routeContext) {
        BeanProcessor answer;
        if (ref != null) {
            answer = new BeanProcessor(new RegistryBean(routeContext.getCamelContext(), ref));
        } else {
            if (bean == null) {
                ObjectHelper.notNull(beanType, "bean, ref or beanType");
                bean = CamelContextHelper.newInstance(routeContext.getCamelContext(), beanType);
            }
            answer = new BeanProcessor(bean, routeContext.getCamelContext());
        }
        if (method != null) {
            answer.setMethod(method);
        }
        return answer;
    }

    @Override
    public String getLabel() {
        if (ref != null) {
            String methodText = "";
            if (method != null) {
                methodText = " method: " + method;
            }
            return "ref: " + ref + methodText;
        } else if (bean != null) {
            return bean.toString();
        } else if (beanType != null) {
            return beanType.getName();
        } else {
            return "";
        }
    }
}
