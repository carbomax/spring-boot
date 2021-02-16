package com.devhighlevel.awss3upload.controller;

import com.devhighlevel.awss3upload.services.IStorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/upload")
public class UploadController {

    final IStorageService storageService;

    public UploadController(IStorageService storageService) {
        this.storageService = storageService;
    }


    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile multipartFile){
        return new ResponseEntity<>(storageService.uploadFile(multipartFile), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ByteArrayResource> uploadFile(@RequestParam String fileName){
        ByteArrayResource resource = new ByteArrayResource(storageService.downloadFile(fileName));
        return ResponseEntity.ok()
                .contentLength(storageService.downloadFile(fileName).length)
                .header("Content-Type", "application/octet-stream")
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam String fileName){
        return new ResponseEntity<>(storageService.deleteFile(fileName), HttpStatus.OK);
    }
}
