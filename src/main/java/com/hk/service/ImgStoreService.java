package com.hk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author smallHK
 * 2019/5/18 17:39
 */
@Service
public class ImgStoreService {

    @Value("${cover.location}")
    private String storePath;

    public void storeCoverImg(Integer creatorId, String coverFileName, byte[] coverData) {
        Path parent = Path.of(storePath, creatorId.toString());
        try {
            Files.createDirectories(parent);
            Path target = Path.of(parent.toString(), coverFileName);
            if (!Files.exists(target)) {
                Files.createFile(target);
                Files.write(target, coverData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
