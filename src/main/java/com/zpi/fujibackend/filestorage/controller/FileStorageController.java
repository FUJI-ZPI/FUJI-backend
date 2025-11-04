package com.zpi.fujibackend.filestorage.controller;


import com.zpi.fujibackend.filestorage.FileStorageFacade;
import com.zpi.fujibackend.filestorage.domain.ValidFilename;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/files")
@Validated
class FileStorageController {

    private final FileStorageFacade fileStorageFacade;


    @GetMapping("/download/{filename}")
    ResponseEntity<InputStreamResource> downloadFile(@PathVariable @ValidFilename String filename) {

        InputStream fileStream = fileStorageFacade.downloadFile(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(fileStream));


    }
}
