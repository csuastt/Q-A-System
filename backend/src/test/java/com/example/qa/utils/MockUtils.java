package com.example.qa.utils;

import com.example.qa.security.SecurityConstants;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class MockUtils {
    private final MockMvc mockMvc;
    private final JsonMapper mapper;

    public MockUtils(MockMvc mockMvc, JsonMapper mapper) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    public MvcResult postUrl(String url, String token, Object request, ResultMatcher matcher) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post(url);
        if (token != null) {
            requestBuilder = requestBuilder.header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        }
        if (request != null) {
            requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request));
        }
        return mockMvc.perform(requestBuilder).andExpect(matcher).andReturn();
    }

    public <T> T postAndDeserialize(String url, String token, Object request, ResultMatcher matcher, Class<T> type) throws Exception {
        return mapper.readValue(postUrl(url, token, request, matcher).getResponse().getContentAsString(), type);
    }

    public MvcResult getUrl(String url, String token, HashMap<String, String> params, Object request, ResultMatcher matcher) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(url);
        if (token != null) {
            requestBuilder = requestBuilder.header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        }
        if (request != null) {
            requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request));
        }
        if(params != null){
            for(var key : params.keySet())
                requestBuilder = requestBuilder.param(key, params.get(key));
        }
        return mockMvc.perform(requestBuilder).andExpect(matcher).andReturn();
    }

    public <T> T getAndDeserialize(String url, String token, HashMap<String, String> params, Object request, ResultMatcher matcher, Class<T> type) throws Exception {
        return mapper.readValue(getUrl(url, token, params, request, matcher).getResponse().getContentAsString(), type);
    }

    public MvcResult putUrl(String url, String token, Object request, ResultMatcher matcher) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = put(url);
        if (token != null) {
            requestBuilder = requestBuilder.header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        }
        if (request != null) {
            requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request));
        }
        return mockMvc.perform(requestBuilder).andExpect(matcher).andReturn();
    }

    public MvcResult deleteUrl(String url, String token, Object request, ResultMatcher matcher) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = delete(url);
        if (token != null) {
            requestBuilder = requestBuilder.header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        }
        if (request != null) {
            requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request));
        }
        return mockMvc.perform(requestBuilder).andExpect(matcher).andReturn();
    }
}
