package uk.co.healtht.healthtouch.comms;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This is from the documentation at:
 * http://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/TypeAdapterFactory.html
 */
public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!type.getRawType().isEnum()) {
            return null;
        }

        final TypeAdapter<T> elementAdapter = gson.getAdapter(type);

        return new TypeAdapter<T>() {

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                }
                else {
                    out.value(value.toString());
                }
            }

            @Override
            public T read(JsonReader reader) throws IOException {
                return elementAdapter.read(reader);
            }
        };
    }
}