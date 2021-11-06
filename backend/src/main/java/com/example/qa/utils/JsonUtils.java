package com.example.qa.utils;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {
    private JsonUtils() {
    }

    public static final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
}
