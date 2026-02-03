package custom.striker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for Reflection related tasks
 */
public final class ReflectionService {
    private static final Logger log = LoggerFactory.getLogger(ReflectionService.class);

    /**
     * Converts a provided Map of Key Value pairs to an object of the provided class
     * @param keyValuePairs The keys and values to map to the object. Keys should match the object's field names
     * @param klass The class of object to create
     * @return The object with the values from the keyValuePairs map
     * @param <T> Type of the object to be returned
     */
    public static <T> T convertKeyValuePairsToObject(Map<String, String> keyValuePairs, Class<T> klass) {
        try {
            final T objectToReturn = klass.getConstructor().newInstance();
            final Field[] declaredFields = klass.getDeclaredFields();
            Map<String, String> convertedPairs = createCaseFormatAgnosticKeyValuePairMap(keyValuePairs);

            for (Field field: declaredFields) {

                boolean accessible = field.canAccess(objectToReturn);
                field.setAccessible(true);
                String value = convertedPairs.get(field.getName().toLowerCase());

                if (value != null) {
                    setFieldValue(value, field, objectToReturn);
                }

                field.setAccessible(accessible);
            }

            return objectToReturn;

        } catch (Exception e) {
            log.error("Error when converting key value pairs to object. Object Class {}", klass.getSimpleName(), e);
        }
        return null;
    }

    private static Map<String, String> createCaseFormatAgnosticKeyValuePairMap(Map<String, String> keyValuePairs) {
        Map<String, String> result = new HashMap<>();

        for (String key: keyValuePairs.keySet()) {
            String newKey = key.toLowerCase().replaceAll("_", "");
            result.put(newKey, keyValuePairs.get(key));
        }

        return result;
    }

    private static <T> void setFieldValue(String value, Field field, T objectToReturn) throws Exception {
        final Class<?> fieldType = field.getType();

        if (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)) {

            field.set(objectToReturn, Integer.valueOf(value));

        } else if (Long.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)) {

            field.set(objectToReturn, Long.valueOf(value));

        } else if (Double.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType)) {

            field.set(objectToReturn, Double.valueOf(value));

        } else if (String.class.isAssignableFrom(fieldType)) {

            field.set(objectToReturn, value);

        } else if (boolean.class.isAssignableFrom(fieldType) || Boolean.class.isAssignableFrom(fieldType)) {

            int i = Integer.parseInt(value);
            field.setBoolean(objectToReturn, i == 1);

        } else {
            log.error("Unknown field type to convert value to. Field {} Object Class {}", fieldType.getSimpleName(), objectToReturn.getClass().getSimpleName());
        }
    }
}
