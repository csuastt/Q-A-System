package com.example.qa.order.exchange;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadRequest {
    private MultipartFile file;

    @JsonCreator
    public UploadRequest(MultipartFile file){
        this.file = file;
    }

}
