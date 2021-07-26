package com.jsoftware.platform.cache.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheMessageVO implements Serializable {
    private String messageCode;
    private String message;
    private String languageCode;
    private String messageGroup;
    private String messageType;
}
