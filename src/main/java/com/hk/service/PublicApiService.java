package com.hk.service;

import com.hk.constant.EntityStatus;
import com.hk.po.NovelInfo;
import com.hk.repository.NovelRepository;
import org.springframework.stereotype.Service;

/**
 * @author smallHK
 * 2019/5/17 17:59
 */
@Service
public class PublicApiService {

    private NovelService novelService;

    private NovelRepository novelRepository;

    public PublicApiService(NovelService novelService,
                            NovelRepository novelRepository) {
        this.novelService = novelService;
        this.novelRepository = novelRepository;
    }


    public NovelInfo gainNovelInfo(String novelName) {
        return novelRepository.findAllByNovelName(novelName)
                .filter(e->!e.getStatus().equals(EntityStatus.NOVEL_PASSED))
                .map(novelService::novelToNovelInfo).orElse(null);
    }
}
