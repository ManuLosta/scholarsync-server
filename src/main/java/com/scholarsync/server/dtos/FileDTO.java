package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO {
    private String id;
    private String name;
    private String file_type;

    public static FileDTO fileToDTO(File file) {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(file.getId());
        fileDTO.setName(file.getFileName());
        fileDTO.setFile_type(file.getFileType());
        return fileDTO;
    }
}
