package com.scholarsync.server.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class QuestionInputDTO {
    private String title;
    private String content;
    private String authorId;
    private String groupId;
    private List<MultipartFile> files;
}
