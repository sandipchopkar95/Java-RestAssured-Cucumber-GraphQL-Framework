package com.hasura.utils.jsonToPojo;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON serialization/deserialization using Jackson ObjectMapper.
 */
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts any Java object to its JSON string representation.
     * @param object The object to convert.
     * @return A JSON string, or null if an error occurs.
     */
    public static String objectToJsonString(Object object) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            System.err.println("Error converting object to JSON string: " + e.getMessage());
            e.printStackTrace();
        }
        return jsonString;
    }
}