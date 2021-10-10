package com.example.qa.user.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Switch between String and JSON
 */
public class JsonHelper {

    public static <T> String toJson(ObjectMapper objectMapper, T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T fromJson(ObjectMapper objectMapper, String payload, Class<T> clazz) throws IOException {
        return objectMapper.readValue(payload, clazz);
    }

    public static <T> T fromJson(ObjectMapper objectMapper, String payload, TypeReference<T> clazz) throws IOException {
        return objectMapper.readValue(payload, clazz);
    }


}
