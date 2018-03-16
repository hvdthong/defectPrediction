package org.apache.camel.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The unique identifier for a bean. The scope of the identifier is the enclosing bean factory.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;complexType name="identifiedType">
 *   &lt;complexContent>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @version $Revision: 660266 $
 */
@XmlType(name = "identifiedType")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class IdentifiedType {
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(String value) {
        this.id = value;
    }
}
