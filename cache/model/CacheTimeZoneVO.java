package com.jsoftware.platform.cache.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CacheTimeZoneVO {
//    private String timeZoneCode;
    private Timestamp creationDate;
    private BigDecimal gmtOffset;
}
