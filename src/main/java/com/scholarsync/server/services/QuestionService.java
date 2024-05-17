package com.scholarsync.server.services;

import com.scholarsync.server.dtos.AnswerDTO;
import com.scholarsync.server.dtos.FileDTO;
import com.scholarsync.server.dtos.QuestionDTO;
import com.scholarsync.server.dtos.QuestionInputDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import com.scholarsync.server.types.levelType;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuestionService {

  @Autowired QuestionRepository questionRepository;
  @Autowired UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private QuestionFileRepository questionFileRepository;
  @Autowired private AnswerRepository answerRepository;

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

  @Transactional
  public ResponseEntity<Object> getAnswersByQuestion(String questionId) {
    Optional<Question> questionOptional = questionRepository.findById(questionId);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();
    List<Answer> answers = new ArrayList<>(question.getAnswers());
    List<Answer> sortedAnswers =
        answers.stream()
            .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
            .toList();
    List<AnswerDTO> answerDTOS = sortedAnswers.stream().map(AnswerDTO::answerToDTO).toList();
    return ResponseEntity.ok(answerDTOS);
  }

  @Transactional
  public ResponseEntity<Object> getQuestionsByScore(int offset, int limit, String userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) return ResponseEntity.status(404).body("user/not-found");
    List<Map<String, Object>> normalizedQuestions = generateNormalizedQuestionsFromUser(userId);
    return generateScoreDto(offset, limit, normalizedQuestions);
  }

  @Transactional
  public ResponseEntity<Object> deleteQuestion(String id, String userId) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) return ResponseEntity.status(404).body("question/not-found");
    Question question = questionOptional.get();
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) return ResponseEntity.status(404).body("user/not-found");
    User user = userOptional.get();
    if (!question.getAuthor().getId().equals(user.getId()))
      return ResponseEntity.status(403).body("user/not-authorized");
    questionRepository.delete(question);
    return ResponseEntity.ok("question/deleted");
  }

  @Transactional
  public ResponseEntity<Object> editQuestion(
      String id, String title, String content, List<MultipartFile> files) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) return ResponseEntity.status(404).body("question/not-found");
    Question question = questionOptional.get();
    if (title != null) question.setTitle(title);
    if (content != null) question.setContent(content);
    if (files != null) {
      questionFileRepository.deleteAll(question.getQuestionFiles());
      addFiles(files, id);
    }
    questionRepository.save(question);
    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

  @Transactional
  public ResponseEntity<Object> getQuestionsByGroup(String groupId, int offset, int limit) {
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    if (groupOptional.isEmpty()) {
      return ResponseEntity.status(404).body("group/not-found");
    }
    List<Map<String, Object>> normalizedQuestions = generateNormalizedQuestionsFromGroup(groupId);
    return generateScoreDto(offset, limit, normalizedQuestions);
  }

  @Transactional
  public ResponseEntity<Object> getQuestionsByDateAndGroup(String groupId, int offset, int limit) {
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    if (groupOptional.isEmpty()) {
      return ResponseEntity.status(404).body("group/not-found");
    }
    Group group = groupOptional.get();
    List<Question> questions = new ArrayList<>(group.getQuestions());
    questions.sort(Comparator.comparing(Question::getCreatedAt).reversed());
    List<QuestionDTO> questionDTOS = questions.stream().map(QuestionDTO::questionToDTO).toList();
    questionDTOS.subList(
        Math.min(offset * limit, questionDTOS.size()),
        Math.min(questionDTOS.size(), offset * limit + limit));
    return ResponseEntity.ok(questionDTOS);
  }

  @Transactional
  public ResponseEntity<Object> getQuestionsByDateAndUser(String userId, int offset, int limit) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      return ResponseEntity.status(404).body("user/not-found");
    }
    User user = userOptional.get();
    List<Question> userQuestions = new ArrayList<>();
    for (Group group : user.getGroups()) {
      userQuestions.addAll(group.getQuestions());
    }
    userQuestions.sort(Comparator.comparing(Question::getCreatedAt).reversed());
    List<QuestionDTO> questionDTOS =
        userQuestions.stream().map(QuestionDTO::questionToDTO).toList();
    questionDTOS.subList(
        Math.min(offset * limit, questionDTOS.size()),
        Math.min(questionDTOS.size(), offset * limit + limit));
    return ResponseEntity.ok(questionDTOS);
  }

  private ResponseEntity<Object> generateScoreDto(
      int offset, int limit, List<Map<String, Object>> normalizedQuestions) {
    List<Map<String, Object>> filteredQuestions =
        normalizedQuestions.subList(
            Math.min(offset * limit, normalizedQuestions.size()),
            Math.min(normalizedQuestions.size(), offset * limit + limit));
    List<QuestionScoreDTO> result = new ArrayList<>();
    for (Map<String, Object> question : filteredQuestions) {
      Question questionEntity = questionRepository.getReferenceById((String) question.get("id"));
      QuestionDTO questionDTO = QuestionDTO.questionToDTO(questionEntity);
      result.add(new QuestionScoreDTO(questionDTO, (double) question.get("totalScore")));
    }
    return ResponseEntity.ok(result);
  }

  // --------------------------------helper methods----------------------------------------------

  public List<Map<String, Object>> getQuestionsFinalScore(
      Map<String, Map<String, Object>> normalizedLikes,
      Map<String, Map<String, Object>> normalizedDates) {

    Set<Map.Entry<String, Map<String, Object>>> normalizedDatesEntry = normalizedDates.entrySet();

    List<Map<String, Object>> questionScores = new ArrayList<>();
    for (Map.Entry<String, Map<String, Object>> entry : normalizedDatesEntry) {
      Question question = questionRepository.getReferenceById(entry.getKey());
      levelType authorLevel = question.getAuthor().getLevel();
      Map<String, Object> item = new HashMap<>();
      String id = entry.getKey();
      Double dateScore = (Double) entry.getValue().get("score");
      Double likeScore = 0.0;
      String likeLevelId = null;
      if (normalizedLikes.get(id) != null) {
        likeScore = (Double) normalizedLikes.get(id).get("score");
        likeLevelId = (String) normalizedLikes.get(id).get("netLikesId");
      }
      item.put("id", id);
      item.put("dateScore", dateScore);
      item.put("likeScore", likeScore);
      item.put("authorLevelScore", authorLevel.ordinal() + 1);
      item.put(
          "likeLevelScore",
          likeLevelId == null
              ? 0
              : answerRepository.getReferenceById(likeLevelId).getUser().getLevel().ordinal() + 1);
      item.put(
          "totalScore",
          dateScore * 0.1
              + likeScore * 0.3
              + (Integer) item.get("authorLevelScore") * 0.04
              + (Integer) item.get("likeLevelScore") * 0.02);
      questionScores.add(item);
    }

    questionScores =
        questionScores.stream()
            .sorted(
                (map1, map2) ->
                    Double.compare(
                        (double) map2.get("totalScore"), (double) map1.get("totalScore")))
            .collect(Collectors.toList());
    return questionScores;
  }

  private List<Map<String, Object>> generateNormalizedQuestionsFromUser(String userId) {
    List<Question> questions = questionRepository.findAll();
    User user = userRepository.findById(userId).orElse(null);
    List<Question> userQuestions =
        questions.stream()
            .filter(question -> question.getGroup().getUsers().contains(user))
            .filter(question -> !question.getAuthor().getId().equals(userId))
            .collect(Collectors.toList());

    return getMaps(userQuestions);
  }

  private List<Map<String, Object>> generateNormalizedQuestionsFromGroup(String groupId) {
    Optional<Group> group = groupRepository.findById(groupId);
    if (group.isEmpty()) return new ArrayList<>();
    Set<Question> questions = group.get().getQuestions();
    List<Question> groupQuestions = new ArrayList<>(questions);

    return getMaps(groupQuestions);
  }

  private List<Map<String, Object>> getMaps(List<Question> groupQuestions) {
    Map<String, List<Map<String, Object>>> questionData = extractQuestionData(groupQuestions);

    List<Map<String, Object>> likes = questionData.get("likes");
    List<Map<String, Object>> dates = questionData.get("dates");

    Map<String, Map<String, Object>> normalizedLikes = normalizeList(likes);
    Map<String, Map<String, Object>> normalizedDates = normalizeList(dates);

    return getQuestionsFinalScore(normalizedLikes, normalizedDates);
  }

  public Map<String, List<Map<String, Object>>> extractQuestionData(List<Question> userQuestions) {
    List<Map<String, Object>> questionDates =
        userQuestions.stream()
            .map(
                question -> {
                  Map<String, Object> map = new HashMap<>();
                  map.put("value", question.getCreatedAt());
                  map.put("id", question.getId());
                  return map;
                })
            .sorted(
                (Comparator.comparing(
                        map -> ((LocalDateTime) ((Map<String, Object>) map).get("value"))))
                    .reversed())
            .collect(Collectors.toList());

    List<Map<String, Object>> maxLikes =
        new ArrayList<>(
            userQuestions.stream()
                .map(
                    question -> {
                      Map<String, Object> info = new HashMap<>();
                      List<Answer> answers = new ArrayList<>(question.getAnswers());
                      List<Map<String, Object>> likesPerAnswer =
                          answers.stream()
                              .map(
                                  answer -> {
                                    Map<String, Object> map = new HashMap<>();
                                    double rating = 0;
                                    for (Rating r : answer.getRatings()) {
                                      rating += r.getRating();
                                    }
                                    if (!answer.getRatings().isEmpty())
                                      rating /= answer.getRatings().size();
                                    map.put("likes", rating);
                                    map.put("id", question.getId());
                                    map.put("responseId", answer.getId());
                                    return map;
                                  })
                              .toList();
                      info.put(
                          "value",
                          likesPerAnswer.stream()
                              .map(map -> (double) map.get("likes"))
                              .max(Comparator.naturalOrder())
                              .orElse(0.0));
                      info.put("id", question.getId());
                      info.put(
                          "netLikesId",
                          likesPerAnswer.stream()
                              .min(
                                  (map1, map2) ->
                                      Double.compare(
                                          (double) map2.get("likes"), (double) map1.get("likes")))
                              .map(map -> map.get("responseId"))
                              .orElse(null));
                      return info;
                    })
                .toList());
    maxLikes.sort(
        (Comparator.comparing(map -> (Double) ((Map<String, Object>) map).get("value")))
            .reversed());

    Map<String, List<Map<String, Object>>> result = new HashMap<>();
    result.put("dates", questionDates);
    result.put("likes", maxLikes);

    return result;
  }

  public static Map<String, Map<String, Object>> normalizeList(List<Map<String, Object>> items) {
    if (items.isEmpty()) {
      return new HashMap<>();
    }

    if (items.getFirst().get("value") instanceof LocalDateTime) {
      int min =
          (int)
              ((LocalDateTime) items.getLast().get("value"))
                  .toEpochSecond(java.time.ZoneOffset.UTC);
      int max =
          (int)
              ((LocalDateTime) items.getFirst().get("value"))
                  .toEpochSecond(java.time.ZoneOffset.UTC);
      Map<String, Map<String, Object>> result = new HashMap<>();
      for (Map<String, Object> item : items) {
        double score =
            (double)
                    (((LocalDateTime) item.get("value")).toEpochSecond(java.time.ZoneOffset.UTC)
                        - min)
                / (max - min);
        Map<String, Object> entry = new HashMap<>();
        entry.put("value", item.get("value"));
        entry.put("score", score);
        result.put((String) item.get("id"), entry);
      }
      return result;
    } else if (items.getFirst().get("value") instanceof Double) {
      double min = (double) items.getLast().get("value");
      double max = (double) items.getFirst().get("value");
      int size = items.size();
      Map<String, Map<String, Object>> result = new HashMap<>();
      for (Map<String, Object> item : items) {
        double score = (double) ((double) item.get("value") - min) / (max - min);
        Map<String, Object> entry = new HashMap<>();
        entry.put("value", item.get("value"));
        entry.put("score", score);
        entry.put("netLikesId", item.get("netLikesId"));
        result.put((String) item.get("id"), entry);
      }
      return result;
    } else {
      throw new IllegalArgumentException("Invalid type");
    }
  }

  public record QuestionScoreDTO(QuestionDTO question, double score) {}
}
