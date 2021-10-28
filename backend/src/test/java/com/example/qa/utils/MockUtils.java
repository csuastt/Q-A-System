package com.example.qa.utils;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class MockUtils {
    private MockUtils() {
    }

    public static MvcResult postUrl(String url, Object request, ResultMatcher matcher) throws Exception {
        return mockMvc
                .perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(matcher)
                .andReturn();
    }

}
