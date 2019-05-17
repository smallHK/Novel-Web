package com.hk.service;

import com.hk.entity.Chapter;
import com.hk.po.NovelInfo;
import com.hk.repository.ChapterRepository;
import com.hk.repository.FavoriteRepo;
import com.hk.repository.NovelRepository;
import org.springframework.stereotype.Service;

/**
 * @author smallHK
 * 2019/5/16 8:31
 */
@Service
public class NovelInfoService {

    private FavoriteRepo favoriteRepo;

    private ChapterRepository chapterRepository;


    public NovelInfoService(FavoriteRepo favoriteRepo,
                            ChapterRepository chapterRepository) {
        this.favoriteRepo = favoriteRepo;
        this.chapterRepository = chapterRepository;
    }


    /**
     * 获取小说的总点击数
     */
    public Integer gainNovelTotalClick(Integer novelId) {
        return chapterRepository.findAllByNovelId(novelId).stream().mapToInt(Chapter::getClickCount).sum();
    }

    /**
     * 获取小说的总收藏数
     */
    public Integer gainNovelTotalFavorite(Integer novelId) {
        return favoriteRepo.countAllByNovelId(novelId);
    }


}
