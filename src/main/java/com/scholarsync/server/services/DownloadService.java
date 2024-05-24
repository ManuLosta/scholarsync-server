package com.scholarsync.server.services;

import com.scholarsync.server.entities.File;
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

  record Result(File file, String resultMsg) {
  }

  @Transactional
  public ResponseEntity<Object> downloadFile(String id) {
    Result result = fetchFile(id);

    if (result.resultMsg.equals("file/not-found")) {
      return ResponseEntity.notFound().build();
    }

    File file = result.file;

    HttpHeaders headers = new HttpHeaders();
    byte[] fileBytes = file.getFile();
    headers.add(
            "Content-Disposition",
            "attachment; filename="
                    + (file.getFileName()));
    headers.add(
            "Content-Type", (file.getFileType()));

    return ResponseEntity.ok().headers(headers).body(file);
  }

  private Result fetchFile(String id) {
    Optional<File> optionalFile = fileRepository.findById(id);
    if (optionalFile.isEmpty()) {
      return new Result(null, "file/not-found");
    }
    return new Result(optionalFile.get(), "file/found");
  }
}