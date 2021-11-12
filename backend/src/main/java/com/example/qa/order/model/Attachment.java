package com.example.qa.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Embeddable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attachment{
        private UUID uuid;
        private String filename;
        private long size;
        private ZonedDateTime uploadTime;


        public Attachment(MultipartFile file){
            this(UUID.fromString(file.getName()), file.getName(), file.getSize(), ZonedDateTime.now());
        }
}
