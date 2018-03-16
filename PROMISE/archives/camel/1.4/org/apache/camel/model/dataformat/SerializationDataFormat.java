package org.apache.camel.model.dataformat;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.RouteContext;

/**
 * Represents the Java Serialization {@link DataFormat}
 *
 * @version $Revision: 659007 $
 */
@XmlRootElement(name = "serialization")
public class SerializationDataFormat extends DataFormatType {
    @Override
    protected DataFormat createDataFormat(RouteContext routeContext) {
        return new org.apache.camel.impl.SerializationDataFormat();
    }
}
