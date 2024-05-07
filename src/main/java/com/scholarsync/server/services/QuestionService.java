package com.scholarsync.server.services;

import com.scholarsync.server.dtos.FileDTO;
import com.scholarsync.server.dtos.QuestionDTO;
import com.scholarsync.server.dtos.QuestionInputDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.QuestionFileRepository;
import com.scholarsync.server.repositories.QuestionRepository;
import com.scholarsync.server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuestionService {

  @Autowired QuestionRepository questionRepository;
  @Autowired UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private QuestionFileRepository questionFileRepository;

  @Transactional
  public ResponseEntity<Object> getQuestion(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();
    QuestionDTO response = QuestionDTO.questionToDTO(question);

    return ResponseEntity.ok(response);
  }

  @Transactional
  public ResponseEntity<Object> downloadFiles(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body(null);
    }
    Question question = questionOptional.get();
    List<QuestionFiles> files = new ArrayList<>(question.getQuestionFiles());
    String questionId = question.getId();

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(baos);
      for (QuestionFiles questionFile : files) {
        if (questionFile.getFileType().contains("image")) {
          continue;
        }
        ZipEntry entry = new ZipEntry(questionFile.getId());
        entry.setSize(questionFile.getFile().length);
        zos.putNextEntry(entry);
        zos.write(questionFile.getFile());
        zos.closeEntry();
      }
      zos.close();
      byte[] zipBytes = baos.toByteArray();

      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=" + questionId + ".zip");

      return ResponseEntity.ok().headers(headers).body(zipBytes);
    } catch (IOException e) {
      return ResponseEntity.status(500).body(null);
    }
  }

  @Transactional
  public ResponseEntity<Object> getFiles(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();
    List<QuestionFiles> files = new ArrayList<>(question.getQuestionFiles());
    FileDTO[] fileDTOs = new FileDTO[files.size()];
    for (QuestionFiles file : files) {
      FileDTO fileDTO = FileDTO.fileToDTO(file);
      fileDTOs[files.indexOf(file)] = fileDTO;
    }
    return ResponseEntity.ok(fileDTOs);
  }

  @Transactional
  public ResponseEntity<Object> downloadFile(String id) {
    Optional<QuestionFiles> questionFileOptional = questionFileRepository.findById(id);
    if (questionFileOptional.isEmpty()) {
      return ResponseEntity.status(404).body("file/not-found");
    }
    QuestionFiles questionFile = questionFileOptional.get();
    byte[] file = questionFile.getFile();
    String fileName = questionFile.getFileName();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=" + fileName);
    headers.add("Content-Type", questionFile.getFileType());

    return ResponseEntity.ok().headers(headers).body(file);
  }

  public ResponseEntity<Object> getQuestionsByTitle(String title) {
    Set<Question> questions = questionRepository.findQuestionsByTitleContaining(title);
    if (questions.isEmpty()) {
      return ResponseEntity.ok(new ArrayList<Question>());
    }

    List<QuestionDTO> result = questions.stream().map(QuestionDTO::questionToDTO).toList();

    return ResponseEntity.ok(result);
  }

  @Transactional
  public ResponseEntity<Object> addFiles(List<MultipartFile> images, String questionId) {

    Optional<Question> questionOptional = questionRepository.findById(questionId);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();

    if (images != null) {
      Set<QuestionFiles> questionFiles =
          images.stream()
              .map(
                  file -> {
                    QuestionFiles questionFile = new QuestionFiles();
                    try {
                      questionFile.setFile(file.getBytes());
                      questionFile.setFileName(file.getOriginalFilename());
                      questionFile.setFileType(file.getContentType());
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    questionFile.setQuestion(question);
                    questionFileRepository.save(questionFile);
                    return questionFile;
                  })
              .collect(Collectors.toSet());

      question.setQuestionFiles(questionFiles);
    }

    questionRepository.save(question);

    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

  public ResponseEntity<Object> publishNoDocQuestion(QuestionInputDTO inputQuestion) {

    Question question = new Question();
    question.setTitle(inputQuestion.getTitle());
    question.setContent(inputQuestion.getContent());
    Optional<User> author = userRepository.findById(inputQuestion.getAuthorId());
    if (author.isEmpty()) {
      return ResponseEntity.status(404).body("user/not-found");
    }
    Optional<Group> group = groupRepository.findById(inputQuestion.getGroupId());
    if (group.isEmpty()) {
      return ResponseEntity.status(404).body("group/not-found");
    }
    author.get().removeCredits(author.get());
    question.setAuthor(author.get());
    question.setGroup(group.get());

    userRepository.save(author.get());
    groupRepository.save(group.get());
    questionRepository.save(question);

    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

  @Transactional
  public ResponseEntity<Object> getImages(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();

    List<Map<String, String>> images =
        question.getQuestionFiles().stream()
            .filter(questionFile -> questionFile.getFileType().contains("image"))
            .map(
                questionFile -> {
                  Map<String, String> imageMap = new HashMap<>();
                  imageMap.put("fileType", questionFile.getFileType());
                  imageMap.put(
                      "base64Encoding", Base64.getEncoder().encodeToString(questionFile.getFile()));
                  imageMap.put("name", questionFile.getId());
                  return imageMap;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(images);
  }

  @Transactional
  public Object publishQuestion(QuestionInputDTO info, List<MultipartFile> files) {

    ResponseEntity<Object> noQuestionInfo = publishNoDocQuestion(info);
    if (noQuestionInfo.getStatusCode() != HttpStatusCode.valueOf(200)) {
      return noQuestionInfo;
    }
    QuestionDTO question = (QuestionDTO) noQuestionInfo.getBody();
    String questionId = question.getId();
    return addFiles(files, questionId);
  }

  public double getQuestionScore(Question question, int offset, int limit) {
    double questionUserLevelWeight = 0.4;
    double mostUpvotedAnswerWeight = 0.3;
    double timeWeight = 0.1;
    double moustUpvoteResponseUserLevelWeight = 0.2;

    double questionScore = 0;
    int questionUserLevel = question.getAuthor().getLevel().ordinal() + 1;

    double timeScore = normalizeTimeDifference(question, offset, limit);

    Answer mostUpvotedAnswer =
        question.getAnswers().stream().max(Comparator.comparing(Answer::getUpvotes)).orElse(null);
    questionScore = questionUserLevel * questionUserLevelWeight + timeScore * timeWeight;

    if (mostUpvotedAnswer == null) {
      return questionScore;
    }
    int answerUserLevel = mostUpvotedAnswer.getUser().getLevel().ordinal() + 1;

    double upvoteScore = normalizeMostUpvotedAnswer(mostUpvotedAnswer, offset, limit);

    questionScore +=
        upvoteScore * mostUpvotedAnswerWeight
            + answerUserLevel * moustUpvoteResponseUserLevelWeight;
    return questionScore;
  }

  private double normalizeTimeDifference(Question question, int offset, int limit) {
    LocalDateTime now = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
    long daysDifference = ChronoUnit.DAYS.between(question.getCreatedAt(), now);
    long maxDaysDifference = 30L * (offset + 1);
    double score =
        limit - (double) Math.max(0, Math.min(limit, (limit * daysDifference) / maxDaysDifference));
    score = Math.max(0, Math.min(limit, score));
    return score;
  }

  private double normalizeMostUpvotedAnswer(Answer mostUpvotedAnswer, int offset, int limit) {
    int scoreDifference = mostUpvotedAnswer.getUpvotes() - mostUpvotedAnswer.getDownvotes();
    int maxScoreDifference = 1000 * (offset + 1);
    double normalizedScore =
        limit
            - (double) Math.max(0, Math.min(limit, (limit * scoreDifference) / maxScoreDifference));
    normalizedScore = Math.max(0, Math.min(limit, normalizedScore));
    return normalizedScore;
  }

  @Async
  public CompletableFuture<ResponseEntity<List<QuestionScoreDTO>>> getQuestionsByScore(
      int offset, int limit, String userId) {
    List<Question> questions = questionRepository.findAll();
    User user = userRepository.findById(userId).orElse(null);
    List<Question> userQuestions =
        questions.stream()
            .filter(question -> question.getGroup().getUsers().contains(user))
            .toList();

    List<QuestionScoreDTO> questionScores =
        userQuestions.parallelStream()
            .map(
                question ->
                    new QuestionScoreDTO(
                        QuestionDTO.questionToDTO(question),
                        getQuestionScore(question, offset, limit)))
            .sorted(Comparator.comparingDouble(QuestionScoreDTO::getScore))
            .collect(Collectors.toList());

    return CompletableFuture.completedFuture(ResponseEntity.ok(questionScores));
  }

  public static class QuestionScoreDTO {
    private final QuestionDTO question;
    private final double score;

    public QuestionScoreDTO(QuestionDTO question, double score) {
      this.question = question;
      this.score = score;
    }

    public QuestionDTO getQuestion() {
      return question;
    }

    public double getScore() {
      return score;
    }
  }
  }
