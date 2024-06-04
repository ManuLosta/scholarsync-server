package com.scholarsync.server.services;

import com.scholarsync.server.entities.Files;
import com.scholarsync.server.repositories.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DownloadService {

  @Autowired
  FileRepository fileRepository;

  record Result(Files files, String resultMsg) {
  }

  @Transactional
  public ResponseEntity<Object> downloadFile(String id) {
    Result result = fetchFile(id);

    if (result.resultMsg.equals("file/not-found")) {
      return ResponseEntity.notFound().build();
    }

    Files files = result.files;

    HttpHeaders headers = new HttpHeaders();
    byte[] fileBytes = files.getFile();
    headers.add(
            "Content-Disposition",
            "attachment; filename="
                    + (files.getFileName()));
    headers.add(
            "Content-Type", (files.getFileType()));

    return ResponseEntity.ok().headers(headers).body(files);
  }

  private Result fetchFile(String id) {
    Optional<Files> optionalFile = fileRepository.findById(id);
    if (optionalFile.isEmpty()) {
      return new Result(null, "file/not-found");
    }
    return new Result(optionalFile.get(), "file/found");
  }
}