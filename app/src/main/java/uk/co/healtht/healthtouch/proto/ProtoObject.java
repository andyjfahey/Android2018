package uk.co.healtht.healthtouch.proto;

import java.lang.reflect.Field;

@SuppressWarnings("serial")
public class ProtoObject implements java.io.Serializable {

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        Class<?> oClass = this.getClass();
        buffer.append(oClass.getSimpleName()).append("{\n");
        for (Field field : oClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                buffer.append(field.getName()).append("=");
                Object value = field.get(this);
                buffer.append(String.valueOf(value));
            }
            catch (Throwable e) {
                buffer.append(e.getMessage());
            }
            buffer.append("; ");
        }
        buffer.append("}\n");
        return buffer.toString();
    }
}