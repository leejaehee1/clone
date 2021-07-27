package com.jsoftware.platform.fileupload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {
    public long fileId;
    public long fileGroupId;
    public long fileSeq;
    public String ClientFilename;
    public String ServerFilename;
    public BigDecimal FileSize;
    public Timestamp CreationDate;
    public String FileExtType;
    public String ServerPath;
    public BigDecimal CreatedBy;
    public BigDecimal lastUpdatedBy;
    public Timestamp lastUpdateDate;

}
