package com.scholarsync.server.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionInputDTO {
    private String title;
    private String content;
    private String authorId;
    private String groupId;
    private List<Byte[]> files;
}
