package com.hk.service;

import com.hk.entity.Chapter;
import com.hk.entity.Novel;
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

    private NovelRepository novelRepository;

    public NovelInfoService(FavoriteRepo favoriteRepo,
                            ChapterRepository chapterRepository,
                            NovelRepository novelRepository) {
        this.favoriteRepo = favoriteRepo;
        this.chapterRepository = chapterRepository;
        this.novelRepository = novelRepository;
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

    /**
     * 小说信息
     */
    public Novel gainPlainNovelInfo(Integer novelId) {
        return novelRepository.findById(novelId).orElseThrow();
    }

}
