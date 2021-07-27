package com.jsoftware.platform.fileupload.service;

import com.jsoftware.platform.fileupload.model.FileVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file);

    void store(MultipartFile file, String encodedFilename);

    Stream<Path> loadAll();

    ArrayList<FileVO> loadAllFileList();

    long uploadFiles(List<MultipartFile> files);

    void insertFileInfoToDB(FileVO fileVO);

    void insertFilesInfoToDB(List<FileVO> fileVOList);

    FileVO uploadFileToServer(MultipartFile file);

    void deleteAll();

    void deleteFileToServer(FileVO fileVO);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void downloadFile(int fileId, HttpServletResponse response);

    void downloadFile(String filename, HttpServletResponse response);

    void downloadFiles(List<Long> filenames, HttpServletResponse response);

    void updateFileToServer(MultipartFile multipartFile, FileVO fileVO);

    void updateFileInfoToDB(FileVO fileVO);

    void updateFile(MultipartFile file, FileVO fileVO);
}
