package com.jsoftware.platform.fileupload.mapper;

import com.jsoftware.platform.fileupload.model.FileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface FileMapper {
    List<FileVO> retrieveFileList();

    public ArrayList<FileVO> retrieveAllFileList();

    public FileVO retrieveFile(FileVO file);

    public long retrieveNextGroupId();

    long retrieveNextId();

    public int insertFilesInfoToDB(FileVO file);


    int updateFile(FileVO file);

    int deleteFile(FileVO file);

}
