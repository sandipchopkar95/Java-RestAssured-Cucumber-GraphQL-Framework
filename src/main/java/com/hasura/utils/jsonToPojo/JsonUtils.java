package com.hasura.utils.jsonToPojo;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    // Method to convert any object to a JSON string using Jackson ObjectMapper
    public static String objectToJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
