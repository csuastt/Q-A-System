package com.example.qa.test;

import com.example.qa.order.storage.StorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@EnableConfigurationProperties(StorageProperties.class)
public class UpLoadControllerTest {
    
}
