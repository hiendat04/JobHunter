package vn.hiendat04.jobhunter.controller;

import vn.hiendat04.jobhunter.service.FileService;
import vn.hiendat04.jobhunter.domain.response.file.ResUploadFileDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.StorageException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${hiendat04.upload-file.base-uri}")
    private String baseUri;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload file successfully!")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {

        // Skip validate
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file!");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. Only allows " + allowedExtensions.toString());
        }

        // Create a directory if not exist
        this.fileService.createDirectory(baseUri + folder);

        // Store file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }
}
