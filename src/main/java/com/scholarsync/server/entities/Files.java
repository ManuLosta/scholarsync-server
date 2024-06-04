package com.scholarsync.server.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Lob
    @Column(name = "file")
    private byte[] file;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;


    public String getRecommendedSize() {
        int sizeInBytes = file.length;

        if (sizeInBytes < 1024) {
            return sizeInBytes + " Bytes";
        }

        double sizeInKB = (double) sizeInBytes / 1024;
        if (sizeInKB < 1024) {
            return String.format("%.2f KB", sizeInKB);
        }

        double sizeInMB = sizeInKB / 1024;
        if (sizeInMB < 1024) {
            return String.format("%.2f MB", sizeInMB);
        }

        double sizeInGB = sizeInMB / 1024;
        return String.format("%.2f GB", sizeInGB);
    }
}
