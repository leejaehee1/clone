package com.jsoftware.platform.fileupload.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {
    @Value("${spring.servlet.multipart.location}")
    private String location;

    @Value("${fileupload.server-path}")
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
