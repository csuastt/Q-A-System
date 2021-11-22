package com.example.qa.order.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "upload-dir";
    private String locationPic = "upload-pic";

    public String getLocation() {
        return location;
    }

    public String getLocationPic(){
        return locationPic;
    }

    public void setLocationPic(String location){
        this.locationPic = location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
