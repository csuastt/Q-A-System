package com.example.qa.test;

import com.example.qa.order.model.Attachment;
import com.example.qa.order.storage.StorageException;
import com.example.qa.order.storage.StorageFileNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UpLoadBeanTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void testForAttachment(){
        UUID uuid = UUID.randomUUID();
        String filename = "testFile.txt";
        long size = 223L;
        ZonedDateTime uploadTime = ZonedDateTime.now();
        Attachment attachment = new Attachment();
        Attachment attachment1 = new Attachment(uuid, filename, size, uploadTime);
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "testFile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        new Attachment(file);
        attachment.setUuid(uuid);
        attachment.setFilename(filename);
        attachment.setSize(223L);
        attachment.setUploadTime(uploadTime);
        assertEquals(uuid, attachment.getUuid());
        assertEquals(filename, attachment.getFilename());
        assertEquals(size, attachment.getSize());
        assertEquals(uploadTime, attachment.getUploadTime());

        assertEquals(uuid, attachment1.getUuid());
        assertEquals(filename, attachment1.getFilename());
        assertEquals(size, attachment1.getSize());
        assertEquals(uploadTime, attachment1.getUploadTime());
    }

    @Test
    void testForException(){
        StorageException exception = new StorageException("This is an exception");
        Exception exception1 = new Exception();
        StorageException exception2 = new StorageException("This is another exception", exception1);
    }

    @Test
    void testForNotFoundException(){
        StorageFileNotFoundException exception = new StorageFileNotFoundException("This is an exception");
        Exception exception1 = new Exception();
        StorageFileNotFoundException exception2 = new StorageFileNotFoundException("This is another exception", exception1);
    }
}
