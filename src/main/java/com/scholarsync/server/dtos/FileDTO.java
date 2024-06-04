package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Files;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO {
    private String id;
    private String name;
    private String file_type;
    private String size;

    public static FileDTO fileToDTO(Files files) {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(files.getId());
        fileDTO.setName(files.getFileName());
        fileDTO.setSize(files.getRecommendedSize());
        fileDTO.setFile_type(files.getFileType());
        return fileDTO;
    }
}
