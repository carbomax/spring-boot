package com.devhighlevel.awss3upload.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
public class StorageServiceImpl implements IStorageService{

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    AmazonS3 s3Client;


    @Override
    public String uploadFile(MultipartFile multipartFile) {

        File fileToBucket = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream outputStream = new FileOutputStream(fileToBucket)){
            outputStream.write(multipartFile.getBytes());
            String fileName = UUID.randomUUID().toString().concat(fileToBucket.getName());
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileToBucket));

        } catch (IOException e) {
           log.error(e);
           throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }finally {
            try {
                Files.delete(Paths.get(fileToBucket.getAbsolutePath()));
            } catch (IOException e) {
                log.error(e);
            }
            return "Create file in bucket";
        }

    }

    @Override
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream content = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(content);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }

    @Override
    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName.concat(" - Deleted");
    }


}
