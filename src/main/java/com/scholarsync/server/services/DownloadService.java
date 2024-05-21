package com.scholarsync.server.services;

import com.scholarsync.server.entities.AnswerFiles;
import com.scholarsync.server.entities.QuestionFiles;
import com.scholarsync.server.repositories.AnswerFileRepository;
import com.scholarsync.server.repositories.QuestionFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DownloadService {

  @Autowired QuestionFileRepository questionFileRepository;
  @Autowired AnswerFileRepository answerFileRepository;

  record Result(Object file, String resultMsg) {};

  @Transactional
  public ResponseEntity<Object> downloadFile(String id, boolean isQuestion) {
    Result result;
    if (isQuestion) {
      result = downloadQuestionFile(id);
    } else {
      result = downloadAnswerFile(id);
    }

    if (result.resultMsg.equals("file/not-found")) {
      return ResponseEntity.notFound().build();
    }

    QuestionFiles questionFile = null;
    AnswerFiles answerFile = null;
    if (isQuestion) {
      questionFile = (QuestionFiles) result.file;
    } else {
      answerFile = (AnswerFiles) result.file;
    }

    HttpHeaders headers = new HttpHeaders();
    byte[] file = isQuestion ? questionFile.getFile() : answerFile.getFile();
    headers.add(
        "Content-Disposition",
        "attachment; filename="
            + (isQuestion ? questionFile.getFileName() : answerFile.getFileName()));
    headers.add(
        "Content-Type", (isQuestion ? questionFile.getFileType() : answerFile.getFileType()));

    return ResponseEntity.ok().headers(headers).body(file);
  }

  private Result downloadAnswerFile(String id) {
    Optional<AnswerFiles> answerFilesOptional = answerFileRepository.findById(id);
    if (answerFilesOptional.isEmpty()) {
      return new Result(null, "file/not-found");
    }
    return new Result(answerFilesOptional.get(), "file/found");
  }

  private Result downloadQuestionFile(String id) {
    Optional<QuestionFiles> questionFileOptional = questionFileRepository.findById(id);
    if (questionFileOptional.isEmpty()) {
      return new Result(null, "file/not-found");
    }
    return new Result(questionFileOptional.get(), "file/found");
  }
}
