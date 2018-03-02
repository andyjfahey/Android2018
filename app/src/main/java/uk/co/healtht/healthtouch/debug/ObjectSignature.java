package uk.co.healtht.healthtouch.debug;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectSignature {
    // --------------------------------------- Debug code -----------------------------------------
    public static String checkObjectMapping(Object obj, Object mapObj) {
        if (obj == null && mapObj != null) {
            return "null!";
        }

        if (mapObj instanceof List<?> && obj instanceof Object[]) {
            Object[] listObject = (Object[]) obj;
            List<?> listMap = (List<?>) mapObj;
            for (int i = 0; i < listMap.size(); i++) {
                String res = checkObjectMapping(listObject[i], listMap.get(i));
                if (res != null) {
                    return "[" + i + "]->" + res;
                }
            }
            return null;
        }

        // If they are both map's, or the return from the server is null, we assume its OK.
        if (mapObj == null || !(mapObj instanceof Map)) {
            return null;
        }

        Map<?, ?> map = (Map<?, ?>) mapObj;
        if (obj instanceof Map) {
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) obj).entrySet()) {
                String res = checkObjectMapping(entry.getValue(), map.get(entry.getKey()));
                if (res != null) {
                    return "{" + entry.getKey() + "}" + res;
                }
            }
            return null;
        }

        Map<String, Field> objFields = findFields(obj.getClass());
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            // Sometimes the server sends us default data... don't care
            if (value == null || "".equals(value)) {
                continue;
            }

            Object subObject;
            try {
                // Field field = obj.getClass().getField(underscoreToCamelCase((String) key));
                Field field = objFields.get(underscoreToCamelCase((String) key));
                field.setAccessible(true);
                subObject = field.get(obj);
            }
            catch (Throwable e) {
                return key + "=" + map.get(key);
            }

            if (value instanceof List<?>) {
                if (subObject instanceof List<?>) {
                    List<?> listMap = (List<?>) value;
                    List<?> listObject = (List<?>) subObject;

                    for (int i = 0; i < listMap.size(); i++) {
                        String res = checkObjectMapping(listObject.get(i), listMap.get(i));
                        if (res != null) {
                            return key + "[" + i + "]->" + res;
                        }
                    }
                }
                else {
                    return key + " is List but Object is not";
                }
            }
            else {
                String res = checkObjectMapping(subObject, value);
                if (res != null) {
                    return key + "->" + res;
                }
            }
        }

        return null;
    }

    public static String underscoreToCamelCase(String str) {
        StringBuilder res = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_' && i < str.length() - 1) {
                i++; // Eat "_"
                c = Character.toUpperCase(str.charAt(i));
            }

            res.append(c);
        }

        return res.toString();
    }

    private static Map<String, Field> findFields(Class<?> classs) {
        Map<String, Field> res = new HashMap<>();
        Class<?> c = classs;
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                SerializedName sn = field.getAnnotation(SerializedName.class);
                if (sn == null) {
                    res.put(field.getName(), field);
                }
                else {
                    // If we have a SerializedName notation, use that name instead
                    res.put(underscoreToCamelCase(sn.value()), field);
                }
            }
            c = c.getSuperclass();
        }
        return res;
    }
}
