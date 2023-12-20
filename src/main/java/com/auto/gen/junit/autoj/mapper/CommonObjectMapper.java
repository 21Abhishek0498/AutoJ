package com.auto.gen.junit.autoj.mapper;

import com.auto.gen.junit.autoj.dto.MyJunitClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Utility class for converting Java objects to JSON strings using Jackson.
 */
@Service
public class CommonObjectMapper {

    /**
     * Converts the specified Java object to a JSON string.
     *
     * @param object The Java object to be converted.
     * @return A JSON string representing the input object, or null if an error occurs during conversion.
     */
    public static String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString((MyJunitClass)object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
