package org.apache.camel.impl.converter;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.TypeConverter;
import org.apache.camel.util.ObjectHelper;

/**
 * A type converter which is used to convert to and from array types
 * particularly for derived types of array component types and dealing with
 * primitive array types.
 *
 * @version $Revision: 687545 $
 */
public class EnumTypeConverter implements TypeConverter {

    public <T> T convertTo(Class<T> type, Object value) {
        if (type.isEnum() && value != null) {
            String text = value.toString();
            Method method = null;
            try {
                method = type.getMethod("valueOf", String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeCamelException("Could not find valueOf method on enum type: " + type.getName());
            }
            return (T) ObjectHelper.invokeMethod(method, null, text);
        }
        return null;
    }

    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) {
        return convertTo(type, value);
    }
}
