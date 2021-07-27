package com.jsoftware.platform.fileupload.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("storage")
public class StorageProperties {

    private String location;

    private String serverPath;

    public String getLocation() {
        return location;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
}
