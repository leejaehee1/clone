package com.jsoftware.platform.fileupload;

import com.jsoftware.platform.fileupload.mapper.FileMapper;
import com.jsoftware.platform.fileupload.model.FileVO;
import com.jsoftware.platform.fileupload.service.StorageService;
import com.jsoftware.platform.fileupload.storage.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    public  FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/uploadForm")
    public String listUploadedFiles(Model model) throws IOException {
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));
        for (FileVO fileVO : storageService.loadAllFileList()) {
            System.out.println(fileVO.getFileId());
        }

        model.addAttribute("filesInfo", storageService.loadAllFileList());

        return "uploadForm.html";
    }

    @PostMapping("/uploadForm")
    public String handleFileUpload(@RequestParam("file")List<MultipartFile> files,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + files.size() + "!");

        long groupId = storageService.uploadFiles(files);

        return "redirect:/uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    //위가 테스트
    // 아래가 실제

    @PostMapping("/fileupload/upload")
    @ResponseBody
    public String handleFilesUpload(@RequestParam("file") List<MultipartFile> files,
                                    RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "You sucessfully uploaded " + files.size() + "!");

        long groupId = storageService.uploadFiles(files);

        return String.valueOf(groupId);

    }

    @GetMapping("/fileupload/download/{id:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(HttpServletResponse response, @PathVariable long id) {
        FileVO file = new FileVO();
        file.setFileId(id);
        file = fileMapper.retrieveFile(file);
        Resource fileResource = storageService.loadAsResource(file.getServerFilename());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getClientFilename() + "\"").body(fileResource);
    }

    @GetMapping("/fileupload/zip/{ids:.+}")
    @ResponseBody
    public String downloadFiles(HttpServletResponse response, @PathVariable("ids") List<Long> ids) {
        storageService.downloadFiles(ids, response);
        return "success!";
    }

    public ResponseEntity<Resource> servefileId(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\'").body(file);
    }

    @PostMapping("fileupload/update")
    @ResponseBody
    public  String updateFiles(@RequestParam("file") MultipartFile file,
                               @RequestParam("id") long id) {
        FileVO fileVO = new FileVO();
        fileVO.setFileId(id);
        storageService.updateFile(file, fileVO);
        return "success!";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
