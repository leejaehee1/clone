package com.jsoftware.platform.fileupload.service;

import com.jsoftware.platform.fileupload.mapper.FileMapper;
import com.jsoftware.platform.fileupload.model.FileVO;
import com.jsoftware.platform.fileupload.storage.StorageException;
import com.jsoftware.platform.fileupload.storage.StorageFileNotFoundException;
import com.jsoftware.platform.fileupload.storage.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
//import java.sql.Timestamp;


public class FileSystemStorageService implements StorageService {
    @Autowired
    FileMapper fileMapper;

    @Autowired
    StorageProperties properties;

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(Objects.requireNonNull(file.getOriginalFilename()))
            ).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException(
                        "Cannot store file outside current directory."
                );
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public void store(MultipartFile file, String encodedFilename) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(encodedFilename)
            ).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException(
                        "Cannot store file outside current directory."
                );
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }


    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public ArrayList<FileVO> loadAllFileList() {
        return fileMapper.retrieveAllFileList();
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file:" +filename, e)
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void insertFileInfoToDB(FileVO fileVO) {
        fileMapper.insertFilesInfoToDB(fileVO);
    }

    @Override
    public void insertFilesInfoToDB(List<FileVO> fileVOList) {
        for (FileVO fileVO : fileVOList) {
            fileMapper.insertFilesInfoToDB(fileVO);
        }

    }

    @Override
    public void updateFileToServer(MultipartFile multipartFile, FileVO fileVO) {
        deleteFileToServer(fileVO);
        store(multipartFile, fileVO.getServerFilename());
    }

    @Override
    public void updateFileInfoToDB(FileVO fileVO) {
        fileMapper.updateFile(fileVO);
    }

    @Override
    public void updateFile(MultipartFile multipartFile, FileVO fileVO) {
        fileVO = fileMapper.retrieveFile(fileVO);
        if (fileVO != null) {
            String clientFilename = multipartFile.getOriginalFilename();
            long fileSize = multipartFile.getSize();
            assert clientFilename != null;
            String extension = clientFilename.substring(clientFilename.lastIndexOf(".") + 1);

            System.out.println("originFileName : " + clientFilename);
            System.out.println("fileSize : " + fileSize);
            System.out.println("extension : " + extension);

            UUID one = UUID.randomUUID();

            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            assert md != null;
            md.update(one.toString().getBytes();
            StringBuilder builder = new StringBuilder();
            for (byte b : md.digest()) {
                builder.append(String.format("%02x", b));
            }
            System.out.println("hash(uuid) : " + builder);

//            Long timeNow = System.currentTimeMillis();

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//            System.currentTimeMillis();
            fileVO.setClientFilename(clientFilename);
            fileVO.setServerFilename(builder.toString());
            fileVO.setFileSize(BigDecimal.valueOf(fileSize));
            fileVO.setLastUpdateDate(timestamp);
//            fileVO.setLastUpdatedBy(BigDecimal.valueOf(551231231555));
            fileVO.setFileExtType(extension);
            fileVO.setServerPath(properties.getServerPath());
//            fileVO.setCreatedBy(BigDecimal.valueOf(2234234234234));

            updateFileToServer(multipartFile, fileVO);
            updateFileInfoToDB(fileVO);
        }
        assert fileVO != null;
//        log.warn("fileId not exists, '{}'", fileVO.getFileId());
    }

    @Override
    public void deleteFileToServer(FileVO file) {
//        String filename = file.getServerPath() + "/" + file.getServerFilename();
//        try {
//            Files.delete(Path.of(filename));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public long uploadFiles(List<MultipartFile> files) {
        List<FileVO> fileVOList = new ArrayList<>();

        long nextGroupId = fileMapper.retrieveNextGroupId();

        System.out.println(nextGroupId);

        int i = 0;

        for (MultipartFile multipartFile : files) {
            long nextId = fileMapper.retrieveNextId();
            FileVO fileVO = uploadFileToServer(multipartFile);
            fileVO.setFileId(nextId);
            fileVO.setFileGroupId(nextGroupId);
            fileVO.setFileSeq(i);
            fileVO.setServerPath(properties.getServerPath());
            // need to get sessionID
            fileVO.setCreatedBy(BigDecimal.valueOf(123123123));
            fileVOList.add(fileVO);
            i++;
        }
        insertFilesInfoToDB(fileVOList);
        return nextGroupId;
    }



    @Override
    public FileVO uploadFileToServer(MultipartFile file) {
        String originFileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        assert originFileName != null;
        String extension = originFileName.substring(originFileName.lastIndexOf(".") + 1);

        System.out.println("originFileName : " + originFileName);
        System.out.println("fileSize : " + fileSize);
        System.out.println("extension : " + extension);

        UUID one = UUID.randomUUID();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        md.update(one.toString().getBytes();
        StringBuilder builder = new StringBuilder();
        for (byte b : md.digest()) {
            builder.append(String.format("%02x", b));
        }
        System.out.println("hash(uuid) : " + builder);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        Timestamp timestamp = new Timestamp();
        store(file, builder.toString());

        FileVO fileVO = new FileVO();
        fileVO.setClientFilename(originFileName);
        fileVO.setServerFilename(builder.toString());
        fileVO.setFileSize(BigDecimal.valueOf(fileSize));
        fileVO.setCreationDate(timestamp);
        fileVO.setFileExtType(extension);
        fileVO.setServerPath(properties.getServerPath());
//        fileVO.setCreatedBy(BigDecimal.valueOf(2234234234234));
        return fileVO;

    }

    @Override
    public void downloadFile(int fileId, HttpServletResponse response) {
        FileVO file = new FileVO();
        file.setFileId(fileId);
        file = fileMapper.retrieveFile(file);
        String filename = file.getServerPath() + "/" + file.getServerFilename();

        response.setContentType("application/oct-stream");
        response.setHeader("Content-Disponsition", "attachment;filename=" + file.getClientFilename());
        response.setStatus(HttpServletResponse.SC_OK);

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            FileSystemResource resource = new FileSystemResource(filename);

            ZipEntry e = new ZipEntry(resource.getFilename());
            e.setSize(resource.contentLength());
            e.setTime(System.currentTimeMillis());

            zippedOut.putNextEntry(e);
            StreamUtils.copy(resource.getInputStream(), zippedOut);
            zippedOut.closeEntry();
            zippedOut.finish();
        } catch (Exception e) {
            //pass
        }
    }

    @Override
    public void downloadFile(String filename, HttpServletResponse response) {
        String clientFilename = filename.substring(filename.lastIndexOf("/" + 1));
        response.setContentType("application/oct-stream");
        response.setHeader("Content-Disponsition", "attachment;filename=" + clientFilename);
        response.setStatus(HttpServletResponse.SC_OK);

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            FileSystemResource resource = new FileSystemResource(filename);

            ZipEntry e = new ZipEntry(resource.getFilename());
            e.setSize(resource.contentLength());
            e.setTime(System.currentTimeMillis());

            zippedOut.putNextEntry(e);
            StreamUtils.copy(resource.getInputStream(), zippedOut);
            zippedOut.closeEntry();
            zippedOut.finish();
        } catch (Exception e) {
            //pass
        }
    }

    @Override
    public void downloadFiles(List<Long> ids, HttpServletResponse response) {
        response.setContentType("application/oct-stream");
        response.setHeader("Content-Disponsition", "attachment;filename=download.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        System.out.println("file size" + ids.size());
        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for (long id : ids) {
                FileVO file = new FileVO();
                file.setFileId(id);
                file = fileMapper.retrieveFile(file);
                Resource resource = loadAsResource(file.getServerFilename());

                ZipEntry e = new ZipEntry(file.getServerFilename());
                e.setSize(resource.contentLength());
                e.setTime(System.currentTimeMillis());

                zippedOut.putNextEntry(e);
                StreamUtils.copy(resource.getInputStream(), zippedOut);
                zippedOut.closeEntry();
            }
            zippedOut.finish();
        } catch (Exception e) {
            //pass
        }


    }
}
